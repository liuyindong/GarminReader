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
 * Implementation of UsbPipeListener.
 * @author Dan Streetman
 */
public class UsbPipeListenerImp extends EventListenerImp implements UsbPipeListener
{
	/** @param event The Event to fire. */
	public void errorEventOccurred(final UsbPipeErrorEvent event)
	{
		if (isEmpty())
			return;

		synchronized (listeners) {
			Iterator iterator = listeners.values().iterator();
			while (iterator.hasNext()) {
				EventListenerRunnableManager elrM = (EventListenerRunnableManager)iterator.next();
				final UsbPipeListener upL = (UsbPipeListener)elrM.getEventListener();
				Runnable r = new Runnable() { public void run() { upL.errorEventOccurred(event); } };
				elrM.add(r);
			}
		}
	}

	/** @param event The Event to fire. */
	public void dataEventOccurred(final UsbPipeDataEvent event)
	{
		if (isEmpty())
			return;

		synchronized (listeners) {
			Iterator iterator = listeners.values().iterator();
			while (iterator.hasNext()) {
				EventListenerRunnableManager elrM = (EventListenerRunnableManager)iterator.next();
				final UsbPipeListener upL = (UsbPipeListener)elrM.getEventListener();
				Runnable r = new Runnable() { public void run() { upL.dataEventOccurred(event); } };
				elrM.add(r);
			}
		}
	}
}                                                                             
