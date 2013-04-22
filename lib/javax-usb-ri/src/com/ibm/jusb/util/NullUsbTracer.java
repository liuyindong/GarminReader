package com.ibm.jusb.util;

/* 
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.*;

/**
 * This implementation of UsbTracer does nothing with any trace messages.
 * @author Dan Streetman
 */
public class NullUsbTracer extends UsbTracer
{
	/**
	 * Create a new NullUsbTracer with the specified name and trace level.
	 * <p>
	 * Since this class does nothing these parameters don't matter.
	 * @param name The default name of this tracer object.
	 * @param level The default trace level of this tracer object.
	 */
	public NullUsbTracer(String name, int level) { super(name, level); }

	/**
	 * This method does nothing.  The trace message is dropped completely.
	 * @param name The name of the trace source.
	 * @param level The trace level of the message.
	 * @param msg The trace message.
	 */
	public void print(String name, int level, String msg) { }
}
