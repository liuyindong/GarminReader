package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.util.*;

import javax.usb.*;
import javax.usb.event.*;
import javax.usb.util.*;

import com.ibm.jusb.os.*;
import com.ibm.jusb.util.*;
import com.ibm.jusb.event.*;

/**
 * UsbPipe platform-independent implementation.
 * <p>
 * This must be set up before use.
 * <ul>
 * <li>The {@link #getUsbEndpointImp() UsbEndpointImp} must be set either in the
 *     constructor or by its {@link #setUsbEndpointImp(UsbEndpointImp) setter}.</li>
 * <li>The {@link #getUsbPipeOsImp() UsbPipeOsImp} may optionally be set either in the
 *     constructor or by its {@link #setUsbPipeOsImp(UsbPipeOsImp) setter}.
 *     If not set, it defaults to a {@link com.ibm.jusb.os.DefaultUsbPipeOsImp DefaultUsbPipeOsImp}.</li>
 * </ul>
 * @author Dan Streetman
 */
public class UsbPipeImp implements UsbPipe,UsbIrpImp.UsbIrpImpListener
{
	/** Constructor. */
	public UsbPipeImp() { }

	/**
	 * Constructor.
	 * @param ep The UsbEndpointImp.
	 */
	public UsbPipeImp( UsbEndpointImp ep ) { setUsbEndpointImp(ep); }

	/**
	 * Constructor.
	 * @param pipe The platform-dependent pipe implementation.
	 */
	public UsbPipeImp( UsbPipeOsImp pipe ) { setUsbPipeOsImp(pipe); }

	/**
	 * Constructor.
	 * @param ep The UsbEndpointImp.
	 * @param pipe The platform-dependent pipe implementation.
	 */
	public UsbPipeImp( UsbEndpointImp ep, UsbPipeOsImp pipe )
	{
		setUsbEndpointImp(ep);
		setUsbPipeOsImp(pipe);
	}

	//**************************************************************************
	// Public methods

	/** @return the UsbPipeOsImp object */
	public UsbPipeOsImp getUsbPipeOsImp() { return usbPipeOsImp; }

	/** @param pipe The UsbPipeOsImp to use */
	public void setUsbPipeOsImp( UsbPipeOsImp pipe )
	{
		if (null == pipe)
			usbPipeOsImp = new DefaultUsbPipeOsImp();
		else
			usbPipeOsImp = pipe;
	}

	/** @return if this UsbPipe is active */
	public boolean isActive() { return getUsbEndpoint().getUsbInterface().isActive(); }

	/** @return if this UsbPipe is open */
	public boolean isOpen() { return open; }

	/** @return the UsbEndpoint associated with this UsbPipe */
	public UsbEndpoint getUsbEndpoint() { return getUsbEndpointImp(); }

	/** @return the UsbEndpointImp associated with this UsbPipeImp */
	public UsbEndpointImp getUsbEndpointImp() { return usbEndpointImp; }

	/**
	 * Set the UsbEndpointImp.
	 * <p>
	 * This will also set this on the parent UsbEndpointImp.
	 * This also sets up this pipe's queueing policy if the user
	 * defined one in the properties file.
	 * @param ep The UsbEndpointImp
	 */
	public void setUsbEndpointImp(UsbEndpointImp ep)
	{
		usbEndpointImp = ep;

		if (null != ep)
			ep.setUsbPipeImp(this);

		setPolicies();
	}

	/**
	 * Opens this UsbPipe.
	 */
	public void open() throws UsbException,UsbNotActiveException,UsbNotClaimedException,UsbDisconnectedException
	{
		checkActive();

		if (!getUsbEndpointImp().getUsbInterfaceImp().hasUsbInterfacePolicy()) {
			String i = UsbUtil.toHexString(getUsbEndpointImp().getUsbInterfaceImp().getUsbInterfaceDescriptor().bInterfaceNumber());
			String a = UsbUtil.toHexString(getUsbEndpointImp().getUsbInterfaceImp().getUsbInterfaceDescriptor().bAlternateSetting());
			throw new UsbNotClaimedException("UsbInterface 0x" + i + " setting 0x" + a + " is not claimed");
		}

/* FIXME - create UsbOpenException ? */
		if (isOpen())
			throw new UsbException("UsbPipe is already open");

		getUsbPipeOsImp().open();
		open = true;
	}

	/** Closes this UsbPipe. */
	public void close() throws UsbException,UsbNotActiveException,UsbNotOpenException,UsbDisconnectedException
	{
		checkOpen();

		if (0 < submissionList.size())
			throw new UsbException("Cannot close pipe with pending submissions");

		getUsbPipeOsImp().close();
		open = false;
	}

