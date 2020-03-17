package datastructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.languagetool.JLanguageTool;
import org.languagetool.language.GermanyGerman;

import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.Concept;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.StringUtils;

// Angelehnt an Ontologie der BA
public class GameSituation {
	
	private Player actor;
	private boolean potentialSituation;
	private boolean processedSituation;
	private Action action;
	private String tickerEntry;
	private String attributeAffection;
	private LiveTicker liveticker;
	
	public GameSituation() {
		super();
		this.actor = new Player("NICHT IN CB");
		this.action = new Action("", "none");
		
	}
	public GameSituation(Player actor, Action action) {
		super();
		this.actor = actor;
		this.action = action;
	}
	public GameSituation(String tickerEntry) {
		this.potentialSituation = false;
		this.processedSituation = false;
		this.tickerEntry = tickerEntry;
	}
	public Player getActor() {
		return actor;
	}
	public void setActor(Player actor) {
		this.actor = actor;
	}
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public void process() {
		
		
		
	} 

	
}
