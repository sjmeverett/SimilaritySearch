package ndi.webapi.domain;

public interface OptimisticUpdateCallback<T> {
	boolean callback(T current, Object[] params);
}
