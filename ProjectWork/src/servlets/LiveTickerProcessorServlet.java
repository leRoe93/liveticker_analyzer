package servlets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;
import org.languagetool.language.GermanyGerman;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import datastructure.Action;
import datastructure.GameSituation;
import datastructure.Player;
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
		
		File file = new File("/Users/tadeus/Desktop/vocabulary.yaml");
		

		// Instantiating a new ObjectMapper as a YAMLFactory
		ObjectMapper om = new ObjectMapper(new YAMLFactory());
		
		JsonNode jn = om.readTree(file);		
		
		
		HashMap<String, String> exampleMap = new HashMap<String, String>();
		String[] fieldNames = { "offensive", "defensive", "passing", "fairplay", "vitality", "duels" };
		for (String fn : fieldNames) {
			JsonNode jn2 = jn.get(fn);
			for (int i = 0; i < jn2.size(); i++) {
				exampleMap.put(jn2.get(i).toString().replaceAll("\"", ""), fn);
				
			}
			
			
		}
		
		
		// ?subpage=aufstellung does not work due to java script generated content
		Document doc = Jsoup.connect(request.getParameter("url_lt")).get();
		
		
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
			
			
		}
		JLanguageTool languagetool = new JLanguageTool(new GermanyGerman());
		
		Properties germanProperties = StringUtils
				.argsToProperties(new String[] { "-props", "StanfordCoreNLP-german.properties" });
		StanfordCoreNLP pipeline = new StanfordCoreNLP(germanProperties);
		
		// Dynamic table creation for progression.jsp
		StringBuilder tableContent = new StringBuilder();

		tableContent.append("<table class='table table-striped table-bordered table-hover' id='tickerEntries'>");
		tableContent.append("<thead class='thead-dark'><tr>" + "<th>Prozessierter Ticker Eintrag</th>" + "<th>Potenzielle Aktionen</th>" + "<th>Verwertete Aktionen</th>" +  "<th>Betroffenes Attribut</th>" + "</tr></thead>");
		tableContent.append("<tbody>");
		int limiter = 10;
		int iterator = 1;
		
		ArrayList<String> playersInDb = getPlayersFromDbAsList();
		
		for (String entry : tickerEntries) {
			
			// Just to decrease processing time while testing around 
			if (iterator == limiter) {
				break;
			}
			
			HashMap<String, ArrayList<GameSituation>> situations = generateGameSituation(entry, playersInDb, pipeline, languagetool);
			
			StringBuilder inDbContent = new StringBuilder("<ol>");
			StringBuilder notInDbContent = new StringBuilder("<ol>");
			StringBuilder affectedAttributes = new StringBuilder("<ol>");

			for (GameSituation sitInDb : situations.get("inDb")) {
				inDbContent.append("<li>Entitaet: " + sitInDb.getActor().getLastName() + "<br/>Aktion: " + sitInDb.getAction().getIdentifier() + "</li>");
				affectedAttributes.append("<li>" + exampleMap.get(sitInDb.getAction().getIdentifier()) + ": Steigung/Senkung</li>");
			}
			for (GameSituation sitNotInDb : situations.get("notInDb")) {
				notInDbContent.append("<li>Entitaet: " + sitNotInDb.getActor().getLastName() + "<br/>Aktion: " + sitNotInDb.getAction().getIdentifier() + "</li>");

			}
			
			
			affectedAttributes.append("</ol>");
			inDbContent.append("</ol>");
			notInDbContent.append("</ol>");
			
			tableContent.append("<tr>");
			tableContent.append("<td class='entry'>" + entry + "</td>");
			tableContent.append("<td class='info'>" + notInDbContent + "</td>");
			tableContent.append("<td class='info'>" + inDbContent + "</td>");
			tableContent.append("<td class='info'>" + affectedAttributes + "</td>");

			tableContent.append("</tr>");
			iterator++;
		}
		tableContent.append("</tbody>");
		tableContent.append("</table>");

		request.setAttribute("ticker_url", request.getParameter("url_lt"));
		request.setAttribute("tickerEntries", tableContent);
		
		request.getRequestDispatcher("progression.jsp").forward(request, response);
	}
	// NLP Logic to convert the string into a structured form
	// Shall return a gamesituation in the future
	private HashMap<String, ArrayList<GameSituation>> generateGameSituation(String tickerEntry, ArrayList<String> playersInDb, StanfordCoreNLP pipeline, JLanguageTool languagetool) {
		
		
		// Initialize a HashMap that contains detected situations separated by the key indicating, if the affected player exists in the DB
		HashMap<String, ArrayList<GameSituation>> situations = new HashMap<String, ArrayList<GameSituation>>();
		situations.put("inDb", new ArrayList<GameSituation>());
		situations.put("notInDb", new ArrayList<GameSituation>());
		
		
		// Initialize Stanford Core NLP pipeline with german properties and a specific ticker entry as content
		CoreDocument document = new CoreDocument(tickerEntry);
		

		// Do all kinds of possible annotations for the german language
		pipeline.annotate(document);

		// Always second one, because first one contains the minute.
		CoreSentence sent = document.sentences().get(1);

		
		// Tokens represent the single words in a sentence
        List<CoreLabel> tokens = sent.tokens();
        
        // POS Tags represent the "kind" of word, e.g. NE = Named Entity
    	List<String> posTags = sent.posTags();
    	
    	
    	
    	String potentialPlayer = "";
    	String actionIdentifier = "";
    	boolean entityExistsInDb = false;
    	
    	for (int i = 0; i < tokens.size(); i++) {
    		boolean situationDefined = false;
    		
    		// Current token is a named entity and thus potentially a player
    		if (posTags.get(i).equals("NE")) {
    			
    			// Initialize a GameSituation that gets specified later on
    			GameSituation sit = new GameSituation();
    			
    			// Get the potential player name from the named entity, regardless if it is a player or not
    			potentialPlayer = tokens.get(i).toString().substring(0, tokens.get(i).toString().indexOf("-"));
    			
    			System.out.println("Current token: " + potentialPlayer + " is a Named Entity!");
    			// Iterate over all players in DB and check if one of them matches the current candidate
    			for (String player : playersInDb) {
    				// System.out.println("Comparing '" + potentialPlayer + "' with '" + player + "'");
    				if (player.equals(potentialPlayer)) {
    					entityExistsInDb = true;
    					System.out.println("It also exists in the Database!");
    				}
    			}
    			
    			if (!entityExistsInDb) {
    				System.out.println("It does not exist in the Database!");
    			}
    			
    			// If there is a word before this named entity
				if ((i - 1) >= 0) {
					System.out.println("There is a word preceeding this entity!");
					// If the preceeding token is a preposition we might detect an action by going further
					if (posTags.get(i - 1).equals("APPR")) {
						System.out.println("It is a preposition!");
						// If there is a word before this preposition
						if ((i - 2) >= 0) {
							System.out.println("There is a further word, preceeding the preposition!");
							// If this word is a noun, it is regarded as an action
							if (posTags.get(i - 2).equals("NN")) {
								System.out.println("It is a noun!");
								actionIdentifier = tokens.get(i - 2).toString().substring(0,
										tokens.get(i - 2).toString().indexOf("-"));

								System.out.println("Found a nominative illustration of situation");
								System.out.println("Player: " + potentialPlayer);
								System.out.println("Action: " + actionIdentifier);

								sit = new GameSituation(new Player(potentialPlayer), new Action(actionIdentifier));
								situationDefined = true;
								
							}
						}
					}
				}
				
				// If there is word after this named entity 
				if ((i + 1) < tokens.size() && situationDefined == false) {
					System.out.println("There is a word following this entity and no situation is defined yet.");
					// If the following token is a verb, it is regarded as an action
					if (posTags.get(i + 1).equals("VVFIN")) {
						actionIdentifier = tokens.get(i + 1).toString().substring(0,
								tokens.get(i + 1).toString().indexOf("-"));

						System.out.println("Found a verb illustration of situation");
						System.out.println("Player: " + potentialPlayer);
						System.out.println("Action: " + tokens.get(i + 1));
						
						String lemmaAction = "";
						try {
							List<AnalyzedSentence> analyzedSentences = languagetool.analyzeText(actionIdentifier);
							for (AnalyzedSentence analyzedSentence : analyzedSentences) {
								for (AnalyzedTokenReadings analyzedTokens : analyzedSentence.getTokensWithoutWhitespace()) {
									lemmaAction = analyzedTokens.getReadings().get(0).getLemma();
									
						
								}
							}
							
							sit = new GameSituation(new Player(potentialPlayer), new Action(lemmaAction));
							situationDefined = true;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				} else {
					System.out.println("No possibility to find a situation!");
				}
				
				
				if (situationDefined == true) {
					System.out.println("A situation has been defined during this iteration!");
					// Depending on the database the situation gets added to specific list
					if (entityExistsInDb) {
						System.out.println("Adding situation into existing content");
						situations.get("inDb").add(sit);
					} else {
						System.out.println("Adding situation into non-existing content");
						situations.get("notInDb").add(sit);
					}
				}

    		} 
    		
    		
    	}
		
		
		
		return situations;
	}
	
	private ArrayList<String> getPlayersFromDbAsList() {
		ArrayList<String> players = new ArrayList<String>();
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
			
			Collection<Instance> allInstances = cb.getCases();
			
			for (Instance instance : allInstances) {
				players.add(instance.getAttForDesc(myConcept.getAttributeDesc("last_name")).getValueAsString());
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return players;

	}
	
	
}
