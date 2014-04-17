package ndi.files;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import metricspaces.PairDistance;
import metricspaces.indexes.SearchResult;

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

    
    public void write(PairDistance pair) throws IOException {
    	writer.write(String.format("%d,%d,%f\n", pair.getObject1(), pair.getObject2(), pair.getDistance()));
    }
    
    
    public void write(SearchResult result) throws IOException {
    	write(new PairDistance(result.getQuery(), result.getResult(), result.getDistance()));
    }
    
    
    public void write(int image1, int image2, double distance) throws IOException {
        write(new PairDistance(image1, image2, distance));
    }
    
    
    public void writeAll(Iterable<PairDistance> pairs) throws IOException {
    	for (PairDistance pair: pairs)
    		write(pair);
    }
    
    
    public void writeAllResults(Iterable<SearchResult> results) throws IOException {
    	for (SearchResult result: results)
    		write(result);
    }


    public void close() throws IOException {
        writer.close();
    }
}
