package metricspaces.files;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class LargeBinaryFile {
	protected RandomAccessFile file;
    protected ByteBuffer buffer;
    protected final String path;
    protected final boolean writable;
    
    public LargeBinaryFile(String path) throws IOException {
    	this.path = path;
		file = new RandomAccessFile(path, "r");
        FileChannel channel = file.getChannel();
        buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        writable = false;
    }
    
    public LargeBinaryFile(String path, int size) throws IOException {
    	this.path = path;
		file = new RandomAccessFile(path, "rw");
        FileChannel channel = file.getChannel();
        buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
        writable = true;
    }
    
    public void resize(int size) throws IOException {
    	int position = buffer.position();
    	file.close();
    	file = new RandomAccessFile(path, "rw");
        FileChannel channel = file.getChannel();
        buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
        buffer.position(position);
    }
    
    public String getString() {
		int length = buffer.getInt();
		
		//if it's bigger than 500 it has probably been read wrong
		if (length > 500)
			throw new IllegalStateException("Corrupt string detected.");
		
		byte[] bytes = new byte[length];
		buffer.get(bytes);
		
		return new String(bytes, StandardCharsets.ISO_8859_1);
	}
	
	public void putString(String str) {
		if (!writable)
			throw new IllegalStateException("file is not writable");
		
		byte[] bytes = str.getBytes(StandardCharsets.ISO_8859_1);
		buffer.putInt(bytes.length);
		buffer.put(bytes);
	}
	
	public ByteBuffer getBuffer() {
		return buffer;
	}
	
	public RandomAccessFile getFile() {
		return file;
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean isWritable() {
		return writable;
	}
}
