package servlets;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.dfki.mycbr.core.ICaseBase;
import de.dfki.mycbr.core.Project;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.AttributeDesc;
import de.dfki.mycbr.core.model.Concept;
import de.dfki.mycbr.core.model.IntegerDesc;
import de.dfki.mycbr.core.model.StringDesc;
import de.dfki.mycbr.core.model.SymbolDesc;
import de.dfki.mycbr.core.retrieval.Retrieval;
import de.dfki.mycbr.core.retrieval.Retrieval.RetrievalMethod;
import de.dfki.mycbr.core.similarity.AmalgamationFct;
import de.dfki.mycbr.core.similarity.ISimFct;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.core.similarity.config.AmalgamationConfig;
import de.dfki.mycbr.util.Pair;
import utils.MaintainerUtils;
import utils.PathingInfo;


/**
 * Servlet implementation class QueryServlet
 */
@WebServlet("/QueryServlet")
public class QueryServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(QueryServlet.class);
	private static DecimalFormat DF = new DecimalFormat("#.##");
       
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
	 * @throws IOException 
	 * @throws ServletException 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	
		Project myproject;
		try {
			myproject = new Project(PathingInfo.PROJECT_PATH + PathingInfo.PROJECT_NAME);
			
			
			// Necessary because it takes some time until MyCBR fully loads the project
			while (myproject.isImporting()) {
				Thread.sleep(1000);
			}
			Concept concept = myproject.getConceptByID(PathingInfo.CONCEPT_NAME);
			ICaseBase cb = updatePlayerCb(myproject, concept);
			
			// A sorted list containing the instance with respective global similarity to the query
			List<Pair<Instance, Similarity>> result = performRetrieval(myproject, request, concept, cb);

			// Dynamic table creation for results.jsp
			StringBuilder tableContent = generateResultTable(result, concept);
			
			request.setAttribute("results", tableContent);
			
		} catch (Exception e) {	
			LOGGER.error("An exception occured whilst opening MyCBR project or processing the query: " + e);
		} 
		
		try {
			request.getRequestDispatcher("/results.jsp").forward(request, response);
			
		} catch (Exception e) {
			LOGGER.error("An exception occured whilst dispatching to results.jsp: " + e);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This method performs a My CBR retrieval according to given parameters.
	 * 
	 * @param request the request element received from submitting the form.
	 * @param concept the currently used concept.
	 * @param cb the currently used case base to search in.
	 * @return a sorted list of results containing cases similar to the query.
	 */
	
	private List<Pair<Instance, Similarity>> performRetrieval(Project project, HttpServletRequest request, Concept concept, ICaseBase cb) {
		// Initialize all paramaters specified by the seach form
		int age = Integer.parseInt(request.getParameter("age"));
		String gender = request.getParameter("gender");
		String league = request.getParameter("league");
		String preferred_position = request.getParameter("preferred_position");

		int offensive = Integer.parseInt(request.getParameter("offensive"));
		int defensive = Integer.parseInt(request.getParameter("defensive"));
		int fairplay = Integer.parseInt(request.getParameter("fairplay"));
		int passing = Integer.parseInt(request.getParameter("passing"));
		int vitality = Integer.parseInt(request.getParameter("vitality"));
		int duels = Integer.parseInt(request.getParameter("duels"));
		
		// Custom weighted sum function based on desired position
		concept = modifyWeightsOfAmalgamtionFct(concept, preferred_position);
		
		for (String desc : concept.getAllAttributeDescs().keySet()) {
			System.out.println(desc + " is active: " + concept.getActiveAmalgamFct().isActive(concept.getAttributeDesc(desc)));
			
		}
		
		System.out.println(concept.getActiveAmalgamFct().getName());
		System.out.println(concept.getActiveAmalgamFct().getWeight(concept.getAttributeDesc("gender")));
		// Getting all the attribute descs from the concept to add attributes to the
		// case instance
		IntegerDesc ageDesc = (IntegerDesc) concept.getAttributeDesc("age");
		IntegerDesc offensiveDesc = (IntegerDesc) concept.getAttributeDesc("offensive");
		IntegerDesc defensiveDesc = (IntegerDesc) concept.getAttributeDesc("defensive");
		IntegerDesc fairplayDesc = (IntegerDesc) concept.getAttributeDesc("fairplay");
		IntegerDesc passingDesc = (IntegerDesc) concept.getAttributeDesc("passing");
		IntegerDesc vitalityDesc = (IntegerDesc) concept.getAttributeDesc("vitality");
		IntegerDesc duelsDesc = (IntegerDesc) concept.getAttributeDesc("duels");

		SymbolDesc genderDesc = (SymbolDesc) concept.getAllAttributeDescs().get("gender");
		SymbolDesc leagueDesc = (SymbolDesc) concept.getAllAttributeDescs().get("league");
		SymbolDesc positionDesc = (SymbolDesc) concept.getAllAttributeDescs().get("preferred_position");
		
		// Initialize sorted retrieval using given concept and case base
		Retrieval ret = new Retrieval(concept, cb);
		ret.setRetrievalMethod(RetrievalMethod.RETRIEVE_SORTED);
		
		Instance query = ret.getQueryInstance();
		
		ISimFct fctAge = ageDesc.getFct("default function");
		ISimFct fctOff = offensiveDesc.getFct("default function");
		ISimFct fctDef = defensiveDesc.getFct("default function");
		ISimFct fctFair = fairplayDesc.getFct("default function");
		ISimFct fctPass = passingDesc.getFct("default function");
		ISimFct fctVit = vitalityDesc.getFct("default function");
		ISimFct fctGen = genderDesc.getFct("default function");
		ISimFct fctPos = positionDesc.getFct("default function");
		ISimFct fctLea = leagueDesc.getFct("default function");
		ISimFct fctDuels = duelsDesc.getFct("default function");
		

		try {
			// Adding attributes to the query
			query.addAttribute(ageDesc, ageDesc.getAttribute(age));
			query.addAttribute(genderDesc, genderDesc.getAttribute(gender));
			query.addAttribute(leagueDesc, leagueDesc.getAttribute(league));
			query.addAttribute(positionDesc, positionDesc.getAttribute(preferred_position));
			query.addAttribute(offensiveDesc, offensiveDesc.getAttribute(offensive));
			query.addAttribute(defensiveDesc, defensiveDesc.getAttribute(defensive));
			query.addAttribute(fairplayDesc, fairplayDesc.getAttribute(fairplay));
			query.addAttribute(passingDesc, passingDesc.getAttribute(passing));
			query.addAttribute(vitalityDesc, vitalityDesc.getAttribute(vitality));
			query.addAttribute(duelsDesc, duelsDesc.getAttribute(duels));
			
		} catch (ParseException e) {
			LOGGER.error("Cannot add attribute to the My CBR query: " + e);
			e.printStackTrace();
		}

		ret.start();
		
		// Give information about used weighting to the result page for better understanding of results
		for (AttributeDesc attDesc : query.getAttributes().keySet()) {
			request.setAttribute(attDesc.getName(), concept.getActiveAmalgamFct().getWeight(attDesc));
		}
		
		for (Pair<Instance, Similarity> pair : ret.getResult()) {
			String playerName = pair.getFirst().getAttForDesc(concept.getAttributeDesc("last_name")).getValueAsString() + ", " + pair.getFirst().getAttForDesc(concept.getAttributeDesc("first_name")).getValueAsString();
			int ageCase = Integer.parseInt(pair.getFirst().getAttForDesc(concept.getAttributeDesc("age")).getValueAsString());
			String genderCase = pair.getFirst().getAttForDesc(concept.getAttributeDesc("gender")).getValueAsString();
			String leagueCase = pair.getFirst().getAttForDesc(concept.getAttributeDesc("league")).getValueAsString();
			String preferred_positionCase = pair.getFirst().getAttForDesc(concept.getAttributeDesc("preferred_position")).getValueAsString();

			int offensiveCase = Integer.parseInt(pair.getFirst().getAttForDesc(concept.getAttributeDesc("offensive")).getValueAsString());
			int defensiveCase = Integer.parseInt(pair.getFirst().getAttForDesc(concept.getAttributeDesc("defensive")).getValueAsString());
			int fairplayCase = Integer.parseInt(pair.getFirst().getAttForDesc(concept.getAttributeDesc("fairplay")).getValueAsString());
			int passingCase = Integer.parseInt(pair.getFirst().getAttForDesc(concept.getAttributeDesc("passing")).getValueAsString());
			int vitalityCase = Integer.parseInt(pair.getFirst().getAttForDesc(concept.getAttributeDesc("vitality")).getValueAsString());
			int duelsCase = Integer.parseInt(pair.getFirst().getAttForDesc(concept.getAttributeDesc("duels")).getValueAsString());
			LOGGER.info("Local similarities for player: " + playerName);
			try {
				LOGGER.info("Similarity for age " + age + " and " + ageCase + " is: " + fctAge.calculateSimilarity(ageDesc.getAttribute(age), ageDesc.getAttribute(ageCase)));
				LOGGER.info("Similarity for gender " + gender + " and " + genderCase + " is: " + fctGen.calculateSimilarity(genderDesc.getAttribute(gender), genderDesc.getAttribute(genderCase)));
				LOGGER.info("Similarity for league " + league + " and " + leagueCase + " is: " + fctLea.calculateSimilarity(leagueDesc.getAttribute(league), leagueDesc.getAttribute(leagueCase)));
				LOGGER.info("Similarity for position " + preferred_position + " and " + preferred_positionCase + " is: " + fctPos.calculateSimilarity(positionDesc.getAttribute(preferred_position), positionDesc.getAttribute(preferred_positionCase)));
				LOGGER.info("Similarity for offensive " + offensive + " and " + offensiveCase + " is: " + fctOff.calculateSimilarity(offensiveDesc.getAttribute(offensive), offensiveDesc.getAttribute(offensiveCase)));
				LOGGER.info("Similarity for fairplay " +fairplay + " and " +fairplayCase + " is: " + fctFair.calculateSimilarity(fairplayDesc.getAttribute(fairplay),fairplayDesc.getAttribute(fairplayCase)));
				LOGGER.info("Similarity for defensive " + defensive + " and " + defensiveCase + " is: " + fctDef.calculateSimilarity(defensiveDesc.getAttribute(defensive), defensiveDesc.getAttribute(defensiveCase)));
				LOGGER.info("Similarity for duels " + duels + " and " + duelsCase + " is: " + fctDuels.calculateSimilarity(duelsDesc.getAttribute(duels), duelsDesc.getAttribute(duelsCase)));
				LOGGER.info("Similarity for passing " + passing + " and " + passingCase + " is: " + fctPass.calculateSimilarity(passingDesc.getAttribute(passing), passingDesc.getAttribute(passingCase)));
				LOGGER.info("Similarity for vitality " + vitality + " and " + vitalityCase + " is: " + fctVit.calculateSimilarity(vitalityDesc.getAttribute(vitality), vitalityDesc.getAttribute(vitalityCase)));


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret.getResult();
	}
	
	/**
	 * This method processes the query results into a dynamic HTML table by iterating over 
	 * the results and the important attributes of each found case.
	 * 
	 * @param result the result provided by the MyCBR retrieval.
	 * @param concept the concept used for the retrieval.
	 * @return the dynamic produced HTML element for result display as a StringBuilder object.
	 */
	
	private StringBuilder generateResultTable(List<Pair<Instance, Similarity>> result, Concept concept) {
		StringBuilder tableContent = new StringBuilder();
		
		// Initializing the table with headlines
		tableContent.append("<table class='table text-center' id='resultTable'>");
		tableContent.append("<thead class='text-center'><tr>"
				+ "<th>Profillink</th>"
				+ "<th>Vorname</th>"
				+ "<th>Nachname</th>"
				+ "<th>Alter</th>"
				+ "<th>Geschlecht</th>"
				+ "<th>Liga</th>"
				+ "<th>Bevorzugte Position</th>"
				+ "<th>Ähnlichkeit</th>"
				+ "</tr><thead>");
		
		// These attributes are getting displayed on the results.jsp for a quick overview
		String[] resultAttributes = { "first_name", "last_name", "age", "gender", "league", "preferred_position" };
		
		// Iterate over every instance in the result
		for (Pair<Instance, Similarity> pair : result) {
			tableContent.append("<tr>");
			// Button working as a link to visit to respective player profile
			tableContent.append("<td>"
					+ "<form action=ProfileServlet method=post> "	
					+ "<input type='hidden' name='instance' value='" + pair.getFirst().getName() + "'/>"
					+ "<button class='btn btn-success btn-link'>Zum Profil</button>"
					+ "</form>"
					+ "</td>");
			
			
			// Iterate over attributes that have to be displayed in results
			for (String att : resultAttributes) {
				tableContent.append("<td>" + pair.getFirst().getAttForDesc(concept.getAttributeDesc(att)).getValueAsString() + "</td>");
			}

			// Display the similarity as a humand readable percentage value
			System.out.println(pair.getSecond().getValue());
			double percentSim = pair.getSecond().getRoundedValue() * 100;
			tableContent.append("<td>" + DF.format(percentSim) + "%</td>");
			tableContent.append("</tr>");

		}
		
		tableContent.append("</table>");
		
		return tableContent;
	}
	
	/**
	 * This method is kind of a init procedure where all birthdays represented as Strings 
	 * are getting parsed to overwrite the age attribute of each case before retrieval. 
	 * This ensures correctness of age values as time goes by. 
	 * This is only implemented due to problems with the MyCBR Date Type attribute.
	 * 
	 * @param project the currently opened MyCBR project.
	 * @param concept the currently used concept.
	 * @return the updated CaseBase.
	 * @throws ParseException if the calculated age could not be added to the case.
	 */
	private ICaseBase updatePlayerCb(Project project, Concept concept) throws ParseException {
		
		LOGGER.info("Updating player ages by parsing birthday Strings.");
		String birthday = "";
		int age = 0;
		for (Instance instance : project.getCB(PathingInfo.CASE_BASE).getCases()) {
			
			birthday = instance.getAttForDesc(concept.getAttributeDesc("birthday")).getValueAsString();
			age = MaintainerUtils.calculateAge(birthday);
			
			LOGGER.info(instance.getName() + " was born on: " + birthday + " and is: " + age + " years old now.");
			try {
				instance.addAttribute(concept.getAttributeDesc("age"), age);
				
			} catch (ParseException e) {
				LOGGER.error("Unable to add new player age to the case: " + e);
				e.printStackTrace();
			}
		}
		
		// Saving changes made to the case base
		project.save();
		
		return project.getCB("player_cb");
	}
	
	/**
	 * This method provides a dynamically weighted similarity function according to the position.
	 * 
	 * @param myConcept the currently used concept.
	 * @param position the desired position in the retrieval.
	 * @return the concept with a modifie default amalgamation function
	 */
	
	private Concept modifyWeightsOfAmalgamtionFct(Concept myConcept, String position) {
		

		// Getting all Attribute Descs that are relevant for the global similarity
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
		

		// Setting static weights
		myConcept.getActiveAmalgamFct().setWeight(ageDesc, 20);
		myConcept.getActiveAmalgamFct().setWeight(genderDesc, 40);
		myConcept.getActiveAmalgamFct().setWeight(leagueDesc, 20);
		myConcept.getActiveAmalgamFct().setWeight(positionDesc, 20);
		
		myConcept.getActiveAmalgamFct().setWeight(fairplayDesc, 5);
		myConcept.getActiveAmalgamFct().setWeight(passingDesc, 5);
		myConcept.getActiveAmalgamFct().setWeight(duelsDesc, 5);
		myConcept.getActiveAmalgamFct().setWeight(vitalityDesc, 5);
		
		int offensiveWeight = 5;
		int defensiveWeight = 5;
		
		// Setting dynamic weights according to given position
		switch (position) {
			case "Torwart":
				offensiveWeight = 0;
				defensiveWeight = 10;
				break;
			case "Verteidigung":
				defensiveWeight = 10;
				break;
			case "Mittelfeld":
				// Not necessary to alter initial weightings
				break;
			case "Sturm":
				offensiveWeight = 10;
				break;
			default:
				LOGGER.warn("Given position: " + position + " is invalid and thus not used for weighting.");
				
		}
		myConcept.getActiveAmalgamFct().setWeight(offensiveDesc, offensiveWeight);
		myConcept.getActiveAmalgamFct().setWeight(defensiveDesc, defensiveWeight);

		
		return myConcept;
	}

}
