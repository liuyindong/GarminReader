package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.io.*;
import java.util.*;

import javax.usb.*;
import javax.usb.util.*;

import com.ibm.jusb.os.*;
import com.ibm.jusb.util.*;

/**
 * UsbInterface platform-independent implementation.
 * <p>
 * This must be set up before use.
 * <ul>
 * <li>The UsbConfigurationImp must be set in the constructor or by its {@link #setUsbConfigurationImp(UsbConfigurationImp) setter}.</li>
 * <li>The UsbInterfaceDescriptor must be set either in the constructor or by its {@link #setUsbInterfaceDescriptor(UsbInterfaceDescriptor) setter}.</li>
 * <li>The UsbInterfaceOsImp may optionally be set either in the constructor or by its
 *     {@link #setUsbInterfaceOsImp(UsbInterfaceOsImp) setter}.
 *     If not set, it defaults to a {@link com.ibm.jusb.os.DefaultUsbInterfaceOsImp DefaultUsbInterfaceOsImp}.</li>
 * <li>If the active alternate setting number is not the first added to the parent UsbConfigurationImp either
 *     {@link com.ibm.jusb.UsbConfigurationImp#addUsbInterfaceImp(UsbInterfaceImp) directly} or by
 *     {@link #setUsbConfigurationImp(UsbConfigurationImp) setUsbConfigurationImp}, it must be
 *     {@link #setActiveSettingNumber(byte) set} after creating the active alternate setting.</li>
 * <li>All UsbEndpointImps must be {@link #addUsbEndpointImp(UsbEndpointImp) added}.</li>
 * </ul>
 * <p>
 * When changing the active alternate setting, call the {@link #setActiveSettingNumber(byte) setActiveSettingNumber} method.
 * This will update the parent config's active interface setting map.
 * @author Dan Streetman
 */
public class UsbInterfaceImp implements UsbInterface
{
	/**
	 * Constructor.
	 * @param config The parent config.  If this is not null, the UsbInterfaceDescriptor <strong>cannot</strong> be null.
	 * @param desc This interface's descriptor.  This <strong>cannot</strong> be null if the parent config is not null.
	 */
	public UsbInterfaceImp( UsbConfigurationImp config, UsbInterfaceDescriptor desc )
	{
		setUsbInterfaceDescriptor(desc);
		setUsbConfigurationImp(config);
	}

	/**
	 * Constructor.
	 * @param config The parent config.  If this is not null, the UsbInterfaceDescriptor <i>cannot</i> be null.
	 * @param desc This interface's descriptor.  This <strong>cannot</strong> be null if the parent config is not null.
	 * @param osImp The UsbInterfaceOsImp.
	 */
	public UsbInterfaceImp( UsbConfigurationImp config, UsbInterfaceDescriptor desc, UsbInterfaceOsImp osImp )
	{
		setUsbInterfaceDescriptor(desc);
		setUsbConfigurationImp(config);
		setUsbInterfaceOsImp(osImp);
	}

	//**************************************************************************
	// Public methods

	/**
	 * Claim this interface.
	 * <p>
	 * This calls the {@link #claim(UsbInterfacePolicy) other claim}
	 * with a {@link com.ibm.jusb.DefaultUsbInterfacePolicy default policy}.
	 * @exception UsbClaimException If the interface is already claimed.
	 * @exception UsbException if the interface could not be claimed.
	 * @exception UsbNotActiveException if the interface setting is not active.
	 * @exception UsbDisconnectedException If this device has been disconnected.
	 */
	public void claim() throws UsbClaimException,UsbException,UsbNotActiveException,UsbDisconnectedException
	{
		claim(defaultUsbInterfacePolicy);
	}

