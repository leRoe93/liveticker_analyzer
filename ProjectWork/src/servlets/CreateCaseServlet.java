package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import de.dfki.mycbr.core.ICaseBase;
import de.dfki.mycbr.core.Project;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.Concept;
import de.dfki.mycbr.core.model.IntegerDesc;
import de.dfki.mycbr.core.model.StringDesc;
import de.dfki.mycbr.core.model.SymbolDesc;
import de.dfki.mycbr.core.retrieval.Retrieval;
import de.dfki.mycbr.core.retrieval.Retrieval.RetrievalMethod;

/**
 * Servlet implementation class CreateCaseServlet
 */
@WebServlet("/CreateCaseServlet")
public class CreateCaseServlet extends HttpServlet {
	private static String data_path = "/Users/tadeus/Desktop/";
	private static String projectName = "projectwork_db.prj";
	private static String conceptName = "player";
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateCaseServlet() {
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
		
		// MyCBR setup
		Project myproject;
		try {
			myproject = new Project(data_path + projectName);
			
			// Takes some time to load until access is possible
			while (myproject.isImporting()) {
				Thread.sleep(1000);
			}
			Concept myConcept = myproject.getConceptByID(conceptName);

			ICaseBase cb = myproject.getCB("player_cb");
			
			String instanceId = Integer.toString(myproject.getAllInstances().size());
			
			Instance instance = myConcept.addInstance("player_" + instanceId);
			
			StringDesc playerIdDesc = (StringDesc) myConcept.getAttributeDesc("player_id");
			StringDesc firstNameDesc = (StringDesc) myConcept.getAttributeDesc("first_name");
			StringDesc lastNameDesc = (StringDesc) myConcept.getAttributeDesc("last_name");
			StringDesc clubDesc = (StringDesc) myConcept.getAttributeDesc("current_club");

			
			IntegerDesc ageDesc = (IntegerDesc) myConcept.getAttributeDesc("age");
			
			IntegerDesc offensiveDesc = (IntegerDesc) myConcept.getAttributeDesc("offensive");
			IntegerDesc defensiveDesc = (IntegerDesc) myConcept.getAttributeDesc("defensive");
			IntegerDesc fairplayDesc = (IntegerDesc) myConcept.getAttributeDesc("fairplay");
			IntegerDesc passingDesc = (IntegerDesc) myConcept.getAttributeDesc("passing");
			IntegerDesc vitalityDesc = (IntegerDesc) myConcept.getAttributeDesc("vitality");
			IntegerDesc duelsDesc = (IntegerDesc) myConcept.getAttributeDesc("duels");


			SymbolDesc genderDesc = (SymbolDesc) myConcept.getAllAttributeDescs().get("gender");
			SymbolDesc leagueDesc = (SymbolDesc) myConcept.getAllAttributeDescs().get("league");
			SymbolDesc positionDesc = (SymbolDesc) myConcept.getAllAttributeDescs().get("preferred_position");
			
			instance.addAttribute(playerIdDesc, instanceId);
			instance.addAttribute(firstNameDesc, request.getParameter("first_name").toString());
			instance.addAttribute(lastNameDesc, request.getParameter("last_name").toString());
			instance.addAttribute(clubDesc, request.getParameter("current_club").toString());
			
			instance.addAttribute(offensiveDesc, 5);
			instance.addAttribute(defensiveDesc, 5);
			instance.addAttribute(fairplayDesc, 5);
			instance.addAttribute(passingDesc, 5);
			instance.addAttribute(vitalityDesc, 5);
			instance.addAttribute(duelsDesc, 5);
			
			instance.addAttribute(ageDesc, calculateAge(request.getParameter("birthday").toString()));
			
			instance.addAttribute(genderDesc, genderDesc.getAttribute(request.getParameter("gender").toString()));
			instance.addAttribute(leagueDesc, leagueDesc.getAttribute(request.getParameter("league").toString()));
			instance.addAttribute(positionDesc, positionDesc.getAttribute(request.getParameter("preferred_position").toString()));

			
			cb.addCase(instance);
			myproject.save();
			request.setAttribute("success", "Spieler wurde erfolgreich der Datenbasis hinzugefügt!");
			request.getRequestDispatcher("addPlayer.jsp").forward(request, response);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private int calculateAge(String birthday) {
		
		String[] birthdayArray = birthday.split("\\.");
		
		LocalDate birthdate = new LocalDate (Integer.parseInt(birthdayArray[2]),
				Integer.parseInt(birthdayArray[1]), 
				Integer.parseInt(birthdayArray[0]));
		LocalDate now = new LocalDate();
		Years age = Years.yearsBetween(birthdate, now);
		return age.getYears();
	}
				
}
