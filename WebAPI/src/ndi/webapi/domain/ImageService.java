package ndi.webapi.domain;

import java.net.UnknownHostException;

import org.jongo.MongoCollection;

public class ImageService {
	private MongoCollection images;
	
	public ImageService() throws UnknownHostException {
		images = Config.getJongo().getCollection("images");
	}
	
	public Image add(AddImageRequest request) throws ServiceException {
		String url = request.getImageUrl();
		
		if (url == null || url.isEmpty())
			throw new ServiceException("No imageUrl provided.");
		
		Image image = new Image(url);
		images.save(image);
		return image;
	}
	
	public Image addDescriptor(AddDescriptorRequest request) throws ServiceException {
		//https://github.com/bguerout/jongo/issues/224
		String str = Config.getGson().toJson(request.getDescriptor());
		
		Image image = images
			.findAndModify("{imageUrl: #}", request.getImageUrl())
			.with("{$set: {'descriptors." + request.getDescriptorName() + "': " + str + "}}")
			.returnNew()
			.upsert()
			.as(Image.class);

		return image;
	}
}
