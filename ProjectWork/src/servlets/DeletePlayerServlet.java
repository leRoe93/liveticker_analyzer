package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.dfki.mycbr.core.ICaseBase;
import de.dfki.mycbr.core.Project;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.Concept;
import de.dfki.mycbr.core.model.IntegerDesc;
import de.dfki.mycbr.core.model.SymbolDesc;
import de.dfki.mycbr.core.retrieval.Retrieval;
import de.dfki.mycbr.core.retrieval.Retrieval.RetrievalMethod;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.util.Pair;

/**
 * Servlet implementation class DeletePlayerServlet
 */
@WebServlet("/DeletePlayerServlet")
public class DeletePlayerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static String data_path = "/Users/tadeus/Desktop/";
	private static String projectName = "projectwork_db.prj";
	private static String conceptName = "player";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeletePlayerServlet() {
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
		
		
		String instance = request.getParameter("instance");
		

		// MyCBR setup
		Project myproject;
		try {
			myproject = new Project(data_path + projectName);
			Concept myConcept = myproject.getConceptByID(conceptName);

			ICaseBase cb = myproject.getCB("player_cb");

			// Takes some time to load until access is possible
			while (myproject.isImporting()) {
				Thread.sleep(1000);
			}
			
			myproject.removeCase(instance);
			
			
			myproject.save();
			request.setAttribute("success", "Spielerinstanz: " + instance + " erfolgreich geloescht!");
			
			request.getRequestDispatcher("profile.jsp").forward(request, response);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
