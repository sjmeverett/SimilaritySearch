package ndi.commands;

import ndi.commands.calculations.CalculateANMRRCommand;
import ndi.commands.calculations.CalculateDistancesCommand;
import ndi.commands.calculations.GetDistanceCommand;
import ndi.commands.calculations.GetMeanDistanceCommand;
import ndi.commands.calculations.TimeMetricCommand;
import ndi.commands.calculations.WriteDistancesCommand;
import ndi.commands.descriptors.CopyDescriptorsCommand;
import ndi.commands.descriptors.CopyEh80Command;
import ndi.commands.descriptors.CopyGistCommand;
import ndi.commands.descriptors.DescriptorInfoCommand;
import ndi.commands.descriptors.ExtractDescriptorCommand;
import ndi.commands.descriptors.SetStatsCommand;
import ndi.commands.indexes.BuildIndexCommand;
import ndi.commands.indexes.FindClosestPairsCommand;
import ndi.commands.indexes.IndexInfoCommand;
import ndi.commands.indexes.SearchAllCommand;
import ndi.commands.indexes.SearchCommand;
import ndi.commands.indexes.SearchTimeCommand;
import commandline.CommandLineProgram;

public class Program {
	public static void main(String[] args) {
		CommandLineProgram program = new CommandLineProgram();
		program.addCommand(new CopyDescriptorsCommand());
		program.addCommand(new TimeMetricCommand());
		program.addCommand(new DescriptorInfoCommand());
		program.addCommand(new GetDistanceCommand());
		program.addCommand(new WriteDistancesCommand());
		program.addCommand(new CalculateDistancesCommand());
		program.addCommand(new CopyEh80Command());
		program.addCommand(new BuildIndexCommand());
		program.addCommand(new SetStatsCommand());
		program.addCommand(new ExtractDescriptorCommand());
		program.addCommand(new SearchAllCommand());
		program.addCommand(new SearchTimeCommand());
		program.addCommand(new IndexInfoCommand());
		program.addCommand(new FindClosestPairsCommand());
		program.addCommand(new SearchCommand());
		program.addCommand(new CalculateANMRRCommand());
		program.addCommand(new GetMeanDistanceCommand());
		program.addCommand(new CopyGistCommand());
		program.run(args);
	}
}
