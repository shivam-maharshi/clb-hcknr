package snip.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

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

	public void init() {
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		Data data = dataService.getData(request.getParameter("v"));
		out.print(new Gson().toJson(data));
		out.close();
	}

}
