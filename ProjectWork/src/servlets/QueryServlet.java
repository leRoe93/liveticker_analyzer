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
import de.dfki.mycbr.core.model.SymbolDesc;
import de.dfki.mycbr.core.retrieval.Retrieval;
import de.dfki.mycbr.core.retrieval.Retrieval.RetrievalMethod;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.util.Pair;


/**
 * Servlet implementation class QueryServlet
 */
@WebServlet("/QueryServlet")
public class QueryServlet extends HttpServlet {
	private static String data_path = "/Users/tadeus/Desktop/";
	private static String projectName = "projektarbeit_db.prj";
	private static String conceptName = "spieler";
	
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
		
		String age = request.getParameter("age");
		String gender = request.getParameter("gender");
		String league = request.getParameter("league");
		String preferred_position = request.getParameter("preferred_position");
		
		
		// MyCBR setup
		Project myproject;
		try {
			myproject = new Project(data_path+projectName);
			Concept myConcept = myproject.getConceptByID(conceptName);
			
			ICaseBase cb = myproject.getCB("spieler_datenbank");
			
			
			// Takes some time to load until access is possible
			while (myproject.isImporting()) {
				Thread.sleep(1000);
			}
			
			Retrieval ret = new Retrieval(myConcept, cb);
			ret.setRetrievalMethod(RetrievalMethod.RETRIEVE_SORTED);
			Instance query = ret.getQueryInstance();
			
			
			IntegerDesc ageDesc = (IntegerDesc) myConcept.getAllAttributeDescs().get("alter");
			query.addAttribute(ageDesc,ageDesc.getAttribute(age));
			
			SymbolDesc genderDesc = (SymbolDesc) myConcept.getAllAttributeDescs().get("geschlecht");
			query.addAttribute(genderDesc,genderDesc.getAttribute(gender));
			
			SymbolDesc leagueDesc = (SymbolDesc) myConcept.getAllAttributeDescs().get("spielklasse");
			query.addAttribute(leagueDesc,leagueDesc.getAttribute(league));
			
			SymbolDesc positionDesc = (SymbolDesc) myConcept.getAllAttributeDescs().get("bevorzugte_position");
			query.addAttribute(positionDesc,positionDesc.getAttribute(preferred_position));
			
			ret.start();
			
			// get the retrieval result
			List<Pair<Instance, Similarity>> result = ret.getResult();

			// Dynamic table creation for results.jsp
			StringBuilder tableContent = new StringBuilder();
			
			tableContent.append("<table class='table' id='resultTable'>");
			tableContent.append("<tr>"
					+ "<th></th>"
					+ "<th>Vorname</th>"
					+ "<th>Nachname</th>"
					+ "<th>Alter</th>"
					+ "<th>Geschlecht</th>"
					+ "<th>Liga</th>"
					+ "<th>Bevorzugte Position</th>"
					+ "<th>Ähnlichkeit</th>"
					+ "</tr>");
			
			String[] resultAttributes = { "vorname", "name", "alter", "geschlecht", "spielklasse", "bevorzugte_position" };
			
			for (Pair<Instance, Similarity> pair : result) {
				
				tableContent.append("<tr>");
				tableContent.append("<td>"
						+ "<form action=ProfileServlet method=post> "	
						+ "<input type='hidden' name='instance' value='" + pair.getFirst().getName() + "'/>"
						+ "<input type='submit' value='Zum Profil'>"
						+ "</form>"
						+ "</td>");
				
				for (String att : resultAttributes) {
					
					tableContent.append("<td>" + pair.getFirst().getAttForDesc(myConcept.getAttributeDesc(att)).getValueAsString() + "</td>");
				}

				tableContent.append("<td>" + pair.getSecond() + "</td>");
				tableContent.append("</tr>");

			}
			
			tableContent.append("</table>");
			
			request.setAttribute("results", tableContent);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		request.getRequestDispatcher("/results.jsp").forward(request, response);
	}

}
