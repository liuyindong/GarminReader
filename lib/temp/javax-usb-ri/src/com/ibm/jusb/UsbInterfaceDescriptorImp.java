package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.UsbInterfaceDescriptor;
import javax.usb.util.UsbUtil;

/**
 * UsbInterfaceDescriptor implementation.
 * @author Dan Streetman
 */
public class UsbInterfaceDescriptorImp extends UsbDescriptorImp implements UsbInterfaceDescriptor
{
	/**
	 * Constructor.
	 * @param bLength This descriptor's bLength.
	 * @param bDescriptorType This descriptor's bDescriptorType.
	 * @param bInterfaceNumber This descriptor's bInterfaceNumber.
	 * @param bAlternateSetting This descriptor's bAlternateSetting.
	 * @param bNumEndpoints This descriptor's bNumEndpoints.
	 * @param bInterfaceClass This descriptor's bInterfaceClass.
	 * @param bInterfaceSubClass This descriptor's bInterfaceSubClass.
	 * @param bInterfaceProtocol This descriptor's bInterfaceProtocol.
	 * @param iInterface This descriptor's iInterface.
	 */
	public UsbInterfaceDescriptorImp( byte bLength, byte bDescriptorType,
		byte bInterfaceNumber, byte bAlternateSetting, byte bNumEndpoints,
		byte bInterfaceClass, byte bInterfaceSubClass, byte bInterfaceProtocol, byte iInterface )
	{
		super(bLength, bDescriptorType);
		this.bInterfaceNumber = bInterfaceNumber;
		this.bAlternateSetting = bAlternateSetting;
		this.bNumEndpoints = bNumEndpoints;
		this.bInterfaceClass = bInterfaceClass;
		this.bInterfaceSubClass = bInterfaceSubClass;
		this.bInterfaceProtocol = bInterfaceProtocol;
		this.iInterface = iInterface;
	}

    /**
	 * Get this descriptor's bInterfaceNumber.
	 * @return This descriptor's bInterfaceNumber.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
    public byte bInterfaceNumber() { return bInterfaceNumber; }

    /**
	 * Get this descriptor's bAlternateSetting.
	 * @return This descriptor's bAlternateSetting.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
    public byte bAlternateSetting() { return bAlternateSetting; }

    /**
	 * Get this descriptor's bNumEndpoints.
	 * @return This descriptor's bNumEndpoints.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
    public byte bNumEndpoints() { return bNumEndpoints; }

    /**
	 * Get this descriptor's bInterfaceClass.
	 * @return This descriptor's bInterfaceClass.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
    public byte bInterfaceClass() { return bInterfaceClass; }

    /**
	 * Get this descriptor's bInterfaceSubClass.
	 * @return This descriptor's bInterfaceSubClass.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
    public byte bInterfaceSubClass() { return bInterfaceSubClass; }

    /**
	 * Get this descriptor's bInterfaceProtocol.
	 * @return This descriptor's bInterfaceProtocol.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
    public byte bInterfaceProtocol() { return bInterfaceProtocol; }

    /**
	 * Get this descriptor's iInterface.
	 * @return This descriptor's iInterface.
	 * @see javax.usb.util.UsbUtil#unsignedInt(byte) This is unsigned.
	 */
    public byte iInterface() { return iInterface; }

	/**
	 * Get a String representing this.
	 * @return A String representing this.
	 */
	public String toString()
	{
		return
			super.toString() +
			"bInterfaceNumber : " + UsbUtil.unsignedInt(bInterfaceNumber()) + "\n" +
			"bAlternateSetting : " + UsbUtil.unsignedInt(bAlternateSetting()) + "\n" +
			"bNumEndpoints : " + UsbUtil.unsignedInt(bNumEndpoints()) + "\n" +
			"bInterfaceClass : 0x" + UsbUtil.toHexString(bInterfaceClass()) + "\n" +
			"bInterfaceSubClass : 0x" + UsbUtil.toHexString(bInterfaceSubClass()) + "\n" +
			"bInterfaceProtocol : 0x" + UsbUtil.toHexString(bInterfaceProtocol()) + "\n" +
			"iInterface : " + UsbUtil.unsignedInt(iInterface()) + "\n";
	}

    private byte bInterfaceNumber = 0x00;
    private byte bAlternateSetting = 0x00;
    private byte bNumEndpoints = 0x00;
    private byte bInterfaceClass = 0x00;
    private byte bInterfaceSubClass = 0x00;
    private byte bInterfaceProtocol = 0x00;
    private byte iInterface = 0x00;
}