	/**
	 * Claim this interface using a UsbInterfacePolicy.
	 * <p>
	 * This will claim all alternate settings using the provided
	 * UsbInterfacePolicy.  If the interface is already claimed this
	 * will fail.
	 * <p>
	 * This can only be called from an
	 * {@link #isActive() active} alternate setting.
	 * @exception UsbClaimException If the interface is already claimed.
	 * @exception UsbException if the interface could not be claimed.
	 * @exception UsbNotActiveException if the interface setting is not active.
	 * @exception UsbDisconnectedException If this device has been disconnected.
	 */
	public synchronized void claim(UsbInterfacePolicy policy) throws UsbClaimException,UsbException,UsbNotActiveException,UsbDisconnectedException
	{
		checkDisconnected();

		checkSettingActive();

		if (isJavaClaimed())
			throw new UsbClaimException("UsbInterface is already claimed");

		getUsbInterfaceOsImp().claim(policy);

		setClaimed(policy);
	}

	/**
	 * Release this interface.
	 * @exception UsbClaimException If the interface is already claimed.
	 * @exception UsbException if the interface could not be released.
	 * @exception UsbNotActiveException if the interface setting is not active.
	 * @exception UsbDisconnectedException If this device has been disconnected.
	 */
	public void release() throws UsbClaimException,UsbException,UsbNotActiveException,UsbDisconnectedException
	{
		checkDisconnected();

		checkSettingActive();

		if (!isJavaClaimed())
			throw new UsbClaimException("UsbInterface is not claimed");

		for (int i=0; i<endpoints.size(); i++)
			if (((UsbEndpoint)endpoints.get(i)).getUsbPipe().isOpen())
				throw new UsbException("Cannot release UsbInterface with any open UsbPipe");

		getUsbInterfaceOsImp().release();

		setClaimed(null);
	}

	/** @return if this interface is claimed. */
	public boolean isClaimed()
	{
		try { checkSettingActive(); }
		catch ( UsbNotActiveException naE ) { return false; }

		if (isJavaClaimed())
			return true;
		else
			return getUsbInterfaceOsImp().isClaimed();
	}

	/**
	 * If this is claimed in java.
	 * <p>
	 * This should only be used by javax.usb implementations;
	 * this is not part of the javax.usb API.
	 * @return if this is claimed in java.
	 */
	public boolean isJavaClaimed()
	{
		try { checkSettingActive(); }
		catch ( UsbNotActiveException naE ) { return false; }

		return hasUsbInterfacePolicy();
	}

	/**
	 * If this interface setting is active.
	 * @return if this UsbInterface setting is active.
	 */
	public boolean isActive()
	{
		try {
			return getUsbInterfaceDescriptor().bAlternateSetting() == getActiveSettingNumber();
		} catch ( UsbNotActiveException naE ) {
			return false;
		}
	}

	/** @return The endpoints. */
	public List getUsbEndpoints() { return Collections.unmodifiableList(endpoints); }

	/**
	 * @param address The address of the UsbEndpoint to get.
	 * @return The UsbEndpoint with the specified address.
	 */
	public UsbEndpoint getUsbEndpoint( byte address ) { return getUsbEndpointImp(address); }

	/**
	 * @param address The address of the UsbEndpointImp to get.
	 * @return The UsbEndpointImp with the specified address, or null.
	 */
	public UsbEndpointImp getUsbEndpointImp( byte address )
	{
		synchronized ( endpoints ) {
			for (int i=0; i<endpoints.size(); i++) {
				UsbEndpointImp ep = (UsbEndpointImp)endpoints.get(i);

				if (address == ep.getUsbEndpointDescriptor().bEndpointAddress())
					return ep;
			}
		}

		return null;
	}

	/**
	 * @param address the address of the UsbEndpoint to check.
	 * @return if this UsbInterface contains the specified UsbEndpoint.
	 */
	public boolean containsUsbEndpoint( byte address )
	{
		if (null != getUsbEndpoint(address))
			return true;
		else
			return false;
	}

	/** @return The parent configuration */
	public UsbConfiguration getUsbConfiguration() { return getUsbConfigurationImp(); }

	/** @return The parent config */
	public UsbConfigurationImp getUsbConfigurationImp() { return usbConfigurationImp; }

