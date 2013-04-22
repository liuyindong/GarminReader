package com.ibm.jusb.event;

/*
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
 * Implementation of UsbDeviceListener.
 * @author Dan Streetman
 */
public class UsbDeviceListenerImp extends EventListenerImp implements UsbDeviceListener
{
	/** @param event The Event to fire. */
	public void errorEventOccurred(final UsbDeviceErrorEvent event)
	{
		if (isEmpty())
			return;

		synchronized (listeners) {
			Iterator iterator = listeners.values().iterator();
			while (iterator.hasNext()) {
				EventListenerRunnableManager elrM = (EventListenerRunnableManager)iterator.next();
				final UsbDeviceListener udL = (UsbDeviceListener)elrM.getEventListener();
				Runnable r = new Runnable() { public void run() { udL.errorEventOccurred(event); } };
				elrM.add(r);
			}
		}
	}

	/** @param event The Event to fire. */
	public void dataEventOccurred(final UsbDeviceDataEvent event)
	{
		if (isEmpty())
			return;

		synchronized (listeners) {
			Iterator iterator = listeners.values().iterator();
			while (iterator.hasNext()) {
				EventListenerRunnableManager elrM = (EventListenerRunnableManager)iterator.next();
				final UsbDeviceListener udL = (UsbDeviceListener)elrM.getEventListener();
				Runnable r = new Runnable() { public void run() { udL.dataEventOccurred(event); } };
				elrM.add(r);
			}
		}
	}

	/** @param event The Event to fire. */
	public void usbDeviceDetached(final UsbDeviceEvent event)
	{
		if (isEmpty())
			return;

		synchronized (listeners) {
			Iterator iterator = listeners.values().iterator();
			while (iterator.hasNext()) {
				EventListenerRunnableManager elrM = (EventListenerRunnableManager)iterator.next();
				final UsbDeviceListener udL = (UsbDeviceListener)elrM.getEventListener();
				Runnable r = new Runnable() { public void run() { udL.usbDeviceDetached(event); } };
				elrM.add(r);
			}
		}
	}
}
