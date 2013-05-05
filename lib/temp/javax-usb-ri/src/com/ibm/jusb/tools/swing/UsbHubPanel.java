package com.ibm.jusb.tools.swing;

/*
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import javax.usb.*;
import javax.usb.util.*;
import javax.usb.event.*;

/**
 * Class to display UsbHub info.
 * @author Dan Streetman
 */
public class UsbHubPanel extends UsbDevicePanel
{
	public UsbHubPanel(UsbHub hub)
	{
		super();
		usbDevice = hub;
		usbHub = hub;
		string = hub.isRootUsbHub() ? "Root UsbHub" : "UsbHub";
		String product = null;
		try { product = usbDevice.getProductString(); } catch ( Exception e ) { }
		if (null != product) {
			string += " (" + product + ")";
		} else {
			String idvendor = UsbUtil.toHexString(usbDevice.getUsbDeviceDescriptor().idVendor());
			String idproduct = UsbUtil.toHexString(usbDevice.getUsbDeviceDescriptor().idProduct());
			string += " <"+idvendor+":"+idproduct+">";
		}
		initPanels();
		refresh();
	}

	public UsbHub getUsbHub() { return usbHub; }

	protected void refresh()
	{
		clear();
		appendln(string);
		initText();
	}

	protected void initText()
	{
		appendln("Number of Ports : " + usbHub.getNumberOfPorts());
		super.initText();
	}

	private UsbHub usbHub = null;
}
