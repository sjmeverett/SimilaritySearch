package metricspaces.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;

public class LargeBinaryFile {
	private static final int MAX_STRING_LENGTH = 500;
	
	protected RandomAccessFile file;
	public FileChannel channel;
    protected ByteBuffer buffer;
    protected final String path;
    protected final boolean writable, exists;
    
    public LargeBinaryFile(String path, boolean writable) throws IOException {
    	this.path = path;
    	File f = new File(path);
    	exists = f.exists();
		file = new RandomAccessFile(f, writable ? "rw" : "r");
		channel = file.getChannel();
		
		long size;
		if (!exists) {
			size = 512;
		} else {
			size = channel.size();
		}
        
        buffer = channel.map(writable ? MapMode.READ_WRITE : MapMode.READ_ONLY, 0, size);
        this.writable = writable;
    }
    
    public LargeBinaryFile(String path, int size) throws IOException {
    	this.path = path;
    	File f = new File(path);
    	exists = f.exists();
		file = new RandomAccessFile(f, "rw");
        channel = file.getChannel();
        buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
        writable = true;
    }
    
    public void resize(int size) throws IOException {
    	int position = buffer.position();
        buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
        buffer.position(position);
    }
    
    public String getString() {
		int length = buffer.getInt();
		
		//if it's quite long it has probably been read wrong
		if (length > MAX_STRING_LENGTH)
			throw new IllegalStateException("corrupt string detected.");
		
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
	
	public boolean exists() {
		return exists;
	}
}
