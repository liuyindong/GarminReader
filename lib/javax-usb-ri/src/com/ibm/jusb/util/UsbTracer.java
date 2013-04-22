package com.ibm.jusb.util;

/* 
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.lang.reflect.*;
import java.util.*;

import javax.usb.*;

/**
 * Class to provide abstracted tracing for javax.usb implementation(s).
 * <p>
 * This allows the javax.usb implementations (common implementation and any
 * platform implementations) to perform tracing in a generic manner, whose output
 * can be controlled by the user and redirected to any desired trace package or function.
 * <p>
 * To use this tracing, either create a UsbTracer object yourself with the
 * {@link #getUsbTracer(String, int) getUsbTracer(name, level)} method and use that
 * object for all your tracing, or use the default/global UsbTracer object via
 * UsbTracer.{@link #getUsbTracer() getUsbTracer()}.
 * <p>
 * Each UsbTracer object has a default name and level.  The name should be used by the UsbTracer
 * implementation, possibly be prefixing the trace message with it.  The level should be used
 * by the UsbTracer implementation, usually by printing out only messages with a higher priority
 * that the implementation's current priority setting (which is dependent on the implementation).
 * @author Dan Streetman
 */
public abstract class UsbTracer
{
	/**
	 * Create a new UsbTracer with the specified name and trace level.
	 * <p>
	 * This creates a new UsbTracer with the specified default values.
	 * This is only usable by subclasses, and all subclasses must invoke this
	 * constructor.
	 * @param name The default name of this tracer object.
	 * @param level The default trace level of this tracer object.
	 */
	protected UsbTracer(String name, int level)
	{
		setDefaultName(name);
		setDefaultLevel(level);
	}

	/**
	 * Trace message with the specified name at the specified level.
	 * @param name The name of the trace source.
	 * @param level The trace level of the message.
	 * @param msg The trace message.
	 */
	public abstract void print(String name, int level, String msg);

	/**
	 * Trace message with the specified name at the specified level.
	 * <p>
	 * This appends a newline to the message.
	 * @param name The name of the trace source.
	 * @param level The trace level of the message.
	 * @param msg The trace message.
	 */
	public void println(String name, int level, String msg)
	{
		print(name, level, msg + "\n");
	}

	/**
	 * Trace message with the specified name at this UsbTracer object's default level.
	 * @param name The name of the trace source.
	 * @param msg The trace message.
	 */
	public void print(String name, String msg)
	{
		print(name, getDefaultLevel(), msg);
	}

	/**
	 * Trace message with the specified name at this UsbTracer object's default level.
	 * <p>
	 * This appends a newline to the message.
	 * @param name The name of the trace source.
	 * @param msg The trace message.
	 */
	public void println(String name, String msg)
	{
		println(name, getDefaultLevel(), msg);
	}

	/**
	 * Trace message with this UsbTracer object's default name at the specified level.
	 * @param level The trace level of the message.
	 * @param msg The trace message.
	 */
	public void print(int level, String msg)
	{
		print(getDefaultName(), level, msg);
	}

	/**
	 * Trace message with this UsbTracer object's default name at the specified level.
	 * <p>
	 * This appends a newline to the message.
	 * @param level The trace level of the message.
	 * @param msg The trace message.
	 */
	public void println(int level, String msg)
	{
		println(getDefaultName(), level, msg);
	}

	/**
	 * Trace message with this UsbTracer object's default name and default level.
	 * @param msg The trace message.
	 */
	public void print(String msg)
	{
		print(getDefaultName(), getDefaultLevel(), msg);
	}

	/**
	 * Trace message with this UsbTracer object's default name and default level.
	 * <p>
	 * This appends a newline to the message.
	 * @param msg The trace message.
	 */
	public void println(String msg)
	{
		println(getDefaultName(), getDefaultLevel(), msg);
	}

	/**
	 * Set the default name for this UsbTracer object.
	 * @param name The default name.
	 */
	public void setDefaultName(String name) { defaultName = name; }

	/**
	 * Get the default name for this UsbTracer object.
	 * @return The default name.
	 */
	public String getDefaultName() { return defaultName; }

	/**
	 * Set the default level for this UsbTracer object.
	 * @param level The default level.
	 */
	public void setDefaultLevel(int level) { defaultLevel = level; }

	/**
	 * Get the default level for this UsbTracer object.
	 * @return The default level.
	 */
	public int getDefaultLevel() { return defaultLevel; }

