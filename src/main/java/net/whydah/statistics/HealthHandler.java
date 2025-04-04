package net.whydah.statistics;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.ByteBufferOutputStream;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HealthHandler extends AbstractHandler {

	HealthReport report = new HealthReport("net.whydah.service", "Whydah-StatisticsService", "Whydah-StatisticsService");
	
    @Override
    public void handle(String target, Request baseRequest, 
                       HttpServletRequest request,
                       HttpServletResponse response) 
                       throws IOException, ServletException {

    	
    	 try {



    		 String method = request.getMethod();

    		 // little cheat for common request
    		 if ( HttpMethod.GET.is(method) && target.equals("/reporter/health")) {
    			 
    			 baseRequest.setHandled(true);

        		
    			 report.put("Status", String.valueOf(true));

    			 Charset charset = StandardCharsets.UTF_8;
    			 // Get writer used
    			 ByteBuffer buffer = baseRequest.getResponse().getHttpOutput().getBuffer();
    			 ByteBufferOutputStream out = new ByteBufferOutputStream(buffer);
    			 PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, charset));

    			 // Set content type, encoding and write response

    			 response.setContentType(MimeTypes.Type.APPLICATION_JSON_UTF_8.asString());
    			 response.setCharacterEncoding(charset.name());
    			 writer.print(report.getReport());
    			 


    			 writer.flush();
    			 baseRequest.getHttpChannel().sendResponseAndComplete();
    		 }
    	 } catch (BufferOverflowException e) {
    		 baseRequest.getResponse().resetContent();
    	 }

    	
    }
}