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
 * Class to display UsbDevice info.
 * @author Dan Streetman
 */
public class UsbDevicePanel extends UsbPanel
{
	public UsbDevicePanel() { super(); }

	public UsbDevicePanel(UsbDevice device)
	{
		super();
		usbDevice = device;
		string = "UsbDevice";
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

	public UsbDevice getUsbDevice() { return usbDevice; }

	protected void refresh()
	{
		clear();
		appendln(string);
		initText();
	}

	protected void initText()
	{
		String manufacturer = null, product = null, serialNumber = null;

		try { manufacturer = usbDevice.getManufacturerString(); } catch ( Exception e ) { manufacturer = "Error : " + e.getMessage(); }
		try { product = usbDevice.getProductString(); } catch ( Exception e ) { product = "Error : " + e.getMessage(); }
		try { serialNumber = usbDevice.getSerialNumberString(); } catch ( Exception e ) { serialNumber = "Error : " + e.getMessage(); }

		if (null == manufacturer) manufacturer = NULL_STRING;
		if (null == product) product = NULL_STRING;
		if (null == serialNumber) serialNumber = NULL_STRING;

		appendln("Manufacturer : " + manufacturer);
		appendln("Product : " + product);
		appendln("Serial Number : " + serialNumber);
		appendln("Speed : " + UsbUtil.getSpeedString(usbDevice.getSpeed()));
		appendln("Is Configured : " + usbDevice.isConfigured());
		appendln("Active UsbConfiguration Number : " + UsbUtil.unsignedInt(usbDevice.getActiveUsbConfigurationNumber()));

		/* Note - this relies on the IBM Reference Implementation to provide a descriptive String */
		append(usbDevice.getUsbDeviceDescriptor().toString());
	}

	protected void initPanels()
	{
		usbDevice.addUsbDeviceListener(deviceListener);

		outputTextArea.setEditable(false);

		clearButton.addActionListener(clearListener);
		submitButton.addActionListener(submitListener);
		newPacketButton.addActionListener(newPacketListener);
		copyPacketButton.addActionListener(copyPacketListener);
		removeButton.addActionListener(removeListener);
		upButton.addActionListener(upListener);
		downButton.addActionListener(downListener);

		packetJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		packetJList.addListSelectionListener(packetListListener);

		clearPanel.add(outputScroll, BorderLayout.CENTER);
		JPanel panel = new JPanel();
		panel.add(clearButton);
		clearPanel.add(panel, BorderLayout.EAST);
		clearPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

		requestPanel.setLayout(requestLayout);

		//packetPanel.add(packetJList);
		
		JPanel buttonsPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		JPanel insidePanel = new JPanel(new GridLayout(3, 2, 5, 5));
		insidePanel.add(upButton);
		insidePanel.add(newPacketButton);
		insidePanel.add(downButton);
		insidePanel.add(copyPacketButton);
		insidePanel.add(submitButton);
		insidePanel.add(removeButton);
		buttonsPanel.add(insidePanel);

		JPanel submitPanel = new JPanel(new BorderLayout());
		submitPanel.add(packetListScroll, BorderLayout.CENTER);
		submitPanel.add(buttonsPanel, BorderLayout.EAST);
		submitPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		add(clearPanel);
		add(Box.createRigidArea(new Dimension(0, 5)));
		add(submitPanel);

		requestPanel.add(emptyPanel, EMPTY_PANEL);
		add(requestPanel);

		refreshButtons();
	}

	protected void refreshButtons()
	{
		submitButton.setEnabled( packetList.size() > 0 );
		upButton.setEnabled( packetList.size() > 0 && getSelectedIndex() > 0 );
		downButton.setEnabled( packetList.size() > 0 && getSelectedIndex() != packetList.size() - 1 );
		removeButton.setEnabled( packetList.size() > 0 );
		copyPacketButton.setEnabled( packetList.size() > 0 );
		newPacketButton.setEnabled( true );
	}

	protected int getSelectedIndex()
	{
		if (packetJList.isSelectionEmpty() && !packetList.isEmpty())
			packetJList.setSelectedIndex(0);
		return packetJList.getSelectedIndex();
	}

	protected void updateSelection()
	{
		if (!packetList.isEmpty())
			requestLayout.show(requestPanel, packetList.get(getSelectedIndex()).toString());
		else
			requestLayout.show(requestPanel, EMPTY_PANEL);
		validate();
		refreshButtons();
	}

	protected void submit()
	{
		UsbControlIrpPanel panel = null;

		try {
			for (int i=0; i<packetList.size(); i++) {
				panel = (UsbControlIrpPanel)packetList.get(i);
				panel.submit(usbDevice);
			}
		} catch ( UsbException uE ) {
			JOptionPane.showMessageDialog(null, "UsbException while submitting " + panel + " : " + uE.getMessage());
		} catch ( NumberFormatException nfE ) {
			JOptionPane.showMessageDialog(null, "NumberFormatException in " + panel + " : " + nfE.getMessage());
		}
	}

	protected void addPacket(UsbControlIrpPanel newPanel)
	{
		int index = packetJList.getSelectedIndex();
		packetList.add(newPanel);
		packetJList.setListData(packetList);
		requestPanel.add(newPanel, newPanel.toString());
		if (0 <= index)
			packetJList.setSelectedIndex(index);
		updateSelection();
		refreshButtons();
	}

	protected void copyPacket()
	{
		if (packetJList.isSelectionEmpty())
			return;

		addPacket((UsbControlIrpPanel)((UsbControlIrpPanel)packetList.get(packetJList.getSelectedIndex())).clone());
	}

	protected void removePacket()
	{
		int index = packetJList.getSelectedIndex();
		if (0 <= index) {
			packetList.remove(index);
			packetJList.setListData(packetList);
			if (packetList.size() <= index)
				index--;
			if (0 <= index)
				packetJList.setSelectedIndex(index);
			updateSelection();
		}
		refreshButtons();
	}

	protected void upPacket()
	{
		if (packetJList.isSelectionEmpty())
			return;

		int index = packetJList.getSelectedIndex();
		if (0 < index) {
			packetList.set(index, packetList.set(index-1, packetList.get(index)));
			packetJList.setListData(packetList);
			packetJList.setSelectedIndex(index-1);
			updateSelection();
		}
		refreshButtons();
	}

	protected void downPacket()
	{
		if (packetJList.isSelectionEmpty())
			return;

		int index = packetJList.getSelectedIndex();
		if (packetList.size() > (index+1)) {
			packetList.set(index, packetList.set(index+1, packetList.get(index)));
			packetJList.setListData(packetList);
			packetJList.setSelectedIndex(index+1);
			updateSelection();
		}
		refreshButtons();
	}

	protected void gotData(byte[] data)
	{
		for (int i=0; i<data.length; i++)
			outputTextArea.append(UsbUtil.toHexString(data[i]) + " ");
		outputTextArea.append("\n");
		outputTextArea.setCaretPosition(outputTextArea.getText().length());
		validate();
	}

	protected void gotError(UsbException uE)
	{
		JOptionPane.showMessageDialog(null, "Got UsbDeviceErrorEvent : " + uE.getMessage());
	}

	private JPanel clearPanel = new JPanel(new BorderLayout());
	private JTextArea outputTextArea = new JTextArea(3, 30);
	private JScrollPane outputScroll = new JScrollPane(outputTextArea);
	private Vector packetList = new Vector();

	private JList packetJList = new JList();
	private JPanel packetPanel = new JPanel();

	//private JScrollPane packetListScroll = new JScrollPane(packetPanel);
	private JScrollPane packetListScroll = new JScrollPane(packetJList);
	
	private JPanel requestPanel = new JPanel();
	private CardLayout requestLayout = new CardLayout();
	private JPanel emptyPanel = new JPanel();
	private static final String EMPTY_PANEL = "Empty Panel";

	private JButton clearButton = new JButton("Clear");
	private JButton submitButton = new JButton("Submit");
	private JButton newPacketButton = new JButton("New");
	private JButton copyPacketButton = new JButton("Copy");
	private JButton removeButton = new JButton("Remove");
	private JButton upButton = new JButton("Up");
	private JButton downButton = new JButton("Down");

	private ActionListener clearListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { outputTextArea.setText(""); } };
	private ActionListener submitListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { submit(); } };
	private ActionListener newPacketListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { addPacket(new UsbControlIrpPanel()); } };
	private ActionListener copyPacketListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { copyPacket(); } };
	private ActionListener removeListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { removePacket(); } };
	private ActionListener upListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { upPacket(); } };
	private ActionListener downListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { downPacket(); } };

	private ListSelectionListener packetListListener =
		new ListSelectionListener() { public void valueChanged(ListSelectionEvent lsE) { updateSelection(); } };

	private UsbDeviceListener deviceListener = new UsbDeviceListener() {
			public void dataEventOccurred(UsbDeviceDataEvent uddE) { gotData(uddE.getData()); }
			public void errorEventOccurred(UsbDeviceErrorEvent udeE) { gotError(udeE.getUsbException()); }
			public void usbDeviceDetached(UsbDeviceEvent udE) { udE.getUsbDevice().removeUsbDeviceListener(deviceListener); }
		};

	protected UsbDevice usbDevice = null;
}
