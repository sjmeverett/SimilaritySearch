package ndi.webapi.serialisers;

import java.lang.reflect.Type;

import metricspaces._double.DoubleDescriptor;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DoubleDescriptorGsonSerialiser implements JsonSerializer<DoubleDescriptor> {
	@Override
	public JsonElement serialize(DoubleDescriptor descriptor, Type type, JsonSerializationContext context) {
		return context.serialize(descriptor.getData());
	}
}
