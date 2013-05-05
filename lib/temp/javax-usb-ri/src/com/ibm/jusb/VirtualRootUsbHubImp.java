package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.*;

import com.ibm.jusb.os.*;

/**
 * Virtual root UsbHub implementation.
 * @author Dan Streetman
 */
public class VirtualRootUsbHubImp extends UsbHubImp implements UsbHub
{
	public VirtualRootUsbHubImp()
	{
		super(virtualDeviceDescriptor, new VirtualRootUsbDeviceOsImp());
		setSpeed(UsbConst.DEVICE_SPEED_FULL);
		UsbConfigurationImp virtualConfiguration = new UsbConfigurationImp(this, virtualConfigurationDescriptor);
		UsbInterfaceImp virtualInterface = new UsbInterfaceImp(virtualConfiguration, virtualInterfaceDescriptor, new VirtualRootUsbInterfaceOsImp());
		setActiveUsbConfigurationNumber(CONFIG_NUM);
	}

	//**************************************************************************
	// Public methods

	/** No connect operation */
	public void connect(UsbHubImp hub, byte portNumber) throws UsbException
	{
		throw new UsbException("Cannot connect the virtual root UsbHub");
	}

	/** @return true if this is the virtual root hub */
	public boolean isRootUsbHub() { return true; }

	/** No disconnect */
	public void disconnect()
	{
		throw new RuntimeException("Cannot disconnect the virtual root UsbHub.");
	}

	/** No UsbPort use */
	public void setParentUsbPortImp(UsbPortImp port)
	{
		throw new RuntimeException("The virtual root UsbHub cannot have any parent UsbPort.");
	}

	/** No UsbPort use */
	public UsbPortImp getParentUsbPortImp() { return null; }

	//**************************************************************************
	// Class constants

	public static final byte MANUFACTURER_INDEX = (byte)0x01;
	public static final byte PRODUCT_INDEX = (byte)0x02;
	public static final byte SERIALNUMBER_INDEX = (byte)0x03;

	public static final String MANUFACTURER_STRING = "javax.usb OS-independent Reference Implementation";
	public static final String PRODUCT_STRING = "Virtual Root UsbHub";
	public static final String SERIALNUMBER_STRING = "19741113";

	public static final String ENCODING = "UTF-16LE";

	public static final String VIRTUAL_ROOT_USBHUB_SUBMIT_STRING = "Only limited standard requests are possible on the virtual root UsbHub default control pipe.";

	public static final short VENDOR_ID = (short)0xffff;
	public static final short PRODUCT_ID = (short)0xffff;
	public static final short DEVICE_BCD = (short)0x0000;
	public static final short USB_BCD = (short)0x0101;

	public static final byte CONFIG_NUM = (byte)0x01;
	public static final short CONFIG_TOTAL_LEN =
		(short)(UsbConst.DESCRIPTOR_MIN_LENGTH_CONFIGURATION + UsbConst.DESCRIPTOR_MIN_LENGTH_INTERFACE);

	public static final byte INTERFACE_NUM = (byte)0x00;
	public static final byte SETTING_NUM = (byte)0x00;

	public static final byte[] stringLangId = {
		(byte)0x04, UsbConst.DESCRIPTOR_TYPE_STRING, 0x00, 0x00 };

