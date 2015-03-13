package org.kbs.archiver;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

import org.kbs.library.*;

import java.sql.*;

//@WebServlet(urlPatterns = {"/startupservlet"}, asyncSupported = false, 
//		loadOnStartup = 1, name = "StartupServlet", displayName = "startup") 
public class Startup extends HttpServlet {
	
	private static final long serialVersionUID = -2778109857170931469L;

	//Initialize global variables
	@Override
	public void init() throws ServletException {
    	System.out.println("bbs start up:");
    	/*
    	try {
	    	InputStream is = getServletContext().getResourceAsStream("/WEB-INF/log4j.properties");
	    	GlobalLogger.init(is);
	    	GlobalLogger.getLogger().info("BBS Start up");
	    	is.close();
    	} catch (IOException ex) {
			System.err.println("WEB-INF/log4j.properties error:"+ex.getMessage());
			throw new ServletException();
    	}
        try{
        	SequenceDB bdb=new SequenceDB();
        	bdb.init();
            Connection conn = DBTools.getConnection();
            String foo = "Got Connection "+conn.toString();
            // test
            Statement stmt = conn.createStatement();
            ResultSet rst =
                    stmt.executeQuery(
                    " select count(*) from board ");
            if(rst.next()) {
                foo=rst.getString(1);
                GlobalLogger.getLogger().info("load "+foo+"board");
            }
            conn.close();
            System.out.println(foo);
        }catch(Exception e) {
        	if (e instanceof SimpleException) {
        		SimpleException se=(SimpleException)e;
        		GlobalLogger.getLogger().error("Get Connection error:"+se.getInfo()+":"+se.getInfo(),se);
        	}
        	else
        		GlobalLogger.getLogger().error("Get Connection error:",e);
        }*/
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
    ServletException, IOException {
	}

    //Clean up resources
	@Override
    public void destroy() {
    	System.out.println("bbs shut down:");
	}
}
