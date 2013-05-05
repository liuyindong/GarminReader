package com.ibm.jusb;

/*
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.util.*;
import java.io.*;

import javax.usb.*;
import javax.usb.event.*;
import javax.usb.util.*;

import com.ibm.jusb.os.*;
import com.ibm.jusb.util.*;
import com.ibm.jusb.event.*;

/**
 * UsbDevice platform-independent implementation.
 * <p>
 * This must be set up before use and/or connection to the topology tree.
 * <ul>
 * <li>The UsbDeviceDescriptor must be set, either in the constructor or by its {@link #setUsbDeviceDescriptor(UsbDeviceDescriptor) setter}.</li>
 * <li>The UsbDeviceOsImp may optionally be set, either in the constructor or
 *     by its {@link #setUsbDeviceOsImp(UsbDeviceOsImp) setter}.
 *     If not set, it defaults to a {@link com.ibm.jusb.os.DefaultUsbDeviceOsImp DefaultUsbDeviceOsImp}.</li>
 * <li>The speed must be set by its {@link #setSpeed(Object) setter}.</li>
 * <li>All UsbConfigurationImps must be {@link #addUsbConfigurationImp(UsbConfigurationImp) added}.</li>
 * <li>The active config number must be {@link #setActiveUsbConfigurationNumber(byte) set}, if this device {@link #isConfigured() is configured}.</li>
 * </ul>
 * After setup, this may be connected to the topology tree by using the {@link #connect(UsbHubImp,byte) connect} method.
 * If the connect method is not used, there are additional steps:
 * <ul>
 * <li>Set the parent UsbPortImp by the {@link #setParentUsbPortImp(UsbPortImp) setter}.</li>
 * <li>Set this on the UsbPortImp by its {@link com.ibm.jusb.UsbPortImp#attachUsbDeviceImp(UsbDeviceImp) setter}.</li>
 * </ul>
 * @author Dan Streetman
 */
public class UsbDeviceImp implements UsbDevice,UsbIrpImp.UsbIrpImpListener
{
	/** Constructor. */
	public UsbDeviceImp() { this(null,null); }

	/**
	 * Constructor.
	 * @param desc This device's Descriptor.
	 */
	public UsbDeviceImp(UsbDeviceDescriptor desc) { this(desc, null); }

	/**
	 * Constructor.
	 * @param device The UsbDeviceOsImp.
	 */
	public UsbDeviceImp(UsbDeviceOsImp device) { this(null, device); }

	/**
	 * Constructor.
	 * @param desc This device's Descriptor.
	 * @param device The UsbDeviceOsImp.
	 */
	public UsbDeviceImp(UsbDeviceDescriptor desc, UsbDeviceOsImp device)
	{
		setUsbDeviceDescriptor(desc);
		setUsbDeviceOsImp(device);
		setPolicies();
	}

	//**************************************************************************
	// Public methods

	/** @return the associated UsbDeviceImp */
	public UsbDeviceOsImp getUsbDeviceOsImp() { return usbDeviceOsImp; }

	/** @param deviceImp the UsbDeviceOsImp to use */
	public void setUsbDeviceOsImp( UsbDeviceOsImp deviceImp )
	{
		if (null == deviceImp)
			usbDeviceOsImp = new DefaultUsbDeviceOsImp();
		else
			usbDeviceOsImp = deviceImp;
	}

	/** @return The port that this device is attached to */
	public UsbPort getParentUsbPort() throws UsbDisconnectedException { return getParentUsbPortImp(); }

	/** @return The port that this device is attached to */
	public UsbPortImp getParentUsbPortImp() throws UsbDisconnectedException
	{
		checkDisconnected();

		return usbPortImp;
	}

	/** @param port The parent port */
	public void setParentUsbPortImp( UsbPortImp port ) { usbPortImp = port; }

	/** @return true if this is a UsbHub and false otherwise */
	public boolean isUsbHub() { return false; }

	/** @return The manufacturer string. */
	public String getManufacturerString() throws UsbException,UnsupportedEncodingException,UsbDisconnectedException
	{
		return getString( getUsbDeviceDescriptor().iManufacturer() );
	}

