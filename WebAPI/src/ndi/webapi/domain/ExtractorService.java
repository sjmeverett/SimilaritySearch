package ndi.webapi.domain;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;

import metricspaces._double.DoubleDescriptor;
import ndi.ImageUrl;
import ndi.extractors.mpeg7.ColourStructureExtractor;
import ndi.extractors.mpeg7.EdgeHistogramExtractor;
import ndi.extractors.phash.PhashDoubleExtractor;

public class ExtractorService {
	private EdgeHistogramExtractor eh;
	private ColourStructureExtractor cs;
	private PhashDoubleExtractor ph;
	
	public ExtractorService() {
		eh = new EdgeHistogramExtractor(1100, 11);
		cs = new ColourStructureExtractor();
		ph = new PhashDoubleExtractor();
	}
	
	public ExtractorResponse extract(ExtractorRequest request) throws ServiceException {
		try {
			ImageUrl url = new ImageUrl(request.getImageUrl());
			BufferedImage image = url.openImage();
			
			DoubleDescriptor descriptor;
			String descriptorName = request.getDescriptorName();
			
			if (descriptorName == null || descriptorName.isEmpty()) {
				throw new ServiceException("No descriptor name supplied.");
			} else if (descriptorName.equals("EH")) {
				descriptor = eh.extract(image);
			} else if (descriptorName.equals("CS")) {
				descriptor = cs.extract(image);
			} else if (descriptorName.equals("pHashDouble")) {
				descriptor = ph.extract(image);
			} else {
				throw new ServiceException("Unrecognised descriptor name: " + descriptorName);
			}
			
			return new ExtractorResponse(descriptor, request);
		} catch (MalformedURLException e) {
			throw new ServiceException("The URL supplied was not formed correctly.");
		} catch (IOException e) {
			throw new ServiceException("Could not read an image from the URL provided.");
		}
	}
}
