package de.frittenburger.web.impl;

import java.net.URL;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;

import de.frittenburger.web.interfaces.WebServer;

public class WebServerImpl implements WebServer {

	private Server server;
	private int port;

	public WebServerImpl(int port) {
		this.port = port;
	}

	@Override
	public void start() throws Exception {
	     	this.server = new Server(port);

	      

	        //data for files to assign with meta data
	        ResourceHandler rh1 = new ResourceHandler();
	        ContextHandler context1 = new ContextHandler();
	        context1.setContextPath("/");
	        
	        URL url = this.getClass().getClassLoader().getResource("htdocs");
	        context1.setBaseResource(Resource.newResource(url.toURI()));
	        context1.setHandler(rh1);

	     
	        
	        ServletContextHandler context2 = new ServletContextHandler(ServletContextHandler.SESSIONS);
	        context2.setContextPath("/");
	        context2.setResourceBase("data");
	  
	        
	        ContextHandlerCollection contexts = new ContextHandlerCollection();
	        contexts.setHandlers(new Handler[] {  context1 , context2 });		        
	        server.setHandler(contexts);
	        context2.addServlet(ViewServlet.class, "/api/v1/*");
	       
	       
	        
	        server.start();
	}

	@Override
	public void stop() throws Exception {
        server.stop();
        server.join();
	}

}