	/**
	 * Synchonously submits this byte[] array to the UsbPipe.
	 */
	public int syncSubmit( byte[] data ) throws UsbException,IllegalArgumentException,UsbNotActiveException,UsbNotOpenException,UsbDisconnectedException
	{
		synchronized (submitLock) {
			checkOpen();

			UsbIrpImp usbIrpImp = createUsbIrpImp();
			usbIrpImp.setData(data);
			syncSubmit(usbIrpImp);

			return usbIrpImp.getActualLength();
		}
	}

	/**
	 * Asynchonously submits this byte[] array to the UsbPipe.
	 */
	public UsbIrp asyncSubmit( byte[] data ) throws UsbException,IllegalArgumentException,UsbNotActiveException,UsbNotOpenException,UsbDisconnectedException
	{
		synchronized (submitLock) {
			checkOpen();

			UsbIrpImp usbIrpImp = createUsbIrpImp();
			usbIrpImp.setData(data);
			asyncSubmit(usbIrpImp);

			return usbIrpImp;
		}
	}

	/**
	 * Synchronous submission using a UsbIrp.
	 */
	public void syncSubmit( UsbIrp usbIrp ) throws UsbException,IllegalArgumentException,UsbNotActiveException,UsbNotOpenException,UsbDisconnectedException
	{
		synchronized (submitLock) {
			checkOpen();

			UsbIrpImp usbIrpImp = usbIrpToUsbIrpImp( usbIrp );
			submissionList.add(usbIrpImp);

			if ( queueSubmissions ) {
				queueUsbIrpImp( usbIrpImp );
				usbIrpImp.waitUntilComplete();
				if (usbIrpImp.isUsbException())
					throw usbIrpImp.getUsbException();
			} else {
				try {
					getUsbPipeOsImp().syncSubmit( usbIrpImp );
				} catch ( UsbException uE ) {
					submissionList.remove(usbIrpImp);
					throw uE;
				}
			}
		}
	}

	/**
	 * Asynchronous submission using a UsbIrp.
	 */
	public void asyncSubmit( UsbIrp usbIrp ) throws UsbException,IllegalArgumentException,UsbNotActiveException,UsbNotOpenException,UsbDisconnectedException
	{
		synchronized (submitLock) {
			checkOpen();

			UsbIrpImp usbIrpImp = usbIrpToUsbIrpImp( usbIrp );
			submissionList.add(usbIrpImp);

			if ( queueSubmissions ) {
				queueUsbIrpImp( usbIrpImp );
			} else {
				try {
					getUsbPipeOsImp().asyncSubmit( usbIrpImp );
				} catch ( UsbException uE ) {
					submissionList.remove(usbIrpImp);
					throw uE;
				}
			}
		}
	}

	/**
	 * Synchronous submission using a List of UsbIrps.
	 */
	public void syncSubmit( List list ) throws UsbException,IllegalArgumentException,UsbNotActiveException,UsbNotOpenException,UsbDisconnectedException
	{
		synchronized (submitLock) {
			checkOpen();

			if (list.isEmpty())
				return;

			List usbIrpImpList = usbIrpListToUsbIrpImpList( list );
			submissionList.addAll(usbIrpImpList);

			if ( queueSubmissions ) {
				queueList( usbIrpImpList );
				((UsbIrp)usbIrpImpList.get(usbIrpImpList.size()-1)).waitUntilComplete();
			} else {
				try {
					getUsbPipeOsImp().syncSubmit( usbIrpImpList );
				} catch ( UsbException uE ) {
					submissionList.removeAll(usbIrpImpList);
					throw uE;
				}
			}
		}
	}

	/**
	 * Asynchronous submission using a List of UsbIrps.
	 */
	public void asyncSubmit( List list ) throws UsbException,IllegalArgumentException,UsbNotActiveException,UsbNotOpenException,UsbDisconnectedException
	{
		synchronized (submitLock) {
			checkOpen();

			if (list.isEmpty())
				return;

			List usbIrpImpList = usbIrpListToUsbIrpImpList( list );
			submissionList.addAll(usbIrpImpList);

			if ( queueSubmissions ) {
				queueList( usbIrpImpList );
			} else {
				try {
					getUsbPipeOsImp().asyncSubmit( usbIrpImpList );
				} catch ( UsbException uE ) {
					submissionList.removeAll(usbIrpImpList);
					throw uE;
				}
			}
		}
	}

