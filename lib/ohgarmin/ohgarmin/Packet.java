package ohgarmin;

//Klasse wo ein Packet erstellt wird 
//Alle Commands & Link-Protocole sind integriert
public class Packet {
//	USB Protocol Layer Packet Id
	public short 
	Pid_Data_Available = 2,
	Pid_Start_Session = 5,
	Pid_Session_Started = 6,
    // protocol L000
	Pid_Protocol_Array = 253, /* may not be implemented in all devices */
	Pid_Product_Rqst = 254,
	Pid_Product_Data = 255,
	Pid_Ext_Product_Data = 248, /* may not be implemented in all devices */
    // protocol L001
    Pid_Command_Data = 10,
    Pid_Xfer_Cmplt = 12,
    Pid_Date_Time_Data = 14,
    Pid_Position_Data = 17,
    Pid_Prx_Wpt_Data = 19,
    Pid_Records = 27,
    Pid_Rte_Hdr = 29,
    Pid_Rte_Wpt_Data = 30,
    Pid_Almanac_Data = 31,
    Pid_Trk_Data = 34,
    Pid_Wpt_Data = 35,
    Pid_Pvt_Data = 51,
    Pid_Rte_Link_Data = 98,
    Pid_Trk_Hdr = 99,
    Pid_FlightBook_Record = 134,
    Pid_Lap = 149,
    Pid_Wpt_Cat = 152,
    Pid_Run = 990,
    Pid_Workout = 991,
    Pid_Workout_Occurrence = 992,
    Pid_Fitness_User_Profile = 993,
    Pid_Workout_Limits = 994,
    Pid_Course = 1061,
    Pid_Course_Lap = 1062,
    Pid_Course_Point = 1063,
    Pid_Course_Trk_Hdr = 1064,
    Pid_Course_Trk_Data = 1065,
    Pid_Course_Limits = 1066,
    // protocol L002
    // Note: some packet ids are duplicated from L002, and some
    // ids are the same name but different values from L001.
	Pid_Almanac_Data2 = 4,
	Pid_Command_Data2 = 11,
	Pid_Xfer_Cmplt2 = 12,
	Pid_Date_Time_Data2 = 20,
	Pid_Position_Data2 = 24,
	Pid_Prx_Wpt_Data2 = 27,
	Pid_Records2 = 35,
	Pid_Rte_Hdr2 = 37,
	Pid_Rte_Wpt_Data2 = 39,
	Pid_Wpt_Data2 = 43,
    //A010
	Cmnd_Abort_Transfer = 0, /* abort current transfer */
	Cmnd_Transfer_Alm = 1, /* transfer almanac */
	Cmnd_Transfer_Posn = 2, /* transfer position */
	Cmnd_Transfer_Prx = 3, /* transfer proximity waypoints */
	Cmnd_Transfer_Rte = 4, /* transfer routes */
	Cmnd_Transfer_Time = 5, /* transfer time */
	Cmnd_Transfer_Trk = 6, /* transfer track log */
	Cmnd_Transfer_Wpt = 7, /* transfer waypoints */
	Cmnd_Turn_Off_Pwr = 8, /* turn off power */
	Cmnd_Start_Pvt_Data = 49, /* start transmitting PVT data */
	Cmnd_Stop_Pvt_Data = 50, /* stop transmitting PVT data */
	Cmnd_FlightBook_Transfer = 92, /* transfer flight records */
	Cmnd_Transfer_Laps = 117, /* transfer fitness laps */
	Cmnd_Transfer_Wpt_Cats = 121, /* transfer waypoint categories */
	Cmnd_Transfer_Runs = 450, /* transfer fitness runs */
	Cmnd_Transfer_Workouts = 451, /* transfer workouts */
	Cmnd_Transfer_Workout_Occurrences = 452, /* transfer workout occurrences */
	Cmnd_Transfer_Fitness_User_Profile = 453, /* transfer fitness user profile */
	Cmnd_Transfer_Workout_Limits = 454, /* transfer workout limits */
	Cmnd_Transfer_Courses = 561, /* transfer fitness courses */
	Cmnd_Transfer_Course_Laps = 562, /* transfer fitness course laps */
	Cmnd_Transfer_Course_Points = 563, /* transfer fitness course points */
	Cmnd_Transfer_Course_Tracks = 564, /* transfer fitness course tracks */
	Cmnd_Transfer_Course_Limits = 565, /* transfer fitness course limits */
	//A011
	Cmnd_Abort_Transfer2 = 0, /* abort current transfer */
	Cmnd_Transfer_Alm2 = 4, /* transfer almanac */
	Cmnd_Transfer_Rte2 = 8, /* transfer routes */
	Cmnd_Transfer_Prx2 = 17, /* transfer proximity waypoints */
	Cmnd_Transfer_Time2 = 20, /* transfer time */
	Cmnd_Transfer_Wpt2 = 21, /* transfer waypoints */
	Cmnd_Turn_Off_Pwr2 = 26; /* turn off power */
	
	//Packetid bitte in Hex eingeben
	public byte[] createPacket(short packetid, byte[] packetdata){
		short s = 0;
		byte[] packet;
		int size = packetdata.length;
		
		if (size < 1){
			packet = new byte[12];
		}else{
			packet = new byte[12+size];
		}
		if (packetid == 2 || packetid == 5 || packetid == 6){
			packet[0] = (byte) 0x00;
		} else packet[0] = (byte) 0x14;
		//Reserved
		packet[1] = (byte) 0x00;
		packet[2] = (byte) 0x00;
	    packet[3] = (byte) 0x00;
	    //PacketId
	    s = (byte) packetid;
	    byte[] packet_2 = Util.shortTobyte(s);
	    packet[4] = packet_2[0];
	    packet[5] = packet_2[1];
		//Reserved2
		packet[6] = (byte) 0x00;
	    packet[7] = (byte) 0x00;
	    //DataSize
	    byte[] packet2 = Util.intTobyte(size);
	    packet[8] = packet2[0];
	    packet[9] = packet2[1];
	    packet[10] = packet2[2];
	    packet[11] = packet2[3];
	    for (int x = 0; x < size; x++){
	    	packet[12+x] = packetdata[x];
	    }
	    
	    return packet;
	}
}
