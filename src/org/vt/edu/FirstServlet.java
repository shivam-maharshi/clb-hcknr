package org.vt.edu;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FirstServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<String, String> paramList;

	public void init() {
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		Enumeration<?> e = config.getInitParameterNames();
		paramList = new HashMap<String, String>();
		while (e.hasMoreElements()) {
			String paramName = (String) e.nextElement();
			paramList.put(paramName, config.getInitParameter(paramName));
		}
		System.out.println("init() With Arguments");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><body>");
		out.println(" Welcome to Java EE Perspective ");
		out.println("<br>" + new Date());
		out.println(paramList);
		out.println("</body></html>");

		ServletContext ctx = getServletContext();
		out.println("Language: " + ctx.getInitParameter("Language"));
		/*
		 * ServletConfig config=getServletConfig(); out.println("<br>Country: "
		 * +getInitParameter("country")); Enumeration
		 * e=config.getInitParameterNames(); while(e.hasMoreElements()) { String
		 * paramName=(String)e.nextElement();
		 * out.println("<br>"+paramName+"=="+config.getInitParameter(paramName))
		 * ; }
		 */
		out.close();
	}

}
