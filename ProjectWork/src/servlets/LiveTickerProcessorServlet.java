package servlets;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;
import org.languagetool.language.GermanyGerman;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import datastructure.Action;
import datastructure.GameSituation;
import datastructure.LiveTicker;
import datastructure.Player;
import de.dfki.mycbr.core.ICaseBase;
import de.dfki.mycbr.core.Project;
import de.dfki.mycbr.core.casebase.Attribute;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.casebase.MultipleAttribute;
import de.dfki.mycbr.core.model.AttributeDesc;
import de.dfki.mycbr.core.model.Concept;
import de.dfki.mycbr.core.model.IntegerDesc;
import de.dfki.mycbr.core.model.StringDesc;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
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
	private ArrayList<String> recognizedPlayers = new ArrayList<String>();
	
	private final static Logger LOGGER = Logger.getLogger(LiveTickerProcessorServlet.class);
	private LiveTicker liveTicker;

	/**
	 * @throws IOException
	 * @throws JsonProcessingException
	 * @see HttpServlet#HttpServlet()
	 */
	public LiveTickerProcessorServlet() throws JsonProcessingException, IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		

		
		this.liveTicker = new LiveTicker(request.getParameter("url_lt"));
		
		expandLiveTicker();
	
		// Dynamic table creation for progression.jsp
		StringBuilder tableContent = buildProgressionTable();

		request.setAttribute("ticker_url", request.getParameter("url_lt"));
		request.setAttribute("tickerEntries", tableContent);

		request.getRequestDispatcher("progression.jsp").forward(request, response);

	}

	private StringBuilder buildProgressionTable() {
		StringBuilder tableContent = new StringBuilder();
		
		tableContent.append("<table class='table table-striped table-bordered table-hover' id='tickerEntries'>");
		tableContent.append("<thead class='thead-dark'><tr>" + "<th>Prozessierter Ticker Eintrag</th>"
				+ "<th>Potenzielle Aktionen</th>" + "<th>Verwertete Aktionen</th>" + "<th>Betroffenes Attribut</th>"
				+ "</tr></thead>");
		tableContent.append("<tbody>");
		
		for (int i = 0; i < this.liveTicker.getPotentialSituations().size(); i++) {
			tableContent.append("<tr>");
			
			tableContent.append("<td class='entry'>" + this.liveTicker.getTickerEntries().get(i) + "</td>");
			
			String potentialContent = "<ol>";
			
			for (GameSituation gs : this.liveTicker.getPotentialSituations().get(i)) {
				potentialContent += "<li>Entitaet: " + gs.getActor().getLastName() + ", Aktion: " + gs.getAction().getIdentifier() + "</li>";
			}
			potentialContent += "</ol>";
			
			String processableContent = "<ol>";
			
			for (GameSituation gs : this.liveTicker.getProcessableSituations().get(i)) {
				if (gs.getActor().getLastName().equals("NICHT IN CB")) {
					processableContent += "<li></li>";
				} else {
					
					processableContent += "<li>Spieler: " + gs.getActor().getLastName() + ", Aktion: " + gs.getAction().getIdentifier() + "</li>";
				}
			}
			processableContent += "</ol>";
			
			String affectionContent = "<ol>";
			
			for (String affection : this.liveTicker.getAttributeAffections().get(i)) {
				affectionContent += "<li>" + affection + "</li>";

			}
			affectionContent += "</ol>";
			
			tableContent.append("<td class='info'>" + potentialContent + "</td>");
			tableContent.append("<td class='info'>" + processableContent + "</td>");
			tableContent.append("<td class='info'>" + affectionContent + "</td>");
			
			tableContent.append("</tr>");
			
			tableContent.append("</tr>");
			
		}
		tableContent.append("</tbody>");
		tableContent.append("</table>");
		
			
			
		return tableContent;
	}
	
	private void expandLiveTicker() throws JsonProcessingException, IOException {
		JLanguageTool languagetool = new JLanguageTool(new GermanyGerman());
		
		Properties germanProperties = StringUtils
				.argsToProperties(new String[] { "-props", "StanfordCoreNLP-german.properties" });
		StanfordCoreNLP pipeline = new StanfordCoreNLP(germanProperties);
		
		Project myproject = null;

		try {
			myproject = new Project(data_path + projectName);
			// Takes some time to load until access is possible
			while (myproject.isImporting()) {
				Thread.sleep(1000);

			}
		} catch (Exception e) {
			LOGGER.error("Exception occured while loading My CBR project: " + e.getStackTrace());
		}
		
		Concept concept = myproject.getConceptByID(conceptName);
		ICaseBase cb = myproject.getCB("player_cb");
		
		int limiter = 10;
		int iterator = 0;
		HashMap<String, String> vocabularyMap = initVocabularyMap();
		for (int i = 0; i < this.liveTicker.getTickerEntries().size(); i++) {
			
			if (iterator == limiter) {
				break;
			}
			String entry = this.liveTicker.getTickerEntries().get(i);
			// Initialize Stanford Core NLP pipeline with german properties and a specific
			// ticker entry as content
			CoreDocument document = new CoreDocument(entry);

			// Do all kinds of possible annotations for the german language
			pipeline.annotate(document);

			// Always second one, because first one contains the minute.
			CoreSentence sent = document.sentences().get(1);

			// Tokens represent the single words in a sentence
			List<CoreLabel> tokens = sent.tokens();

			// POS Tags represent the "kind" of word, e.g. NE = Named Entity
			List<String> posTags = sent.posTags();
			
			ArrayList<GameSituation> potentialSituations = evaluatePotentialSituations(posTags, tokens, vocabularyMap, languagetool);
			this.liveTicker.getPotentialSituations().add(potentialSituations);
			ArrayList<GameSituation> processableSituations = evaluateProcessableSituations(potentialSituations, cb, concept, vocabularyMap);
			this.liveTicker.getProcessableSituations().add(processableSituations);
			ArrayList<String> attributeAffections = evaluateAttributeAffections(processableSituations, concept, cb);
			this.liveTicker.getAttributeAffections().add(attributeAffections);
			
			myproject.save();
			iterator++;
		}
		
		for (String caseRecognized : this.recognizedPlayers) {
			try {
				normalizeAttributes(caseRecognized, concept, cb);
			} catch (Exception e) {
				LOGGER.error("Exception occured whilst normalizing case attributes: " + e.getStackTrace());
			}
		}
	}
	
	
	private ArrayList<String> evaluateAttributeAffections(ArrayList<GameSituation> processableSituations, Concept concept, ICaseBase cb) {
		ArrayList<String> attributeAffections = new ArrayList<String>();
		String affection = "Attribut: ";
		
		for (GameSituation processableSituation : processableSituations) {

			if (processableSituation.getAction().getAffectedAttribute().equals("none")) {
				attributeAffections.add("Keine Wertung");
			} else {
				
				String affectedAttribute = processableSituation.getAction().getAffectedAttribute();
				affection += affectedAttribute;
				String caseName = getPlayerFromDatabase(concept, cb.getCases(),
					processableSituation.getActor().getLastName());

				for (Instance in : cb.getCases()) {
					if (in.getName().equals(caseName)) {
						AttributeDesc attDesc = concept.getAttributeDesc(affectedAttribute);
						int tickerCounter = Integer
								.parseInt(in.getAttForDesc(concept.getAttributeDesc("ticker_counter")).getValueAsString());
						int oldValue = Integer
								.parseInt(in.getAttForDesc(concept.getAttributeDesc(affectedAttribute)).getValueAsString());
						int newValue = calculateNewAttributeValue(attDesc, in, affectedAttribute, tickerCounter);
						affection += " " + oldValue + " -> " + newValue;
						attributeAffections.add(affection);
						try {
							in.addAttribute(concept.getAttributeDesc(affectedAttribute), newValue);
						} catch (ParseException e) {
							LOGGER.error("Altering attribute: " + affectedAttribute + " of player: " + in.getName()
							+ " failed: " + e.getStackTrace());
						}
						
					}
					
				}
			}
		}
		return attributeAffections;
	}

	private ArrayList<GameSituation> evaluateProcessableSituations(ArrayList<GameSituation> potentialSituations,
			ICaseBase cb, Concept concept, HashMap<String, String> vocabulary) {
		ArrayList<GameSituation> processableSituations = new ArrayList<GameSituation>();
		for (int i = 0; i < potentialSituations.size(); i++) {
			String caseName = getPlayerFromDatabase(concept, cb.getCases(), potentialSituations.get(i).getActor().getLastName());
			
			// Check if case exists in case base
			if (!caseName.equals("DEFAULT")) {
				// Check if the action identifier is contained in the vocabulary
				if (vocabulary.containsKey(potentialSituations.get(i).getAction().getIdentifier())) {
					
					boolean caseAlreadyRecognized = false;
					for (String caseRecognized : this.recognizedPlayers) {
						if (caseRecognized.equals(caseName)) {
							caseAlreadyRecognized = true;
						}
					}
					if (!caseAlreadyRecognized) {
						this.recognizedPlayers.add(caseName);
					}
					
					processableSituations.add(potentialSituations.get(i));
				} else {
					LOGGER.info("Action is not valid according to the used vocabulary.");
					processableSituations.add(new GameSituation());
				}
			} else {
				LOGGER.info("Case not found in DB, so no procession possible.");
				processableSituations.add(new GameSituation());
			}
		}
		
		return processableSituations;
	}

	/**
	 * This method tries to verify that a recognized named entity is present in the
	 * case base by comparing specific information. It is not fully reliable due to
	 * the circumstance that it is not possible to determine unique IDs for players
	 * within a fully textual live-ticker.
	 * 
	 * 
	 * @param concept    the my cbr concept to determine attribute descriptions
	 * @param players    the players which are present in the case base
	 * @param liveticker the livetcker object containing metadata about the game
	 *                   observed
	 * @param name       the name of the recognized NE by Stanford Core NLP
	 * @return a String indicating the case name to refer for later alteration of
	 *         the case if the player does not exist in the case base return
	 *         "DEFAULT"
	 */

	private String getPlayerFromDatabase(Concept concept, Collection<Instance> players, String name) {

		String caseName = "DEFAULT";
		for (Instance player : players) {
			// Name fits
			if (player.getAttForDesc(concept.getAttributeDesc("last_name")).getValueAsString().equals(name)) {
				// Current club is one of the clubs in the observed live ticker
				if (player.getAttForDesc(concept.getAttributeDesc("current_club")).getValueAsString()
						.equals(this.liveTicker.getTeamOne())
						|| player.getAttForDesc(concept.getAttributeDesc("current_club")).getValueAsString()
								.equals(this.liveTicker.getTeamTwo())) {
					// Player from case base is playing in the league of the observed live ticker
					if (player.getAttForDesc(concept.getAttributeDesc("league")).getValueAsString()
							.equals(this.liveTicker.getLeague())) {
						return player.getName();
					}

				}
			}
		}

		return caseName;
	}
	


	private ArrayList<GameSituation> evaluatePotentialSituations(List<String> posTags, List<CoreLabel> tokens, HashMap<String, String> vocabulary, JLanguageTool languagetool) {

		String potentialPlayer = "";
		String actionIdentifier = "";
		
		ArrayList<GameSituation> potentialSituations = new ArrayList<GameSituation>();
		
		for (int i = 0; i < tokens.size(); i++) {
			boolean situationDefined = false;
			// Current token is a named entity and thus potentially a player
			if (posTags.get(i).equals("NE")) {

				// Initialize a GameSituation that gets specified later on
				GameSituation sit = new GameSituation();

				// Get the potential player name from the named entity, regardless if it is a
				// player or not
				potentialPlayer = tokens.get(i).toString().substring(0, tokens.get(i).toString().indexOf("-"));

				// If there is a word before this named entity
				if ((i - 1) >= 0) {
					// If the preceeding token is a preposition we might detect an action by going
					// further
					if (posTags.get(i - 1).equals("APPR")) {
						System.out.println("It is a preposition!");
						// If there is a word before this preposition
						if ((i - 2) >= 0) {
							// If this word is a noun, it is regarded as an action
							if (posTags.get(i - 2).equals("NN")) {
								System.out.println("It is a noun!");
								actionIdentifier = tokens.get(i - 2).toString().substring(0,
										tokens.get(i - 2).toString().indexOf("-"));

								sit = new GameSituation(new Player(potentialPlayer),
										new Action(actionIdentifier, vocabulary.get(actionIdentifier)));
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

						String lemmaAction = "";
						try {
							List<AnalyzedSentence> analyzedSentences = languagetool.analyzeText(actionIdentifier);
							for (AnalyzedSentence analyzedSentence : analyzedSentences) {
								for (AnalyzedTokenReadings analyzedTokens : analyzedSentence
										.getTokensWithoutWhitespace()) {
									lemmaAction = analyzedTokens.getReadings().get(0).getLemma();

								}
							}

							sit = new GameSituation(new Player(potentialPlayer),
									new Action(lemmaAction, vocabulary.get(actionIdentifier)));
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
					
					potentialSituations.add(sit);

				}

			}
		}
		return potentialSituations;
		
	}

	private void normalizeAttributes(String caseName, Concept concept, ICaseBase cb) throws NumberFormatException, ParseException {
		
		Instance player = null;
		
		for (Instance in : cb.getCases()) {
			if (in.getName().equals(caseName)) {
				player = in;
			}
		}
		
		int increment = 5;
		int decrement = -5;
		
		IntegerDesc offensiveDesc = (IntegerDesc) concept.getAttributeDesc("offensive");
		IntegerDesc defensiveDesc = (IntegerDesc) concept.getAttributeDesc("defensive");
		IntegerDesc fairplayDesc = (IntegerDesc) concept.getAttributeDesc("fairplay");
		IntegerDesc passingDesc = (IntegerDesc) concept.getAttributeDesc("passing");
		IntegerDesc vitalityDesc = (IntegerDesc) concept.getAttributeDesc("vitality");
		IntegerDesc duelsDesc = (IntegerDesc) concept.getAttributeDesc("duels");

		int offensiveValue = Integer.parseInt(player.getAttForDesc(offensiveDesc).getValueAsString());
		int defensiveValue = Integer.parseInt(player.getAttForDesc(offensiveDesc).getValueAsString());
		int fairplayValue = Integer.parseInt(player.getAttForDesc(offensiveDesc).getValueAsString());
		int passingValue = Integer.parseInt(player.getAttForDesc(offensiveDesc).getValueAsString());
		int vitalityValue = Integer.parseInt(player.getAttForDesc(offensiveDesc).getValueAsString());
		int duelsValue = Integer.parseInt(player.getAttForDesc(offensiveDesc).getValueAsString());

		player.addAttribute(offensiveDesc, offensiveValue + decrement);
		player.addAttribute(defensiveDesc, defensiveValue + decrement);
		player.addAttribute(fairplayDesc, fairplayValue + decrement);
		player.addAttribute(passingDesc, passingValue + decrement);
		player.addAttribute(vitalityDesc, vitalityValue + increment);
		player.addAttribute(duelsDesc, duelsValue + increment);

	}

	private HashMap<String, String> initVocabularyMap() throws JsonProcessingException, IOException {
		File file = new File("/Users/tadeus/Desktop/vocabulary.yaml");
		System.out.println("Initializing the vocabulary map.");

		// Instantiating a new ObjectMapper as a YAMLFactory
		ObjectMapper om = new ObjectMapper(new YAMLFactory());

		JsonNode jn = om.readTree(file);

		HashMap<String, String> vocabulary = new HashMap<String, String>();
		String[] fieldNames = { "offensive", "defensive", "passing", "fairplay", "vitality", "duels" };
		for (String fn : fieldNames) {
			System.out.println("At attribute: " + fn);
			JsonNode jn2 = jn.get(fn);
			for (int i = 0; i < jn2.size(); i++) {
				System.out.println("Adding " + jn2.get(i).toString() + " as a valid term.");
				vocabulary.put(jn2.get(i).toString().replaceAll("\"", ""), fn);

			}

		}

		return vocabulary;

	}

	private int calculateNewAttributeValue(AttributeDesc attDesc, Instance instance, String affectedAttribute,
			int tickerCounter) {
		// Fairplay and vitality can only get negatively influenced due to actions
		System.out.println("Calculating new attribute value for: " + affectedAttribute);
		int currentScore = Integer.parseInt(instance.getAttForDesc(attDesc).getValueAsString());
		System.out.println("Current score for this player is: " + currentScore);
		int newScore = currentScore;
		switch (affectedAttribute) {
		case "offensive":
		case "defensive":
		case "duels":
		case "passing":
			newScore += 5;
			break;
		case "fairplay":
		case "vitality":
			newScore -= 5;
			break;
		default:
			System.out.println("Invalid attribute affected: " + affectedAttribute);
		}

		System.out.println("New score is: " + newScore);
		if (newScore > 100) {
			return 100;
		} else {
			return newScore;
		}

	}

}
