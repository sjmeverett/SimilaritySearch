package ndi.extractors.mpeg7;

import java.awt.image.BufferedImage;
import java.io.IOException;

import metricspaces._double.DoubleDescriptor;
import metricspaces._double.DoubleDescriptorFile;
import ndi.extractors.AbstractDescriptorExtractor;

/**
 * The MPEG-7 Edge Histogram descriptor.
 * (based on http://www.cs.bilkent.edu.tr/~bilmdg/bilvideo-7/Software.html)
 * @author stewart
 *
 */
public class EdgeHistogramExtractor extends AbstractDescriptorExtractor<DoubleDescriptor> {
	private int numberOfBlocks;
	private double edgeThreshold;

	private static final double ROOT2 = Math.sqrt(2);
	
	public EdgeHistogramExtractor(String path, int size, int numberOfBlocks, double edgeThreshold) throws IOException {
		super.init(new DoubleDescriptorFile(path, size, getDimensions(), "EH"));
		
		this.numberOfBlocks = numberOfBlocks;
		this.edgeThreshold = edgeThreshold;
	}
	
	public EdgeHistogramExtractor(int numberOfBlocks, double edgeThreshold) {
		this.numberOfBlocks = numberOfBlocks;
		this.edgeThreshold = edgeThreshold;
	}

	@Override
	public DoubleDescriptor extract(BufferedImage image) {
		int blockSize = Math.max((int)(Math.sqrt(image.getWidth() * image.getHeight() / numberOfBlocks) / 2) * 2, 2);
		int xmax = image.getWidth() - blockSize;
		int ymax = image.getHeight() - blockSize;
		int subImageWidth = image.getWidth() / 4;
		int subImageHeight = image.getHeight() / 4;
		int[] histogram = new int[80];
		int[] blockCounts = new int[16];
		
		//iterate through the regions accumulating edge counts
		for (int y = 0; y < ymax; y += blockSize) {
			for (int x = 0; x < xmax; x += blockSize) {
				int subImage = y / subImageHeight * 4 + x / subImageWidth;
				int edgeType = getEdgeType(image, x, y, blockSize);
				
				if (edgeType > -1) {
					histogram[subImage * 5 + edgeType]++;
				}
				
				//I'm sure there's a better way to do this...
				blockCounts[subImage]++;
			}
		}
		
		//normalise histogram values to number of blocks in the sub image
		int index = 0;
		double[] data = new double[80];
		
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 5; j++) {
				data[index] = (double)histogram[index] / blockCounts[i];
				index++;
			}
		}
		
		return new DoubleDescriptor(data);
	}
	
	@Override
	public int getDimensions() {
		return 80;
	}

	
	private int getEdgeType(BufferedImage image, int startX, int startY, int blockSize) {
		double[] block = new double[4];
		int sectionSize = blockSize / 2;
		
		//sum the luminance in each of the 4 sections of the image block
		for (int y = 0; y < blockSize; y++) {
			for (int x = 0; x < blockSize; x++) {
				block[(y / sectionSize) * 2 + (x / sectionSize)] += grey(image.getRGB(startX + x, startY + y));
			}
		}
		
		//get the average luminance
		int blockSectionPixelCount = blockSize * blockSize / 4;
		
		for (int i = 0; i < block.length; i++) {
			block[i] /= blockSectionPixelCount;
		}
		
		double[] edges = new double[5];
		//vertical
		edges[0] = Math.abs(block[0] + block[2] - (block[1] + block[3]));
		//horizontal
		edges[1] = Math.abs(block[0] + block[1] - (block[2] + block[3]));
		//45 degree diagonal
		edges[2] = Math.abs(ROOT2 * (block[0] - block[3]));
		//135 degree diagonal
		edges[3] = Math.abs(ROOT2 * (block[1] - block[2]));
		//non-directional
		edges[4] = Math.abs(2 * (block[0] - block[1] - block[2] + block[3]));
		
		double max = edgeThreshold;
		int edge = -1;
		
		//find the max valued edge (if there are none greater than edgeThreshold, edge will remain -1)
		for (int i = 0; i < edges.length; i++) {
			if (edges[i] > max) {
				edge = i;
				max = edges[i];
			}
		}
		
		return edge;
	}

	private int grey(int colour) {
		int r = (colour >> 16) & 0xFF;
		int g = (colour >> 8) & 0xFF;
		int b = colour & 0xFF;
		return (int)(0.2126 * r + 0.7152 * g + 0.0722 * b);
	}
}
