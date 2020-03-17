package servlets;

import java.io.IOException;
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
	private final static Logger LOGGER = Logger.getLogger(DeletePlayerServlet.class);

       
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
		
		// Instance that shall be deleted from the case base
		String player = request.getParameter("instance");
		
		Project myproject;
		try {
			myproject = new Project(data_path + projectName);

			ICaseBase cb = myproject.getCB("player_cb");

			// Necessary because it takes some time until MyCBR fully loads the project
			while (myproject.isImporting()) {
				Thread.sleep(1000);
			}
			
			myproject.removeCase(player);
			cb.removeCase(player);
			
			myproject.save();
			
			request.setAttribute("success", "Spielerinstanz: " + player + " erfolgreich geloescht!");
			
			request.getRequestDispatcher("profile.jsp").forward(request, response);

		} catch (Exception e) {
			LOGGER.error("Exception occured whilst deleting player from case base: " + e.getStackTrace());
		}

	}

}
