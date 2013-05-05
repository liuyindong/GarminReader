package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.UsbConfigurationDescriptor;
import javax.usb.util.UsbUtil;

/**
 * UsbConfigurationDescriptor implementation.
 * @author Dan Streetman
 */
public class UsbConfigurationDescriptorImp extends UsbDescriptorImp implements UsbConfigurationDescriptor
{
	/**
	 * Constructor.
	 * @param bLength Descriptor length.
	 * @param bType Descriptor type.
	 * @param wTotalLength Descriptor Total Length.
	 * @param bNumInterfaces Number of interfaces.
	 * @param bConfigurationValue The ConfigValue.
	 * @param iConfiguration The ConfigIndex.
	 * @param bmAttributes The attributes.
	 * @param bMaxPower The max power.
	 */
	public UsbConfigurationDescriptorImp( byte bLength, byte bType,
		short wTotalLength, byte bNumInterfaces, byte bConfigurationValue,
		byte iConfiguration, byte bmAttributes, byte bMaxPower )
	{
		super(bLength, bType);
		this.wTotalLength = wTotalLength;
		this.bNumInterfaces = bNumInterfaces;
		this.bConfigurationValue = bConfigurationValue;
		this.iConfiguration = iConfiguration;
		this.bmAttributes = bmAttributes;
		this.bMaxPower = bMaxPower;
	}

	/**
	 * Get this descriptor's wTotalLength.
	 * @return This descriptor's wTotalLength.
	 * @see javax.usb.util.UsbUtil#unsignedInt(short) This is unsigned.
	 */
	public short wTotalLength() { return wTotalLength; }

    /**
	 * Get this descriptor's bNumInterfaces.
	 * @return This descriptor's bNumInterfaces.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
    public byte bNumInterfaces() { return bNumInterfaces; }

    /**
	 * Get this descriptor's bConfigurationValue.
	 * @return This descriptor's bConfigurationValue.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
    public byte bConfigurationValue() { return bConfigurationValue; }

    /**
	 * Get this descriptor's iConfiguration.
	 * @return This descriptor's iConfiguration.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
    public byte iConfiguration() { return iConfiguration; }

    /**
	 * Get this descriptor's bmAttributes.
	 * @return This descriptor's bmAttributes.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
     */
    public byte bmAttributes() { return bmAttributes; }

    /**
	 * Get this descriptor's bMaxPower.
	 * @return This descriptor's bMaxPower.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
    public byte bMaxPower() { return bMaxPower; }

	/**
	 * Get a String representing this.
	 * @return A String representing this.
	 */
	public String toString()
	{
		return
			super.toString() +
			"wTotalLength : " + UsbUtil.unsignedInt(wTotalLength()) + "\n" +
			"bNumInterfaces : " + UsbUtil.unsignedInt(bNumInterfaces()) + "\n" +
			"bConfigurationValue : " + UsbUtil.unsignedInt(bConfigurationValue()) + "\n" +
			"iConfiguration : " + UsbUtil.unsignedInt(iConfiguration()) + "\n" +
			"bmAttributes : 0x" + UsbUtil.toHexString(bmAttributes()) + "\n" +
			"bMaxPower : " + (2 * UsbUtil.unsignedInt(bMaxPower())) + " mA\n";
	}

	private short wTotalLength = 0x0000;
	private byte bNumInterfaces = 0x00;
	private byte bConfigurationValue = 0x00;
	private byte iConfiguration = 0x00;
	private byte bmAttributes = 0x00;
	private byte bMaxPower = 0x00;
}
