package ndi.webapi.test;

import java.io.IOException;
import java.util.Arrays;

import metricspaces._double.DoubleDescriptor;
import metricspaces._double.DoubleDescriptorFile;
import metricspaces.descriptors.DescriptorFileFactory;
import metricspaces.descriptors.DescriptorFormat;
import metricspaces.util.Progress;
import ndi.MirFlickrUrl;
import ndi.webapi.domain.AddDescriptorRequest;
import ndi.webapi.domain.Image;
import ndi.webapi.domain.ImageService;
import ndi.webapi.domain.IndexImageRequest;
import ndi.webapi.domain.ServiceException;
import ndi.webapi.domain.vptree.IndexService;

public class EhAllLoader {
	private static final int COUNT = 10000;
	
	public static void main(String[] args) {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			DoubleDescriptorFile file = (DoubleDescriptorFile)DescriptorFileFactory.open("/Users/stewart/image-data/EhAll.dat", false);
			DescriptorFormat<DoubleDescriptor> descriptors = file.getFormat();
			ImageService imageService = new ImageService();
			IndexService indexService = new IndexService();
			
			progress.setOperation("copying to db", COUNT);
			long ms = System.currentTimeMillis();
			
			for (int i = 0; i < COUNT; i++) {
				Image image = imageService.addDescriptor(new AddDescriptorRequest(new MirFlickrUrl(i).getURL().toString(), "EhAll", descriptors.get(i)));
				indexService.indexImage(new IndexImageRequest(image.getId(), "EhAll", "SED"));
				progress.incrementDone();
			}
			
			ms = System.currentTimeMillis() - ms;
			reporter.stop();
			
			System.out.printf("Time: %.3f ms\n", (double)ms / COUNT);
			
			print(descriptors, 1471);
			print(descriptors, 1606);
			print(descriptors, 6601);
			print(descriptors, 6920);
		} catch (IOException | ServiceException e) {
			reporter.stop();
			e.printStackTrace();
		}
	}
	
	private static void print(DescriptorFormat<DoubleDescriptor> descriptors, int id) {
		System.out.println(id + ": " + Arrays.toString(descriptors.get(id).getData()));
	}
}
