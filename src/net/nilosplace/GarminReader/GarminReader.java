package net.nilosplace.GarminReader;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import dk.itu.haas.GPS.IDate;
import dk.itu.haas.GPS.IGPSlistener;
import dk.itu.haas.GPS.IPosition;
import dk.itu.haas.GPS.ITime;
import dk.itu.haas.GPS.Garmin.GarminGPS;

public class GarminReader extends Thread implements IGPSlistener {
	
	private GregorianCalendar cal;
	private SimpleDateFormat format;
	private int indent = 0;
	GarminGPS gps;
	JTextArea output;

	
	private BufferedWriter bw;
	//private int lastSecond = -1;
	//private int currentSecond = 0;
	
	public GarminReader() {
		
        CommPortIdentifier port;
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        cal = new GregorianCalendar(new Locale("en"));
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.ZONE_OFFSET, 0);
        cal.set(Calendar.DST_OFFSET, 0);
        JFrame frame = new JFrame("Garmin Reader");
        frame.setMinimumSize(new Dimension(500,600));
        JLabel label = new JLabel("Test Label");
        frame.getContentPane().add(label);
        output = new JTextArea();
        output.setAutoscrolls(true);
        JScrollPane jscroll = new JScrollPane(output);
        frame.getContentPane().add(jscroll, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			    exitProcedure();
			}
		});
        frame.pack();
        frame.setVisible(true);

		try {
			Calendar filecal = Calendar.getInstance();
			String filename = filecal.getTime().toString().replaceAll(" ", "_").replaceAll(":", "_");
			FileWriter fw = new FileWriter("files/" + filename + ".gpx");
	        bw = new BufferedWriter(fw);
	        DoFileHeader();
			port = CommPortIdentifier.getPortIdentifier("COM3");
			SerialPort port2 = (SerialPort)port.open("ComControl", 2000);
            port2.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
            port2.setDTR(true);
            port2.setRTS(true);
			gps = new GarminGPS(new BufferedInputStream(port2.getInputStream()), new BufferedOutputStream(port2.getOutputStream()));
			gps.addGPSlistener(this);
			//gps.addGarminListener(this);
            gps.setAutoTransmit(true);
            gps.requestDate();
            gps.requestPosition();
            gps.run();
            
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void DoFileHeader() {
    	write("<?xml version=\"1.0\" standalone=\"yes\"?>");
    	write("<gpx");
    	indent();
    	write("xmlns=\"http://www.topografix.com/GPX/1/0\"");
    	write("version=\"1.0\" creator=\"TopoFusion 3.15\"");
    	write("xmlns:TopoFusion=\"http://www.TopoFusion.com\"");
    	write("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
    	write("xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.TopoFusion.com http://www.TopoFusion.com/topofusion.xsd\">");
    	write("<bounds maxlat=\"0.000000\" minlon=\"0.000000\" minlat=\"0.000000\" maxlon=\"0.000000\"/>");
    	write("<trk>");
    	indent();
    	write("<trkseg>");
    	indent();
	}
    
    private void DoFileFooter() {
    	unindent();
    	write("</trkseg>");
    	unindent();
    	write("</trk>");
    	unindent();
    	write("</gpx>");
    	try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	public static void main(String[] args) {
		GarminReader reader = new GarminReader();
		//Runtime.getRuntime().addShutdownHook(reader);
		//reader.run();
    }

	public void dateReceived(IDate arg0) {
		cal.set(Calendar.YEAR, arg0.getYear());
		cal.set(Calendar.MONTH, arg0.getMonth() - 1);
		cal.set(Calendar.DAY_OF_MONTH, arg0.getDay());
	}

	public void positionReceived(IPosition arg0) {
		String time = this.format.format(cal.getTime());
		double lat = arg0.getLatitude().getDegrees() + (arg0.getLatitude().getMinutes() / 60);
		double lon = arg0.getLongitude().getDegrees() + (arg0.getLongitude().getMinutes() / 60);
		float alt = arg0.getAltitude();
		if(alt == 0.0) return;
		
		write("<trkpt lat=\"" + lat + "\" lon=\"" + lon + "\">");
		indent();
		write("<ele>" + alt + "</ele>");
		write("<time>" + time + "</time>");
		unindent();
		write("</trkpt>");
		String line = time + " " + lat + " " + lon + " " + alt + "\n";
		output.append(line);
		output.setCaretPosition(output.getCaretPosition()+line.length());
	}

	public void timeReceived(ITime arg0) {
		cal.set(Calendar.HOUR_OF_DAY, arg0.getHours());
		cal.set(Calendar.MINUTE, arg0.getMinutes());
		cal.set(Calendar.SECOND, arg0.getSeconds());
		//currentSecond = arg0.getSeconds();
	}
	
	private void write(String line) {
		try {
			for(int i = 0; i < indent; i++) {
				bw.write("\t");
			}
			bw.write(line + "\r\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void unindent() {
		indent--;
	}
	private void indent() {
		indent++;
	}
	private void exitProcedure() {
		gps.shutdown(false);
		DoFileFooter();
		System.exit(0);
	}
}
