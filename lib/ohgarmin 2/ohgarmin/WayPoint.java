package ohgarmin;

public class WayPoint {
	private byte dtyp; /* data packet type (0x01 for D110) */
	private byte wpt_class; /* class */
	private byte dspl_color; /* display & color (see below) */
	private byte attr; /* attributes (0x80 for D110) */
	private short smbl; /* waypoint symbol */
	private byte[] subclass = new byte[18]; /* subclass */
	private double latitude; /* position */
	private double longitude;
	private float alt; /* altitude in meters */
	private float dpth; /* depth in meters */
	private float dist; /* proximity distance in meters */
	private char[] state; /* state */
	private char[] cc; /* country code */
	private int ete; /* outbound link ete in seconds */
	private float temp; /* temperature */
	private int time; /* timestamp */
	private short wpt_cat; /* category membership */
	/* char ident[]; variable length string */
	/* char comment[]; waypoint user comment */
	/* char facility[]; facility name */
	/* char city[]; city name */
	/* char addr[]; address number */
	/* char cross_road[]; intersecting road label */
	private char[] ident;
	private char[] comment;
	public WayPoint(){
		
		
	}
	
	public WayPoint(byte[] antwortByte){
		this.setDtyp(antwortByte[12]);
		this.setWpt_Class(antwortByte[13]);
		this.setDspl_Color(antwortByte[14]);
		this.setAttr(antwortByte[15]);
		this.setSmbl(Util.byteToShort(antwortByte, 16));
		byte[] subclass = new byte[18];
		for (int y = 18; y < 36; y++){
			subclass[0] = antwortByte[y];
		}
		this.setSubclass(subclass);
		this.setLatitude(Util.byteToInt(antwortByte, 36));
		this.setLongitude(Util.byteToInt(antwortByte, 40));
		this.setAlt(Util.byteToFloat(antwortByte, 44));
		this.setDpth(Util.byteToFloat(antwortByte, 48));
		this.setDist(Util.byteToFloat(antwortByte, 52));
		char[] statehelp = new char[2];
		char[] cchelp = new char[2];
		try{
			statehelp[0] = (char) antwortByte[56]; 
			statehelp[1] = (char) antwortByte[57];
			cchelp[0] = (char) antwortByte[58]; 
			cchelp[1] = (char) antwortByte[59];
			this.setState(statehelp);
			this.setCC(cchelp);
		}catch(NullPointerException ex){
			
		}
		this.setEte(Util.byteToInt(antwortByte, 60));
		this.setTemp(Util.byteToFloat(antwortByte, 64));
		this.setTime(Util.byteToInt(antwortByte, 68));
		this.setWpt_cat(Util.byteToShort(antwortByte, 72));
		int firstnull = Util.nullTerminator(antwortByte, 74);
		
		
		char[] helpIdent = new char[firstnull];
		for (int x = 0; x < firstnull; x++){
			helpIdent[x] = (char)antwortByte[x+74];
		}
		this.setIdent(helpIdent);
		int secondnull = Util.nullTerminator(antwortByte, (74+firstnull+1));
				
		
		char[] helpComment = new char[secondnull];
		for (int x = 0; x < secondnull; x++){
			helpComment[x] = (char)antwortByte[x+74+firstnull+1];
		}
		this.setComment(helpComment);
		//Debug: Util.showByteCode(antwortByte);
		//Debug: to_String();
	}
	
	public void setDtyp(byte dtyp){
		this.dtyp = dtyp;
	}
	
	public void setWpt_Class(byte wpt_class){
		this.wpt_class = wpt_class;
	}
	
	public void setDspl_Color(byte dspl_class){
		this.dspl_color = dspl_class;
	}
	
	public void setAttr(byte attr){
		this.attr = attr;
	}
	
	public void setSmbl(short smbl){
		this.smbl = smbl;
	}
	
	public void setSubclass (byte[] subclass){
		this.subclass = subclass;
	}
	
	public void setLatitude(double latitude){
		this.latitude = latitude;
	}
	
	public void setLongitude(double longitude){
		this.longitude = longitude;
	}
	
	public void setAlt(float alt){
		this.alt = alt;
	}
	
	public void setDpth(float dpth){
		this.dpth = dpth;
	}
	
	public void setDist(float dist){
		this.dist = dist;
	}
	
	public void setState(char[] state){
		this.state = state;
	}
	
	public void setCC(char[] cc){
		this.cc = cc;
	}
	
	public void setEte(int ete){
		this.ete = ete;
	}
	
	public void setTemp(float temp){
		this.temp = temp;
	}
	
	public void setTime(int time){
		this.time = time;
	}
	
	
	public void setWpt_cat(short wpt_cat){
		this.wpt_cat = wpt_cat;
	}
	
	public void setIdent(char[] ident){
		this.ident = ident;
	}
	
	public void setComment(char[] comment){
		this.comment = comment;
	}
	
	//getter
	
	public byte getDtyp(){
		return this.dtyp;
	}
	
	public byte getWpt_Class(){
		return this.wpt_class;
	}
	
	public byte getDspl_Color(){
		return this.dspl_color;
	}
	
	public byte getAttr(){
		return this.attr;
	}
	
	public short getSmbl(){
		return this.smbl;
	}
	
	public byte[] getSubclass (){
		return this.subclass;
	}
	
