package ndi.webapi.serialisers;

import java.io.IOException;

import metricspaces._double.DoubleDescriptor;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DoubleDescriptorJacksonSerialiser extends JsonSerializer<DoubleDescriptor> {
	@Override
	public void serialize(DoubleDescriptor descriptor, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
		provider.defaultSerializeValue(descriptor.getData(), generator);
	}
}