	/**
	 * Stop all submissions in progress.
	 */
	public void abortAllSubmissions() throws UsbNotActiveException,UsbNotOpenException,UsbDisconnectedException
	{
		synchronized (submitLock) {
			checkOpen();

			if (queueSubmissions) {
				synchronized (abortLock) {
					abortInProgress = true;

					/* FIXME (maybe) - note, I believe there should be no race here...
					 * the XXXsubmit methods are sync'ed by the submitLock, and
					 * if using queueing, the queue submission is sync'ed using the
					 * abortLock, all the way through the asyncSubmit.  So this should
					 * never allow a queued up IRP to slip through after the OS-level
					 * abort call...
					 */

					getUsbPipeOsImp().abortAllSubmissions();

					/* How can the queueManager's Runnable task notify us that
					 * all the Runnable tasks have been completed?  Not possible.
					 * The last one could wake us up, but we would still have to wait for
					 * it to complete so it's easier to just slowly poll the size.
					 * The RunnableManager class could use a method like waitUntilEmpty()
					 * or something similar.
					 */
					while (0 < queueManager.getSize()) {
						try { abortLock.wait(500); } catch ( InterruptedException iE ) { }
					}

					abortInProgress = false;
				}
			} else {
				getUsbPipeOsImp().abortAllSubmissions();
			}
		}
	}

	/**
	 * Create a UsbIrp.
	 * @return A UsbIrp ready for use.
	 */
	public UsbIrp createUsbIrp() { return new UsbIrpImp(); }

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
	 * <p>
	 * This is called after isComplete() is set to true.
	 * @param irp The UsbIrpImp that completed.
	 */
	public void usbIrpImpComplete( UsbIrpImp irp )
	{
		synchronized (completeLock) {
			submissionList.remove(irp);

			if (listTable.containsKey(irp)) {
				List list = (List)listTable.get(irp);
				/* Starting with the first UsbIrpImp in the list,
				 * fire all consecutive that are complete.
				 */
				while (!list.isEmpty()) {
					UsbIrpImp usbIrpImp = (UsbIrpImp)list.get(0);
					if (usbIrpImp.isComplete()) {
						list.remove(0);
						listTable.remove(irp);
						fireEvent(usbIrpImp);
					} else {
						break;
					}
				}
			} else {
				fireEvent(irp);
			}
		}
	}

	/**
	 * Register's the listener object for UsbPipeEvent
	 * @param listener the UsbPipeListener instance
	 */
	public void addUsbPipeListener( UsbPipeListener listener )
	{
		if (!listenerNameSet) {
			listenerImp.setName(getName() + " UsbPipeListenerImp");
			listenerNameSet = true;
		}

		listenerImp.addEventListener( listener );
	}

	/**
	 * Removes the listener object from the listener list
	 * @param listener the UsbPipeListener instance
	 */
	public void removeUsbPipeListener( UsbPipeListener listener ) { listenerImp.removeEventListener( listener ); }

	public void setupUsbIrpImp(UsbIrpImp irp)
	{
		irp.setUsbIrpImpListener(this);

		/* some implementations *cough*4690*cough* don't implement short packet detection. */
		irp.setCreateShortPacketException(createShortPacketException && isInDirection(irp));
	}

	//**************************************************************************
	// Protected methods

	/**
	 * Fire an event for the specified UsbIrpImp.
	 * <p>
	 * Note that any submission of a byte[] will not have a wrapped
	 * UsbIrp, but to ease implementation, the UsbIrpImp generated by
	 * this implementation to manage the byte[] will be included in the event.
	 * This means this implementation will <i>never</i> use the non-UsbIrp
	 * constructors from the UsbPipeEvent classes.
	 * @param usbIrpImp The UsbIrpImp to fire an event for.
	 */
	protected void fireEvent(UsbIrpImp usbIrpImp)
	{
		UsbIrp irp = usbIrpImp.getUsbIrp();
		if (null == irp)
			irp = usbIrpImp;

		if (irp.isUsbException()) {
			/* If the device was disconnected, replace the error with a UsbAbortException. */
			if (isDisconnected())
				irp.setUsbException(new UsbAbortException("The device was disconnected"));

			listenerImp.errorEventOccurred(new UsbPipeErrorEvent(this,irp));
		} else {
			listenerImp.dataEventOccurred(new UsbPipeDataEvent(this,irp));
		}
	}

