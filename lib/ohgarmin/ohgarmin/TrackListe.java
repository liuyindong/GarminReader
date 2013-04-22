package ohgarmin;
import java.util.*;
public class TrackListe {
	/*typedef struct
	{
	 char rte_ident[]; variable length string 
	} D202_Rte_Hdr_Type;
	*/
	
	private Vector<Track> Trackliste;
	private String ident;
	
	public TrackListe(byte[] b){
		 this.ident = new String(b,12,Util.nullTerminator(b, 12));
		 Trackliste = new Vector<Track>();
	}
	
	public void add(Track track){
		Trackliste.add(track);
	}
	
	public void del(int index){
		Trackliste.removeElementAt(index);
	}
	
	public int getSize(){
		return Trackliste.size();
	}
	
	public Track getTrack(int index){
		return (Track)Trackliste.elementAt(index);
	}
	
	public String toString(){
		return "Trackliste hat die Id: "+ident;
	}
}
