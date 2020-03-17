package servlets;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

		
		
		// MyCBR setup
		Project myproject;
		try {
			myproject = new Project(data_path+projectName);
			Concept myConcept = myproject.getConceptByID(conceptName);
			
			// Takes some time to load until access is possible
			while (myproject.isImporting()) {
				Thread.sleep(1000);
			}
			ICaseBase cb = updatePlayerCb(myproject, myConcept);
			
			
			
			AmalgamationFct customFct = generateWeightedFct(myConcept, preferred_position);
			myConcept.setActiveAmalgamFct(customFct);
			
			
			
			// Getting all the attribute descs from the concept to add attributes to the case instance

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
			
			Retrieval ret = new Retrieval(myConcept, cb);
			ret.setRetrievalMethod(RetrievalMethod.RETRIEVE_SORTED);
			Instance query = ret.getQueryInstance();
			// Adding attributes to the query
			query.addAttribute(ageDesc,ageDesc.getAttribute(age));
			
			query.addAttribute(genderDesc,genderDesc.getAttribute(gender));
			
			query.addAttribute(leagueDesc,leagueDesc.getAttribute(league));
			
			query.addAttribute(positionDesc,positionDesc.getAttribute(preferred_position));
			
			query.addAttribute(offensiveDesc,ageDesc.getAttribute(offensive));
			
			query.addAttribute(defensiveDesc,genderDesc.getAttribute(defensive));
			
			query.addAttribute(fairplayDesc,leagueDesc.getAttribute(fairplay));
			
			query.addAttribute(passingDesc,ageDesc.getAttribute(passing));
			
			query.addAttribute(vitalityDesc,genderDesc.getAttribute(vitality));
			
			query.addAttribute(duelsDesc,leagueDesc.getAttribute(duels));
			
			ret.start();
			
			
			List<Pair<Instance, Similarity>> result = ret.getResult();

			// Dynamic table creation for results.jsp
			StringBuilder tableContent = new StringBuilder();
			
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
			
			String[] resultAttributes = { "first_name", "last_name", "age", "gender", "league", "preferred_position" };
			
			for (Pair<Instance, Similarity> pair : result) {
				tableContent.append("<tr>");
				tableContent.append("<td>"
						+ "<form action=ProfileServlet method=post> "	
						+ "<input type='hidden' name='instance' value='" + pair.getFirst().getName() + "'/>"
						+ "<button class='btn btn-success btn-link'>Zum Profil</button>"
						+ "</form>"
						+ "</td>");
				
				for (String att : resultAttributes) {
					
					tableContent.append("<td>" + pair.getFirst().getAttForDesc(myConcept.getAttributeDesc(att)).getValueAsString() + "</td>");
				}

				double percentSim = pair.getSecond().getRoundedValue() * 100;
				tableContent.append("<td>" + percentSim + "%</td>");
				tableContent.append("</tr>");

			}
			
			tableContent.append("</table>");
			
			
			for (AttributeDesc attDesc : query.getAttributes().keySet()) {
				request.setAttribute(attDesc.getName(), customFct.getWeight(attDesc));
			}
			
			request.setAttribute("results", tableContent);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		request.getRequestDispatcher("/results.jsp").forward(request, response);
	}
	
	// This way it works, to alter the case base effectively
	private ICaseBase updatePlayerCb(Project project, Concept concept) throws ParseException {
		
		System.out.println("Now updating ages of players by parsing their birthday.");
		String birthday = "";
		int age = 0;
		for (Instance instance : project.getCB("player_cb").getCases()) {
			
			birthday = instance.getAttForDesc(concept.getAttributeDesc("birthday")).getValueAsString();
			age = MaintainerUtils.calculateAge(birthday);
			
			System.out.println(instance.getName() + " has birthday: " + birthday + " and is: " + age + " years old now.");
			instance.addAttribute(concept.getAttributeDesc("age"), age);
		}
		
		project.save();
		
		return project.getCB("player_cb");
	}
	
	private AmalgamationFct generateWeightedFct(Concept myConcept, String position) {
		AmalgamationFct function = new AmalgamationFct(AmalgamationConfig.WEIGHTED_SUM, myConcept, "customFunction");

		// Getting all the attribute descs from the concept to add attributes to the case instance

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
		
		switch (position) {
			case "Torwart":
				offensiveWeight = 0;
				defensiveWeight = 10;
				break;
			case "Verteidigung":
				defensiveWeight = 10;
				break;
			case "Mittelfeld":
				offensiveWeight = 5;
				defensiveWeight = 5;
				break;
			case "Sturm":
				offensiveWeight = 10;
				break;
				
			default:
				
		}
		function.setWeight(offensiveDesc, offensiveWeight);
		function.setWeight(defensiveDesc, defensiveWeight);

		
		return function;
	}

}
