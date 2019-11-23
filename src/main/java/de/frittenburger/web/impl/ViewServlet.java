package de.frittenburger.web.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.web.interfaces.ApplicationState;

public class ViewServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	
	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ApplicationState applicationState = ApplicationStateImpl.getInstance();
		String pathInfo = request.getPathInfo();
		
		int skip = 0;
		String skipParam = request.getParameter("skip");
		if(skipParam != null)
			skip = Integer.parseInt(skipParam);
		
		int limit = 10;
		String limitParam = request.getParameter("limit");
		if(limitParam != null)
			limit = Integer.parseInt(limitParam);
		
		
		if (pathInfo.equals("/status")) {
			
			Map<String,Object> status = new HashMap<>();
			
			status.put("jobs", applicationState.getJobState());
			status.put("files", applicationState.getFilesState());

			ObjectMapper mapper = new ObjectMapper();
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");			
			response.getWriter().print(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(status));
			return;
		}
		
		if (pathInfo.equals("/errors")) {
			List<String> errors = applicationState.getErrors(skip,limit);
			
			ObjectMapper mapper = new ObjectMapper();
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");			
			response.getWriter().print(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors));
			return;
		}
		
		if (pathInfo.equals("/uploads")) {
			List<String> uploads = applicationState.getUploads(skip,limit);
			
			ObjectMapper mapper = new ObjectMapper();
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");			
			response.getWriter().print(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(uploads));
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/html");		
		OutputStream out = response.getOutputStream();
		out.write(("Hello "+pathInfo).getBytes());
		out.flush();
		

	}
	

	
}