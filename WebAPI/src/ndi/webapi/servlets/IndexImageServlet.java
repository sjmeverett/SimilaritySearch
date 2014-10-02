package ndi.webapi.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ndi.webapi.domain.Config;
import ndi.webapi.domain.IndexImageRequest;
import ndi.webapi.domain.IndexImageResponse;
import ndi.webapi.domain.ServiceException;
import ndi.webapi.domain.ServiceExceptionModel;
import ndi.webapi.domain.vptree.IndexService;

import com.google.gson.Gson;

/**
 * Servlet implementation class AddToIndexServlet
 */
@WebServlet("/indeximage")
public class IndexImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Gson gson;
    private IndexService service;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IndexImageServlet() {
        super();
        gson = Config.getGson();
        service = new IndexService();
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
		try {
			IndexImageRequest indexImageRequest = gson.fromJson(request.getReader(), IndexImageRequest.class);
			IndexImageResponse indexImageResponse = service.indexImage(indexImageRequest);
			gson.toJson(indexImageResponse, response.getWriter());
		} catch (ServiceException ex) {
			gson.toJson(new ServiceExceptionModel(ex), response.getWriter());
		}
	}

}
