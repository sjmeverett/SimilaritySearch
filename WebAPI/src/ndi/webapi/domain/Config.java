package ndi.webapi.domain;

import java.net.UnknownHostException;

import metricspaces._double.DoubleDescriptor;
import ndi.webapi.serialisers.DoubleDescriptorGsonDeserialiser;
import ndi.webapi.serialisers.DoubleDescriptorGsonSerialiser;
import ndi.webapi.serialisers.DoubleDescriptorJacksonDeserialiser;
import ndi.webapi.serialisers.DoubleDescriptorJacksonSerialiser;

import org.jongo.Jongo;
import org.jongo.marshall.jackson.JacksonMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

//a poor man's DI
public class Config {
	private static Jongo jongo;
	private static Gson gson;
	
	public static Jongo getJongo() {
		if (jongo == null) {
			try {
				String connectionString = System.getProperty("mongodbConnection");
				MongoClientURI uri = new MongoClientURI(connectionString);
				DB db = new MongoClient(uri).getDB(uri.getDatabase());
			
				jongo = new Jongo(db,
					new JacksonMapper.Builder()
						.addSerializer(DoubleDescriptor.class, new DoubleDescriptorJacksonSerialiser())
						.addDeserializer(DoubleDescriptor.class, new DoubleDescriptorJacksonDeserialiser())
						.build());
			} catch (UnknownHostException ex) {
				//pretty much need to die here...
				throw new RuntimeException(ex);
			}
		}
		
		return jongo;
	}
	
	
	public static Gson getGson() {
		if (gson == null) {
			gson = new GsonBuilder()
				.serializeSpecialFloatingPointValues()
				.registerTypeAdapter(DoubleDescriptor.class, new DoubleDescriptorGsonSerialiser())
				.registerTypeAdapter(DoubleDescriptor.class, new DoubleDescriptorGsonDeserialiser())
				.create();
		}
		
		return gson;
	}
}
