package org.kbs.archiver;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.apache.jasper.servlet.JspServlet;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

public class TestJsp {
	ServletTester tester = new ServletTester();
	HttpTester request = new HttpTester();
	HttpTester response = new HttpTester();

	@Before
	public void setUp() throws Exception {
		tester.setResourceBase("./src/main/webapp");
		tester.addServlet(JspServlet.class, "*.jsp");
		tester.start();

		request.setMethod("GET");
		request.setVersion("HTTP/1.0");	}

	@Test
	public void testIndex() {
		request.setURI("/index.jsp");
		try {
			response.parse(tester.getResponses(request.generate()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fail("send request error:"+e.getMessage());
		}

		assertTrue(response.getMethod() == null);
		assertEquals(200, response.getStatus());
//		System.out.println(response.getContent());
		assertEquals("Hello World!\n", response
				.getContent());
	}

}
