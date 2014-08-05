package ndi.extractors.update;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.update._double.DoubleDescriptorFile;
import metricspaces.update.common.DescriptorFile;
import metricspaces.update.common.LargeBinaryFile;
import ndi.extractors.mpeg7.Mpeg7Colour;

/**
 * Extracts the MPEG-7 Colour Structure Descriptor.
 * @author stewart
 *
 */
public class ColourStructureExtractor extends AbstractDescriptorExtractor<DoubleDescriptor> {
	private static final int
		totalBins = 128,
		sumBins0 = 16,
		sumBins1 = 4,
		sumBins2 = 4,
		sumBins3 = 4,
		sumBins4 = 4,
	  //hueBins0 = 1,
		hueBins1 = 4,
		hueBins2 = 8,
		hueBins3 = 8,
		hueBins4 = 8;
	
	private static final double
		sumBinSize0 = 1.0 / sumBins0,
		sumBinSize1 = 249.0 / 255 / sumBins1,
		sumBinSize2 = 235.0 / 255 / sumBins2,
		sumBinSize3 = 195.0 / 255 / sumBins3,
		sumBinSize4 = 145.0 / 255 / sumBins4,
	  //hueBinSize0 = 360.0 / hueBins0,
		hueBinSize1 = 360.0 / hueBins1,
		hueBinSize2 = 360.0 / hueBins2,
		hueBinSize3 = 360.0 / hueBins3,
		hueBinSize4 = 360.0 / hueBins4;
	
	
	public ColourStructureExtractor(String path, int size) throws IOException {
		super(new DoubleDescriptorFile(new LargeBinaryFile(path, true)), size, DescriptorFile.DOUBLE_TYPE, "CS");
	}
	
	
	@Override
	public DoubleDescriptor extract(BufferedImage image) {
		//figure out what size the structuring element is supposed to be
		int width = image.getWidth();
		int height = image.getHeight();
		int p = (31 - Integer.numberOfLeadingZeros(width * height)) / 2 - 8;
		if (p < 0) p = 0;
		int stride = 1 << p;
		
		//sample the image at the appropriate intervals to get the histogram bin numbers
		int sampledWidth = width / stride;
		int sampledHeight = height / stride;
		int[] samples = sampleImage(image, sampledWidth, sampledHeight, stride);
		
		//extract
		int[] histogram = new int[totalBins];
		
		for (int y = 0; y < sampledHeight - 8; y++) {
			for (int x = 0; x < sampledWidth - 8; x++) {
				//extract the 8 x 8 structuring element
				Set<Integer> colours = extractElement(samples, x, y, sampledWidth);
				
				//increment the appropriate histogram bin for each colour found
				for (Integer colour: colours) {
					histogram[colour]++;
				}
			}
		}
		
		//normalise
		int rmax = (sampledWidth - 7) * (sampledHeight - 7);
		double[] data = new double[totalBins];
		
		for (int i = 0; i < histogram.length; i++) {
			data[i] = (double)histogram[i] / rmax;
		}
		
		return new DoubleDescriptor(data);
	}
	
	
	@Override
	public int getDimensions() {
		return totalBins;
	}
	
	
	private Set<Integer> extractElement(int[] samples, int xstart, int ystart, int width) {
		Set<Integer> colours = new HashSet<Integer>(totalBins);
		
		//calculate the starting offset within the sample data 
		int index = ystart * width + xstart;
		
		//extract an 8 x 8 section from the sample data
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				//each colour only gets counted once
				colours.add(samples[index++]);
			}
			
			//move onto the next row
			index += width - 8;
		}
		
		return colours;
	}
	
	
	private int[] sampleImage(BufferedImage image, int sampledWidth, int sampledHeight, int stride) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[] samples = new int[sampledWidth * sampledHeight];
		int index = 0;
		
		for (int y = 0; y < height; y += stride) {
			for (int x = 0; x < width; x += stride) {
				int bin = getBin(new Mpeg7Colour(image.getRGB(x, y)));
				assert(bin >= 0);
				samples[index++] = bin;
			}
		}
		
		return samples;
	}
	
	
	private int getBin(Mpeg7Colour colour) {
		int diff = (int)(colour.getDiff() * 255 + 0.5);
		double sum = colour.getSum();
		double hue = colour.getHue();
		
		//there are 5 'subspaces' formed by dividing the diff axis at specified points
		//within these subspaces, there are different numbers of bins for both hue and sum
		if (diff < 6) {
			return getSumBin(sum, sumBinSize0, sumBins0);
		}
		else if (diff < 20) {
			return (int)(hue / hueBinSize1) * sumBins1 + getSumBin(sum, sumBinSize1, sumBins1);
		}
		else if (diff < 60) {
			return (int)(hue / hueBinSize2) * sumBins2 + getSumBin(sum, sumBinSize2, sumBins2);
		}
		else if (diff < 110) {
			return (int)(hue / hueBinSize3) * sumBins3 + getSumBin(sum, sumBinSize3, sumBins3);
		}
		else {
			return (int)(hue / hueBinSize4) * sumBins4 + getSumBin(sum, sumBinSize4, sumBins4);
		}
	}
	
	
	private int getSumBin(double value, double binSize, int binCount) {
		//the bins are centered around 0.5
		int b = (int)((value - 0.5) / binSize) + binCount / 2;
		
		if (b == binCount)
			return b - 1;
		else
			return b;
	}
}