	/**
	 * Convert a UsbIrp to UsbIrpImp.
	 * @param irp The UsbIrp to convert.
	 * @exception UsbException If the UsbIrp is not ready for submission.
	 * @exception IllegalArgumentException If the UsbIrp is not valid.
	 */
	protected UsbIrpImp usbIrpToUsbIrpImp(UsbIrp irp) throws UsbException,IllegalArgumentException
	{
		UsbIrpImp.checkUsbIrp(irp);

		UsbIrpImp usbIrpImp = null;
		try {
			usbIrpImp = (UsbIrpImp)irp;
		} catch ( ClassCastException ccE ) {
			usbIrpImp = new UsbIrpImp(irp);
		}

		setupUsbIrpImp(usbIrpImp);

		return usbIrpImp;
	}

	protected List usbIrpListToUsbIrpImpList(List list) throws UsbException,IllegalArgumentException
	{
		ArrayList newlist = new ArrayList();

		/* if any of the UsbIrps are invalid, an exception is thrown */
		try {
			for (int i=0; i<list.size(); i++)
				newlist.add(usbIrpToUsbIrpImp((UsbIrp)list.get(i)));
		} catch ( ClassCastException ccE ) {
			throw new IllegalArgumentException("The List contains a non-UsbIrp object.");
		}

		List delayEventList = (ArrayList)newlist.clone();

		/* Use a different list so we can modify it if needed */
		for (int i=0; i<delayEventList.size(); i++)
			listTable.put(delayEventList.get(i), delayEventList);

		return newlist;
	}

	/**
	 * Check if this device is disconnected.
	 * @exception UsbDisconnectedException If this device is disconnected.
	 */
	protected void checkDisconnected() throws UsbDisconnectedException
	{
		getUsbEndpointImp().checkDisconnected();
	}

	/** @return If this device is disconnected. */
	protected boolean isDisconnected() { return getUsbEndpointImp().isDisconnected(); }

	/**
	 * Check if this pipe is active.
	 * @throws UsbNotActiveException If the pipe is not active.
	 * @exception UsbDisconnectedException If this device is disconnected.
	 */
	protected void checkActive() throws UsbNotActiveException,UsbDisconnectedException
	{
		checkDisconnected();

		if (!isActive())
			throw new UsbNotActiveException("UsbPipe not active");
	}

	/**
	 * Check if this pipe is open.
	 * <p>
	 * A pipe must be active to be open.
	 * @exception UsbNotActiveException If the pipe is not active.
	 * @exception UsbNotOpenException If the pipe is not open.
	 * @exception UsbDisconnectedException If this device is disconnected.
	 */
	protected void checkOpen() throws UsbNotActiveException,UsbNotOpenException,UsbDisconnectedException
	{
		checkActive();

		if (!isOpen())
			throw new UsbNotOpenException("UsbPipe not open");
	}

