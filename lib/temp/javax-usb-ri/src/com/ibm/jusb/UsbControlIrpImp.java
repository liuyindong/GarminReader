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
import javax.usb.util.*;
import javax.usb.event.*;

import com.ibm.jusb.os.*;
import com.ibm.jusb.util.*;

/**
 * UsbControlIrp implementation.
 * <p>
 * This is the same as UsbIrpImp, except this contains Control-specific
 * setup packet information.
 * <p>
 * This does <i>not</i> extend javax.usb.util.DefaultUsbControlIrp.
 * @author Dan Streetman
 */
public class UsbControlIrpImp extends UsbIrpImp implements UsbControlIrp
{
	/**
	 * Constructor.
	 * @param bmRequestType The bmRequestType.
	 * @param bRequest The bRequest.
	 * @param wValue The wValue.
	 * @param wIndex The wIndex.
	 */
	public UsbControlIrpImp(byte bmRequestType, byte bRequest, short wValue, short wIndex)
	{
		super();
		this.bmRequestType = bmRequestType;
		this.bRequest = bRequest;
		this.wValue = wValue;
		this.wIndex = wIndex;
	}

	/**
	 * Constructor.
	 * @param controlUsbIrp The UsbControlIrp this should wrap.
	 */
	public UsbControlIrpImp(UsbControlIrp controlUsbIrp)
	{
		super(controlUsbIrp);
		this.bmRequestType = controlUsbIrp.bmRequestType();
		this.bRequest = controlUsbIrp.bRequest();
		this.wValue = controlUsbIrp.wValue();
		this.wIndex = controlUsbIrp.wIndex();
	}

	/**
	 * Get the bmRequestType.
	 * @return The bmRequestType.
	 */
	public byte bmRequestType() { return bmRequestType; }

	/**
	 * Get the bRequest.
	 * @return The bRequest.
	 */
	public byte bRequest() { return bRequest; }

	/**
	 * Get the wValue.
	 * @return The wValue.
	 */
	public short wValue() { return wValue; }

	/**
	 * Get the wIndex.
	 * @return The wIndex.
	 */
	public short wIndex() { return wIndex; }

	/**
	 * Get the wLength.
	 * @return The wLength.
	 */
	public short wLength() { return (short)getLength(); }

	/**
	 * Complete this submission.
	 * <p>
	 * If this is a successful {@link #isSetConfiguration() set configuration} request,
	 * this will {@link com.ibm.jusb.UsbDeviceImp#setActiveUsbConfigurationNumber(byte) set the active configuration number}.
	 * If this is a successful {@link #isSetInterface() set interface} request,
	 * this will {@link com.ibm.jusb.UsbInterfaceImp#setActiveSettingNumber(byte) set the active setting number}.
	 * Then, it will perform the {@link com.ibm.jusb.UsbIrpImp#complete() superclass's complete}.
	 */
	public void complete()
	{
		if (!isUsbException()) {
			if (isSetConfiguration()) {
				try { getUsbDeviceImp().setActiveUsbConfigurationNumber((byte)wValue()); }
				catch ( Exception e ) { /* FIXME - log? */ }
			} else if (isSetInterface()) {
				try { getUsbDeviceImp().getActiveUsbConfigurationImp().getUsbInterfaceImp((byte)wIndex()).setActiveSettingNumber((byte)wValue()); }
				catch ( Exception e ) { /* FIXME - log? */ }
			}
		}

		super.complete();
	}

	/**
	 * If this is a SET_CONFIGURATION UsbIrp.
	 * @return If this is a SET_CONFIGURATION UsbIrp.
	 */
	public boolean isSetConfiguration()
	{
		return (bmRequestType() == REQUESTTYPE_SET_CONFIGURATION) && (bRequest() == UsbConst.REQUEST_SET_CONFIGURATION);
	}

	/**
	 * If this is a SET_INTERFACE UsbIrp.
	 * @return If this is a SET_INTERFACE UsbIrp.
	 */
	public boolean isSetInterface()
	{
		return (bmRequestType() == REQUESTTYPE_SET_INTERFACE) && (bRequest() == UsbConst.REQUEST_SET_INTERFACE);
	}

	/**
	 * Get the setup packet (Control header).
	 * <p>
	 * This creates a new byte[] constructed using the Control-specific methods
	 * in this class.  See the USB 1.1 specification section 9.3; specifically,
	 * the setup packet is constructed as such:
	 * <ol>
	 * <li>{@link #bmRequestType() bmRequestType}</li>
	 * <li>{@link #bRequest() bRequest}</li>
	 * <li>{@link #wValue() wValue}'s LSB (Least Significant Byte)</li>
	 * <li>{@link #wValue() wValue}'s MSB (Most Significant Byte)</li>
	 * <li>{@link #wIndex() wIndex}'s LSB</li>
	 * <li>{@link #wIndex() wIndex}'s MSB</li>
	 * <li>{@link #wLength() wLength}'s LSB</li>
	 * <li>{@link #wLength() wLength}'s MSB</li>
	 * </ol>
	 * @return The setup packet (Control header).
	 */
	public byte[] getSetupPacket()
	{
		byte[] setupPacket = new byte[8];

		setupPacket[0] = bmRequestType();
		setupPacket[1] = bRequest();
		setupPacket[2] = (byte)wValue();
		setupPacket[3] = (byte)(wValue() >> 8);
		setupPacket[4] = (byte)wIndex();
		setupPacket[5] = (byte)(wIndex() >> 8);
		setupPacket[6] = (byte)wLength();
		setupPacket[7] = (byte)(wLength() >> 8);

		return setupPacket;
	}

	/**
	 * Get the UsbDeviceImp.
	 * @return The UsbDeviceImp.
	 */
	public UsbDeviceImp getUsbDeviceImp() { return usbDeviceImp; }

	/**
	 * Set the UsbDeviceImp.
	 * @param device The UsbDeviceImp.
	 */
	public void setUsbDeviceImp(UsbDeviceImp device) { usbDeviceImp = device; }

	/**
	 * Check the specified UsbControlIrp.
	 * <p>
	 * This may be used to check the validity of an UsbControlIrp.
	 * This will throw an IllegalArgumentException if the UsbControlIrp
	 * does not behave as specified in the UsbControlIrp interface documentation.
	 * This will throw an UsbException if the UsbControlIrp is in a state not
	 * ready for submission, such as being complete or having a UsbException.
	 * @exception IllegalArgumentException If the UsbControlIrp is not valid.
	 * @exception UsbException If the UsbControlIrp is not ready for submission.
	 */
	public static void checkUsbControlIrp(UsbControlIrp irp) throws IllegalArgumentException,UsbException
	{
		UsbIrpImp.checkUsbIrp(irp);

		/* FIXME - check the setup packet fields here? */
	}

	protected byte bmRequestType = 0x00;
	protected byte bRequest = 0x00;
	protected short wValue = 0x0000;
	protected short wIndex = 0x0000;

	protected UsbDeviceImp usbDeviceImp = null;

	private static final byte REQUESTTYPE_SET_CONFIGURATION =
		UsbConst.REQUESTTYPE_DIRECTION_OUT | UsbConst.REQUESTTYPE_TYPE_STANDARD | UsbConst.REQUESTTYPE_RECIPIENT_DEVICE;
	private static final byte REQUESTTYPE_SET_INTERFACE =
		UsbConst.REQUESTTYPE_DIRECTION_OUT | UsbConst.REQUESTTYPE_TYPE_STANDARD | UsbConst.REQUESTTYPE_RECIPIENT_INTERFACE;
}
