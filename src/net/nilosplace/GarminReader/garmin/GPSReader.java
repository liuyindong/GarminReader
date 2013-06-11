package net.nilosplace.GarminReader.garmin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GPSReader extends JFrame {
	private JTextArea output;
	private LogicHandler lh;
	private JLabel label;
	private JLabel label2;
	private JLabel label3;
	
	public static void main(String[] args) {
		GPSReader gps = new GPSReader();
	}
	
	public GPSReader() {
		super("Garmin Reader");
		lh = new LogicHandler();
		lh.setGUI(this);
		setMinimumSize(new Dimension(500,300));

		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		label = new JLabel("Test Label");
		label2 = new JLabel("Test Label2");
		label3 = new JLabel("Test Label3");
		
		panel.add(label);
		panel.add(label2);
		panel.add(label3);
		JButton productInfo = new JButton("Info");
		productInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lh.getProductInfo();
			}
		});
		panel.add(productInfo);
		JButton startData = new JButton("Start");
		startData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lh.startData();
			}
		});
		panel.add(startData);
		JButton stopData = new JButton("Stop");
		stopData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lh.stopData();
			}
		});
		panel.add(stopData);
		
		JButton getpacket = new JButton("Get Packet");
		getpacket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lh.getPacket();
			}
		});
		panel.add(getpacket);
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitProcedure();
			}
		});
		panel.add(close);
		
		output = new JTextArea();
		output.setAutoscrolls(true);
		JScrollPane jscroll = new JScrollPane(output);
		jscroll.setPreferredSize(new Dimension(500, 150));
		panel2.add(jscroll, BorderLayout.PAGE_END);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitProcedure();
			}
		});
		add(panel, BorderLayout.NORTH);
		add(panel2, BorderLayout.SOUTH);
		setVisible(true);
		lh.init();
	}

	private void exitProcedure() {
		lh.close();
		System.exit(0);
	}

	public void setProductInfo(String info) {
		label.setText(info);
	}
	
	public void setCurrnetLocation(byte[] location) {
		
	}
}