	/**
	 * Set the UsbConfigurationImp.
	 * <p>
	 * This also adds this to the parent UsbConfigurationImp.  The
	 * UsbInterfaceDescriptor <i>must</i> be {@link #setUsbInterfaceDescriptor(UsbInterfaceDescriptor) set}
	 * before calling this.
	 * @param config The parent config
	 */
	public void setUsbConfigurationImp(UsbConfigurationImp config)
	{
		usbConfigurationImp = config;

		if (null != config)
			config.addUsbInterfaceImp(this);
	}

	/** @return the number of alternate settings */
	public int getNumSettings() { return getSettings().size(); }

	/**
	 * Get the number of the active alternate setting for this interface
	 * @return the active setting for this interface
	 * @exception UsbNotActiveException if the interface is inactive.
	 */
	public byte getActiveSettingNumber() throws UsbNotActiveException
	{
		checkActive();

		return ((UsbInterfaceImp)getUsbConfigurationImp().getUsbInterfaceSettingList(getUsbInterfaceDescriptor().bInterfaceNumber()).get(0)).getUsbInterfaceDescriptor().bAlternateSetting();
	}

	/**
	 * Get the active alternate setting.
	 * @return the active setting UsbInterface object for this interface
	 * @throws UsbNotActiveException if the interface (not setting) is inactive.
	 */
	public UsbInterface getActiveSetting() throws UsbNotActiveException { return getActiveSettingImp(); }

	/**
	 * Get the active alternate setting.
	 * @return the active setting UsbInterface object for this interface
	 * @throws UsbNotActiveException if the interface (not setting) is inactive.
	 */
	public UsbInterfaceImp getActiveSettingImp() throws UsbNotActiveException
	{
		/* Active check done in getActiveSettingNumber() */

		return getSettingImp( getActiveSettingNumber() );
	}

	/**
	 * Get the alternate setting with the specified number.
	 * @return the alternate setting with the specified number.
	 */
	public UsbInterface getSetting( byte number ) { return getSettingImp(number); }

	/**
	 * Get the alternate setting with the specified number.
	 * @return the alternate setting with the specified number, or null.
	 */
	public UsbInterfaceImp getSettingImp( byte number )
	{
		List list = getUsbConfigurationImp().getUsbInterfaceSettingList(getUsbInterfaceDescriptor().bInterfaceNumber());

		synchronized (list) {
			for (int i=0; i<list.size(); i++) {
				UsbInterfaceImp setting = (UsbInterfaceImp)list.get(i);

				if (number == setting.getUsbInterfaceDescriptor().bAlternateSetting())
					return setting;
			}
		}

		return null;
	}

	/**
	 * @param number the number of the alternate setting to check.
	 * @return if the alternate setting exists.
	 */
	public boolean containsSetting( byte number )
	{
		if (null != getSetting(number))
			return true;
		else
			return false;
	}

	/**
	 * Get all alternate settings for this interface.
	 * @return All of this interface's alternate settings (including this setting).
	 */
	public List getSettings()
	{
		return Collections.unmodifiableList( getUsbConfigurationImp().getUsbInterfaceSettingList(getUsbInterfaceDescriptor().bInterfaceNumber()) );
	}

	/** @return the interface descriptor for this interface */
	public UsbInterfaceDescriptor getUsbInterfaceDescriptor() { return usbInterfaceDescriptor; }

	/** @return the String description of this interface */
	public String getInterfaceString() throws UsbException,UnsupportedEncodingException,UsbDisconnectedException
	{
		return getUsbConfigurationImp().getUsbDeviceImp().getString( getUsbInterfaceDescriptor().iInterface() );
	}

	/** @return the associated UsbInterfaceOsImp */
	public UsbInterfaceOsImp getUsbInterfaceOsImp() { return usbInterfaceOsImp; }