	/** @return The serial number string. */
	public String getSerialNumberString() throws UsbException,UnsupportedEncodingException,UsbDisconnectedException
	{
		return getString( getUsbDeviceDescriptor().iSerialNumber() );
	}

	/** @return The product string. */
	public String getProductString() throws UsbException,UnsupportedEncodingException,UsbDisconnectedException
	{
		return getString( getUsbDeviceDescriptor().iProduct() );
	}

	/** @return The speed of this device. */
	public Object getSpeed() { return speed; }

	/** @return the UsbConfiguration objects associated with this UsbDevice */
	public List getUsbConfigurations() { return Collections.unmodifiableList(configurations); }

	/** @return the UsbConfiguration with the specified number as reported by getConfigurationNumber() */
	public UsbConfiguration getUsbConfiguration( byte number ) { return getUsbConfigurationImp(number); }

	/** @return the UsbConfigurationImp with the specified number as reported by getConfigurationNumber() */
	public UsbConfigurationImp getUsbConfigurationImp( byte number )
	{
		synchronized ( configurations ) {
			for (int i=0; i<configurations.size(); i++) {
				UsbConfigurationImp configuration = (UsbConfigurationImp)configurations.get(i);

				if (number == configuration.getUsbConfigurationDescriptor().bConfigurationValue())
					return configuration;
			}
		}

		return null;
	}

	/** @return if the specified UsbConfiguration is contained in this UsbDevice */
	public boolean containsUsbConfiguration( byte number )
	{
		if (null == getUsbConfiguration( number ))
			return false;
		else
			return true;
	}

	/** @return if this device is configured */
	public boolean isConfigured() { return 0 != getActiveUsbConfigurationNumber(); }

	/** @return the active UsbConfiguration number */
	public byte getActiveUsbConfigurationNumber() { return activeConfigurationNumber; }

	/** @return the active UsbConfiguration object */
	public UsbConfiguration getActiveUsbConfiguration() { return getActiveUsbConfigurationImp(); }

	/** @return the active UsbConfigurationImp object */
	public UsbConfigurationImp getActiveUsbConfigurationImp() { return getUsbConfigurationImp( getActiveUsbConfigurationNumber() ); }

	/** @return the device descriptor for this device */
	public UsbDeviceDescriptor getUsbDeviceDescriptor() { return usbDeviceDescriptor; }

	/*
	 * @return the specified string descriptor
	 * @param the index of the string descriptor to get
	 * @exception javax.usb.UsbException if an error occurrs while getting the UsbStringDescriptor.
	 * @exception UsbDisconnectedException If the device has been disconnected.
	 */
	public UsbStringDescriptor getUsbStringDescriptor( byte index ) throws UsbException,UsbDisconnectedException
	{
		checkDisconnected();

		/* There is no UsbStringDescriptor for index 0 */
		if (0 == index)
			return null;

		UsbStringDescriptor desc = getCachedUsbStringDescriptor( index );

		if ( null == desc ) {
			requestUsbStringDescriptor( index );
			desc = getCachedUsbStringDescriptor( index );
		}

		return desc;
	}

	/**
	 * @return the String from the specified STringDescriptor
	 * @exception UsbException if an error occurrs while getting the UsbStringDescriptor.
	 * @exception UnsupportedEncodingException If the string encoding is not supported.
	 * @exception UsbDisconnectedException If the device has been disconnected.
	 */
	public String getString( byte index ) throws UsbException,UnsupportedEncodingException,UsbDisconnectedException
	{
		UsbStringDescriptor desc = getUsbStringDescriptor( index );

		try {
			return desc.getString();
		} catch ( NullPointerException npE ) {
			return null;
		}
	}

	/**
	 * Create a UsbControlIrp.
	 * @param bmRequestType The bmRequestType.
	 * @param bRequest The bRequest.
	 * @param wValue The wValue.
	 * @param wIndex The wIndex.
	 * @return A UsbControlIrp ready for use.
	 */
	public UsbControlIrp createUsbControlIrp(byte bmRequestType, byte bRequest, short wValue, short wIndex)
	{ return new UsbControlIrpImp(bmRequestType, bRequest, wValue, wIndex); }

