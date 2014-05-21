package metricspaces;

import static org.junit.Assert.assertEquals;
import metricspaces.RelativePath;

import org.junit.Test;

public class RelativePathTest {
	
	@Test
	public void sameDirectoryTest() {
		RelativePath path = new RelativePath("/foo/bar");
		String p = path.getRelativeTo("/foo/baz");
		assertEquals("bar", p);
	}
	
	
	@Test
	public void subDirectoryTest() {
		RelativePath path = new RelativePath("/foo/bar/baz");
		String p = path.getRelativeTo("/foo/baz");
		assertEquals("bar/baz", p);
	}

	
	@Test
	public void parentDirectoryTest() {
		RelativePath path = new RelativePath("/foo/fum");
		String p = path.getRelativeTo("/foo/bar/baz");
		assertEquals("../fum", p);
	}
}
