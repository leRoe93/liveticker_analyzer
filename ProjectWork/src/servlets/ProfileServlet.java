package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	private static String projectName = "projektarbeit_db.prj";
	private static String conceptName = "spieler";
	private static final long serialVersionUID = 1L;
       
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
			
			// Takes some time to load until access is possible
			while (myproject.isImporting()) {
				Thread.sleep(1000);
			}
			

			Instance instance = myproject.getInstance(request.getParameter("instance"));
		
			
			request.setAttribute("first_name", instance.getAttForDesc(myConcept.getAttributeDesc("vorname")).getValueAsString());
			request.setAttribute("last_name", instance.getAttForDesc(myConcept.getAttributeDesc("name")).getValueAsString());
			request.setAttribute("gender", instance.getAttForDesc(myConcept.getAttributeDesc("geschlecht")).getValueAsString());
			request.setAttribute("age", instance.getAttForDesc(myConcept.getAttributeDesc("alter")).getValueAsString());
			request.setAttribute("current_club", instance.getAttForDesc(myConcept.getAttributeDesc("aktueller_verein")).getValueAsString());
			request.setAttribute("preferred_position", instance.getAttForDesc(myConcept.getAttributeDesc("bevorzugte_position")).getValueAsString());
			request.setAttribute("league", instance.getAttForDesc(myConcept.getAttributeDesc("spielklasse")).getValueAsString());
			request.setAttribute("offensive", instance.getAttForDesc(myConcept.getAttributeDesc("offensive")).getValueAsString());
			request.setAttribute("defensive", instance.getAttForDesc(myConcept.getAttributeDesc("defensive")).getValueAsString());
			request.setAttribute("fairplay", instance.getAttForDesc(myConcept.getAttributeDesc("fairplay")).getValueAsString());
			request.setAttribute("duels", instance.getAttForDesc(myConcept.getAttributeDesc("zweikampf")).getValueAsString());
			request.setAttribute("vitality", instance.getAttForDesc(myConcept.getAttributeDesc("vitalitaet")).getValueAsString());
			request.setAttribute("passes", instance.getAttForDesc(myConcept.getAttributeDesc("zuspiele")).getValueAsString());
			request.setAttribute("player_id", instance.getAttForDesc(myConcept.getAttributeDesc("spieler_id")).getValueAsString());
			request.setAttribute("ticker_entries", instance.getAttForDesc(myConcept.getAttributeDesc("ticker_eintraege")).getValueAsString().split(";"));
			
			

			request.getRequestDispatcher("/profile.jsp").forward(request, response);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
