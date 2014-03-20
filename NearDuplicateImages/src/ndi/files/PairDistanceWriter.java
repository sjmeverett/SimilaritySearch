package ndi.files;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Writes CSV files which contain image pairs and the distances between them.
 * @author stewart
 */
public class PairDistanceWriter {
    private BufferedWriter writer;


    public PairDistanceWriter(String path) throws IOException {
        writer = new BufferedWriter(new FileWriter(path));

        writer.write("Image1,Image2,Distance\n");
    }


    public void write(int image1, int image2, double distance) throws IOException {
        writer.write(String.format("%d,%d,%f\n", image1, image2, distance));
    }


    public void close() throws IOException {
        writer.close();
    }
}
