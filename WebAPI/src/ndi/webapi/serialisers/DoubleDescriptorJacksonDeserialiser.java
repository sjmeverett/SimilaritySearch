package ndi.webapi.serialisers;

import java.io.IOException;

import metricspaces._double.DoubleDescriptor;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DoubleDescriptorJacksonDeserialiser extends JsonDeserializer<DoubleDescriptor> {

	@Override
	public DoubleDescriptor deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		double[] data = parser.readValueAs(double[].class);
		return new DoubleDescriptor(data);
	}

}
