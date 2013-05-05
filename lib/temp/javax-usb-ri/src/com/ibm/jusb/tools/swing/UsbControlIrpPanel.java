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
 * Class to display UsbControlIrp information.
 * @author Dan Streetman
 */
public class UsbControlIrpPanel extends JPanel implements Cloneable
{
	public UsbControlIrpPanel()
	{
		setLayout( new BorderLayout());

		refreshButton.addActionListener(refreshListener);
		clearButton.addActionListener(clearListener);
		resetButton.addActionListener(resetListener);

		lengthCheckBox.addChangeListener(lengthListener);

		offsetField.setText("0");
		lengthField.setText("0");
		lengthCheckBox.setSelected(false);

		JPanel setupPacketPanel = new JPanel( new GridLayout(4,2,6,6));
		setupPacketPanel.add(bmRequestTypeLabel);
		setupPacketPanel.add(bmRequestTypeField);
		setupPacketPanel.add(bRequestLabel);
		setupPacketPanel.add(bRequestField);
		setupPacketPanel.add(wValueLabel);
		setupPacketPanel.add(wValueField);
		setupPacketPanel.add(wIndexLabel);
		setupPacketPanel.add(wIndexField);
		
		JPanel panel = new JPanel( new BorderLayout());
		panel.add(setupPacketPanel, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createEmptyBorder(2,2,4,2));
		
		selectionPanel.add(syncCheckBox);
		selectionPanel.add(syncCheckBox);
		selectionPanel.add(offsetLabel);
		selectionPanel.add(offsetField);
		selectionPanel.add(lengthCheckBox);
		selectionPanel.add(lengthField);
		selectionPanel.add(acceptShortCheckBox);

		JPanel buttonsPanel = new JPanel();
		JPanel insidePanel = new JPanel( new GridLayout(1,3,8,2));
		insidePanel.add(refreshButton);
		insidePanel.add(clearButton);
		insidePanel.add(resetButton);
		buttonsPanel.add(insidePanel);

		JPanel rightPanel = new JPanel(new BorderLayout());

		rightPanel.add(selectionPanel, BorderLayout.NORTH);
		rightPanel.add(packetDataScroll, BorderLayout.CENTER);
		rightPanel.add(buttonsPanel, BorderLayout.SOUTH);
		rightPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,5));

		add(panel, BorderLayout.WEST);
		add(rightPanel, BorderLayout.CENTER);
	}

	public String toString() { return "Buffer @" + UsbUtil.toHexString(hashCode()); }

	public Object clone()
	{
		UsbControlIrpPanel newPanel = new UsbControlIrpPanel();
		newPanel.syncCheckBox.setSelected(syncCheckBox.isSelected());
		newPanel.packetDataTextArea.setText(packetDataTextArea.getText());
		newPanel.bmRequestTypeField.setText(bmRequestTypeField.getText());
		newPanel.bRequestField.setText(bRequestField.getText());
		newPanel.wIndexField.setText(wIndexField.getText());
		newPanel.wValueField.setText(wValueField.getText());
		newPanel.offsetField.setText(offsetField.getText());
		newPanel.lengthField.setText(lengthField.getText());
		return newPanel;
	}

	public void submit(UsbDevice device) throws UsbException,NumberFormatException
	{
		lastData = getData();

		byte bmRequestType = (byte)Integer.decode(bmRequestTypeField.getText()).intValue();
		byte bRequest = (byte)Integer.decode(bRequestField.getText()).intValue();
		short wValue = (short)Integer.decode(wValueField.getText()).intValue();
		short wIndex = (short)Integer.decode(wIndexField.getText()).intValue();
		DefaultUsbControlIrp usbControlIrp = new DefaultUsbControlIrp(bmRequestType, bRequest, wValue, wIndex);
		usbControlIrp.setData(lastData);
		usbControlIrp.setOffset(getOffset());
		usbControlIrp.setLength(getLength(lastData));
		usbControlIrp.setAcceptShortPacket(acceptShortCheckBox.isSelected());

		if (syncCheckBox.isSelected())
			device.syncSubmit(usbControlIrp);
		else
			device.asyncSubmit(usbControlIrp);
	}

	protected byte[] getData()
	{
		java.util.List list = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(packetDataTextArea.getText());
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			byte b;
			try {
				b = (byte)Integer.decode(token).intValue(); /* truncate without error anything greater than 1 byte */
			} catch ( NumberFormatException nfE ) {
				/* If character specified as 'X' or "X", then use the raw character specified */
				if ((3 == token.length()) && ((0x22 == token.charAt(0) && 0x22 == token.charAt(2)) || (0x27 == token.charAt(0) && 0x27 == token.charAt(2))))
					b = (byte)token.charAt(1);
				else
					throw nfE;
			}
			list.add(new Byte(b));
		}

		byte[] data = new byte[list.size()];
		for (int i=0; i<data.length; i++)
			data[i] = ((Byte)list.get(i)).byteValue();

		return data;
	}

	protected int getOffset()
	{
		return Integer.decode(offsetField.getText()).intValue();
	}

	protected int getLength(byte[] data)
	{
		if (lengthCheckBox.isSelected())
			return Integer.decode(lengthField.getText()).intValue();
		else
			return data.length;
	}

	protected void refresh()
	{
		if (null == lastData)
			return;

		if (!Arrays.equals(lastData, getData())) {
			packetDataTextArea.setText("");
			for (int i=0; i<lastData.length; i++)
				packetDataTextArea.append("0x" + UsbUtil.toHexString(lastData[i]) + " ");
		}
	}

	protected void clear()
	{
		packetDataTextArea.setText("");
	}
	
	protected void reset() {
		packetDataTextArea.setText("");
		offsetField.setText("0");
		lengthField.setText("0");
		bmRequestTypeField.setText("0x00");
		bRequestField.setText("0x00");
		wValueField.setText("0x0000");
		wIndexField.setText("0x0000");
		syncCheckBox.setSelected(true);
		acceptShortCheckBox.setSelected(true);
		lengthCheckBox.setSelected(false);
	}

	protected void lengthSelectionChanged()
	{
		lengthField.setEnabled(lengthCheckBox.isSelected());
	}

	private JPanel packetOptionsPanel = new JPanel();
	protected JCheckBox syncCheckBox = new JCheckBox("Sync", true);
	private JLabel offsetLabel = new JLabel("Offset");
	protected JTextField offsetField = new JTextField(4);
	protected JCheckBox lengthCheckBox = new JCheckBox("Length", true);
	protected JTextField lengthField = new JTextField(4);
	protected JCheckBox acceptShortCheckBox = new JCheckBox("AcceptShort", true);
	private JPanel selectionPanel = new JPanel();
	private Vector requestTypeVector = new Vector();
	private Box bmRequestTypeBox = new Box(BoxLayout.X_AXIS);
	private JLabel bmRequestTypeLabel = new JLabel("bmReqType");
	private JPanel bmRequestTypePanel = new JPanel();
	protected JTextField bmRequestTypeField = new JTextField("0x00", 4);
	private Box bRequestBox = new Box(BoxLayout.X_AXIS);
	private JLabel bRequestLabel = new JLabel("bRequest");
	private JPanel bRequestPanel = new JPanel();
	protected JTextField bRequestField = new JTextField("0x00", 4);
	private Box wValueBox = new Box(BoxLayout.X_AXIS);
	private JLabel wValueLabel = new JLabel("wValue");
	private JPanel wValuePanel = new JPanel();
	protected JTextField wValueField = new JTextField("0x0000", 6);
	private Box wIndexBox = new Box(BoxLayout.X_AXIS);
	private JLabel wIndexLabel = new JLabel("wIndex");
	private JPanel wIndexPanel = new JPanel();
	protected JTextField wIndexField = new JTextField("0x0000", 6);
	private JButton refreshButton = new JButton("Refresh");
	private JButton clearButton = new JButton("Clear Data");
	protected JTextArea packetDataTextArea = new JTextArea(3, 25);
	private JScrollPane packetDataScroll = new JScrollPane(packetDataTextArea);
	private JButton resetButton = new JButton( "Reset All" );

	private byte[] lastData = null;

	private ActionListener refreshListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { refresh(); } };
	private ActionListener clearListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { clear(); } };
	private ActionListener resetListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { reset(); } };

	private ChangeListener lengthListener =
		new ChangeListener() { public void stateChanged(ChangeEvent cE) { lengthSelectionChanged(); } };
}
