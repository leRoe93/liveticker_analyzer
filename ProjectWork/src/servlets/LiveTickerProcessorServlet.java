package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datastructure.GameSituation;
import de.dfki.mycbr.core.ICaseBase;
import de.dfki.mycbr.core.Project;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.Concept;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.util.Pair;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;


/**
 * Servlet implementation class LiveTickerProcessorServlet
 */
@WebServlet("/LiveTickerProcessorServlet")
public class LiveTickerProcessorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String data_path = "/Users/tadeus/Desktop/";
	private static String projectName = "projectwork_db.prj";
	private static String conceptName = "player";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LiveTickerProcessorServlet() {
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
		
		// ?subpage=aufstellung
		Document doc = Jsoup.connect(request.getParameter("url_lt")).get();
		
		// To get the minutes
		// Elements links = doc.select(".minuteText");
		
		Elements links = doc.select(".rowBright");
		
		
		ArrayList<String> tickerEntries = new ArrayList<String>();
		String content = "";
		
		for (Element link : links) {
			
			// Removing the inner html block for the minute text, because it is not needed in this application
			content = link.getElementsByClass("text").html().replaceAll("<[^>]*>", "");
			// Line begings with a digit and therefore is a minute text
			if (Character.isDigit(content.charAt(0))) {
				tickerEntries.add(content);
			}
			
			
			// .substring(content.indexOf("|") + 2).replaceAll("<[^>]*>", "")
			
		}
		
		// Dynamic table creation for results.jsp
		StringBuilder tableContent = new StringBuilder();

		tableContent.append("<table class='table' id='tickerEntries'>");
		tableContent.append("<tr>" + "<th>Ticker Eintrag</th>" + "<th>Erkannte Spieler</th>" + "<th>Erkannte Situation</th>" + "</tr>");
		
		int limiter = 10;
		int iterator = 1;
		
		for (String entry : tickerEntries) {
			
			// Just to decrease processing time while testing around 
			if (iterator == limiter) {
				break;
			}
			
			GameSituation sit = generateGameSituation(entry);

			tableContent.append("<tr>");
			tableContent.append("<td>" + entry + "</td>");
			tableContent.append("<td>" + sit.getPlayerProposals() + "</td>");
			tableContent.append("<td>" + sit.getActionProposals() + "</td>");

			tableContent.append("</tr>");
			iterator++;
		}

		tableContent.append("</table>");

		request.setAttribute("tickerEntries", tableContent);
		
		request.getRequestDispatcher("progression.jsp").forward(request, response);
	}
	// NLP Logic to convert the string into a structured form
	// Shall return a gamesituation in the future
	private GameSituation generateGameSituation(String tickerEntry) {
		GameSituation sit = new GameSituation();

		CoreDocument document = new CoreDocument(tickerEntry);
		Properties germanProperties = StringUtils
				.argsToProperties(new String[] { "-props", "StanfordCoreNLP-german.properties" });
		StanfordCoreNLP pipeline = new StanfordCoreNLP(germanProperties);

		pipeline.annotate(document);

		// Always second one, because first one contains the minute.
		CoreSentence sent = document.sentences().get(1);
		/*
		 * System.out.println("This is the sentence: " + sent.text());
		 * System.out.println("These are the POS tags: " + sent.posTags());
		 * System.out.println("These are the NER tags: " + sent.nerTags());
		 * System.out.println("This is the constituencyParse: " +
		 * sent.constituencyParse()); System.out.println("This is the dependencyParse: "
		 * + sent.dependencyParse());
		 * System.out.println("These are the entity mentions");
		 */

		List<CoreLabel> tokens = sent.tokens();
		
		ArrayList<String> playerProposals = new ArrayList<String>();
        ArrayList<String> actionProposals = new ArrayList<String>();

		for (int i = 0; i < tokens.size(); i++) {
			String token = tokens.get(i).toString();
			String posTag = sent.posTags().get(i).toString();
			// System.out.println(token.substring(0, token.indexOf("-")) + " is a " +
			// sent.posTags().get(i).toString());

			if (posTag.equals("NE")) {
				playerProposals.add(token.substring(0, token.indexOf("-")));
			} else if (posTag.contentEquals("NN") || posTag.equals("VVFIN")) {
				actionProposals.add(token.substring(0, token.indexOf("-")));
			}
		}
		
		sit.setPlayerProposals(playerProposals);
		sit.setActionProposals(actionProposals);
		
		return sit;
	}
	
	private void entityCleanUp(ArrayList<String> playerProposals) {
		
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
			
			String suffix = "[Nicht_in_DB]";
			for (int i = 0; i < playerProposals.size(); i++) {
				for (Instance instance : myproject.getAllInstances()) {
					if (playerProposals.get(i).contains(instance.getAttForDesc(myConcept.getAttributeDesc("last_name")).getValueAsString())) {
						System.out.println("MUSS NOCH ÜBERDACHT WERDEN!");
					}
				}
				
				playerProposals.get(i).concat(suffix);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
}
