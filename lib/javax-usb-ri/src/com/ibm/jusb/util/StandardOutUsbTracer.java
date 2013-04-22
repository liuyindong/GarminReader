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
 * This implementation of UsbTracer sends messages to standard out.
 * <p>
 * This will print all messages on standard out, if the message level meets the
 * currently set priority level or is higher priority.
 * @author Dan Streetman
 */
public class StandardOutUsbTracer extends UsbTracer
{
	/**
	 * Create a new StandardOutUsbTracer with the specified name and trace level.
	 * @param name The default name of this tracer object.
	 * @param level The default trace level of this tracer object.
	 */
	public StandardOutUsbTracer(String name, int level)
	{
		super(name, level);

		try {
			currentLevel = getIntTraceLevel(UsbHostManager.getProperties().getProperty(TRACE_MSG_LEVEL));
		} catch ( Exception e ) {
			currentLevel = TRACE_DEBUG;
		}
	}

	/**
	 * This traces to standard out if appropriate.
	 * <p>
	 * If the level meets or exceeds the current level, the message is printed to
	 * standard out.
	 * @param name The name of the trace source.
	 * @param level The trace level of the message.
	 * @param msg The trace message.
	 */
	public void print(String name, int level, String msg)
	{
		if (level <= currentLevel)
			System.out.print(name + ":" + msg);
	}

	protected int currentLevel;

	public static final String TRACE_MSG_LEVEL = "com.ibm.jusb.util.StandardOutUsbTracer.currentLevel";
}
