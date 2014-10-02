package ndi.webapi.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ndi.webapi.domain.Config;
import ndi.webapi.domain.ExtractorRequest;
import ndi.webapi.domain.ExtractorResponse;
import ndi.webapi.domain.ExtractorService;
import ndi.webapi.domain.ServiceException;
import ndi.webapi.domain.ServiceExceptionModel;

import com.google.gson.Gson;

/**
 * Servlet implementation class ExtractorServlet
 */
@WebServlet("/extract")
public class ExtractorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ExtractorService service;
    private Gson gson;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExtractorServlet() {
        super();
        service = new ExtractorService();
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
			ExtractorRequest extractorRequest = gson.fromJson(request.getReader(), ExtractorRequest.class);
			ExtractorResponse extractorResponse = service.extract(extractorRequest);
			
			gson.toJson(extractorResponse, response.getWriter());
		} catch (ServiceException ex) {
			response.setStatus(400);
			gson.toJson(new ServiceExceptionModel(ex), response.getWriter());
		}
	}

}
