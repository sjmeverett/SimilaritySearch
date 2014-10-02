package ndi.webapi.domain;

public class ServiceException extends Exception {
	private static final long serialVersionUID = -6619172505209036144L;
	
	public ServiceException(String message) {
		super(message);
	}
}
