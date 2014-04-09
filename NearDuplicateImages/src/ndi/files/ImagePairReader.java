package ndi.files;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ndi.ImagePair;

public class ImagePairReader {
    private BufferedReader reader;

    public ImagePairReader(String file, boolean header) throws IOException {
        reader = new BufferedReader(new FileReader(file));

        if (header)
            reader.readLine();
    }


    public ImagePair read() throws IOException, FileFormatException {
        String line = reader.readLine();

        if (line == null)
            return null;

        String[] a = line.split(",");

        if (a.length < 2)
            throw new FileFormatException("Expected at least two items per line.");

        try {
            int image1 = Integer.parseInt(a[0]);
            int image2 = Integer.parseInt(a[1]);

            return new ImagePair(image1, image2);
        }
        catch (NumberFormatException ex) {
            throw new FileFormatException("Image field IDs must be integers.");
        }
    }

    public void close() throws IOException {
        reader.close();
    }
}
