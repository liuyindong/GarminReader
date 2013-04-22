package ohgarmin;

import java.util.*;

public class testprog {

	//TestDemo
	public static void main(String[] args){
		GarminApi test = new GarminApi();
		//Create WayPoint - take the first 3 chars of the input param.
		/*char[] id = new char[3];
		id[0] = args[0].charAt(0);
		id[1] = args[0].charAt(1);
		id[2] = args[0].charAt(2);*/
		//test.createWayPoint(id, Util.degreeToSemicircles(48.5), Util.degreeToSemicircles(16.4));
		//Get All WayPoints - use the toString-Method of WayPoints
		//System.out.println(test.getWayPoints());
		//get TrackRouteList - use the toString - Method so we will get the id's of the TrackList
		Vector ts = test.getTrackRoute();
		
		for (int x = 0; x < ts.size(); x++){
			TrackListe tk = (TrackListe) ts.elementAt(x);
			for (int y = 0; y < tk.getSize(); y++){
				System.out.println(tk.getTrack(y).toString());
			}
			
		}
		//get ProductData
		//System.out.println(test.getProductData());
		//get ProtocolData
		//System.out.println(test.getProtocolData()); 
		//Turn Off the Device
		//test.TurnOffPower();
	}
}
