package ndi.extractors;

public class YuvColour {
	private int y, u, v;
	private static final double Wr = 0.299;
	private static final double Wg = 0.587;
	private static final double Wb = 0.114;
	private static final double Umax = 0.436;
	private static final double Vmax = 0.615;
	
	
	public YuvColour(int rgb) {
		this((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
	}
	
	public YuvColour(int r, int g, int b) {
		this((double)r / 255, (double)g / 255, (double)b / 255);
	}
	
	public YuvColour(double r, double g, double b) {
		double y = Wr * r + Wg * g + Wb * b;
		u = (int)(Umax * (b - y) / (1 - Wb) * 255);
		v = (int)(Vmax * (r - y) / (1 - Wr) * 255);
		this.y = (int)(y * 255);
	}
	
	public int getY() {
		return y;
	}
	
	public int getU() {
		return u;
	}
	
	public int getV() {
		return v;
	}
}