	/**
	 * Indicate that a specific UsbIrpImp has completed.
	 * @param usbIrpImp The UsbIrpImp that completed.
	 */
	public void usbIrpImpComplete( UsbIrpImp usbIrpImp )
	{
		submissionCount--;

		UsbControlIrpImp irp = null;

		try {
			irp = (UsbControlIrpImp)usbIrpImp;
		} catch ( ClassCastException ccE ) {
			//FIXME - log?  this shouldn't happen.
			//FIXME - this method should accept a UsbControlIrpImp...!
		}

		if (listTable.containsKey(irp)) {
			List list = (List)listTable.get(irp);
			/* Starting with the first UsbControlIrpImp in the list,
			 * fire all consecutive that are complete.
			 */
			while (!list.isEmpty()) {
				UsbControlIrpImp usbControlIrpImp = (UsbControlIrpImp)list.get(0);
				if (usbControlIrpImp.isComplete()) {
					list.remove(0);
					listTable.remove(irp);
					fireEvent(usbControlIrpImp);
				} else {
					break;
				}
			}
		} else {
			fireEvent(irp);
		}
	}

	/** @param listener The listener to add */
	public void addUsbDeviceListener( UsbDeviceListener listener ) 
	{
		if (!listenerNameSet) {
			listenerImp.setName(getName() + " UsbDeviceListenerImp");
			listenerNameSet = true;
		}

		listenerImp.addEventListener(listener);
	}

	/** @param listener The listener to remove */
	public void removeUsbDeviceListener( UsbDeviceListener listener )
	{
		listenerImp.removeEventListener(listener);
	}

	/** @param desc The new device descriptor */
	public void setUsbDeviceDescriptor( UsbDeviceDescriptor desc ) { usbDeviceDescriptor = desc; }

	/**
	 * Get a cached UsbStringDescriptor.
	 * @param index The index of the UsbStringDescriptor.
	 * @return The specified UsbStringDescriptor, or null.
	 */
	public UsbStringDescriptor getCachedUsbStringDescriptor( byte index )
	{
		return (UsbStringDescriptor)usbStringDescriptors.get( new Byte(index).toString() );
	}

	/**
	 * Set a cached UsbStringDescriptor.
	 * @param index The index.
	 * @param desc The UsbStringDescriptor.
	 */
	public void setCachedUsbStringDescriptor( byte index, UsbStringDescriptor desc )
	{
		usbStringDescriptors.put( new Byte(index).toString(), desc );
	}

	/**
	 * Sets the speed of this device.
	 * @param o The speed.
	 * @exception IllegalArgumentException If the speed is not one of the defined speeds.
	 */
	public void setSpeed( Object o )
	{
		if (UsbConst.DEVICE_SPEED_UNKNOWN == o || UsbConst.DEVICE_SPEED_LOW == o || UsbConst.DEVICE_SPEED_FULL == o)
			speed = o;
		else
			throw new IllegalArgumentException("Device speed must be DEVICE_SPEED_UNKNOWN, DEVICE_SPEED_LOW, or DEVICE_SPEED_FULL.");
	}

	/**
	 * Sets the active configuration index.
	 * <p>
	 * Since this may be called before the associated configuration is actually added,
	 * this does not check if the specified configuration index actually associates to
	 * a valid configuration; the caller must ensure that.
	 * @param num the active configuration number (0 if device has been unconfigured)
	 */
	public void setActiveUsbConfigurationNumber( byte num ) { activeConfigurationNumber = num; }

	/** @param configuration The configuration to add */
	public void addUsbConfigurationImp( UsbConfigurationImp configuration )
	{
		if (!configurations.contains(configuration))
			configurations.add( configuration );
	}

	/**
	 * Connect to the parent UsbHubImp.
	 * @param hub The parent.
	 * @param portNumber The port on the parent this is connected to.
	 */
	public void connect(UsbHubImp hub, byte portNumber) throws UsbException
	{
		hub.addUsbDeviceImp( this, portNumber );

		setParentUsbPortImp(hub.getUsbPortImp(portNumber));
	}

