package ndi.webapi.domain;

public class ServiceExceptionModel {
	private String error;
	
	public ServiceExceptionModel(ServiceException exception) {
		this.error = exception.getMessage();
	}
	
	public String getError() {
		return error;
	}
}