	/**
	 * Get a new UsbTracer object with the specified name and level.
	 * @param name The default trace name for the new UsbTracer.
	 * @param level The default trace level for the new UsbTracer.
	 * @return A new UsbTracer object.
	 */
	public static UsbTracer getUsbTracer(String name, int level)
	{
		try {
			Class[] constructorClasses = { String.class, int.class };
			String className = UsbHostManager.getProperties().getProperty(TRACE_IMPLEMENTATION_PROPERTY);
			Constructor classConstructor = Class.forName(className).getConstructor(constructorClasses);
			Object[] constructorObjects = { name, new Integer(level) };
			return (UsbTracer)classConstructor.newInstance(constructorObjects);
		} catch ( Exception e ) {
			return new NullUsbTracer(name, level);
		}
	}

	/**
	 * Get the default/global UsbTracer.
	 * <p>
	 * This should be used for easy tracing access.  The default/global UsbTracer object is returned.
	 * Its name defaults to "Main UsbTracer" and level to TRACE_DEBUG.  Both are settable from the
	 * javax.usb.properties file.
	 * @return The global/main UsbTracer.
	 */
	public static UsbTracer getUsbTracer()
	{
		synchronized (globalUsbTracerLock) {
			if (null == globalUsbTracer) {
				String name = GLOBAL_TRACER_DEFAULT_NAME;
				int level = GLOBAL_TRACER_DEFAULT_LEVEL;
				try { name = UsbHostManager.getProperties().getProperty(GLOBAL_TRACER_NAME_PROPERTY); }
				catch ( Exception e ) {	}

				try { level = getIntTraceLevel(UsbHostManager.getProperties().getProperty(GLOBAL_TRACER_LEVEL_PROPERTY)); }
				catch ( Exception e ) { }

				globalUsbTracer = getUsbTracer(name, level);
			}
		}

		return globalUsbTracer;
	}

	/**
	 * This translates the provided String trace level to an int level.
	 * @param level The String trace level.
	 * @return The int trace level.
	 * @exception Exception If the String level did not match a trace int level.
	 */
	protected static int getIntTraceLevel(String level) throws Exception
	{
		if (TRACE_CRITICAL_PROPERTY.equalsIgnoreCase(level.trim()))
			return TRACE_CRITICAL;
		else if (TRACE_ERROR_PROPERTY.equalsIgnoreCase(level.trim()))
			return TRACE_ERROR;
		else if (TRACE_WARN_PROPERTY.equalsIgnoreCase(level.trim()))
			return TRACE_WARN;
		else if (TRACE_NOTICE_PROPERTY.equalsIgnoreCase(level.trim()))
			return TRACE_NOTICE;
		else if (TRACE_INFO_PROPERTY.equalsIgnoreCase(level.trim()))
			return TRACE_INFO;
		else if (TRACE_DEBUG_PROPERTY.equalsIgnoreCase(level.trim()))
			return TRACE_DEBUG;
		else
			throw new Exception("Unknown trace level : " + level);
	}

	private static UsbTracer globalUsbTracer;
	private static Object globalUsbTracerLock = new Object();

	protected String defaultName;
	protected int defaultLevel;

	public static final String TRACE_IMPLEMENTATION_PROPERTY = "com.ibm.jusb.util.UsbTracer";
	public static final String GLOBAL_TRACER_NAME_PROPERTY = "com.ibm.jusb.util.UsbTracer.global.name";
	public static final String GLOBAL_TRACER_LEVEL_PROPERTY = "com.ibm.jusb.util.UsbTracer.global.level";

	public static final String TRACE_CRITICAL_PROPERTY = "TRACE_CRITICAL";
	public static final String TRACE_ERROR_PROPERTY = "TRACE_ERROR";
	public static final String TRACE_WARN_PROPERTY = "TRACE_WARN";
	public static final String TRACE_NOTICE_PROPERTY = "TRACE_NOTICE";
	public static final String TRACE_INFO_PROPERTY = "TRACE_INFO";
	public static final String TRACE_DEBUG_PROPERTY = "TRACE_DEBUG";

	public static final int TRACE_CRITICAL = 100;
	public static final int TRACE_ERROR = 200;
	public static final int TRACE_WARN = 300;
	public static final int TRACE_NOTICE = 400;
	public static final int TRACE_INFO = 500;
	public static final int TRACE_DEBUG = 600;

	private static final String GLOBAL_TRACER_DEFAULT_NAME = "Main UsbTracer";
	private static final int GLOBAL_TRACER_DEFAULT_LEVEL = TRACE_DEBUG;
}
