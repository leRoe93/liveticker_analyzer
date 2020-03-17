package servlets;

import java.io.IOException;
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
import de.dfki.mycbr.core.model.SymbolDesc;
import de.dfki.mycbr.core.retrieval.Retrieval;
import de.dfki.mycbr.core.retrieval.Retrieval.RetrievalMethod;
import de.dfki.mycbr.core.similarity.AmalgamationFct;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.core.similarity.config.AmalgamationConfig;
import de.dfki.mycbr.util.Pair;
import utils.MaintainerUtils;


/**
 * Servlet implementation class QueryServlet
 */
@WebServlet("/QueryServlet")
public class QueryServlet extends HttpServlet {
	private static String data_path = "/Users/tadeus/Desktop/";
	private static String projectName = "projectwork_db.prj";
	private static String conceptName = "player";
	
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(QueryServlet.class);
       
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
			myproject = new Project(data_path+projectName);
			Concept concept = myproject.getConceptByID(conceptName);
			
			// Necessary because it takes some time until MyCBR fully loads the project
			while (myproject.isImporting()) {
				Thread.sleep(1000);
			}
			ICaseBase cb = updatePlayerCb(myproject, concept);
			
			// A sorted list containing the instance with respective global similarity to the query
			List<Pair<Instance, Similarity>> result = performRetrieval(request, concept, cb);

			// Dynamic table creation for results.jsp
			StringBuilder tableContent = generateResultTable(result, concept);
			
			request.setAttribute("results", tableContent);
			
		} catch (Exception e) {	
			System.err.println("An exception occured whilst opening MyCBR project or processing the query: " + e.getStackTrace());
		} 
		
		try {
			request.getRequestDispatcher("/results.jsp").forward(request, response);
			
		} catch (Exception e) {
			System.err.println("An exception occured whilst dispatching to results.jsp");
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
	
	private List<Pair<Instance, Similarity>> performRetrieval(HttpServletRequest request, Concept concept, ICaseBase cb) {
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
		AmalgamationFct customFct = generateWeightedFct(concept, preferred_position);
		concept.setActiveAmalgamFct(customFct);
		
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

		try {
			// Adding attributes to the query
			query.addAttribute(ageDesc, ageDesc.getAttribute(age));
			query.addAttribute(genderDesc, genderDesc.getAttribute(gender));
			query.addAttribute(leagueDesc, leagueDesc.getAttribute(league));
			query.addAttribute(positionDesc, positionDesc.getAttribute(preferred_position));
			query.addAttribute(offensiveDesc, ageDesc.getAttribute(offensive));
			query.addAttribute(defensiveDesc, genderDesc.getAttribute(defensive));
			query.addAttribute(fairplayDesc, leagueDesc.getAttribute(fairplay));
			query.addAttribute(passingDesc, ageDesc.getAttribute(passing));
			query.addAttribute(vitalityDesc, genderDesc.getAttribute(vitality));
			query.addAttribute(duelsDesc, leagueDesc.getAttribute(duels));
			
		} catch (ParseException e) {
			LOGGER.error("Cannot add attribute to the My CBR query: " + e.getStackTrace());
		}

		ret.start();
		
		// Give information about used weighting to the result page for better understanding of results
		for (AttributeDesc attDesc : query.getAttributes().keySet()) {
			request.setAttribute(attDesc.getName(), customFct.getWeight(attDesc));
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
			double percentSim = pair.getSecond().getRoundedValue() * 100;
			tableContent.append("<td>" + percentSim + "%</td>");
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
		for (Instance instance : project.getCB("player_cb").getCases()) {
			
			birthday = instance.getAttForDesc(concept.getAttributeDesc("birthday")).getValueAsString();
			age = MaintainerUtils.calculateAge(birthday);
			
			LOGGER.info(instance.getName() + " was born on: " + birthday + " and is: " + age + " years old now.");
			try {
				instance.addAttribute(concept.getAttributeDesc("age"), age);
				
			} catch (ParseException e) {
				LOGGER.error("Unable to add new player age to the case: " + e.getStackTrace());
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
	 * @return a dynamically produced AlgamationFct
	 */
	
	private AmalgamationFct generateWeightedFct(Concept myConcept, String position) {
		
		// Initialize function
		AmalgamationFct function = new AmalgamationFct(AmalgamationConfig.WEIGHTED_SUM, myConcept, "customFunction");

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
		function.setWeight(ageDesc, 20);
		function.setWeight(genderDesc, 40);
		function.setWeight(leagueDesc, 20);
		function.setWeight(positionDesc, 20);
		
		function.setWeight(fairplayDesc, 5);
		function.setWeight(passingDesc, 5);
		function.setWeight(duelsDesc, 5);
		function.setWeight(vitalityDesc, 5);
		
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
				LOGGER.warn("Given position: " + position + " is invalid and thus not used for mapping.");
				
		}
		function.setWeight(offensiveDesc, offensiveWeight);
		function.setWeight(defensiveDesc, defensiveWeight);

		
		return function;
	}

}
