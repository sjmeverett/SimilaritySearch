package ndi.webapi.serialisers;

import java.lang.reflect.Type;

import metricspaces._double.DoubleDescriptor;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class DoubleDescriptorGsonDeserialiser implements JsonDeserializer<DoubleDescriptor> {

	@Override
	public DoubleDescriptor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		double[] data = context.deserialize(json, double[].class);
		return new DoubleDescriptor(data);
	}

}
