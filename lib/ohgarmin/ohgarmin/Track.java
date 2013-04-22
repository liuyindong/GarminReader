package ohgarmin;

public class Track {
	private double latitude;
	private double longitude;
	private int time;
	private float alt;
	private float dpth;
	private float temp;
	private byte trackseg;
	public Track(){
		/*typedef struct
		{
		position_type posn; /* position
		time_type time; /* time 
		float32 alt; /* altitude in meters 
		float32 dpth; /* depth in meters 
		bool new_trk; /* new track segment?
		} D301_Trk_Point_Type;
	*/
	}
	
	public Track(byte[] b){

		this.setLatitude(Util.semicirclesToDegree(Util.byteToInt(b, 12)));
		this.setLongitude(Util.semicirclesToDegree(Util.byteToInt(b, 16)));
		this.setTime(Util.byteToInt(b, 20));
		this.setAlt(Util.byteToFloat(b, 24));
		this.setDpth(Util.byteToFloat(b, 28));
		this.setTemp(Util.byteToFloat(b, 32));
		this.setNewTrack(b[36]);
	}
	
	public void setLatitude(double latitude){
		this.latitude = latitude;
	}
	
	public void setTime(int time){
		this.time = time;
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
	
	public void setTemp(float temp){
		this.temp = temp;
	}
	public void setNewTrack(byte next){
		this.trackseg = next;
	}
	
	public double getLongitude(){
		return Util.semicirclesToDegree(this.longitude);
	}
	
	public double getLatitude(){
		return Util.semicirclesToDegree(this.latitude);
	}
	
	public int getTime(){
		return this.time;
	}
	
	public float getAlt(){
		return this.alt;
	}
	
	public float getDpth(){
		return this.dpth;
	}
	
	public float getTemp(){
		return this.temp;
	}
	
	public byte getNextTrack(){
		return this.trackseg;
	}
	
	public String toString(){
		return ("Latitude: "+this.getLatitude() + " Longitude: "+this.getLongitude()+" Time: "+this.getTime()+" sekunden");
	}
}
