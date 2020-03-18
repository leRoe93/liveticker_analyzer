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
import utils.PathingInfo;

/**
 * Servlet implementation class LiveTickerProcessorServlet
 */
@WebServlet("/LiveTickerProcessorServlet")
public class LiveTickerProcessorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private ArrayList<String> recognizedPlayers;
	
	private final static Logger LOGGER = Logger.getLogger(LiveTickerProcessorServlet.class);
	private LiveTicker liveTicker;

	/**
	 * @throws IOException
	 * @throws JsonProcessingException
	 * @see HttpServlet#HttpServlet()
	 */
	public LiveTickerProcessorServlet() throws JsonProcessingException, IOException {
		super();
		this.recognizedPlayers = new ArrayList<String>();
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
		this.recognizedPlayers = new ArrayList<String>();

		
		this.liveTicker = new LiveTicker(request.getParameter("url_lt"));
		
		expandLiveTicker();
	
		// Dynamic table creation for progression.jsp
		StringBuilder tableContent = buildProgressionTable();

		request.setAttribute("ticker_url", request.getParameter("url_lt"));
		request.setAttribute("tickerEntries", tableContent);

		request.getRequestDispatcher("progression.jsp").forward(request, response);

	}
	
	/**
	 * This method expands the liveticker object in this class by using NLP techniques to find processable 
	 * situations and attribute affections according to the defined vocabulary.
	 * 
	 * @throws JsonProcessingException if parsing the vocabulary file content fails.
	 * @throws IOException if reading the vocabulary file fails.
	 */
	private void expandLiveTicker() throws JsonProcessingException, IOException {
		
		// Initialize NLP tools for german language
		JLanguageTool languagetool = new JLanguageTool(new GermanyGerman());
		Properties germanProperties = StringUtils
				.argsToProperties(new String[] { "-props", "StanfordCoreNLP-german.properties" });
		StanfordCoreNLP pipeline = new StanfordCoreNLP(germanProperties);
		
		
		Project myproject = null;
		try {
			myproject = new Project(PathingInfo.PROJECT_PATH + PathingInfo.PROJECT_NAME);
			// Necessary because it takes some time until MyCBR fully loads the project
			while (myproject.isImporting()) {
				Thread.sleep(1000);
	
			}
		} catch (Exception e) {
			LOGGER.error("Exception occured while loading My CBR project: " + e.getMessage());
		}
		Concept concept = myproject.getConceptByID(PathingInfo.CONCEPT_NAME);
		ICaseBase cb = myproject.getCB(PathingInfo.CASE_BASE);
		
		// Initialize domain vocabulary 
		HashMap<String, String> vocabularyMap = initVocabularyMap();
		
		// Iterate over all live ticker entries available
		for (int i = 0; i < this.liveTicker.getTickerEntries().size(); i++) {
			
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
			
			// Generating the ArrayLists holding information to be displayed and adding them to the liveticker object
			ArrayList<GameSituation> potentialSituations = evaluatePotentialSituations(entry, posTags, tokens, languagetool, vocabularyMap);
			this.liveTicker.getPotentialSituations().add(potentialSituations);
			ArrayList<GameSituation> processableSituations = evaluateProcessableSituations(potentialSituations, cb, concept, vocabularyMap);
			this.liveTicker.getProcessableSituations().add(processableSituations);
			ArrayList<String> attributeAffections = evaluateAttributeAffections(processableSituations, concept, cb);
			this.liveTicker.getAttributeAffections().add(attributeAffections);
			
			// Necessary to make all changes to the project / case base persistent
			myproject.save();
		}
		// Always needs to be saved after changes are made to anthing within the project
		myproject.save();
	}

	/**
	 * This method initializes the domain vocabulary which is used to detect processable situations.
	 * 
	 * @return the vocabulary as a HashMap
	 * @throws JsonProcessingException if parsing the content of the vocabulary file failed.
	 * @throws IOException if opening the vocabulary file failed.
	 */
	private HashMap<String, String> initVocabularyMap() throws JsonProcessingException, IOException {
		File file = new File(PathingInfo.VOC_YAML);
		LOGGER.info("Initializing the vocabulary map.");
	
		// Instantiating a new ObjectMapper as a YAMLFactory
		ObjectMapper om = new ObjectMapper(new YAMLFactory());
	
		JsonNode jn = om.readTree(file);
	
		HashMap<String, String> vocabulary = new HashMap<String, String>();
		String[] fieldNames = { "offensive", "defensive", "passing", "fairplay", "vitality", "duels" };
		for (String fn : fieldNames) {
			LOGGER.info("At attribute: " + fn);
			JsonNode jn2 = jn.get(fn);
			for (int i = 0; i < jn2.size(); i++) {
				LOGGER.info("Adding " + jn2.get(i).toString() + " as a valid term.");
				vocabulary.put(jn2.get(i).toString().replaceAll("\"", ""), fn);
	
			}
	
		}
	
		return vocabulary;
	
	}

	/**
	 * This method processes a live ticker entry and detects potential situations according to self-designed algortihm.
	 * Valid potential situations are:
	 * <ul>
	 * 	<li>A named entity preceeeded by a preposition and a noun (e.g. Zuspiel von Müller)</li>
	 * 	<li>A named entity directly followed by a verb (e.g. Müller schießt)</li>
	 * </ul>
	 * 
	 * @param posTags the posTags assigned by Stanford Core NLP.
	 * @param tokens the tokens splitted by Stanford Core NLP.
	 * @param the tickerEntry that gets analyzed.
	 * @param languagetool the languageTool to lemmatize a german verb.
	 * @return A List containing game situations that fit the definition of a valid potential situation.
	 */
	private ArrayList<GameSituation> evaluatePotentialSituations(String entry, List<String> posTags, List<CoreLabel> tokens, JLanguageTool languagetool, HashMap<String, String> vocabulary) {
	
		String potentialPlayer = "";
		String actionIdentifier = "";
		
		ArrayList<GameSituation> potentialSituations = new ArrayList<GameSituation>();
		
		for (int i = 0; i < tokens.size(); i++) {
			boolean situationDefined = false;
			// Current token is a named entity and thus potentially a player
			if (posTags.get(i).equals("NE")) {
				LOGGER.info(tokens.get(i) + " is a named entity.");
				// Initialize a GameSituation that gets specified later on
				GameSituation sit = new GameSituation();
	
				// Get the potential player name from the named entity, regardless if it is a
				// player or not
				potentialPlayer = tokens.get(i).toString().substring(0, tokens.get(i).toString().indexOf("-"));
	
				// If there is a word before this named entity
				if ((i - 1) >= 0) {
					// If the preceeding token is a preposition we might detect an action by going
					// further
					if (posTags.get(i - 1).equals("APPR") && tokens.get(i - 1).toString().substring(0, tokens.get(i - 1).toString().indexOf("-")).equals("von")) {
						LOGGER.info(tokens.get(i - 1) + " is a preposition preceeding: " + tokens.get(i));
						// If there is a word before this preposition
						if ((i - 2) >= 0) {
							// If this word is a noun, it is regarded as an action
							if (posTags.get(i - 2).equals("NN")) {
								LOGGER.info(tokens.get(i - 2) + " is a noun preceeding: " + tokens.get(i - 1));
								LOGGER.info("Found a valid potential situation: " + tokens.get(i - 2) + tokens.get(i - 1) + tokens.get(i));
								actionIdentifier = tokens.get(i - 2).toString().substring(0,
										tokens.get(i - 2).toString().indexOf("-"));
	
								sit = new GameSituation(new Player(potentialPlayer),
										new Action(actionIdentifier, vocabulary.get(actionIdentifier)), entry);
								situationDefined = true;
	
							}
						}
					}
				}
	
				// If there is word after this named entity
				if ((i + 1) < tokens.size() && situationDefined == false) {
					// If the following token is a verb, it is regarded as an action
					if (posTags.get(i + 1).equals("VVFIN")) {
						LOGGER.info(posTags.get(i + 1) + " is a verb following " + posTags.get(i) + " and no situation got defined yet.");
						LOGGER.info("Found a valid potential situation: " + posTags.get(i) + posTags.get(i + 1));
						actionIdentifier = tokens.get(i + 1).toString().substring(0,
								tokens.get(i + 1).toString().indexOf("-"));
	
						String lemmaAction = "";
						
						// Find lemma of the verb for uniformly detecting actions
						try {
							List<AnalyzedSentence> analyzedSentences = languagetool.analyzeText(actionIdentifier);
							for (AnalyzedSentence analyzedSentence : analyzedSentences) {
								for (AnalyzedTokenReadings analyzedTokens : analyzedSentence
										.getTokensWithoutWhitespace()) {
									lemmaAction = analyzedTokens.getReadings().get(0).getLemma();
	
								}
							}
	
							sit = new GameSituation(new Player(potentialPlayer),
									new Action(lemmaAction, vocabulary.get(lemmaAction)), entry);
							situationDefined = true;
						} catch (IOException e) {
							LOGGER.error("Exception occured while analyzing action with languagatool: " + e.getMessage());
						}
	
					}
				} else {
					LOGGER.info("No possibility to find a situation!");
				}
	
				// Only add it to the list if a situationed actually has been defined
				if (situationDefined == true) {
					
					potentialSituations.add(sit);
	
				}
	
			}
		}
		return potentialSituations;
		
	}

	/**
	 * This method "filters" the potential situations to get processable situations. 
	 * For that it compares the detected NE with the case base and the action with the valid vocabulary.
	 * 
	 * @param potentialSituations the potential situations detected before.
	 * @param cb the currently used cased base.
	 * @param concept the currently used concept.
	 * @param vocabulary the valid domain vocabulary for actions.
	 * @return a List containing placeholders for non-processable situations and processable situations.
	 */
	private ArrayList<GameSituation> evaluateProcessableSituations(ArrayList<GameSituation> potentialSituations,
			ICaseBase cb, Concept concept, HashMap<String, String> vocabulary) {
		ArrayList<GameSituation> processableSituations = new ArrayList<GameSituation>();
		for (int i = 0; i < potentialSituations.size(); i++) {
			String caseName = retrieveCaseName(concept, cb.getCases(), potentialSituations.get(i).getActor().getLastName());
			
			// Check if case exists in case base
			if (!caseName.equals("DEFAULT")) {
				// Check if the action identifier is contained in the vocabulary
				if (vocabulary.containsKey(potentialSituations.get(i).getAction().getIdentifier())) {
					
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
	 * This method evaluates the attribute affections to display them in the result as well as 
	 * edits the respective player as a case to assign the new attribute value.
	 * 
	 * @param processableSituations the processable Situations with NEs contained in CB and actions defined in vocabulary.
	 * @param concept the currently used concept.
	 * @param cb the currently used case base.
	 * @return a List of Strings defining the output to be displayed on progression.jsp
	 */
	private ArrayList<String> evaluateAttributeAffections(ArrayList<GameSituation> processableSituations, Concept concept, ICaseBase cb) {
		ArrayList<String> attributeAffections = new ArrayList<String>();
		String affection = "Attribut: ";
	
		// Iterate over every processable situation
		for (GameSituation processableSituation : processableSituations) {
	
			// If the processable situation is just a placeholder
			if (processableSituation.getAction().getAffectedAttribute() == null || processableSituation.getAction().getAffectedAttribute().equals("none")) {
				attributeAffections.add("Keine Wertung");
			} else {
				// Store affected attribute to use it to determine attribute in instance later on
				String affectedAttribute = processableSituation.getAction().getAffectedAttribute();
				affection += affectedAttribute;
				
				// Get the case name of the current actor based on last name and liveticker context information
				String caseName = retrieveCaseName(concept, cb.getCases(),
					processableSituation.getActor().getLastName());
	
				// Iterate over all instances given in the case base
				for (Instance in : cb.getCases()) {
					// Find the fitting case (can be combined with getPlayerFromDatabase method later on)
					if (in.getName().equals(caseName)) {
						
						try {
							System.out.println(this.recognizedPlayers);
							LOGGER.fatal(caseName + " seems to be in that list");
							// If player case gets modified for the first time, normalize all attributes
							if (!this.recognizedPlayers.contains(caseName)) {
								normalizeAttributes(caseName, concept, cb);
								this.recognizedPlayers.add(caseName);
							}
							
							// Getting desc and information for affected case
							AttributeDesc attDesc = concept.getAttributeDesc(affectedAttribute);
							int tickerCounter = Integer.parseInt(
									in.getAttForDesc(concept.getAttributeDesc("ticker_counter")).getValueAsString());
							int oldValue = Integer.parseInt(
									in.getAttForDesc(concept.getAttributeDesc(affectedAttribute)).getValueAsString());
							// Determine the new value based on affected attribute
							int newValue = calculateNewAttributeValue(attDesc, in, affectedAttribute, tickerCounter);
							
							// Complete affection information and add it to List
							affection += " " + oldValue + " -> " + newValue;
							attributeAffections.add(affection);
							
							// Getting the "old" ticker entries and append them with the currently processed one
							StringDesc entryDesc = (StringDesc) concept.getAttributeDesc("ticker_entries");
							LinkedList<Attribute> entryList = new LinkedList<Attribute>();
							
							for (String oldEntry : in.getAttForDesc(concept.getAttributeDesc("ticker_entries")).getValueAsString().split(";")) {
								entryList.add(concept.getAttributeDesc("ticker_entries").getAttribute(oldEntry));
							}
							entryList.add(concept.getAttributeDesc("ticker_entries").getAttribute(processableSituation.getTickerEntry()));
							MultipleAttribute<StringDesc> tickerEntries = new MultipleAttribute<StringDesc>(entryDesc, entryList);
							// Alter the case with new attribute value
							in.addAttribute(entryDesc, tickerEntries);
							in.addAttribute(concept.getAttributeDesc(affectedAttribute), newValue);
						} catch (ParseException e) {
							LOGGER.error("Altering attribute: " + affectedAttribute + " of player: " + in.getName()
							+ " failed: " + e.getMessage());
						}
						
					}
					
				}
			}
		}
		return attributeAffections;
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

	private String retrieveCaseName(Concept concept, Collection<Instance> players, String name) {

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
	

	/**
	 * This method normalizes each attribute when a player gets detected in the live ticker for the first time.
	 * --> Normalizing means to decrease and increase attributes to ensure changing values over time
	 *     if mentions regarding specific attributes are absent.
	 * <ul>
	 * 	<li>Offensive, Defensive, Passing, Duels  -> decrease by 5.</li>
	 *  <li>Fairplay, Vitality -> increase by 5.</li>
	 * </ul>
	 * @param caseName the caseName for the case that has to be normalized.
	 * @param concept the currently used concept.
	 * @param cb the currently used case base.
	 * @throws ParseException if adding the new attribute values to the case failed.
	 */
	private void normalizeAttributes(String caseName, Concept concept, ICaseBase cb) throws ParseException {
		
		LOGGER.info("Normalizing attributes for player: " + caseName);
		Instance player = null;
		
		// Force getting the player from the case base
		for (Instance in : cb.getCases()) {
			if (in.getName().equals(caseName)) {
				player = in;
			}
		}
		
		int increment = 5;
		int decrement = -5;
		
		// Getting all descs for attribute assignment
		IntegerDesc offensiveDesc = (IntegerDesc) concept.getAttributeDesc("offensive");
		IntegerDesc defensiveDesc = (IntegerDesc) concept.getAttributeDesc("defensive");
		IntegerDesc fairplayDesc = (IntegerDesc) concept.getAttributeDesc("fairplay");
		IntegerDesc passingDesc = (IntegerDesc) concept.getAttributeDesc("passing");
		IntegerDesc vitalityDesc = (IntegerDesc) concept.getAttributeDesc("vitality");
		IntegerDesc duelsDesc = (IntegerDesc) concept.getAttributeDesc("duels");
		
		LOGGER.info("Values before normalizing: ");
		LOGGER.info("Offensive: " + player.getAttForDesc(offensiveDesc).getValueAsString());
		LOGGER.info("Defensive: " + player.getAttForDesc(defensiveDesc).getValueAsString());
		LOGGER.info("Fairplay: " + player.getAttForDesc(fairplayDesc).getValueAsString());
		LOGGER.info("Zuspiele: " + player.getAttForDesc(passingDesc).getValueAsString());
		LOGGER.info("Vitalitaet: " + player.getAttForDesc(vitalityDesc).getValueAsString());
		LOGGER.info("Zweikaempfe: " + player.getAttForDesc(duelsDesc).getValueAsString());

		// Getting all current values from case
		int offensiveValue = Integer.parseInt(player.getAttForDesc(offensiveDesc).getValueAsString());
		int defensiveValue = Integer.parseInt(player.getAttForDesc(defensiveDesc).getValueAsString());
		int fairplayValue = Integer.parseInt(player.getAttForDesc(fairplayDesc).getValueAsString());
		int passingValue = Integer.parseInt(player.getAttForDesc(passingDesc).getValueAsString());
		int vitalityValue = Integer.parseInt(player.getAttForDesc(vitalityDesc).getValueAsString());
		int duelsValue = Integer.parseInt(player.getAttForDesc(duelsDesc).getValueAsString());

		// Adding the new values to the case
		player.addAttribute(offensiveDesc, offensiveValue + decrement);
		player.addAttribute(defensiveDesc, defensiveValue + decrement);
		player.addAttribute(fairplayDesc, fairplayValue + decrement);
		player.addAttribute(passingDesc, passingValue + decrement);
		player.addAttribute(vitalityDesc, vitalityValue + increment);
		player.addAttribute(duelsDesc, duelsValue + increment);
		
		LOGGER.info("Values after normalizing: ");
		LOGGER.info("Offensive: " + player.getAttForDesc(offensiveDesc).getValueAsString());
		LOGGER.info("Defensive: " + player.getAttForDesc(defensiveDesc).getValueAsString());
		LOGGER.info("Fairplay: " + player.getAttForDesc(fairplayDesc).getValueAsString());
		LOGGER.info("Zuspiele: " + player.getAttForDesc(passingDesc).getValueAsString());
		LOGGER.info("Vitalitaet: " + player.getAttForDesc(vitalityDesc).getValueAsString());
		LOGGER.info("Zweikaempfe: " + player.getAttForDesc(duelsDesc).getValueAsString());

	}

	/**
	 * This method calculates a new attribute value if an action mentioned affects a certain attribute.
	 * Offensive, Defensive, Passing and Duels have to be increased on mention.
	 * Fairplay and Vitality have to be decreased on mention.
	 * 
	 * @param attDesc the attribute desc of the affected attribute.
	 * @param instance the instance that has to be altered with effective change.
	 * @param affectedAttribute the attribute name that is affected.
	 * @param tickerCounter the tickerCounter indicating how many tickers have been 
	 * 		  processed for this player (not used currently).
	 * @return the new attribute value.
	 */
	private int calculateNewAttributeValue(AttributeDesc attDesc, Instance instance, String affectedAttribute,
			int tickerCounter) {
		LOGGER.info("Calculating new attribute value for: " + affectedAttribute);
		int currentScore = Integer.parseInt(instance.getAttForDesc(attDesc).getValueAsString());
		LOGGER.info("Current score for this player is: " + currentScore);
		int newScore = currentScore;
		switch (affectedAttribute) {
			case "offensive":
			case "defensive":
			case "duels":
			case "passing":
				newScore += 5;
				break;
			// Fairplay and vitality can only get negatively influenced due to actions
			case "fairplay":
			case "vitality":
				newScore -= 5;
				break;
			default:
				LOGGER.warn("Invalid attribute affected: " + affectedAttribute);
		}

		if (newScore > 100) {
			LOGGER.info("New score is: " + 100);
			return 100;
		} else {
			LOGGER.info("New score is: " + newScore);
			return newScore;
		}

	}

	/**
	 * This method produces a dynamic table containing the result of the processes liveticker object.
	 * 
	 * @return the HTML table.
	 */
	
	private StringBuilder buildProgressionTable() {
		StringBuilder tableContent = new StringBuilder();
		
		// Initialize table and headlines
		tableContent.append("<table class='table table-striped table-bordered table-hover' id='tickerEntries'>");
		tableContent.append("<thead class='thead-dark'><tr>" + "<th>Prozessierter Ticker Eintrag</th>"
				+ "<th>Potenzielle Aktionen</th>" + "<th>Verwertete Aktionen</th>" + "<th>Betroffenes Attribut</th>"
				+ "</tr></thead>");
		tableContent.append("<tbody>");
		
		// Only use potential for iteration because processable situations and affected attributes are holding the same indices
		for (int i = 0; i < this.liveTicker.getPotentialSituations().size(); i++) {
			
			tableContent.append("<tr>");
			tableContent.append("<td class='entry'>" + this.liveTicker.getTickerEntries().get(i) + "</td>");
			
			// Column with potential situations information
			String potentialContent = "<ol>";
			for (GameSituation gs : this.liveTicker.getPotentialSituations().get(i)) {
				potentialContent += "<li>Entitaet: " + gs.getActor().getLastName() + ", Aktion: " + gs.getAction().getIdentifier() + "</li>";
			}
			potentialContent += "</ol>";
			
			// Column with processable situations information
			String processableContent = "<ol>";
			for (GameSituation gs : this.liveTicker.getProcessableSituations().get(i)) {
				// Placeholder if situation is not possible to process according to case base and vocabulary
				if (gs.getActor().getLastName().equals("NICHT IN CB")) {
					processableContent += "<li></li>";
				} else {
					processableContent += "<li>Spieler: " + gs.getActor().getLastName() + ", Aktion: " + gs.getAction().getIdentifier() + "</li>";
				}
			}
			processableContent += "</ol>";
			
			// Column with attribute affection information
			String affectionContent = "<ol>";
			for (String affection : this.liveTicker.getAttributeAffections().get(i)) {
				affectionContent += "<li>" + affection + "</li>";
	
			}
			affectionContent += "</ol>";
			
			// Add information to tableConcent
			tableContent.append("<td class='info'>" + potentialContent + "</td>");
			tableContent.append("<td class='info'>" + processableContent + "</td>");
			tableContent.append("<td class='info'>" + affectionContent + "</td>");
			
			tableContent.append("</tr>");
			
		}
		
		// Close body and table
		tableContent.append("</tbody>");
		tableContent.append("</table>");
		
		return tableContent;
	}

}