	/** @param iface The UsbInterfaceOsImp to use */
	public void setUsbInterfaceOsImp( UsbInterfaceOsImp iface )
	{
		if (null == iface)
			usbInterfaceOsImp = new DefaultUsbInterfaceOsImp();
		else
			usbInterfaceOsImp = iface;
	}

	/** @param desc the new interface descriptor */
	public void setUsbInterfaceDescriptor( UsbInterfaceDescriptor desc ) { usbInterfaceDescriptor = desc; }

	/**
	 * Set the active alternate setting number for ALL UsbInterfaces
	 * on the AlternateSettings list
	 * @param number The number of the active alternate setting
	 * @throws IllegalArgumentException If the specified setting does not exist in this interface.
	 */
	public void setActiveSettingNumber( byte number ) throws IllegalArgumentException
	{
		getUsbConfigurationImp().setActiveUsbInterfaceImpSetting(getSettingImp(number));
	}

	/** @param ep the endpoint to add */
	public void addUsbEndpointImp( UsbEndpointImp ep )
	{
		if (!endpoints.contains(ep))
			endpoints.add( ep );
	}

	//**************************************************************************
	// Package methods

	/** Disconnect this and all subcomponents. */
	void disconnect()
	{
		Iterator i = getUsbEndpoints().iterator();
		while (i.hasNext())
			((UsbEndpointImp)i.next()).disconnect();
	}

	/** Check if this device is disconnected. */
	void checkDisconnected() throws UsbDisconnectedException
	{
		getUsbConfigurationImp().checkDisconnected();
	}

	/** @return If this device is disconnected. */
	boolean isDisconnected() { return getUsbConfigurationImp().isDisconnected(); }

	//**************************************************************************
	// Protected methods

	/**
	 * Get the current UsbInterfacePolicy.
	 * @return The current UsbInterfacePolicy.
	 */
	protected UsbInterfacePolicy getUsbInterfacePolicy() { return usbClaimPolicy; }

	/**
	 * If this interface currently has a policy.
	 * @return If this interface currently has a policy.
	 */
	protected boolean hasUsbInterfacePolicy() { return null != usbClaimPolicy; }

	//**************************************************************************
	// Private methods

	/** check if interface itself is active */
	private void checkActive() throws UsbNotActiveException
	{
		if (!getUsbConfiguration().isActive())
			throw new UsbNotActiveException( "Configuration " + getUsbConfiguration().getUsbConfigurationDescriptor().bConfigurationValue() + " is not active" );
	}

	/** check if this specific interface setting is active */
	private void checkSettingActive() throws UsbNotActiveException
	{
		/* If the interface (i.e. parent configuration) is not active, neither are any interface settings */
		checkActive();

		if (!isActive()) {
			String i = UsbUtil.toHexString(getUsbInterfaceDescriptor().bInterfaceNumber());
			String a = UsbUtil.toHexString(getUsbInterfaceDescriptor().bAlternateSetting());
			throw new UsbNotActiveException( "UsbInterface 0x" + i + " setting 0x" + a + " is not active" );
		}
	}

	/**
	 * Set all alternate settings' claimed policy.
	 * @param policy The claim policy.
	 */
	private void setClaimed(UsbInterfacePolicy policy)
	{
		List list = getUsbConfigurationImp().getUsbInterfaceSettingList(getUsbInterfaceDescriptor().bInterfaceNumber());
		for (int i=0; i<list.size(); i++)
			((UsbInterfaceImp)list.get(i)).usbClaimPolicy = policy;
	}

	//**************************************************************************
	// Instance variables

	private UsbConfigurationImp usbConfigurationImp = null;
	private UsbInterfaceOsImp usbInterfaceOsImp = new DefaultUsbInterfaceOsImp();

	private UsbInterfaceDescriptor usbInterfaceDescriptor = null;

	private List endpoints = new ArrayList();

	protected UsbInterfacePolicy usbClaimPolicy = null;
	protected UsbInterfacePolicy defaultUsbInterfacePolicy = new DefaultUsbInterfacePolicy();

	protected boolean disconnected = false;

}
