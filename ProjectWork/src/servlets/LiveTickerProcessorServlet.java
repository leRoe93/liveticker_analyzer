package servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.util.Pair;


/**
 * Servlet implementation class LiveTickerProcessorServlet
 */
@WebServlet("/LiveTickerProcessorServlet")
public class LiveTickerProcessorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LiveTickerProcessorServlet() {
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
		// TODO Auto-generated method stub
		doGet(request, response);
		
		// ?subpage=aufstellung
		Document doc = Jsoup.connect(request.getParameter("url_lt")).get();
		Elements links = doc.select(".rowBright");
		
		ArrayList<String> tickerEntries = new ArrayList<String>();
		String content = "";
		for (Element link : links) {
			content = link.getElementsByClass("text").html();
			tickerEntries.add(content.substring(content.indexOf("|") + 2).replaceAll("<[^>]*>", ""));
		}
		
		// Dynamic table creation for results.jsp
		StringBuilder tableContent = new StringBuilder();

		tableContent.append("<table class='table' id='tickerEntries'>");
		tableContent.append("<tr>" + "<th>Ticker Eintrag</th>" + "<th>Erkannte Spieler</th>" + "<th>Erkannte Situation</th>" + "</tr>");

		for (String entry : tickerEntries) {

			tableContent.append("<tr>");
			tableContent.append("<td>" + entry + "</td>");
			tableContent.append("<td>... In Arbeit ...</td>");
			tableContent.append("<td>... In Arbeit ...</td>");

			tableContent.append("</tr>");

		}

		tableContent.append("</table>");

		request.setAttribute("tickerEntries", tableContent);
		
		request.getRequestDispatcher("progression.jsp").forward(request, response);
	}

}
