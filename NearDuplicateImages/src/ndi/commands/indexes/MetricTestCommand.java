package ndi.commands.indexes;

import ndi.files.DescriptorFileLoader;
import commandline.Command;
import commandline.Parameters;

public class MetricTestCommand implements Command {
	private DescriptorFileLoader loader;
	
	@Override
	public void init(Parameters parameters) {
		loader = new DescriptorFileLoader(parameters);
	}

	@Override
	public void run() {
		
	}

	@Override
	public String getName() {
		return "MetricTest";
	}

	@Override
	public String describe() {
		return "";
	}

}
