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
 * Class to display UsbEndpoint info.
 * @author Dan Streetman
 */
public class UsbEndpointPanel extends UsbPanel
{
	public UsbEndpointPanel(UsbEndpoint ep)
	{
		super();
		usbEndpoint = ep;
		string = "UsbEndpoint 0x" + UsbUtil.toHexString(ep.getUsbEndpointDescriptor().bEndpointAddress());

		add(Box.createVerticalGlue());
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(Box.createRigidArea(new Dimension(0,10)));
		add(panel);
	
		refresh();

	}

	public UsbEndpoint getUsbEndpoint() { return usbEndpoint; }

	protected void refresh()
	{
		clear();
		appendln(string);
		initText();
	}

	protected void initText()
	{
		String type = null;
		switch (usbEndpoint.getType()) {
		case UsbConst.ENDPOINT_TYPE_CONTROL: type = "Control"; break;
		case UsbConst.ENDPOINT_TYPE_BULK: type = "Bulk"; break;
		case UsbConst.ENDPOINT_TYPE_INTERRUPT: type = "Interrupt"; break;
		case UsbConst.ENDPOINT_TYPE_ISOCHRONOUS: type = "Isochronous"; break;
		default: type = "Unknown"; break;
		}

		String direction = null;
		switch (usbEndpoint.getDirection()) {
		case UsbConst.ENDPOINT_DIRECTION_IN: direction = "IN"; break;
		case UsbConst.ENDPOINT_DIRECTION_OUT: direction = "OUT"; break;
		default: direction = "Unknown"; break;
		}

		appendln("Type : " + UsbUtil.toHexString(usbEndpoint.getType()) + " (" + type + ")");
		appendln("Direction : " + direction);

		/* Note - this relies on the IBM Reference Implementation to provide a descriptive String */
		append(usbEndpoint.getUsbEndpointDescriptor().toString());
	}

	private UsbEndpoint usbEndpoint = null;
}
