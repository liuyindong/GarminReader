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
 * Class to display UsbIrp (or raw byte[]) information.
 * @author Dan Streetman
 */
public class UsbIrpPanel extends JPanel implements Cloneable
{
	public UsbIrpPanel()
	{
		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setLayout( new BorderLayout());

		irpCheckBox.addChangeListener(irpListener);
		lengthCheckBox.addChangeListener(lengthListener);
		refreshButton.addActionListener(refreshListener);
		clearButton.addActionListener(clearListener);

		offsetField.setText("0");
		lengthField.setText("0");
		lengthCheckBox.setSelected(false);

		packetOptionsPanel.add(syncCheckBox);
		packetOptionsPanel.add(irpCheckBox);
		packetOptionsPanel.add(offsetLabel);
		packetOptionsPanel.add(offsetField);
		packetOptionsPanel.add(lengthCheckBox);
		packetOptionsPanel.add(lengthField);
		packetOptionsPanel.add(acceptShortCheckBox);
		
		buttonPanel.add(refreshButton);
		buttonPanel.add(clearButton);

		add(packetOptionsPanel, BorderLayout.NORTH);
		add(packetDataScroll, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

	}

	public String toString() { return "Buffer @" + UsbUtil.toHexString(hashCode()); }

	public Object clone()
	{
		UsbIrpPanel newPanel = new UsbIrpPanel();
		newPanel.syncCheckBox.setSelected(syncCheckBox.isSelected());
		newPanel.irpCheckBox.setSelected(irpCheckBox.isSelected());
		newPanel.acceptShortCheckBox.setSelected(acceptShortCheckBox.isSelected());
		newPanel.acceptShortCheckBox.setEnabled(acceptShortCheckBox.isEnabled());
		newPanel.packetDataTextArea.setText(packetDataTextArea.getText());
		return newPanel;
	}

	public void submit(UsbPipe pipe) throws UsbException,NumberFormatException
	{
		lastData = getData();

		if (irpCheckBox.isSelected()) {
			DefaultUsbIrp irp = new DefaultUsbIrp();
			irp.setData(lastData);
			irp.setOffset(getOffset());
			irp.setLength(getLength(lastData));
			irp.setAcceptShortPacket(acceptShortCheckBox.isSelected());
			if (syncCheckBox.isSelected())
				pipe.syncSubmit(irp);
			else
				pipe.asyncSubmit(irp);
		} else {
			if (syncCheckBox.isSelected())
				pipe.syncSubmit(lastData);
			else
				pipe.asyncSubmit(lastData);
		}
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

	protected void irpSelectionChanged()
	{
		if (irpCheckBox.isSelected()) {
			offsetField.setEnabled(true);
			lengthCheckBox.setEnabled(true);
			lengthField.setEnabled(lengthCheckBox.isSelected());
			acceptShortCheckBox.setEnabled(true);
		} else {
			offsetField.setEnabled(false);
			lengthCheckBox.setEnabled(false);
			lengthField.setEnabled(false);
			acceptShortCheckBox.setEnabled(false);
		}
	}

	protected void lengthSelectionChanged()
	{
		if (irpCheckBox.isSelected())
			lengthField.setEnabled(lengthCheckBox.isSelected());
	}

	private JPanel packetOptionsPanel = new JPanel();
	protected JCheckBox syncCheckBox = new JCheckBox("Sync", true);
	protected JCheckBox irpCheckBox = new JCheckBox("UsbIrp", true);
	protected JCheckBox acceptShortCheckBox = new JCheckBox("AcceptShort", true);
	private JLabel offsetLabel = new JLabel("Offset");
	protected JTextField offsetField = new JTextField(4);
	protected JCheckBox lengthCheckBox = new JCheckBox("Length", true);
	protected JTextField lengthField = new JTextField(4);
	private JPanel buttonPanel = new JPanel();
	private JButton refreshButton = new JButton("Refresh");
	private JButton clearButton = new JButton("Clear");
	protected JTextArea packetDataTextArea = new JTextArea();
	private JScrollPane packetDataScroll = new JScrollPane(packetDataTextArea);

	private byte[] lastData = null;

	private ActionListener refreshListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { refresh(); } };
	private ActionListener clearListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { clear(); } };

	private ChangeListener irpListener =
		new ChangeListener() { public void stateChanged(ChangeEvent cE) { irpSelectionChanged(); } };
	private ChangeListener lengthListener =
		new ChangeListener() { public void stateChanged(ChangeEvent cE) { lengthSelectionChanged(); } };

}
