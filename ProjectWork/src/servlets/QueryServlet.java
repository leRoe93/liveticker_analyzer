package servlets;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.dfki.mycbr.core.ICaseBase;
import de.dfki.mycbr.core.Project;
import de.dfki.mycbr.core.casebase.Attribute;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.AttributeDesc;
import de.dfki.mycbr.core.model.Concept;
import de.dfki.mycbr.core.model.IntegerDesc;
import de.dfki.mycbr.core.model.StringDesc;
import de.dfki.mycbr.core.model.SymbolDesc;
import de.dfki.mycbr.core.retrieval.Retrieval;
import de.dfki.mycbr.core.retrieval.Retrieval.RetrievalMethod;
import de.dfki.mycbr.core.similarity.AdvancedIntegerFct;
import de.dfki.mycbr.core.similarity.AmalgamationFct;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.core.similarity.config.AmalgamationConfig;
import de.dfki.mycbr.util.Pair;


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
			
			ICaseBase cb = myproject.getCB("player_cb");
			
			
			// Takes some time to load until access is possible
			while (myproject.isImporting()) {
				Thread.sleep(1000);
			}
			
			
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

				tableContent.append("<td>" + pair.getSecond() + "</td>");
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
		
		function.setWeight(ageDesc, 1);
		function.setWeight(genderDesc, 10);
		function.setWeight(leagueDesc, 5);
		function.setWeight(positionDesc, 5);
		
		function.setWeight(fairplayDesc, 1);
		function.setWeight(passingDesc, 1);
		function.setWeight(duelsDesc, 1);
		function.setWeight(vitalityDesc, 1);
		
		int offensiveWeight = 1;
		int defensiveWeight = 1;
		
		switch (position) {
			case "Torwart":
				offensiveWeight = 0;
				defensiveWeight = 5;
				break;
			case "Verteidigung":
				defensiveWeight = 5;
				break;
			case "Mittelfeld":
				offensiveWeight = 3;
				defensiveWeight = 3;
				break;
			case "Sturm":
				offensiveWeight = 5;
				break;
		}
		function.setWeight(offensiveDesc, offensiveWeight);
		function.setWeight(defensiveDesc, defensiveWeight);

		
		return function;
	}

}
