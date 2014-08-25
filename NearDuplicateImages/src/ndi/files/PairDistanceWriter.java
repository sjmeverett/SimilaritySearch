package ndi.files;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import metricspaces.indices.SearchResult;
import metricspaces.pairs.PairDistance;

/**
 * Writes CSV files which contain image pairs and the distances between them.  The file
 * consists of an 'Image1' column, an 'Image2' column, and one or more distance columns.
 * 
 * The 'write' methods keep the smaller of the two IDs in the 'Image1' column, while the
 * 'writeExactly' methods don't.
 * @author stewart
 */
public class PairDistanceWriter {
    private BufferedWriter writer;

    /**
     * Constructor.
     * @param path the path to the CSV file to output
     * @throws IOException
     */
    public PairDistanceWriter(String path) throws IOException {
        writer = new BufferedWriter(new FileWriter(path));
        writer.write("Image1,Image2,Distance\n");
    }
    
    /**
     * Constructor for files with multiple distances per pair.
     * @param path the path to the CSV file to output
     * @param headers the headers for the distance columns
     * @throws IOException
     */
    public PairDistanceWriter(String path, String[] headers) throws IOException {
        writer = new BufferedWriter(new FileWriter(path));
        writer.write("Image1,Image2,");
        
        for (int i = 0; i < headers.length - 1; i++) {
        	writer.write(headers[i] + ",");
        }
        
        writer.write(headers[headers.length - 1] + "\n");
    }

    /**
     * Writes the specified pair and distance out to the file.  Note that the 'Image1' will always
     * be the smaller of the two IDs.
     * @param pair
     * @throws IOException
     */
    public void write(PairDistance pair) throws IOException {
    	writer.write(String.format("%d,%d,%f\n", pair.getObject1(), pair.getObject2(), pair.getDistance()));
    }
    
    /**
     * Writes the specified search result out to the file.  Note that the 'Image1' will always be
     * the smaller of the query and the result IDs.
     * @param result
     * @throws IOException
     */
    public void write(SearchResult result) throws IOException {
    	write(new PairDistance(result.getQuery(), result.getResult(), result.getDistance()));
    }
    
    /**
     * Writes out the search result with the query as 'Image1' and the result as 'Image2'.
     * @param result
     * @throws IOException
     */
    public void writeExactly(SearchResult result) throws IOException {
    	writeExactly(result.getQuery(), result.getResult(), result.getDistance());
    }
    
    /**
     * Writes the specified pair and distance out to the file.  Note that the 'Image1' will
     * always be the smaller of the two IDs.
     * @param a
     * @param b
     * @param distance
     * @throws IOException
     */
    public void write(int a, int b, double distance) throws IOException {
        write(new PairDistance(a, b, distance));
    }
    
    /**
     * Writes the specified pair and distance out to the file exactly as is.
     * @param image1
     * @param image2
     * @param distance
     * @throws IOException
     */
    public void writeExactly(int image1, int image2, double distance) throws IOException {
    	writer.write(String.format("%d,%d,%f\n", image1, image2, distance));
    }
    
    /**
     * Writes the specified pair and distances out to the file.  Note that 
     * @param a
     * @param b
     * @param distances
     * @throws IOException
     */
    public void write(int a, int b, double[] distances) throws IOException {
    	PairDistance pair = new PairDistance(a, b, 0);
    	writer.write(String.format("%d,%d,", pair.getObject1(), pair.getObject2()));
    	
    	for (int i = 0; i < distances.length - 1; i++) {
    		writer.write(String.format("%f,", distances[i]));
    	}
    	
    	writer.write(String.format("%f\n", distances[distances.length - 1]));
    }
    
    /**
     * Writes out all the pairs to the file, keeping the smaller of each of the pairs of IDs
     * in the 'Image1' column.
     * @param pairs
     * @throws IOException
     */
    public void writeAll(Iterable<PairDistance> pairs) throws IOException {
    	for (PairDistance pair: pairs)
    		write(pair);
    }
    
    /**
     * Writes out all the results to the file, keeping the smaller of each of the pairs of IDs
     * in the 'Image2' column.
     * @param results
     * @throws IOException
     */
    public void writeAllResults(Iterable<SearchResult> results) throws IOException {
    	for (SearchResult result: results)
    		write(result);
    }

    /**
     * Closes the file.
     * @throws IOException
     */
    public void close() throws IOException {
        writer.close();
    }
}