	/**
	 * Disconnect UsbDeviceImp.
	 * <p>
	 * Only call this if the device was connected to the topology tree;
	 * i.e. the UsbPortImp has been {@link #setParentUsbPortImp(UsbPortImp) set}.
	 * This will fire
	 * {@link javax.usb.event.UsbDeviceListener#usbDeviceDetached(UsbDeviceEvent) usbDeviceDetached}
	 * events to all listeners.
	 * <p>
	 * This method must be called when a device is disconnected.
	 */
	public void disconnect()
	{
		try {
			getParentUsbPortImp().detachUsbDeviceImp( this );
		} catch ( IllegalArgumentException iaE ) {
			/* FIXME - log, handle? */
		}

		Iterator i = getUsbConfigurations().iterator();
		while (i.hasNext())
			((UsbConfigurationImp)i.next()).disconnect();

		disconnected = true;

		listenerImp.usbDeviceDetached(new UsbDeviceEvent(this));

		if (queueSubmissions) {
			Runnable r = new Runnable() {
					public void run() { listenerImp.clear(); }
				};

			addRunnable(r);
			queueManager.stop();
		} else {
			listenerImp.clear();
		}
	}

	/**
	 * Submit a UsbControlIrp synchronously to the Default Control Pipe.
	 * @param irp The UsbControlIrp.
	 * @exception UsbException If an error occurrs.
	 * @exception IllegalArgumentException If the UsbControlIrp is invalid.
	 * @exception UsbDisconnectedException If the device has been disconnected.
	 */
	public void syncSubmit( UsbControlIrp irp ) throws UsbException,IllegalArgumentException,UsbDisconnectedException
	{
		synchronized (submitLock) {
			checkDisconnected();

			UsbControlIrpImp usbControlIrpImp = usbControlIrpToUsbControlIrpImp( irp );

			if (queueSubmissions) {
				queueUsbControlIrpImp(usbControlIrpImp);
				usbControlIrpImp.waitUntilComplete();
				if (usbControlIrpImp.isUsbException())
					throw usbControlIrpImp.getUsbException();
			} else {
				submissionCount++;
				getUsbDeviceOsImp().syncSubmit( usbControlIrpImp );
			}
		}
	}

	/**
	 * Submit a UsbControlIrp asynchronously to the Default Control Pipe.
	 * @param irp The UsbControlIrp.
	 * @exception UsbException If an error occurrs.
	 * @exception IllegalArgumentException If the UsbControlIrp is invalid.
	 * @exception UsbDisconnectedException If the device has been disconnected.
	 */
	public void asyncSubmit( UsbControlIrp irp ) throws UsbException,IllegalArgumentException,UsbDisconnectedException
	{
		synchronized (submitLock) {
			checkDisconnected();

			UsbControlIrpImp usbControlIrpImp = usbControlIrpToUsbControlIrpImp( irp );

			if (queueSubmissions) {
				queueUsbControlIrpImp(usbControlIrpImp);
			} else {
				submissionCount++;
				getUsbDeviceOsImp().asyncSubmit( usbControlIrpImp );
			}
		}
	}

	/**
	 * Submit a List of UsbControlIrps synchronously to the Default Control Pipe.
	 * <p>
	 * All UsbControlIrps are guaranteed to be atomically (with respect to other clients
	 * of this API) submitted to the Default Control Pipe.  Atomicity on a native level
	 * is implementation-dependent.
	 * @param list The List of UsbControlIrps.
	 * @exception UsbException If an error occurrs.
	 * @exception IllegalArgumentException If one of the UsbControlIrps is invalid.
	 * @exception UsbDisconnectedException If the device has been disconnected.
	 */
	public void syncSubmit( List list ) throws UsbException,IllegalArgumentException,UsbDisconnectedException
	{
		synchronized (submitLock) {
			checkDisconnected();

			if (list.isEmpty())
				return;

			List usbControlIrpImpList = usbControlIrpListToUsbControlIrpImpList( list );

			if (queueSubmissions) {
				queueList(usbControlIrpImpList);
				((UsbControlIrp)usbControlIrpImpList.get(usbControlIrpImpList.size()-1)).waitUntilComplete();
			} else {
				submissionCount += usbControlIrpImpList.size();
				getUsbDeviceOsImp().syncSubmit( usbControlIrpImpList );
			}
		}
	}

