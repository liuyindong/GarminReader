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
 * Class to display UsbInterface info.
 * @author Dan Streetman
 */
public class UsbInterfacePanel extends UsbPanel
{
	public UsbInterfacePanel(UsbInterface iface)
	{
		super();
		usbInterface = iface;
		createClaimPanel();
		string = "UsbInterface " + UsbUtil.unsignedInt(iface.getUsbInterfaceDescriptor().bInterfaceNumber());

		if (1 < iface.getNumSettings())
			string += " setting " + UsbUtil.unsignedInt(iface.getUsbInterfaceDescriptor().bAlternateSetting());

		add(Box.createVerticalGlue());
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(Box.createRigidArea(new Dimension(0,10)));
		add(panel);
		
		refresh();
	}

	public UsbInterface getUsbInterface() { return usbInterface; }

	protected void refresh()
	{
		clear();
		appendln(string);
		initText();
	}

	protected void initText()
	{
		String ifaceString = null;
		String activeSetting = "Not Active";

		try { ifaceString = usbInterface.getInterfaceString(); } catch ( Exception e ) { ifaceString = "Error : " + e.getMessage(); }
		try { activeSetting = Integer.toString(UsbUtil.unsignedInt(usbInterface.getActiveSettingNumber())); } catch ( UsbNotActiveException unaE ) { }

		if (null == ifaceString) ifaceString = NULL_STRING;

		appendln("Interface String : " + ifaceString);
		appendln("Is Active : " + usbInterface.isActive());
		appendln("Is Claimed : " + usbInterface.isClaimed());
		appendln("Active Alternate Setting Number : " + activeSetting);
		appendln("Number of Alternate Settings : " + usbInterface.getNumSettings());

		/* Note - this relies on the IBM Reference Implementation to provide a descriptive String */
		append(usbInterface.getUsbInterfaceDescriptor().toString());
	}

	protected void createClaimPanel()
	{
		claimButton.addActionListener(claimListener);
		releaseButton.addActionListener(releaseListener);
		useUsbInterfacePolicyBox.addActionListener(usePolicyListener);

		claimPanel.add(useUsbInterfacePolicyBox);
		claimPanel.add(claimButton);
		claimPanel.add(releaseButton);

		boolean b = useUsbInterfacePolicyBox.isSelected();
		allowReleaseBox.setEnabled(b);
		allowOpenBox.setEnabled(b);
		forceClaimBox.setEnabled(b);
		policyPanel.add(allowReleaseBox);
		policyPanel.add(allowOpenBox);
		policyPanel.add(forceClaimBox);

		add(claimPanel);
		add(policyPanel);
	}

	protected void claim()
	{
		try {
			if (useUsbInterfacePolicyBox.isSelected()) {
				final boolean allowRelease = allowReleaseBox.isSelected();
				final boolean allowOpen = allowOpenBox.isSelected();
				final boolean forceClaim = forceClaimBox.isSelected();
				UsbInterfacePolicy policy = new UsbInterfacePolicy() {
						public boolean release(UsbInterface uI, Object o) { return allowRelease; }
						public boolean open(UsbPipe uP, Object o) { return allowOpen; }
						public boolean forceClaim(UsbInterface uI) { return forceClaim; }
					};
				usbInterface.claim(policy);
			} else {
				usbInterface.claim();
			}
		} catch ( UsbException uE ) {
			JOptionPane.showMessageDialog(null, "Could not claim UsbInterface : " + uE.getMessage());
		} catch ( UsbNotActiveException unaE ) {
			JOptionPane.showMessageDialog(null, "Could not claim UsbInterface : " + unaE.getMessage());
		}
		refresh();
	}

	protected void release()
	{
		try { usbInterface.release(); }
		catch ( UsbException uE ) { JOptionPane.showMessageDialog(null, "Could not release UsbInterface : " + uE.getMessage()); }
		catch ( UsbNotActiveException unaE ) { JOptionPane.showMessageDialog(null, "Could not release UsbInterface : " + unaE.getMessage()); }
		refresh();
	}

	private UsbInterface usbInterface = null;

	private JPanel claimPanel = new JPanel();
	private JButton claimButton = new JButton("Claim");
	private ActionListener claimListener = new ActionListener()
	{ public void actionPerformed(ActionEvent aE) { claim(); } };

	private JButton releaseButton = new JButton("Release");
	private ActionListener releaseListener = new ActionListener()
	{ public void actionPerformed(ActionEvent aE) { release(); } };

	private JPanel policyPanel = new JPanel();
	private JCheckBox useUsbInterfacePolicyBox = new JCheckBox("Use UsbInterfacePolicy", false);
	private ActionListener usePolicyListener = new ActionListener() {
			public void actionPerformed(ActionEvent aE)
			{
				boolean b = useUsbInterfacePolicyBox.isSelected();
				allowReleaseBox.setEnabled(b);
				allowOpenBox.setEnabled(b);
				forceClaimBox.setEnabled(b);
			}
		};

	private JCheckBox allowReleaseBox = new JCheckBox("allow release()", true);
	private JCheckBox allowOpenBox = new JCheckBox("allow open()", true);
	private JCheckBox forceClaimBox = new JCheckBox("forceClaim()", false);

}
