package ndi.extractors.phash;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;

import metricspaces._double.DoubleDescriptor;
import metricspaces._double.DoubleDescriptorFile;
import ndi.extractors.AbstractDescriptorExtractor;

public class PhashDoubleExtractor extends AbstractDescriptorExtractor<DoubleDescriptor> {
	private final ColorConvertOp colorConvert;
	private double[] coefficients;
	
	private static final int SIZE = 32;
	private static final int REDUCED_SIZE = 8;
	private static final int LENGTH = REDUCED_SIZE * REDUCED_SIZE - 1;

	
	public PhashDoubleExtractor() {
		colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		initCoefficients();
	}
	
	
	public PhashDoubleExtractor(String path, int size) throws IOException {
		this();
		super.init(new DoubleDescriptorFile(path, size, getDimensions(), "pHashDouble"));
	}

	@Override
	public DoubleDescriptor extract(BufferedImage image) {
		//resize and greyscale the image
		image = resize(image, SIZE, SIZE);
		double[][] vals = greyscale(image);
		
		//compute the the top left 8x8 of the DCT excluding the DC component
		double[] dctvals = applyDCT(vals);
		
		return new DoubleDescriptor(dctvals);
	}

	@Override
	public int getDimensions() {
		return LENGTH;
	}

	private BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}

	private double[][] greyscale(BufferedImage img) {
		colorConvert.filter(img, img);

		double[][] vals = new double[SIZE][SIZE];

		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				vals[x][y] = img.getRGB(x, y) & 0xFF;
			}
		}

		return vals;
	}

	
	private double[] applyDCT(double[][] f) {
		double[] F = new double[REDUCED_SIZE * REDUCED_SIZE - 1];
		int index = 0;
		
		for (int u = 0; u < REDUCED_SIZE; u++) {
			for (int v = 0; v < REDUCED_SIZE; v++) {
				//don't bother working out the DC component
				if (u == 0 && v == 0)
					continue;
				
				double sum = 0.0;
				
				for (int i = 0; i < SIZE; i++) {
					for (int j = 0; j < SIZE; j++) {
						sum += Math.cos(((2 * i + 1) / (2.0 * SIZE)) * u * Math.PI) *
							Math.cos(((2 * j + 1) / (2.0 * SIZE)) * v * Math.PI) * (f[i][j]);
					}
				}
				
				sum *= ((coefficients[u] * coefficients[v]) / 4.0);
				F[index++] = sum;
			}
		}
		
		return F;
	}
	
	
	private void initCoefficients() {
		coefficients = new double[SIZE];

		for (int i = 1; i < SIZE; i++) {
			coefficients[i] = 1;
		}
		
		coefficients[0] = 1 / Math.sqrt(2.0);
	}
}
