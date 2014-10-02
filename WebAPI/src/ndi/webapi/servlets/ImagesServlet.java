package ndi.webapi.servlets;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ndi.webapi.domain.AddImageRequest;
import ndi.webapi.domain.Config;
import ndi.webapi.domain.Image;
import ndi.webapi.domain.ImageService;
import ndi.webapi.domain.ServiceException;
import ndi.webapi.domain.ServiceExceptionModel;

import com.google.gson.Gson;

/**
 * Servlet implementation class ImagesServlet
 */
@WebServlet("/images")
public class ImagesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ImageService service;
	private Gson gson;
       
    /**
     * @throws UnknownHostException 
     * @see HttpServlet#HttpServlet()
     */
    public ImagesServlet() throws UnknownHostException {
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
			AddImageRequest addImageRequest = gson.fromJson(request.getReader(), AddImageRequest.class);
			Image image = service.add(addImageRequest);

			response.setStatus(201);
			gson.toJson(image, response.getWriter());
		} catch (ServiceException ex) {
			response.setStatus(400);
			gson.toJson(new ServiceExceptionModel(ex), response.getWriter());
		}
	}

}
