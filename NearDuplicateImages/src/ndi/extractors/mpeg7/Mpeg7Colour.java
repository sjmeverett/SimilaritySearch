package ndi.extractors.mpeg7;

public class Mpeg7Colour {
	private double Y, Cb, Cr, hue, min, max, diff, sum, saturation, value;
	
	public Mpeg7Colour(int rgb) {
		this((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
	}
	
	public Mpeg7Colour(int r, int g, int b) {
		this((double)r / 255, (double)g / 255, (double)b / 255);
	}
	
	public Mpeg7Colour(double r, double g, double b) {
		Y  =  0.299 * r + 0.587 * g + 0.114 * b;
		Cb = -0.169 * r - 0.331 * g + 0.500 * b;
		Cr =  0.500 * r - 0.419 * g - 0.081 * b;
		
		max = Math.max(Math.max(r, g), b);
		min = Math.min(Math.min(r, g), b);
		diff = max - min;
		sum = (max + min) / 2;
		
		value = max;
		
		if (max == 0) {
			saturation = 0;
		}
		else {
			saturation = diff / max;
		}
		
		if (max == min) {
			hue = 0;
		}
		else {
			if (max == r && g >= b) {
				hue = 60 * (g - b) / diff;
			}
			else if (max == r && g < b) {
				hue = 360 + 60 * (g - b) / diff;
			}
			else if (max == g) {
				hue = 60 * (2 + (b - r) / diff);
			}
			else {
				hue = 60 * (5 + (r - g) / diff);
			}
		}
	}
	
	
	public double getY() {
		return Y;
	}
	
	public double getCb() {
		return Cb;
	}
	
	public double getCr() {
		return Cr;
	}
	
	public double getHue() {
		return hue;
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}
	
	public double getDiff() {
		return diff;
	}
	
	public double getSum() {
		return sum;
	}
	
	public double getSaturation() {
		return saturation;
	}
	
	public double getValue() {
		return value;
	}
}