	/**
	 * Submit a List of UsbControlIrps asynchronously to the Default Control Pipe.
	 * <p>
	 * All UsbControlIrps are guaranteed to be atomically (with respect to other clients
	 * of this API) submitted to the Default Control Pipe.  Atomicity on a native level
	 * is implementation-dependent.
	 * @param list The List of UsbControlIrps.
	 * @exception UsbException If an error occurrs.
	 * @exception IllegalArgumentException If one of the UsbControlIrps is invalid.
	 * @exception UsbDisconnectedException If the device has been disconnected.
	 */
	public void asyncSubmit( List list ) throws UsbException,IllegalArgumentException,UsbDisconnectedException
	{
		synchronized (submitLock) {
			checkDisconnected();

			if (list.isEmpty())
				return;

			List usbControlIrpImpList = usbControlIrpListToUsbControlIrpImpList( list );

			if (queueSubmissions) {
				queueList(usbControlIrpImpList);
			} else {
				submissionCount += usbControlIrpImpList.size();
				getUsbDeviceOsImp().asyncSubmit( usbControlIrpImpList );
			}
		}
	}

	//**************************************************************************
	// Protected methods

	/**
	 * Fire an event for the specified UsbControlIrpImp.
	 * @param usbControlIrpImp The UsbControlIrpImp to fire an event for.
	 */
	protected void fireEvent(UsbControlIrpImp usbControlIrpImp)
	{
		UsbControlIrp irp = (UsbControlIrp)usbControlIrpImp.getUsbIrp();
		if (null == irp)
			irp = usbControlIrpImp;

		if (irp.isUsbException()) {
			/* If the device was disconnected, replace the error with a UsbAbortException. */
			if (isDisconnected())
				irp.setUsbException(new UsbAbortException("The device was disconnected"));

			listenerImp.errorEventOccurred(new UsbDeviceErrorEvent(this,irp));
		} else {
			listenerImp.dataEventOccurred(new UsbDeviceDataEvent(this,irp));
		}
	}

	/** @return the device's default langID */
	protected synchronized short getLangId() throws UsbException
	{
		if (0x0000 == langId) {
			byte[] data = new byte[256];

			int len = StandardRequest.getDescriptor( this, UsbConst.DESCRIPTOR_TYPE_STRING, (byte)0, (short)0, data );

			if (4 > len || 4 > UsbUtil.unsignedInt(data[0]))
				throw new UsbException("Strings not supported by device");

			langId = (short)((data[3] << 8) | data[2]);
		}

		return langId;
	}

	/** Update the UsbStringDescriptor at the specified index. */
	protected void requestUsbStringDescriptor( byte index ) throws UsbException
	{
		byte[] data = new byte[256];

		int len = StandardRequest.getDescriptor( this, UsbConst.DESCRIPTOR_TYPE_STRING, index, getLangId(), data );

		/* requested string not present or invalid */
		if (2 > len || 2 > UsbUtil.unsignedInt(data[0]))
			return;

		/* String claims to be longer than actual data transferred */
		if (UsbUtil.unsignedInt(data[0]) > len)
			throw new UsbException("String Descriptor length byte is longer than Descriptor data");

		/* string length (descriptor len minus 2 for header) */
		int strLen = UsbUtil.unsignedInt(data[0]) - 2;

		byte[] bString = new byte[strLen];
		System.arraycopy(data, 2, bString, 0, strLen);

		setCachedUsbStringDescriptor( index, new UsbStringDescriptorImp( data[0], data[1], bString ) );
	}

