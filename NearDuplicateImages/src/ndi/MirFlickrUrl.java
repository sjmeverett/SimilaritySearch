package ndi;

import java.io.File;
import java.net.MalformedURLException;

public class MirFlickrUrl extends ImageUrl {
    private static final String urlTemplate = "https://local.cis.strath.ac.uk/mirflickr1m/images/%d/%d.jpg";
    private static final String fileTemplate = "%d/%d.jpg";

    public MirFlickrUrl(int imageId) throws MalformedURLException {
        super(String.format(urlTemplate, imageId / 10000, imageId));
    }


    public MirFlickrUrl(int imageId, File imageDirectory) throws MalformedURLException {
        super(new File(imageDirectory, String.format(fileTemplate, imageId / 10000, imageId)).toURI().toURL());
    }
}
