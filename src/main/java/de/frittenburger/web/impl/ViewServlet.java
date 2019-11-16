package de.frittenburger.web.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;





import com.fasterxml.jackson.databind.ObjectMapper;

import de.immerarchiv.job.impl.ApplicationState;



public class ViewServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	* 
	*/
	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		String pathInfo = request.getPathInfo();
		
		
		if (pathInfo.equals("/jobs")) {
			
			Map<String,Object> resp = ApplicationState.get();

			ObjectMapper mapper = new ObjectMapper();
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");			
			response.getWriter().print(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp));
			return;
		}
		
		
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/html");		
		OutputStream out = response.getOutputStream();
		out.write(("Hello "+pathInfo).getBytes());
		out.flush();
		

	}
	

	
}