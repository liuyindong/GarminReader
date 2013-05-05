package com.ibm.jusb.event;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.util.*;

import javax.usb.event.*;

/**
 * Implementation of UsbServicesListener.
 * @author Dan Streetman
 */
public class UsbServicesListenerImp extends EventListenerImp implements UsbServicesListener
{
	/** UsbDevices attached */
	public void usbDeviceAttached(final UsbServicesEvent event)
	{
		if (isEmpty())
			return;

		synchronized (listeners) {
			Iterator iterator = listeners.values().iterator();
			while (iterator.hasNext()) {
				EventListenerRunnableManager elrM = (EventListenerRunnableManager)iterator.next();
				final UsbServicesListener usL = (UsbServicesListener)elrM.getEventListener();
				Runnable r = new Runnable() { public void run() { usL.usbDeviceAttached(event); } };
				elrM.add(r);
			}
		}
	}

	/** UsbDevices detached */
	public void usbDeviceDetached(final UsbServicesEvent event)
	{
		if (isEmpty())
			return;

		synchronized (listeners) {
			Iterator iterator = listeners.values().iterator();
			while (iterator.hasNext()) {
				EventListenerRunnableManager elrM = (EventListenerRunnableManager)iterator.next();
				final UsbServicesListener usL = (UsbServicesListener)elrM.getEventListener();
				Runnable r = new Runnable() { public void run() { usL.usbDeviceDetached(event); } };
				elrM.add(r);
			}
		}
	}
}                                                                             
