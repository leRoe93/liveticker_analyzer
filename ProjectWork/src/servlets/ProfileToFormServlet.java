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

/**
 * Servlet implementation class ProfileToCaseBaseServlet
 */
@WebServlet("/ProfileToFormServlet")
public class ProfileToFormServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProfileToFormServlet() {
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
		Document doc = Jsoup.connect(request.getParameter("url_profile")).get();

		Elements rightColumn = doc.select(".column-right");
		Elements profileName = doc.select(".profile-name");
		Elements clubName = doc.select(".club-name");

		
		String teamType = rightColumn.get(0).getElementsByClass("inner").get(0).getElementsByClass("profile-value").get(0).html();
		String birthday = rightColumn.get(0).getElementsByClass("inner").get(0).getElementsByClass("profile-value").get(1).html();
		String currentClub = clubName.get(0).html();
		String firstName = profileName.get(0).html().split(" ")[0];
		String lastName = profileName.get(0).html().split(" ")[1];
		
		String gender = "";
		
		if (teamType.equals("Herren")) {
			gender = "maennlich";
		} else {
			gender = "weiblich";
		}
		
		request.setAttribute("firstName", firstName);
		request.setAttribute("birthday", birthday);
		request.setAttribute("currentClub", currentClub);
		request.setAttribute("lastName", lastName);
		request.setAttribute("gender", gender);
		
		
		request.getRequestDispatcher("addPlayer.jsp").forward(request, response);

	
	}

}