	/**
	 * Setup a UsbControlIrpImp.
	 * @param irp The UsbControlIrpImp to setup.
	 */
	protected void setupUsbControlIrpImp( UsbControlIrpImp irp )
	{
		irp.setUsbIrpImpListener(this);
		irp.setUsbDeviceImp(this);

		/* some implementations *cough*4690*cough* don't implement short packet detection. */
		boolean inDirection = UsbConst.REQUESTTYPE_DIRECTION_IN == (byte)(UsbConst.REQUESTTYPE_DIRECTION_MASK & irp.bmRequestType());
		irp.setCreateShortPacketException(createShortPacketException && inDirection);
	}

	/**
	 * Convert a UsbControlIrp to a UsbControlIrpImp.
	 * @param usbControlIrp The UsbControlIrp.
	 * @return A UsbControlIrpImp that corresponds to the usbControlIrp.
	 * @exception IllegalArgumentException If the UsbControlIrp is invalid.
	 * @exception UsbException If the UsbControlIrp is not ready for submission.
	 */
	protected UsbControlIrpImp usbControlIrpToUsbControlIrpImp(UsbControlIrp usbControlIrp) throws UsbException,IllegalArgumentException
	{
		UsbControlIrpImp.checkUsbControlIrp(usbControlIrp);

		UsbControlIrpImp usbControlIrpImp = null;

		try {
			usbControlIrpImp = (UsbControlIrpImp)usbControlIrp;
		} catch ( ClassCastException ccE ) {
			usbControlIrpImp = new UsbControlIrpImp(usbControlIrp);
		}

		setupUsbControlIrpImp( usbControlIrpImp );

		return usbControlIrpImp;
	}

	/**
	 * Convert a List of UsbControlIrps to a List of UsbControlIrpImps.
	 * @param list The List of UsbControlIrps.
	 * @return A List of UsbControlIrpImps that correspond to the UsbControlIrp List.
	 * @exception IllegalArgumentException If the UsbControlIrp is invalid.
	 * @exception UsbException If the UsbControlIrp is not ready for submission.
	 */
	protected List usbControlIrpListToUsbControlIrpImpList(List list) throws IllegalArgumentException,UsbException
	{
		ArrayList newList = new ArrayList();

		try {
			for (int i=0; i<list.size(); i++)
				newList.add(usbControlIrpToUsbControlIrpImp((UsbControlIrp)list.get(i)));
		} catch ( ClassCastException ccE ) {
			throw new IllegalArgumentException("The List contains a non-UsbIrp object.");
		}

		List delayEventList = (ArrayList)newList.clone();

		/* Use a different list so we can modify it if needed */
		for (int i=0; i<delayEventList.size(); i++)
			listTable.put(delayEventList.get(i), delayEventList);

		return newList;
	}

	/**
	 * Add a Runnable to the queueManager.
	 * @param r The Runnable to add.
	 */
	protected void addRunnable(Runnable r)
	{
		if (!queueManager.isRunning()) {
			queueManager.setName(getName() + " RunnableManager");
			queueManager.setMaxSize(RunnableManager.SIZE_UNLIMITED);
			queueManager.start();
		}

		queueManager.add(r);
	}

	/**
	 * Submit a UsbControlIrpImp from the queueManager.
	 * @param usbControlIrpImp The UsbControlIrpImp to submit.
	 */
	protected void submitUsbControlIrpImpFromQueue(UsbControlIrpImp usbControlIrpImp)
	{
		/* This is needed if the DCP supports aborting submissions
		 *		synchronized (abortLock) {
		 *			if (abortInProgress) {
		 *				usbIrpImp.setUsbException(new UsbAbortException());
		 *				usbIrpImp.complete();
		 *				return;
		 *			}
		 *		}
		 */
		try {
			/* NOTE: no need to synchronize on the submitLock as queueing gaurantees serialized, synchronized submission */
			submissionCount++;
			getUsbDeviceOsImp().syncSubmit(usbControlIrpImp);
		} catch ( UsbException uE ) {
			/* ignore this, as the UsbControlIrp's UsbException will be set and this is handled elsewhere. */
		}
	}

	/**
	 * Queue a UsbControlIrpImp
	 * @param usbControlIrpImp The UsbControlIrpImp to queue.
	 */
	protected void queueUsbControlIrpImp(final UsbControlIrpImp usbControlIrpImp)
	{
		Runnable r = new Runnable()	{ public void run() { submitUsbControlIrpImpFromQueue(usbControlIrpImp); } };

		addRunnable(r);
	}

