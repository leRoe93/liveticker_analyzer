package servlets;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.Years;

import de.dfki.mycbr.core.ICaseBase;
import de.dfki.mycbr.core.Project;
import de.dfki.mycbr.core.casebase.Attribute;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.casebase.MultipleAttribute;
import de.dfki.mycbr.core.model.Concept;
import de.dfki.mycbr.core.model.IntegerDesc;
import de.dfki.mycbr.core.model.StringDesc;
import de.dfki.mycbr.core.model.SymbolDesc;
import de.dfki.mycbr.core.retrieval.Retrieval;
import de.dfki.mycbr.core.retrieval.Retrieval.RetrievalMethod;
import utils.MaintainerUtils;
import utils.PathingInfo;

/**
 * Servlet implementation class CreateCaseServlet
 */
@WebServlet("/CreatePlayerServlet")
public class CreatePlayerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private final static Logger LOGGER = Logger.getLogger(CreatePlayerServlet.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreatePlayerServlet() {
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
			myproject = new Project(PathingInfo.PROJECT_PATH + PathingInfo.PROJECT_NAME);
			
			// Necessary because it takes some time until MyCBR fully loads the project
			while (myproject.isImporting()) {
				Thread.sleep(1000);
			}
			Concept concept = myproject.getConceptByID(PathingInfo.CONCEPT_NAME);
			ICaseBase cb = myproject.getCB(PathingInfo.CASE_BASE);
			
			String existingInstance = playerAlreadyExists(concept, cb, request);
			
			// If the player seem to not exist in the case base
			if (existingInstance.isEmpty()) {
				
				String instanceId = Integer.toString(myproject.getAllInstances().size());
				
				Instance instance = concept.addInstance("player_" + instanceId);
				existingInstance = instance.getName();
				
				// Getting all the attribute descs from the concept to add attributes to the case instance
				StringDesc playerIdDesc = (StringDesc) concept.getAttributeDesc("player_id");
				StringDesc firstNameDesc = (StringDesc) concept.getAttributeDesc("first_name");
				StringDesc lastNameDesc = (StringDesc) concept.getAttributeDesc("last_name");
				StringDesc clubDesc = (StringDesc) concept.getAttributeDesc("current_club");
				StringDesc birthdayDesc = (StringDesc) concept.getAttributeDesc("birthday");

				IntegerDesc ageDesc = (IntegerDesc) concept.getAttributeDesc("age");
				IntegerDesc tickerCounterDesc = (IntegerDesc) concept.getAttributeDesc("ticker_counter");
				
				IntegerDesc offensiveDesc = (IntegerDesc) concept.getAttributeDesc("offensive");
				IntegerDesc defensiveDesc = (IntegerDesc) concept.getAttributeDesc("defensive");
				IntegerDesc fairplayDesc = (IntegerDesc) concept.getAttributeDesc("fairplay");
				IntegerDesc passingDesc = (IntegerDesc) concept.getAttributeDesc("passing");
				IntegerDesc vitalityDesc = (IntegerDesc) concept.getAttributeDesc("vitality");
				IntegerDesc duelsDesc = (IntegerDesc) concept.getAttributeDesc("duels");
				
				SymbolDesc genderDesc = (SymbolDesc) concept.getAllAttributeDescs().get("gender");
				SymbolDesc leagueDesc = (SymbolDesc) concept.getAllAttributeDescs().get("league");
				SymbolDesc positionDesc = (SymbolDesc) concept.getAllAttributeDescs().get("preferred_position");
				StringDesc entryDesc = (StringDesc) concept.getAttributeDesc("ticker_entries");

				LinkedList<Attribute> entryList = new LinkedList<Attribute>();
				MultipleAttribute<StringDesc> tickerEntries = new MultipleAttribute<StringDesc>((StringDesc) concept.getAttributeDesc("ticker_entries"), entryList);

				
				// Filling the instance with information
				instance.addAttribute(entryDesc, tickerEntries);
				instance.addAttribute(playerIdDesc, instanceId);
				instance.addAttribute(firstNameDesc, request.getParameter("first_name").toString());
				instance.addAttribute(lastNameDesc, request.getParameter("last_name").toString());
				instance.addAttribute(clubDesc, request.getParameter("current_club").toString());
				instance.addAttribute(birthdayDesc, request.getParameter("birthday").toString());

				instance.addAttribute(offensiveDesc, 50);
				instance.addAttribute(defensiveDesc, 50);
				instance.addAttribute(fairplayDesc, 50);
				instance.addAttribute(passingDesc, 50);
				instance.addAttribute(vitalityDesc, 50);
				instance.addAttribute(duelsDesc, 50);
				
				instance.addAttribute(ageDesc, MaintainerUtils.calculateAge(request.getParameter("birthday").toString()));

				instance.addAttribute(tickerCounterDesc, 0);

				
				instance.addAttribute(genderDesc, genderDesc.getAttribute(request.getParameter("gender").toString()));
				instance.addAttribute(leagueDesc, leagueDesc.getAttribute(request.getParameter("league").toString()));
				instance.addAttribute(positionDesc, positionDesc.getAttribute(request.getParameter("preferred_position").toString()));
				
				
				cb.addCase(instance);
				myproject.save();
				
				String answerWithProfileLink = "<form action=ProfileServlet method=post>"
						+ "<input type='hidden' name='instance' value='" + existingInstance + "'/>"
						+ "<button class='btn btn-success btn-link'>Spieler</button>"
						+ "</form>";
				
				request.setAttribute("success", answerWithProfileLink + " <span>wurde erfolgreich der Fallbasis hinzugefuegt!</span>");
			} else {
				
				String answerWithProfileLink = "<form action=ProfileServlet method=post>"
						+ "<input type='hidden' name='instance' value='" + existingInstance + "'/>"
						+ "<button class='btn btn-success btn-link'>Spieler</button>"
						+ "</form>";
				request.setAttribute("success", answerWithProfileLink + " <span>existiert bereits!</span>");
			}
			
			request.getRequestDispatcher("addPlayer.jsp").forward(request, response);


		} catch (Exception e) {
			LOGGER.error("Exception occured whilst opening the My CBR database: " + e.getMessage());
			e.printStackTrace();
		}
		
	}

	/**
	 * This method checks if the player already exists in the database by comparing a handful 
	 * of personal information about the instance the input.
	 * 
	 * @param concept the currently used concept
	 * @param cb the currently used case base
	 * @param request the request object provided by the addPlayer.jsp
	 * @return the name of the instance 
	 */
	private String playerAlreadyExists(Concept concept, ICaseBase cb, HttpServletRequest request) {
		String instanceName = "";
		
		// Perform check according to some reliable attribute parameters
		String firstName = request.getParameter("first_name").toString();
		String lastName = request.getParameter("last_name").toString();
		String currentClub = request.getParameter("current_club").toString();
		String birthday = request.getParameter("birthday").toString();
		String league = request.getParameter("league").toString();
		
		LOGGER.info("Comparing the new case to cases in the database.");
		for (Instance instance : cb.getCases()) {
			
			String instanceFirstName = instance.getAttForDesc(concept.getAttributeDesc("first_name")).getValueAsString();
			String instanceLastName = instance.getAttForDesc(concept.getAttributeDesc("last_name")).getValueAsString();
			String instanceCurrentClub = instance.getAttForDesc(concept.getAttributeDesc("current_club")).getValueAsString();
			String instanceBirthday = instance.getAttForDesc(concept.getAttributeDesc("birthday")).getValueAsString();
			String instanceLeague = instance.getAttForDesc(concept.getAttributeDesc("league")).getValueAsString();

			LOGGER.info("Instance first name: " + instanceFirstName + " equals " + firstName + "?");
			LOGGER.info("Instance last name: " + instanceLastName + " equals " + lastName + "?");
			LOGGER.info("Instance birthday: " + instanceBirthday + " equals " + birthday + "?");
			LOGGER.info("Instance current club: " + instanceCurrentClub + " equals " + currentClub + "?");
			LOGGER.info("Instance league: " + instanceLeague + " equals " + league + "?");

			// Current solution is to have a check for every informational attribute of the player for its entity
			// Not reliable for all scenarios, needs to be enhanced
			if (instanceFirstName.equals(firstName) && instanceLastName.equals(lastName) 
					&& instanceCurrentClub.equals(currentClub) && instanceBirthday.equals(birthday)
					&& instanceLeague.equals(league)) {
				
				LOGGER.warn("Yes, everything matches!");
				return instance.getName();
			}
		}
		
		return instanceName;
	}
	
	
				
}
