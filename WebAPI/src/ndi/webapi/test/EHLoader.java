package ndi.webapi.test;

import java.io.IOException;

import metricspaces.util.Progress;
import ndi.MirFlickrUrl;
import ndi.extractors.mpeg7.EdgeHistogramExtractor;
import ndi.webapi.domain.AddDescriptorRequest;
import ndi.webapi.domain.Image;
import ndi.webapi.domain.ImageService;
import ndi.webapi.domain.IndexImageRequest;
import ndi.webapi.domain.ServiceException;
import ndi.webapi.domain.vptree.IndexService;

public class EHLoader {
	public static void main(String[] args) {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			EdgeHistogramExtractor extractor = new EdgeHistogramExtractor(1100, 11);
			ImageService imageService = new ImageService();
			IndexService indexService = new IndexService();
			
			progress.setOperation("copying to db", 10000);
			
			for (int i = 0; i < 10000; i++) {
				MirFlickrUrl url = new MirFlickrUrl(i);
				Image image = imageService.addDescriptor(new AddDescriptorRequest(url.getURL().toString(), "EH", extractor.extract(url.openImage())));
				indexService.indexImage(new IndexImageRequest(image.getId(), "EH", "SED"));
				progress.incrementDone();
			}
			
//			indexService.balance("EH", "SED", progress);
			
			reporter.stop();
		} catch (IOException | ServiceException e) {
			reporter.stop();
			e.printStackTrace();
		}
	}
}
