package ohgarmin;
import java.awt.image.BufferedImage;

public class FotoObject {
	private BufferedImage foto;
	private int X;
	private int Y;
	private double GPSX;
    private double GPSY;
	private int Size;			
	private long timestamp;
	private final int FotoSmallSize=30;
	private final int FotoBigSize=250;
	private boolean marked;
	
	
   public FotoObject()
   {	   	   
	   this.GPSX=0;
	   this.GPSY=0;
	   this.Size=FotoSmallSize;
	   this.timestamp=0;
	   this.marked=false;
   }
   
   public String getGPSXString(){
	   return ""+GPSX;
   }
   
   public String getGPSYString(){
	   return ""+GPSY;
   }
   
   public void setGPSX(double x){
	   GPSX = x;
   }
   
   public void setGPSY(double y){
	   GPSY = y;
   }
   
   public void setX(int x){
	   X = x;
   }
   
   public void setY(int y){
	   Y = y;
   }
   
   public void setImage(BufferedImage image){
	   this.foto = image;
   }
      
   public void setSize(int s){
	   Size = s;
   }
   
   public void setTimestamp(long l){
	   timestamp = l;
   }
   
   public double getGPSY(){
	   return this.GPSY;
   }
   
   public double getGPSX(){
	   return this.GPSX;
   }
   
   public int getY(){
	   return this.Y;
   }
   
   public int getX(){
	   return this.X;
   }
   
   public void setMarked(){
	   marked=true;
   }
   
   public void setUnMarked(){
	   marked=false;
   }
   
   public boolean isMarked(){
	   return marked;
   }
   
   public int getFotoSmallSize(){
	   return this.FotoSmallSize;
   }
   
   public int getFotoBigSize(){
	   return this.FotoBigSize;
   }
   
   public int getSize(){
	   return this.Size;
   }
   
   public long getTimestamp(){
	   return this.timestamp;
   }
   
   public BufferedImage getImage(){
	   return this.foto;
   }
   
   
}
