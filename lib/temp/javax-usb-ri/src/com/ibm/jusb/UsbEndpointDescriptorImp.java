package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.UsbEndpointDescriptor;
import javax.usb.util.UsbUtil;

/**
 * UsbEndpointDescriptor implementation.
 * @author Dan Streetman
 */
public class UsbEndpointDescriptorImp extends UsbDescriptorImp implements UsbEndpointDescriptor
{
	/**
	 * Constructor.
	 * @param bLength This descriptor's bLength.
	 * @param bDescriptorType This descriptor's bDescriptorType.
	 * @param bEndpointAddress This descriptor's bEndpointAddress.
	 * @param bmAttributes This descriptor's bmAttributes.
	 * @param bInterval This descriptor's bInterval.
	 * @param wMaxPacketSize This descriptor's wMaxPacketSize.
	 */
	public UsbEndpointDescriptorImp( byte bLength, byte bDescriptorType,
		byte bEndpointAddress, byte bmAttributes, byte bInterval, short wMaxPacketSize )
	{
		super(bLength, bDescriptorType);
		this.bEndpointAddress = bEndpointAddress;
		this.bmAttributes = bmAttributes;
		this.bInterval = bInterval;
		this.wMaxPacketSize = wMaxPacketSize;
	}

	/**
	 * Get this descriptor's bEndpointAddress.
	 * @return This descriptor's bEndpointAddress.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
	public byte bEndpointAddress() { return bEndpointAddress; }

	/**
	 * Get this descriptor's bmAttributes.
	 * @return This descriptor's bmAttributes.
	 */
	public byte bmAttributes() { return bmAttributes; }

	/**
	 * Get this descriptor's wMaxPacketSize.
	 * @return This descriptor's wMaxPacketSize.
	 * @see javax.usb.util.UsbUtil#unsignedInt(short) This is unsigned.
	 */
	public short wMaxPacketSize() { return wMaxPacketSize; }

	/**
	 * Get this descriptor's bInterval.
	 * @return This descriptor's bInterval.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
	public byte bInterval() { return bInterval; }

	/**
	 * Get a String representing this.
	 * @return A String representing this.
	 */
	public String toString()
	{
		return
			super.toString() +
			"bEndpointAddress : 0x" + UsbUtil.toHexString(bEndpointAddress()) + "\n" +
			"bmAttributes : 0x" + UsbUtil.toHexString(bmAttributes()) + "\n" +
			"wMaxPacketSize : " + UsbUtil.unsignedInt(wMaxPacketSize()) + "\n" +
			"bInterval : " + UsbUtil.unsignedInt(bInterval()) + "\n";
	}

	private byte bEndpointAddress = 0x00;
	private byte bmAttributes = 0x00;
	private short wMaxPacketSize = 0x0000;
	private byte bInterval = 0x00;
}
