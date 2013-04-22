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

/**
 * UsbConfiguration implementation.
 * <p>
 * This must be set up before use.
 * <ul>
 * <li>The UsbDeviceImp must be set either in the constructor or by the {@link #setUsbDeviceImp(UsbDeviceImp) setter}.</li>
 * <li>The UsbConfigurationDescriptor must be set either in the constructor or by the {@link #setUsbConfigurationDescriptor(UsbConfigurationDescriptor) setter}.</li>
 * <li>All UsbInterfaceImp settings (active and inactive) must be {@link #addUsbInterfaceImp(UsbInterfaceImp) added}.</li>
 * </ul>
 * @author Dan Streetman
 */
public class UsbConfigurationImp implements UsbConfiguration
{
	/**
	 * Constructor.
	 * @param device The parent device.
	 * @param desc This configuration's descriptor.
	 */
	public UsbConfigurationImp( UsbDeviceImp device, UsbConfigurationDescriptor desc )
	{
		setUsbDeviceImp( device );
		setUsbConfigurationDescriptor( desc );
	}

	//**************************************************************************
	// Public methods

	/** @return if this UsbConfiguration is active */
	public boolean isActive() { return getUsbDevice().getActiveUsbConfigurationNumber() == getUsbConfigurationDescriptor().bConfigurationValue(); }

	/** @return All interfaces. */
	public List getUsbInterfaces()
	{
		synchronized ( interfaces ) {
			List list = new LinkedList();
			Iterator iterator = interfaces.values().iterator();

			while (iterator.hasNext())
				list.add(0, ((List)iterator.next()).get(0));

			return Collections.unmodifiableList(list);
		}
	}

	/**
	 * Get a UsbInterface.
	 * @param number The number of the interface to get
	 * @return A UsbInterface with the given number
	 */
	public UsbInterface getUsbInterface( byte number ) { return getUsbInterfaceImp(number); }

	/**
	 * Get a UsbInterfaceImp.
	 * @param number The number of the interface to get.
	 * @return A UsbInterfaceImp with the given number.
	 */
	public UsbInterfaceImp getUsbInterfaceImp( byte number )
	{
		synchronized ( interfaces ) {
			String key = new Byte(number).toString();

			if (!interfaces.containsKey(key))
				return null;

			return (UsbInterfaceImp)((List)interfaces.get(key)).get(0);
		}
	}

	/**
	 * @param number the number of the UsbInterface to check.
	 * @return if this configuration contains the specified UsbInterface.
	 */
	public boolean containsUsbInterface( byte number )
	{
		if (null != getUsbInterface(number))
			return true;
		else
			return false;
	}

	/**
	 * Add a UsbInterfaceImp.
	 * <p>
	 * The first setting for a particular interface number will default as the active setting.
	 * If the setting being added has already been added,
	 * it will be changed to be the active setting for the interface number.
	 * @param setting The UsbInterfaceImp to add.
	 */
	public void addUsbInterfaceImp( UsbInterfaceImp setting )
	{
		synchronized ( interfaces ) {
			String key = Byte.toString(setting.getUsbInterfaceDescriptor().bInterfaceNumber());

			if (!interfaces.containsKey(key))
				interfaces.put(key, new ArrayList());

			List list = (List)interfaces.get(key);

			synchronized (list) {
				if (list.contains(setting)) {
					list.remove(setting);
					list.add(0, setting);
				} else {
					list.add(setting);
				}
			}
		}
	}

	/**
	 * Change an interface setting to be the active alternate setting.
	 * <p>
	 * This behaves identical to {@link #addUsbInterfaceImp(UsbInterfaceImp) addUsbInterfaceImp}.
	 * @param setting The UsbInterfaceImp setting to change.
	 */
	public void setActiveUsbInterfaceImpSetting(UsbInterfaceImp setting) { addUsbInterfaceImp(setting); }

	/**
	 * Get the List of settings for the specified interface numer.
	 * @param number The interface number.
	 * @return The List of settings, or null if no such interface number exists.
	 */
	public List getUsbInterfaceSettingList(byte number)
	{
		synchronized (interfaces) {
			return (List)interfaces.get(new Byte(number).toString());
		}
	}

	/** @return The parent UsbDevice */
	public UsbDevice getUsbDevice() { return getUsbDeviceImp(); }

	/** @return The parent UsbDeviceImp */
	public UsbDeviceImp getUsbDeviceImp() { return usbDeviceImp; }

	/**
	 * Set the UsbDeviceImp.
	 * <p>
	 * This will also add this to the parent UsbDeviceImp.
	 * @param device The parent UsbDeviceImp
	 */
	public void setUsbDeviceImp(UsbDeviceImp device)
	{
		usbDeviceImp = device;

		if (null != device)
			device.addUsbConfigurationImp(this);
	}

	/** @return the configuration descriptor for this configuration */
	public UsbConfigurationDescriptor getUsbConfigurationDescriptor() { return usbConfigurationDescriptor; }

	/** @return the String description of this configuration */
	public String getConfigurationString() throws UsbException,UnsupportedEncodingException,UsbDisconnectedException
	{
		return getUsbDeviceImp().getString( getUsbConfigurationDescriptor().iConfiguration() );
	}

	/** @param desc the new configuration descriptor */
	public void setUsbConfigurationDescriptor( UsbConfigurationDescriptor desc ) { usbConfigurationDescriptor = desc; }

	//**************************************************************************
	// Package methods

	/**
	 * Disconnect this and all subcomponents.
	 */
	void disconnect()
	{
		Iterator i = getUsbInterfaces().iterator();
		while (i.hasNext()) {
			Iterator as = ((UsbInterfaceImp)i.next()).getSettings().iterator();
			while (as.hasNext())
				((UsbInterfaceImp)as.next()).disconnect();
		}
	}

	/** Check if this device is disconnected. */
	void checkDisconnected() { getUsbDeviceImp().checkDisconnected(); }

	/** @return If this device is disconnected. */
	boolean isDisconnected() { return getUsbDeviceImp().isDisconnected(); }

	//**************************************************************************
	// Instance variables

	private UsbDeviceImp usbDeviceImp = null;

	private UsbConfigurationDescriptor usbConfigurationDescriptor = null;

	private HashMap interfaces = new HashMap();
}
