package ndi;

import metricspaces.indexes.pivotselectors.DivergencePivotSelector;
import metricspaces.indexes.pivotselectors.KMeansPivotSelector;
import metricspaces.indexes.pivotselectors.PivotSelector;
import metricspaces.indexes.pivotselectors.RandomPivotSelector;
import commandline.ParameterException;
import commandline.Parameters;

public class PivotSelectorLoader {
	private Parameters parameters;
	
	public PivotSelectorLoader(Parameters parameters) {
		this.parameters = parameters;
	}
	
	public PivotSelector getPivotSelector() throws ParameterException {
		String pivotSelectorName = parameters.get("pivotSelection");
		
		if (pivotSelectorName == null || pivotSelectorName.equals("random")) {
			return new RandomPivotSelector();
		}
		else if (pivotSelectorName.equals("kmeans")) {
			return new KMeansPivotSelector();
		}
		else if (pivotSelectorName.equals("divergence")) {
			return new DivergencePivotSelector(parameters.getInt("maxCandidates", 1000));
		}
		else {
			throw new ParameterException("Unknown pivot selection method " + pivotSelectorName);
		}
	}
	
	public void describe() {
		parameters.describe("pivotSelection", "The means by which pivots are selected for EP: random, kmeans or divergence.");
		parameters.describe("maxCandidates", "For divergence pivot selector: the maximum number of points to consider "
				+ "as pivot points (default 1000).");
	}
}
