package ohgarmin;
import java.util.*;
import java.io.*;
import java.awt.*;
public interface Int_WegPunkte {
	public void addWayPoint(double lati, double longi);
	public void removeWayPoint(int Index);
	public void showWayPoints(Graphics g);
	public void saveWayPoints();
	public void listWayPoints();
}
