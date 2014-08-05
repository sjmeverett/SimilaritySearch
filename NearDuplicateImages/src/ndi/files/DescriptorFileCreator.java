package ndi.files;

import java.io.IOException;

import metricspaces.files.ByteDescriptorFile;
import metricspaces.files.DescriptorFile;
import metricspaces.files.DescriptorFileHeader;
import metricspaces.files.DoubleDescriptorFile;
import metricspaces.files.RelativeDescriptorFile;
import metricspaces.files.SingleDescriptorFile;

import commandline.ParameterException;
import commandline.Parameters;

/**
 * Provides methods for loading and creating descriptor files.
 * @author stewart
 *
 */
public class DescriptorFileCreator {
	private Parameters parameters;
	
	/**
	 * Constructor.
	 * @param parameters The command line parameters.
	 */
	public DescriptorFileCreator(Parameters parameters) {
		this.parameters = parameters;
	}
	
	public void describe() {
		//describe the parameters for the help message
		parameters.describe("descriptortype", "The type of descriptor file to create (byte, double or single)");
		parameters.describe("elementmax", "For byte descriptors: the maximum value for any element in the descriptor. "
				+ "The elements will be scaled by this before being converted to byte. Defaults to 1.");
		parameters.describe("l1norm", "For byte descriptors: the fixed value that all descriptors sum to, or 0 if "
				+ "they are not normalised. Defaults to 0.");
	}
	
	
	/**
	 * Creates a new descriptor file, using settings in the parameters given at construction.
	 * @param path The path to the new descriptor file.
	 * @param capacity The number of objects the file will hold.
	 * @param dimensions The number of elements in each descriptor.
	 * @param descriptorName The name of the descriptor.
	 * @return
	 * @throws IOException There was an error writing the descriptor file.
	 * @throws ParameterException The descriptor type is not supported / invalid.
	 */
	public DescriptorFile create(String path, int capacity, int dimensions, String descriptorName) throws IOException, ParameterException {
		String descriptorType = parameters.require("descriptortype");
		
		if (descriptorType.equals("byte")) {
			double elementMax = parameters.getDouble("elementmax", 1);
			int l1norm = parameters.getInt("l1norm", 0);
			
			DescriptorFileHeader header = new DescriptorFileHeader(path, DescriptorFileHeader.BYTE_TYPE,
					capacity, dimensions, descriptorName);
			
			return new ByteDescriptorFile(header, elementMax, l1norm);
		}
		else if (descriptorType.equals("double")) {
			DescriptorFileHeader header = new DescriptorFileHeader(path, DescriptorFileHeader.DOUBLE_TYPE,
					capacity, dimensions, descriptorName);
			
			return new DoubleDescriptorFile(header);
		}
		else if (descriptorType.equals("single")) {
			DescriptorFileHeader header = new DescriptorFileHeader(path, DescriptorFileHeader.SINGLE_TYPE,
					capacity, dimensions, descriptorName);
			
			return new SingleDescriptorFile(header);
		} 
		else {
			throw new ParameterException("descriptor type not supported");
		}
	}
}