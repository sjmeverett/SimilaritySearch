package ndi.commands;

import ndi.commands.calculations.CalculateDistancesCommand;
import ndi.commands.calculations.GetDistanceCommand;
import ndi.commands.calculations.GetMeanDistanceCommand;
import ndi.commands.calculations.TimeMetricCommand;
import ndi.commands.calculations.WriteDistancesCommand;
import ndi.commands.descriptors.CopyDescriptorsCommand;
import ndi.commands.descriptors.CopyEh80Command;
import ndi.commands.descriptors.CopyGistCommand;
import ndi.commands.descriptors.ExtractDescriptorCommand;
import ndi.commands.descriptors.RandomDescriptorCommand;
import ndi.commands.descriptors.RelativeDescriptorCommand;
import ndi.commands.indices.BuildIndexCommand;
import ndi.commands.indices.NearestNeighbourCommand;
import ndi.commands.indices.SearchAllCommand;
import ndi.commands.indices.SearchCommand;
import ndi.commands.indices.SearchTimeCommand;
import commandline.CommandLineProgram;

public class Program {
	public static void main(String[] args) {
		CommandLineProgram program = new CommandLineProgram();
		program.addCommand(new CopyDescriptorsCommand());
		program.addCommand(new TimeMetricCommand());
		program.addCommand(new GetDistanceCommand());
		program.addCommand(new WriteDistancesCommand());
		program.addCommand(new CalculateDistancesCommand());
		program.addCommand(new CopyEh80Command());
		program.addCommand(new BuildIndexCommand());
		program.addCommand(new ExtractDescriptorCommand());
		program.addCommand(new SearchAllCommand());
		program.addCommand(new SearchTimeCommand());
		program.addCommand(new SearchCommand());
		program.addCommand(new GetMeanDistanceCommand());
		program.addCommand(new CopyGistCommand());
		program.addCommand(new RandomDescriptorCommand());
		program.addCommand(new RelativeDescriptorCommand());
		program.addCommand(new NearestNeighbourCommand());
		program.run(args);
	}
}