	public double getLatitude(){
		return this.latitude;
	}
	
	public double getLongitude(){
		return this.longitude;
	}
	
	public float getAlt(){
		return this.alt;
	}
	
	public float getDpth(){
		return this.dpth;
	}
	
	public float getDist(){
		return this.dist;
	}
	
	public String getState(){
		return new String(this.state);
	}
	
	public String getCC(){
		return new String(this.cc);
	}
	
	public int getEte(){
		return this.ete;
	}
	
	public float getTemp(){
		return this.temp;
	}
	
	public int getTime(){
		return this.time;
	}
	
	public short getWpt_cat(){
		return this.wpt_cat;
	}
	
	public String getIdent(){
		return new String(this.ident);
	}
	
	public String getComment(){
		return new String(this.comment);
	}
	
	public void to_String(){
		System.out.println("**********************************************");
		System.out.println("Dytp: "+this.getDtyp());
		System.out.println("Wpt_class: "+this.getWpt_Class());
		System.out.println("Dspl-Color: "+this.getDspl_Color());
		System.out.println("Attr: "+this.getAttr());
		System.out.println("Smbl: "+this.getSmbl());
		System.out.println("Latitude: "+Util.LatHelp(Util.semicirclesToDegree(this.getLatitude())));
		System.out.println("Longitude: "+Util.LongHelp(Util.semicirclesToDegree(this.getLongitude())));
		System.out.println("Alt: "+this.getAlt()+ " m");
		System.out.println("Dpth: "+this.getDpth()+" m");
		System.out.println("Dist: "+this.getDist()+" m");
		System.out.println("State: "+this.getState());
		System.out.println("CC: "+this.getCC());
		System.out.println("Ete: "+this.getEte());
		System.out.println("Temp: "+this.getTemp()+" �C");
		System.out.println("Time: "+this.getTime()+" sek");
		System.out.println("Wpt_cat: "+this.getWpt_cat());
		System.out.println("ident: "+this.getIdent());
		System.out.println("comment: "+this.getComment());
		System.out.println("**********************************************");
	}
	
	
	//Generiert aus WayPoint-Objekt ein BytePacket
	public byte[] createBytePacket(int laenge){
	
		byte[] standard = new byte[laenge];
		standard[0] = this.dtyp;
		standard[1] = this.wpt_class;
		standard[2] = this.dspl_color;
		standard[3] = this.attr;
		byte[] help = new byte[2];
		help = Util.shortTobyte(this.smbl);
		standard[4] = help[0];
		standard[5] = help[1];
		
		for (int x = 6; x < 24; x ++){
			standard[x] = this.subclass[x-6];
		}
		byte[] helpLat = Util.intTobyte(new Double(this.latitude).intValue());
		standard[24] = (byte) helpLat[0];
		standard[25] = (byte) helpLat[1];
		standard[26] = (byte) helpLat[2];
		standard[27] = (byte) helpLat[3];
		
		byte[] helpLong = Util.intTobyte(new Double(this.longitude).intValue());
		
		standard[28] = (byte) helpLong[0];
		standard[29] = (byte) helpLong[1];
		standard[30] = (byte) helpLong[2];
		standard[31] = (byte) helpLong[3];
		
		help = Util.intTobyte(new Float(this.alt).intValue());
		standard[32] = (byte) help[0];
		standard[33] = (byte) help[1];
		standard[34] = (byte) help[2];
		standard[35] = (byte) help[3];
		
		help = Util.intTobyte(new Float(this.dpth).intValue());
		standard[36] = (byte) help[0];
		standard[37] = (byte) help[1];
		standard[38] = (byte) help[2];
		standard[39] = (byte) help[3];
		
		help = Util.intTobyte(new Float(this.dist).intValue());
		standard[40] = (byte) help[0];
		standard[41] = (byte) help[1];
		standard[42] = (byte) help[2];
		standard[43] = (byte) help[3];
	
		char[] help2 = new char[2];
		help2 = this.state;
		
		standard[44] = (byte)help2[0];
		standard[45] = (byte)help2[1];
		
		help2 = this.cc;
		standard[46] = (byte)help2[0];
		standard[47] = (byte)help2[1];
		
		
		help = Util.intTobyte(new Float(this.ete).intValue());
		standard[48] = (byte) help[0];
		standard[49] = (byte) help[1];
		standard[50] = (byte) help[2];
		standard[51] = (byte) help[3];
		
		help = Util.intTobyte(new Float(this.temp).intValue());
		standard[52] = (byte) help[0];
		standard[53] = (byte) help[1];
		standard[54] = (byte) help[2];
		standard[55] = (byte) help[3];
		
		help = Util.intTobyte(this.time);
		standard[56] = (byte) help[0];
		standard[57] = (byte) help[1];
		standard[58] = (byte) help[2];
		standard[59] = (byte) help[3];
		
		help = Util.shortTobyte(this.wpt_cat);
		standard[60] = help[0];
		standard[61] = help[1];
		for (int x = 62; x < 62+this.ident.length; x ++){
			standard[x] = (byte) this.ident[x-62];
		}
		
		for (int x = 62+this.ident.length; x < 62+this.ident.length+this.comment.length; x++ ){
			standard[x] = (byte) this.comment[x-(62+this.ident.length)];
		}
		
		//Util.showByteCode(standard,laenge);
		return standard;
		
	}
	
}
