package snip.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import snip.pojo.Data;
import snip.service.DataService;
import snip.utils.OutputFormatter;

/**
 * Web end point for social networks data integration services as Web APIs.
 * 
 * @author shivam.maharshi
 */
public class DataApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataService dataService = new DataService();
	private OutputFormatter outputFormatter = new OutputFormatter();

	public void init() {
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		Data data = dataService.getData(request.getParameter("v"));
		out.print(outputFormatter.format(data));
		out.close();
	}

}
