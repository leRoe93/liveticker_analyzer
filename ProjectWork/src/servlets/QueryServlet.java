package servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class QueryServlet
 */
@WebServlet("/QueryServlet")
public class QueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
		
		String search = "Die Suche mit folgenden Parametern:\n";
		
		Map<String, String[]> parameterMap = request.getParameterMap();
		for (String attribute : parameterMap.keySet()) {
			search += "Attribut: " + attribute + ", Wert: ";
			
			
			search += request.getAttribute(attribute) + "\n";
				
		}
		System.out.println(request.getParameter("offensive"));
		search += " ... ergab folgende Treffer:";
		
		request.setAttribute("search", search);
		
		request.getRequestDispatcher("/results.jsp").forward(request, response);
	}

}
