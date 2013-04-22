package ohgarmin;

import java.util.*;

public class FotoDate 
{ //rechnet Millisekunden seit xxx in ein Datum um
	int aktJahr,aktMonat,aktTag,aktStunde,aktMinute,aktSekunde;
	int startJahr;	//Datum von dem aus gez�hlt wird: Jahr 
	int startMonat;	//Monat
	int startTag; 	//und Tag	
	final long MSDAY = 86400000; //Anzahl der Millisekunden eines Tages (24*60*60*1000)
	final int MSHOUR =  3600000; //Anzahl der Millisekunden einer Stunde
 	final int MSMINUTE = 60000;  //Anzahl der Millisekunde einer Minute
 	final int MSSECOND = 1000;  //Anzahl der Millisiekunde einer Sekunde
 	int ZeitZone = 1; //Unterchied zur GMT-Zeit
 	
 	public int getAktJahr(){
 		return aktJahr;
 	}
 	public int getAktMonat(){
 		return aktMonat;
 	}
 	public int getAktTag(){
 		return aktTag;
 	}
 	public int getAktStunde(){
 		return aktStunde;
 	}
 	public int getAktMinute(){
 		return aktMinute;
 	}
 	public int getAktSekunde(){
 		return aktSekunde;
 	}
 	
	public FotoDate(int Tag, int Monat, int Jahr) {
		startJahr = Jahr; 	//neues Startjahr
		startMonat = Monat; //neuer Startmonat
		startTag = Tag;		//neuer Starttag
	}
	
	public boolean isSchaltjahr(int Jahr)
	{//berechnet ob das Jahr ein Schaltjahr ist
		if ((Jahr % 400 == 0) || ((Jahr % 4 == 0) && (Jahr % 100 != 0)))
			return true; //wenn jahr durch 400 teilbar ODER (jahr durch 4 teilbar
		else  // und NICHT durch 100 teilbar) -> dann ist es ein Schaltjahr
			return false;
	}

	/*public void printDate()
	{//liefert das aktuelle Datum in ms seit 1970
	   Date x = new Date();
	   System.out.println("tag: "+x.getDay()+", monat: "+x.getMonth()+", jahr: "+x.getYear()+", Zeit: "+getAktDate(x.getTime()));
	}*/
	
	public int monatsTage(int m, int j)
	{//Gibt zurück wieviele Tage es im Monat m im Jahr j gibt
		int ret=0; //return-Wert
	
		if (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 || m == 12)
			ret=31; //diese Monate haben immer 31 Tage
		else if (m==4 || m == 6 || m == 9 || m == 11)
			ret = 30; //diese Monate haben immer 30 Tage
		else //-> Februar bleibt �ber
		   if (isSchaltjahr(j))
				ret= 29; //Schaltjahr -> Februar hat 29 Tage
		   else
				ret= 28; //Kein Schaltjahr -> Februar hat 28 Tage
				
		return ret;
	}
	
	public String getAktDateSec(long s)
	{
		s=s*1000;
		return getAktDate(s);
	}
	
	public String getAktDate(long ms)
	{ //Funktion gibt das Datum als String zurueck
		//int Minuten;		
		
		int Jahr = startJahr;
		int Monat = startMonat;
		int Tag = startTag;		
		int Stunden=0; 
		int Minuten=0;
		int Sekunden=0;
		
		//System.out.println("Tage: "+Tage);
		
		for (;ms>MSDAY;ms=ms-MSDAY) //Es wird immer um einen Tag (in ms) verringert
		{ //Die Tage werden runtergezählt			
		   Tag++; //MonatsTag wird um eins mehr
		   if (Tag>monatsTage(Monat,Jahr))
		   { //Tag geht über monat hinaus (zb 32. Juli)
			   Tag=1; //Tag wird wieder 1
			   Monat++; //Monat wird um eins mehr
		   }
		   if (Monat>12)
		   {  //Monat geht übers Jahr hinaus
			   Monat = 1; //Monat ist wieder Jänner
			   Jahr++; //Jahr wird erh�ht
		   }
		}				
		
		while (ms>MSHOUR)
		{ //Stunden werden ausgerechnet
			ms = ms-MSHOUR;
			Stunden++;
		}
		
		while (ms>MSMINUTE)
		{ //Minuten werden ausgerechnet
			ms = ms-MSMINUTE;
			Minuten++;
		}
		
		while (ms>MSSECOND)
		{ //Minuten werden ausgerechnet
			ms = ms-MSSECOND;
			Sekunden++;
		}								
				
		
		Stunden = Stunden+ZeitZone; //Zeitzonen-Unterschied -> +1 Stunde
		if (Stunden > 23)
		{	
			Stunden=0;
			Tag++;
			if (Tag>monatsTage(Monat,Jahr))
			   { //Tag geht über monat hinaus (zb 32. Juli)
				   Tag=1; //Tag wird wieder 1
				   Monat++; //Monat wird um eins mehr
			   }
			   if (Monat>12)
			   {  //Monat geht übers Jahr hinaus
				   Monat = 1; //Monat ist wieder Jänner
				   Jahr++; //Jahr wird erhöht
			   }
		}
		aktTag=Tag;
		aktMonat=Monat;
		aktJahr=Jahr;
		aktStunde=Stunden;
		aktMinute=Minuten;
		aktSekunde=Sekunden;
		
		return  get2Format(Tag) + "." +get2Format(Monat) + "." + String.valueOf(Jahr)
			+ "; "+get2Format(Stunden)+":"+get2Format(Minuten)+":"+get2Format(Sekunden);
	}
	
	public long getMS(int tag, int monat, int jahr, int tag2, int monat2, int jahr2)
	{			
		long count=0;
		
		while ((tag != tag2 || monat != monat2 || jahr != jahr2))
		{
		   count++;
		   tag++; //MonatsTag wird um eins mehr
		   if (tag>monatsTage(monat,jahr))
		   { //Tag geht �ber monat hinaus (zb 32. Juli)
			   tag=1; //Tag wird wieder 1
			   monat++; //Monat wird um eins mehr
		   }
		   if (monat>12)
		   {  //Monat geht �bers Jahr hinaus
			   monat = 1; //Monat ist wieder J�nner
			   jahr++; //Jahr wird erh�ht
		   }
		}
		
		return count*MSDAY;
	}
	
	public long getSec(int tag, int monat, int jahr, int tag2, int monat2, int jahr2)
	{			
		long count=0;
		
		while ((tag != tag2 || monat != monat2 || jahr != jahr2))
		{
		   count++;
		   tag++; //MonatsTag wird um eins mehr
		   if (tag>monatsTage(monat,jahr))
		   { //Tag geht �ber monat hinaus (zb 32. Juli)
			   tag=1; //Tag wird wieder 1
			   monat++; //Monat wird um eins mehr
		   }
		   if (monat>12)
		   {  //Monat geht �bers Jahr hinaus
			   monat = 1; //Monat ist wieder J�nner
			   jahr++; //Jahr wird erh�ht
		   }
		}
		
		return count*MSDAY/1000;
	}
	
	private String get2Format(int m)
	{//f�gt bei einstelligen Werten vorne eine "0" dran, ergebnis wird als String zur�ckgegeben
		if (m<10) 
			return "0"+Integer.toString(m);
		else
			return Integer.toString(m);
	}
}
