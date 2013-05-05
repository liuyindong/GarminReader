package com.ibm.jusb.os;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.*;
import javax.usb.event.*;

import com.ibm.jusb.*;
import com.ibm.jusb.event.*;
import com.ibm.jusb.util.*;

/**
 * Abstract implementation of UsbServices.
 * @author Dan Streetman
 */
public abstract class AbstractUsbServices implements UsbServices
{
	/** @return The root UsbHub. */
	public UsbHub getRootUsbHub() throws UsbException { return getRootUsbHubImp(); }

	/** @return The root UsbHubImp. */
	public UsbHubImp getRootUsbHubImp() { return rootUsbHubImp; }

	/** @param listener The listener to add. */
	public synchronized void addUsbServicesListener( UsbServicesListener listener )
	{ listenerImp.addEventListener( listener ); }

	/** @param listener The listener to remove. */
	public synchronized void removeUsbServicesListener( UsbServicesListener listener )
	{ listenerImp.removeEventListener( listener ); }

	private UsbHubImp rootUsbHubImp = new VirtualRootUsbHubImp();
	protected UsbServicesListenerImp listenerImp = new UsbServicesListenerImp();
}
