package ohgarmin;
public interface Int_Berechnung {
	public double PixelToMeter(int PixelDistanz);
	public double PixelToGradX(int PixelDistanz);
	public double PixelToGradY(int PixelDistanz);
	public int MeterToPixel(double MeterDistanz);
	public double MeterToGradX(double MeterDistanz);
	public double MeterToGradY(double MeterDistanz);
	public double GradToMeterX(double GradDistanz);
	public double GradToMeterY(double GradDistanz);
	public int GradToPixelX(double GradDistanz);
	public int GradToPixelY(double GradDistanz);
	public int DistanzToMidX_Pixel(int PixelX);
	public int DistanzToMidY_Pixel(int PixelY);
	public double DistanzToMidX_Meter(double MeterX);
	public double DistanzToMidY_Meter(double MeterY);
	public double DistanzToMidX_Grad(double GradX);
	public double DistanzToMidY_Grad(double GradY);
	public String WertFormatieren(double wert);
	public double getNextNorth(int height);
	public double getNextSouth(int height);
	public double getNextEast(int width);
	public double getNextWest(int width);
}
