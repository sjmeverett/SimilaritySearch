package ndi;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;

public class MirFlickrUrl {
    private static final String urlTemplate = "https://local.cis.strath.ac.uk/mirflickr1m/images/%d/%d.jpg";
    private static final String fileTemplate = "%d/%d.jpg";
    private URL url;

    public MirFlickrUrl(int imageId) throws MalformedURLException {
        url = new URL(String.format(urlTemplate, imageId / 10000, imageId));
    }


    public MirFlickrUrl(int imageId, File imageDirectory) throws MalformedURLException {
        url = new File(imageDirectory, String.format(fileTemplate, imageId / 10000, imageId)).toURI().toURL();
    }


    public BufferedImage openImage() throws IOException {
        try {
            return ImageIO.read(url);
        }
        catch (IllegalArgumentException ex) {
            return openImageCarefully();
        }
    }


    /**
     * Workaround for colour profile bug.
     * http://stackoverflow.com/a/11571181/632636
     * @return
     * @throws IOException
     */
    private BufferedImage openImageCarefully() throws IOException {
        BufferedImage bufferedImage = null;
        ImageInputStream stream = ImageIO.createImageInputStream(url.openStream());
        Iterator<ImageReader> iter = ImageIO.getImageReaders(stream);

        while (iter.hasNext()) {
            ImageReader reader = null;

            try {
                reader = iter.next();
                ImageReadParam param = reader.getDefaultReadParam();
                reader.setInput(stream, true, true);
                Iterator<ImageTypeSpecifier> imageTypes = reader.getImageTypes(0);

                while (imageTypes.hasNext()) {
                    ImageTypeSpecifier imageTypeSpecifier = imageTypes.next();
                    int bufferedImageType = imageTypeSpecifier.getBufferedImageType();

                    if (bufferedImageType == BufferedImage.TYPE_BYTE_GRAY) {
                        param.setDestinationType(imageTypeSpecifier);
                        break;
                    }
                }

                bufferedImage = reader.read(0, param);

                if (bufferedImage != null)
                    break;

            }
            finally {
                if (reader != null)
                    reader.dispose();
            }
        }

        if (bufferedImage == null)
            throw new IllegalArgumentException("Could not open image: " + url.toString());

        return bufferedImage;
    }
}
