package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.dfki.mycbr.core.ICaseBase;
import de.dfki.mycbr.core.Project;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.Concept;

/**
 * Servlet implementation class ProfileServlet
 */
@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
	private static String data_path = "/Users/tadeus/Desktop/";
	private static String projectName = "projectwork_db.prj";
	private static String conceptName = "player";
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(ProfileServlet.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProfileServlet() {
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
				
		Project myproject;
		try {
			myproject = new Project(data_path+projectName);
			Concept myConcept = myproject.getConceptByID(conceptName);
			ICaseBase cb = myproject.getCB("player_cb");
			
			// Necessary because it takes some time until MyCBR fully loads the project
			while (myproject.isImporting()) {
				Thread.sleep(1000);
			}
			
			// Force retrieve player from case base instead of project due to consistency
			Instance player = null;
			for (Instance playerInCb : cb.getCases()) {
				if (playerInCb.getName().equals(request.getParameter("instance"))) {
					player = playerInCb;
				}
			}
		
			// Provide all necessary information for the profile retrieved from the case
			request.setAttribute("first_name", player.getAttForDesc(myConcept.getAttributeDesc("first_name")).getValueAsString());
			request.setAttribute("last_name", player.getAttForDesc(myConcept.getAttributeDesc("last_name")).getValueAsString());
			request.setAttribute("gender", player.getAttForDesc(myConcept.getAttributeDesc("gender")).getValueAsString());
			request.setAttribute("birthday", player.getAttForDesc(myConcept.getAttributeDesc("birthday")).getValueAsString());
			request.setAttribute("age", player.getAttForDesc(myConcept.getAttributeDesc("age")).getValueAsString());
			request.setAttribute("current_club", player.getAttForDesc(myConcept.getAttributeDesc("current_club")).getValueAsString());
			request.setAttribute("preferred_position", player.getAttForDesc(myConcept.getAttributeDesc("preferred_position")).getValueAsString());
			request.setAttribute("league", player.getAttForDesc(myConcept.getAttributeDesc("league")).getValueAsString());
			request.setAttribute("offensive", player.getAttForDesc(myConcept.getAttributeDesc("offensive")).getValueAsString());
			request.setAttribute("defensive", player.getAttForDesc(myConcept.getAttributeDesc("defensive")).getValueAsString());
			request.setAttribute("fairplay", player.getAttForDesc(myConcept.getAttributeDesc("fairplay")).getValueAsString());
			request.setAttribute("duels", player.getAttForDesc(myConcept.getAttributeDesc("duels")).getValueAsString());
			request.setAttribute("vitality", player.getAttForDesc(myConcept.getAttributeDesc("vitality")).getValueAsString());
			request.setAttribute("passing", player.getAttForDesc(myConcept.getAttributeDesc("passing")).getValueAsString());
			request.setAttribute("player_id", player.getAttForDesc(myConcept.getAttributeDesc("player_id")).getValueAsString());
			request.setAttribute("ticker_entries", player.getAttForDesc(myConcept.getAttributeDesc("ticker_entries")).getValueAsString().split(";"));
			
			// Dynamic button that is able to delete the player from the database
			String buttonHtml = "<form action=DeletePlayerServlet method=post> "	
					+ "<input type='hidden' name='instance' value='" + player.getName() + "'/>"
					+ "<button class='btn btn-danger'>Spieler loeschen!</button>"
					+ "</form>";
			
			request.setAttribute("deleteButton", buttonHtml);

			request.getRequestDispatcher("/profile.jsp").forward(request, response);
			
		} catch (Exception e) {
			if (e.getClass() == NullPointerException.class) {
				LOGGER.error("Player : '" + request.getParameter("instance") + "' could not be found in case base: " + e.getStackTrace());
			} else {
				LOGGER.error("Exception occured whilst opening the project/case base: " + e.getStackTrace());
			}
		} 
		
	}

}
