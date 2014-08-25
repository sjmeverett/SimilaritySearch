package ndi.extractors.pdna;

import java.awt.image.BufferedImage;
import java.io.IOException;

import metricspaces.quantised.QuantisedDescriptor;
import metricspaces.quantised.QuantisedDescriptorContext;
import metricspaces.quantised.QuantisedDescriptorFile;
import ndi.extractors.AbstractDescriptorExtractor;


/**
 * Implements what we believe to be a description of the PhotoDNA descriptor in
 * http://www.robots.ox.ac.uk/~vgg/publications/papers/chum07.pdf (section 3.1).
 *
 * @author stewart
 */
public class PhotoDnaExtractor extends AbstractDescriptorExtractor<QuantisedDescriptor> {
	private double level1Width, level1Height, level2Width, level2Height;
    private Histogram[] level0, level1, level2;
    private QuantisedDescriptorContext context;
    
    public PhotoDnaExtractor(String path, int size) throws IOException {
    	QuantisedDescriptorFile file = new QuantisedDescriptorFile(path, size, getDimensions(), "PDNA", 1, 63 * 255);
    	context = file.getDescriptorContext();
    	
    	super.init(file);
    }
    
    public PhotoDnaExtractor() {
    	
    }

    @Override
    public QuantisedDescriptor extract(BufferedImage image) {
    	double width = image.getWidth();
        double height = image.getHeight();

        level1Width = 2.0 / width;
        level1Height = 2.0 / height;

        level2Width = 4.0 / width;
        level2Height = 4.0 / height;

        initialiseHistograms();
        
    	for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                OpponentColour colour = new OpponentColour(image.getRGB(x, y));
                updateHistogram(x, y, 0, colour.getI());
                updateHistogram(x, y, 1, colour.getO1());
                updateHistogram(x, y, 2, colour.getO2());
            }
        }

        return new QuantisedDescriptor(concatHistograms(), context);
    }

    @Override
    public int getDimensions() {
    	return 384;
    }
    
    
    private void initialiseHistograms() {
        level0 = new Histogram[3];
        level0[0] = new Histogram(64, 1);
        level0[1] = new Histogram(32, 1);
        level0[2] = new Histogram(32, 1);

        level1 = new Histogram[12];

        for (int i = 0; i < 12; i += 3) {
            level1[i] = new Histogram(16, 1);
            level1[i+1] = new Histogram(8, 1);
            level1[i+2] = new Histogram(8, 1);
        }

        level2 = new Histogram[48];

        for (int i = 0; i < 48; i += 3) {
            level2[i] = new Histogram(4, 1);
            level2[i+1] = new Histogram(2, 1);
            level2[i+2] = new Histogram(2, 1);
        }
    }


    private void updateHistogram(int x, int y, int channel, double value) {
        int level1region = (int)(y * level1Height) * 2 + (int)(x * level1Width);
        int level2region = (int)(y * level2Height) * 4 + (int)(x * level2Width);

        try {
            level0[channel].addValue(value);
            level1[level1region * 3 + channel].addValue(value);
            level2[level2region * 3 + channel].addValue(value);
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new RuntimeException(String.format("x: %d, y: %d, channel: %d, level1region: %d, level2region: %d, " +
                "level1Height: %f, level1Width: %f, level2Height: %f, level2Width: %f",
                x, y, channel, level1region, level2region,
                level1Height, level1Width, level2Height, level2Width));
        }
    }


    private byte[] concatHistograms() {
        byte[] point = new byte[384];
        int index = 0;

        index = concatHistograms(level0, point, index);
        index = concatHistograms(level1, point, index);
        concatHistograms(level2, point, index);

        return point;
    }


    private int concatHistograms(Histogram[] histograms, byte[] point, int index) {
        for (int i = 0; i < histograms.length; i++) {
            byte[] data = histograms[i].getL1NormalisedByte();
            System.arraycopy(data, 0, point, index, data.length);
            index += data.length;
        }

        return index;
    }
}
