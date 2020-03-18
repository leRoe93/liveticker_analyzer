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
	private Action action;
	private String tickerEntry;
	private String attributeAffection;
	
	public GameSituation() {
		super();
		this.actor = new Player("NICHT IN CB");
		this.action = new Action("", "none");
		
	}
	public GameSituation(Player actor, Action action, String tickerEntry) {
		super();
		this.actor = actor;
		this.action = action;
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
	public String getTickerEntry() {
		return tickerEntry;
	}
	public void setTickerEntry(String tickerEntry) {
		this.tickerEntry = tickerEntry;
	}
	public String getAttributeAffection() {
		return attributeAffection;
	}
	public void setAttributeAffection(String attributeAffection) {
		this.attributeAffection = attributeAffection;
	}

	
}