	/* Be sure to update this if the real descriptor is changed */
	public static final byte[] deviceDescriptorBytes = {
		UsbConst.DESCRIPTOR_MIN_LENGTH_DEVICE, UsbConst.DESCRIPTOR_TYPE_DEVICE, (byte)(USB_BCD), (byte)(USB_BCD>>8),
		UsbConst.HUB_CLASSCODE, (byte)0x00, (byte)0x00, (byte)0x08, VENDOR_ID, PRODUCT_ID, DEVICE_BCD,
		(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01 };

	public static final UsbDeviceDescriptorImp virtualDeviceDescriptor = new UsbDeviceDescriptorImp(
		UsbConst.DESCRIPTOR_MIN_LENGTH_DEVICE,
		UsbConst.DESCRIPTOR_TYPE_DEVICE,
		USB_BCD,
		UsbConst.HUB_CLASSCODE,
		(byte)0x00, /* subclass */
		(byte)0x00, /* protocol */
		(byte)0x08, /* maxpacketsize */
		VENDOR_ID,
		PRODUCT_ID,
		DEVICE_BCD,
		MANUFACTURER_INDEX,
		PRODUCT_INDEX,
		SERIALNUMBER_INDEX,
		(byte)0x01 /* n configs */ );

	/* be sure to update this if the real descriptor(s) are changed */
	public static final byte[] configurationDescriptorBytes = {
		UsbConst.DESCRIPTOR_MIN_LENGTH_CONFIGURATION, UsbConst.DESCRIPTOR_TYPE_CONFIGURATION,
		(byte)(CONFIG_TOTAL_LEN), (byte)(CONFIG_TOTAL_LEN>>8), (byte)0x01, CONFIG_NUM, (byte)0x00, (byte)0x80, (byte)0x00,
		UsbConst.DESCRIPTOR_MIN_LENGTH_INTERFACE, UsbConst.DESCRIPTOR_TYPE_INTERFACE, INTERFACE_NUM, SETTING_NUM,
		(byte)0x00, UsbConst.HUB_CLASSCODE, (byte)0x00, (byte)0x00, (byte)0x00 };

	public static final UsbConfigurationDescriptorImp virtualConfigurationDescriptor = new UsbConfigurationDescriptorImp(
		UsbConst.DESCRIPTOR_MIN_LENGTH_CONFIGURATION,
		UsbConst.DESCRIPTOR_TYPE_CONFIGURATION,
		(short)CONFIG_TOTAL_LEN,
		(byte)0x01, /* n interfaces */
		CONFIG_NUM,
		(byte)0x00, /* config index */
		(byte)0x80, /* attr */
		(byte)0x00 ); /* maxpower */

	public static final UsbInterfaceDescriptorImp virtualInterfaceDescriptor = new UsbInterfaceDescriptorImp(
		UsbConst.DESCRIPTOR_MIN_LENGTH_INTERFACE,
		UsbConst.DESCRIPTOR_TYPE_INTERFACE,
		INTERFACE_NUM,
		SETTING_NUM,
		(byte)0x00, /* num endpoints */
		UsbConst.HUB_CLASSCODE,
		(byte)0x00, /* subclass */
		(byte)0x00, /* protocol */
		(byte)0x00 ); /* iface index */

	/**
	 * No-claim UsbInterfaceOsImp.
	 */
	private static class VirtualRootUsbInterfaceOsImp extends DefaultUsbInterfaceOsImp implements UsbInterfaceOsImp
	{
		public void claim() throws UsbException { throw new UsbException("Cannot claim an interface on a virtual root hub."); }
		public boolean isClaimed() { return true; }
	}

	/**
	 * Class to intercept Standard Requests.
	 */
	private static class VirtualRootUsbDeviceOsImp extends DefaultUsbDeviceOsImp implements UsbDeviceOsImp
	{
		public VirtualRootUsbDeviceOsImp() { super(VIRTUAL_ROOT_USBHUB_SUBMIT_STRING); }

		/**
		 * Process a UsbControlIrpImp.
		 * <p>
		 * This cheats, it should be asynchronous but is really synchronous.
		 * That's ok, since it is virtual, it will be fast; it doesn't communicate
		 * with a physical device anyway.
		 * @param usbControlIrpImp The UsbControlIrpImp to process.
		 * @exception If the request was not a supported standard request.
		 */
		public void asyncSubmit(UsbControlIrpImp irp) throws UsbException
		{
			try {
				if (UsbConst.REQUESTTYPE_DIRECTION_IN != (UsbConst.REQUESTTYPE_DIRECTION_MASK & irp.bmRequestType()))
					throw new UsbException(getSubmitString());

				if (UsbConst.REQUESTTYPE_TYPE_STANDARD != (UsbConst.REQUESTTYPE_TYPE_MASK & irp.bmRequestType()))
					throw new UsbException(getSubmitString());

				if (UsbConst.REQUESTTYPE_RECIPIENT_DEVICE == (UsbConst.REQUESTTYPE_RECIPIENT_MASK & irp.bmRequestType())) {
					if (UsbConst.REQUEST_GET_CONFIGURATION == irp.bRequest()) {
						if (0 == irp.wValue() && 0 == irp.wIndex() && 1 == irp.getLength()) {
							irp.getData()[irp.getOffset()] = CONFIG_NUM;
							irp.setActualLength(1);
						} else {
							throw new UsbException(getSubmitString());
						}
					} else if (UsbConst.REQUEST_GET_DESCRIPTOR == irp.bRequest()) {
						if (UsbConst.DESCRIPTOR_TYPE_DEVICE == (byte)(irp.wValue() >> 8)) {
							if (0 == (byte)irp.wValue() && 0 == irp.wIndex()) {
								irp.setActualLength(irp.getLength() < deviceDescriptorBytes.length ? irp.getLength() : deviceDescriptorBytes.length);
								System.arraycopy(deviceDescriptorBytes, 0, irp.getData(), irp.getOffset(), irp.getActualLength());
							} else {
								throw new UsbException(getSubmitString());
							}
						} else if (UsbConst.DESCRIPTOR_TYPE_CONFIGURATION == (irp.wValue() >> 8)) {
							if (CONFIG_NUM == (byte)irp.wValue() && 0 == irp.wIndex()) {
								irp.setActualLength(irp.getLength() < configurationDescriptorBytes.length ? irp.getLength() : configurationDescriptorBytes.length);
								System.arraycopy(configurationDescriptorBytes, 0, irp.getData(), irp.getOffset(), irp.getActualLength());
							} else {
								throw new UsbException(getSubmitString());
							}
						} else if (UsbConst.DESCRIPTOR_TYPE_STRING == (irp.wValue() >> 8)) {
							getStringDescriptor(irp);
						} else {
							throw new UsbException(getSubmitString());
						}
					} else {
						throw new UsbException(getSubmitString());
					}
				} else if (UsbConst.REQUESTTYPE_RECIPIENT_INTERFACE == (UsbConst.REQUESTTYPE_RECIPIENT_MASK & irp.bmRequestType())) {
					if (UsbConst.REQUEST_GET_INTERFACE == irp.bRequest()) {
						if (0 == irp.wValue() && INTERFACE_NUM == irp.wIndex() && 1 == irp.getLength()) {
							irp.getData()[irp.getOffset()] = SETTING_NUM;
							irp.setActualLength(1);
						} else {
							throw new UsbException(getSubmitString());
						}
					} else {
						throw new UsbException(getSubmitString());
					}
				} else {
					throw new UsbException(getSubmitString());
				}
			} catch ( UsbException uE ) {
				irp.setUsbException(uE);
				throw uE;
			} finally {
				irp.complete();
			}
		}

		/**
		 * Process a request for a string descriptor.
		 * @param irp The UsbControlIrpImp.
		 * @exception UsbException If the request was invalid.
		 */
		protected void getStringDescriptor(UsbControlIrpImp irp) throws UsbException
		{
			byte[] str = new byte[0];

			switch ((byte)irp.wValue()) {
			case (byte)0x00:
				irp.setActualLength(irp.getLength() < stringLangId.length ? irp.getLength() : stringLangId.length);
				System.arraycopy(stringLangId, 0, irp.getData(), irp.getOffset(), irp.getActualLength());
				return;
			case MANUFACTURER_INDEX:
				try {
					str = MANUFACTURER_STRING.getBytes(ENCODING);
				} catch ( Exception e ) {
					irp.setActualLength(0);
					return;
				}
				break;
			case PRODUCT_INDEX:
				try {
					str = PRODUCT_STRING.getBytes(ENCODING);
				} catch ( Exception e ) {
					irp.setActualLength(0);
					return;
				}
				break;
			case SERIALNUMBER_INDEX:
				try {
					str = SERIALNUMBER_STRING.getBytes(ENCODING);
				} catch ( Exception e ) {
					irp.setActualLength(0);
					return;
				}
				break;
			default:
				irp.setActualLength(0);
				return;
			}

			irp.setActualLength(irp.getLength() < (2 + str.length) ? irp.getLength() : (2 + str.length));
			irp.getData()[irp.getOffset()] = (byte)irp.getActualLength();
			irp.getData()[1 + irp.getOffset()] = UsbConst.DESCRIPTOR_TYPE_STRING;
			System.arraycopy(str, 0, irp.getData(), 2 + irp.getOffset(), irp.getActualLength() - 2);
		}

	}

}
