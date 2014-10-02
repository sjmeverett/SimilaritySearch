package ndi.webapi.servlets;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ndi.webapi.domain.AddDescriptorRequest;
import ndi.webapi.domain.Config;
import ndi.webapi.domain.Image;
import ndi.webapi.domain.ImageService;
import ndi.webapi.domain.ServiceException;
import ndi.webapi.domain.ServiceExceptionModel;

import com.google.gson.Gson;

/**
 * Servlet implementation class DescriptorsServlet
 */
@WebServlet("/descriptors")
public class DescriptorsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ImageService service;
	private Gson gson;
	
    /**
     * @throws UnknownHostException 
     * @see HttpServlet#HttpServlet()
     */
    public DescriptorsServlet() throws UnknownHostException {
    	super();
        service = new ImageService();
        gson = Config.getGson();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		
		try {
			AddDescriptorRequest addDescriptorRequest = gson.fromJson(request.getReader(), AddDescriptorRequest.class);
			Image image = service.addDescriptor(addDescriptorRequest);
			gson.toJson(image, Image.class);
		} catch (ServiceException ex) {
			response.setStatus(400);
			gson.toJson(new ServiceExceptionModel(ex), response.getWriter());
		}
	}
}