	/** Get a uniquely-numbered UsbIrpImp */
	protected UsbIrpImp createUsbIrpImp()
	{
		return new UsbIrpImp();
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
	 * Submit a UsbIrpImp from the queueManager.
	 * @param usbIrpImp The UsbIrpImp to submit.
	 */
	protected void submitUsbIrpImpFromQueue(UsbIrpImp usbIrpImp)
	{
		synchronized (abortLock) {
			if (abortInProgress) {
				usbIrpImp.setUsbException(new UsbAbortException());
				usbIrpImp.complete();
				return;
			}
			try {
				getUsbPipeOsImp().asyncSubmit(usbIrpImp);
			} catch ( UsbException uE ) {
				submissionList.remove(usbIrpImp);
				/* ignore this, as the UsbIrp's UsbException will be set and this is handled elsewhere. */
			}
		}

		usbIrpImp.waitUntilComplete();
	}

	/**
	 * Queue a UsbIrpImp
	 * @param usbIrpImp The UsbIrpImp to queue.
	 */
	protected void queueUsbIrpImp(final UsbIrpImp usbIrpImp)
	{
		Runnable r = new Runnable()	{ public void run() { submitUsbIrpImpFromQueue(usbIrpImp); } };

		addRunnable(r);
	}

	/**
	 * Queue a List of UsbIrpImps.
	 * @param list The List of UsbIrpImps to queue.
	 */
	protected void queueList(final List list)
	{
		Runnable r = new Runnable()
			{
				public void run() {
					for (int i=0; i<list.size(); i++)
						submitUsbIrpImpFromQueue((UsbIrpImp)list.get(i));
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

		String policy = null;
		byte endpointType = (byte)(UsbConst.ENDPOINT_TYPE_MASK & getUsbEndpoint().getUsbEndpointDescriptor().bmAttributes());
		switch (endpointType) {
		case UsbConst.ENDPOINT_TYPE_CONTROL:
			policy = p.getProperty(PIPE_CONTROL_QUEUE_POLICY_KEY);
			break;
		case UsbConst.ENDPOINT_TYPE_INTERRUPT:
			policy = p.getProperty(PIPE_INTERRUPT_QUEUE_POLICY_KEY);
			break;
		case UsbConst.ENDPOINT_TYPE_ISOCHRONOUS:
			policy = p.getProperty(PIPE_ISOCHRONOUS_QUEUE_POLICY_KEY);
			break;
		case UsbConst.ENDPOINT_TYPE_BULK:
			policy = p.getProperty(PIPE_BULK_QUEUE_POLICY_KEY);
			break;
		default:
			throw new RuntimeException("Illegal endpoint type 0x" + UsbUtil.toHexString(endpointType));
		}
		if (null != policy)
			queueSubmissions = Boolean.valueOf(policy.trim()).booleanValue();

		policy = p.getProperty(CREATE_SHORT_PACKET_EXCEPTION_POLICY_KEY);
		if (null != policy)
			createShortPacketException = Boolean.valueOf(policy.trim()).booleanValue();
	}

	/**
	 * Return if this is an in-direction pipe (or UsbIrp in the case of control pipes).
	 * @param irp The UsbIrpImp.
	 * @return If this is an in-direction transfer.
	 */
	protected boolean isInDirection(UsbIrpImp irp)
	{
		byte dir = getUsbEndpoint().getDirection();

		try { dir = (byte)(UsbConst.REQUESTTYPE_DIRECTION_MASK & ((UsbControlIrp)irp).bmRequestType()); }
		catch ( ClassCastException ccE ) { /* not a control irp, so the pipe determines direction */ }

		return UsbConst.REQUESTTYPE_DIRECTION_IN == dir;
	}

	//**************************************************************************
	// Package methods

	/** @return A name describing this */
	String getName()
	{
		String deviceName = getUsbEndpointImp().getUsbInterfaceImp().getUsbConfigurationImp().getUsbDeviceImp().getName();
		String ep = "";
		if (null == getUsbEndpoint().getUsbEndpointDescriptor())
			ep = "??";
		else
			ep = UsbUtil.toHexString(getUsbEndpoint().getUsbEndpointDescriptor().bEndpointAddress());
		return deviceName + " UsbPipeImp 0x" + ep;
	}

	/** Disconnect this. */
	void disconnect()
	{
		/* Stop the queueManager. */
		queueManager.stop();

		/* Nothing to see here, move along! */
		listenerImp.clear();

		/* We could abortAllSubmissions, but we probably don't need to.
		 * Any pending submissions should either be automatically aborted,
		 * or remain pending - but their status is not (or should not be)
		 * relevant after a device disconnect.
		 */
	}

	//**************************************************************************
	// Instance variables

	private boolean open = false;
	private UsbPipeOsImp usbPipeOsImp = new DefaultUsbPipeOsImp();

	private UsbEndpointImp usbEndpointImp = null;

	protected UsbPipeListenerImp listenerImp = new UsbPipeListenerImp();
	private boolean listenerNameSet = false;

	/* If the queue policy is set to true for this pipe, all submissions will be queued and
	 * submitted via the UsbPipeOsImp.syncSubmit() method, so the OS will not have to queue.
	 * The OS is most likely much more efficient at queueing, so if it supports it,
	 * OS queueing should be used.
	 */
	protected RunnableManager queueManager = new RunnableManager(false);
	protected boolean queueSubmissions = false;
	protected boolean abortInProgress = false;

	protected boolean createShortPacketException = false;

	protected Hashtable listTable = new Hashtable();

	protected List submissionList = new Vector();

	private Object submitLock = new Object();
	private Object completeLock = new Object();
	private Object abortLock = new Object();

	//**************************************************************************
	// Class constants

	public static final String PIPE_CONTROL_QUEUE_POLICY_KEY = "com.ibm.jusb.UsbPipeImp.queueSubmissions.control";
	public static final String PIPE_INTERRUPT_QUEUE_POLICY_KEY = "com.ibm.jusb.UsbPipeImp.queueSubmissions.interrupt";
	public static final String PIPE_ISOCHRONOUS_QUEUE_POLICY_KEY = "com.ibm.jusb.UsbPipeImp.queueSubmissions.isochronous";
	public static final String PIPE_BULK_QUEUE_POLICY_KEY = "com.ibm.jusb.UsbPipeImp.queueSubmissions.bulk";

	public static final String CREATE_SHORT_PACKET_EXCEPTION_POLICY_KEY = "com.ibm.jusb.UsbIrpImp.createShortPacketException";

}
