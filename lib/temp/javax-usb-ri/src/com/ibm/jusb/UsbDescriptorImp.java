package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.UsbDescriptor;
import javax.usb.util.UsbUtil;

/**
 * UsbDescriptor implementation.
 * @author Dan Streetman
 */
public class UsbDescriptorImp implements UsbDescriptor
{
	public UsbDescriptorImp( byte bLength, byte bDescriptorType )
	{
		this.bLength = bLength;
		this.bDescriptorType = bDescriptorType;
	}
	
    /**
	 * Get this descriptor's bLength.
	 * @return This descriptor's bLength.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
    public byte bLength() { return bLength; }

    /**
	 * Get this descriptor's bDescriptorType.
	 * @return This descriptor's bDescriptorType.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
    public byte bDescriptorType() { return bDescriptorType; }

	/**
	 * Compare this to an Object.
	 * @param object The Object to compare to.
	 * @return If this is equal to the Object.
	 */
	public boolean equals(Object object)
	{
		try { return toString().equals(object.toString()); }
		catch ( NullPointerException npE ) { return false; }
	}

	/**
	 * Get the hashcode.
	 * <p>
	 * This is implemented using the {@link #toString() String}'s hashCode.
	 * @return The hashcode.
	 */
	public int hashCode() { return toString().hashCode(); }

	/**
	 * Get a String representing this.
	 * @return A String representing this.
	 */
	public String toString()
	{
		return
			"bLength : " + UsbUtil.unsignedInt(bLength()) + "\n" +
			"bDescriptorType : 0x" + UsbUtil.toHexString(bDescriptorType()) + "\n";
	}

	private byte bLength = 0x00;
	private byte bDescriptorType = 0x00;
}
