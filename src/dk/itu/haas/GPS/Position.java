package dk.itu.haas.GPS;

/**
* This is a class meant for containing positions. 
*/

public class Position implements IPosition {
	private PositionRadians lat, lon;
	private float alt;
	
	/**
	* Makes a new position. Initializes the latitude and the longitude to 0.
	*/
	public Position() {
		this(0,0,0);
	}
	
	/** 
	* Initializes the Position with la as the latitude and lo as the longitude.
	*/
	public Position(double la, double lo, float al) {
		lat = new PositionRadians(la);
		lon = new PositionRadians(lo);
		alt = al;
	}
	
	/**
	* Initializes the position object from an IPosition reference.
	*/
	public Position(IPosition pos) {
		lat = pos.getLatitude();
		lon = pos.getLongitude();
	}
	
	/**
	* Sets the latitude of this position.
	*/
	public void setLatitude(PositionRadians l) {
		lat = l;
	}

	/**
	* Sets the longitude of this position.
	*/
	public void setLongitude(PositionRadians l) {
		lon = l;
	}
	
	/**
	* Returns the latitude of this position.
	*/
	public PositionRadians getLatitude() {
		return lat;
	}
	
	/**
	* Returns the longitude of this position.
	*/
	public PositionRadians getLongitude() {
		return lon;
	}

	public float getAltitude() {
		// TODO Auto-generated method stub
		return 0;
	}
}