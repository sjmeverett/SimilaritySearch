package ndi.commands.descriptors;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import metricspaces.Progress;
import metricspaces.descriptors.SingleDescriptor;
import metricspaces.update.common.DescriptorFile;
import metricspaces.update.common.DescriptorFormat;
import metricspaces.update.common.LargeBinaryFile;
import metricspaces.update.single.SingleDescriptorFile;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class CopyGistCommand implements Command {
	private Parameters parameters;
	private RandomAccessFile raf;
	
	private static final int COUNT = 1000000;
	private static final int N_SCALES = 6;
	private static final int N_ORIENTATIONS = 5;
	private static final int N_WINDOWS = 4;

	private static final int GIST_SIZE = N_SCALES * N_ORIENTATIONS * N_WINDOWS * N_WINDOWS;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			File dir = new File(parameters.require("dir"));
			
			SingleDescriptorFile output = new SingleDescriptorFile(new LargeBinaryFile(parameters.require("out"), true));
			DescriptorFormat<SingleDescriptor> outputFormat = output.getFormat();
			output.writeHeader(DescriptorFile.SINGLE_TYPE, COUNT, GIST_SIZE, "GIST");
			
			progress.setOperation("Copying", COUNT);
			float[] data = new float[GIST_SIZE];
			
			for (int i = 0; i < COUNT; i++) {
				FloatBuffer buffer = getBuffer(dir, i);
				buffer.get(data);
				outputFormat.put(new SingleDescriptor(data));
				raf.close();
				progress.incrementDone();
			}
			
			output.close();
			reporter.stop();
		}
		catch (ParameterException | IOException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}
	
	private FloatBuffer getBuffer(File dir, int i) throws IOException {
		File file = new File(new File(dir, "" + (i / 10000)), "" + i + ".dat");
		raf = new RandomAccessFile(file, "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer.asFloatBuffer();
	}

	@Override
	public String getName() {
		return "CopyGIST";
	}

	@Override
	public String describe() {
		parameters.describe("dir", "The directory containing the GIST information.");
		parameters.describe("out", "The path of the descriptor file to create.");
		return "Creates a GIST descriptor file by copying descriptors from separate data files."; 
	}

}
