package ohgarmin;

import java.io.*;

public class Demo {
	
	public static void main(String[] args){
		GarminApi api = new GarminApi();
		while (true){
			System.out.println("1: Get Product Data");
			System.out.println("2: Get Protocol Data");
			System.out.println("3: Turn Off");
			
			
			BufferedReader in = new BufferedReader(new InputStreamReader( System.in ) );
			try{
				String s = in.readLine();
				
				if (s.equals("1")){
					System.out.println("Retrieve Product Data - plz don't plug off the Device...");				System.out.println(api.getProductData());
				}else if (s.equals("2")){
					System.out.println("Retrieve Protocol Data - plz don't plug off the Device...");
					System.out.println(api.getProtocolData());
				}else if(s.equals("3")){
					System.out.println("Ready to shut down");
					api.TurnOffPower();
					break;
				}
			
			}catch(IOException ex){
				System.out.println("Problem mit der Eingabe");
			}
		}
		System.out.println("The End ;)");
	}
}
