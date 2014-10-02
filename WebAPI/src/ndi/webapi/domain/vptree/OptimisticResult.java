package ndi.webapi.domain.vptree;

public class OptimisticResult<T> {
	private boolean success;
	private T result;
	
	public OptimisticResult(boolean success, T result) {
		this.success = success;
		this.result = result;
	}
	
	public boolean success() {
		return success;
	}
	
	public T getResult() {
		return result;
	}
}