	/**
	 * Queue a List of UsbControlIrpImps.
	 * @param list The List of UsbControlIrpImps to queue.
	 */
	protected void queueList(final List list)
	{
		Runnable r = new Runnable()
			{
				public void run() {
					for (int i=0; i<list.size(); i++)
						submitUsbControlIrpImpFromQueue((UsbControlIrpImp)list.get(i));
				}
			};

		addRunnable(r);
	}

	/** Set the policies, if defined */
	protected void setPolicies()
	{
		Properties p = null;
		try {
			p = UsbHostManager.getProperties();
		} catch ( Exception e ) {
/* FIXME - change UsbHostManager.getProperties() into throwing only RuntimeExcepiton */
			e.printStackTrace();
			throw new RuntimeException("Unexpected Exception while calling UsbHostManager.getProperties() : " + e.getMessage());
		}

		String policy = p.getProperty(DCP_QUEUE_POLICY_KEY);
		if (null != policy)
			queueSubmissions = Boolean.valueOf(policy.trim()).booleanValue();

		policy = p.getProperty(CREATE_SHORT_PACKET_EXCEPTION_POLICY_KEY);
		if (null != policy)
			createShortPacketException = Boolean.valueOf(policy.trim()).booleanValue();
	}

	//**************************************************************************
	// Package methods

	/** @return A name describing this */
	String getName()
	{
		String vendor = "", product = "";
		if (null == getUsbDeviceDescriptor()) {
			vendor = "????";
			product = "????";
		} else {
			vendor = UsbUtil.toHexString(getUsbDeviceDescriptor().idVendor());
			product = UsbUtil.toHexString(getUsbDeviceDescriptor().idProduct());
		}
		return "UsbDeviceImp <" + vendor + ":" + product + ">";
	}

	/**
	 * Check if the device is disconnected.
	 * @exception UsbDisconnectedException If this device has been disconnected.
	 */
	void checkDisconnected() throws UsbDisconnectedException
	{
		if (isDisconnected())
			throw new UsbDisconnectedException("This device has been disconnected");
	}

	/**
	 * If this device is disconnected.
	 * @return If this device is disconnected.
	 */
	boolean isDisconnected() { return disconnected; }

	//**************************************************************************
	// Instance variables

	private UsbDeviceOsImp usbDeviceOsImp = null;

	private Object submitLock = new Object();

	private UsbDeviceDescriptor usbDeviceDescriptor = null;

	private Hashtable usbStringDescriptors = new Hashtable();
	private short langId = 0x0000;

	private Object speed = UsbConst.DEVICE_SPEED_UNKNOWN;
    
	private List configurations = new ArrayList();
	private byte activeConfigurationNumber = 0;

	private UsbPortImp usbPortImp = null;

	private UsbDeviceListenerImp listenerImp = new UsbDeviceListenerImp();
	private boolean listenerNameSet = false;

	/* If the queue policy is set to true for this DCP, all submissions will be queued and
	 * submitted via the UsbDeviceOsImp.syncSubmit() method, so the OS will not have to queue.
	 * The OS is most likely much more efficient at queueing, so if it supports it,
	 * OS queueing should be used.
	 */
	protected RunnableManager queueManager = new RunnableManager(false);
	protected boolean queueSubmissions = false;

	protected boolean createShortPacketException = false;

	/* FIXME - should the DCP have an abortAllSubmissions() method?  If so these would be needed
	 *	protected Object abortLock = new Object();
	 *	protected boolean abortInProgress = false;
	 */

	protected Hashtable listTable = new Hashtable();

	protected int submissionCount = 0;

	protected boolean disconnected = false;

	//**************************************************************************
	// Class constants

	public static final String DCP_QUEUE_POLICY_KEY = "com.ibm.jusb.UsbDeviceImp.queueSubmissions";

	public static final String CREATE_SHORT_PACKET_EXCEPTION_POLICY_KEY = "com.ibm.jusb.UsbIrpImp.createShortPacketException";
}
