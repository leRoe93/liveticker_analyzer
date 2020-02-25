package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.simple.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import edu.stanford.nlp.util.StringUtils;


import datastructure.GameSituation;

public class NaturalLanguageProcessor {
	
	// Information darüber, welche Spieler beteiligt sind für ER
	private HashMap<String, ArrayList<String>> players;
	private String entry;
	
	
	public NaturalLanguageProcessor(HashMap<String, ArrayList<String>> players, String entry) {
		super();
		this.players = players;
		this.entry = entry;
	}
	
	
	// Returns a list of recognized game situations
	public ArrayList<GameSituation> process() {
		
		/*
		 * Requirements:
		 * 
		 * -> Splitting the entry into sentences.
		 * -> Recognition of player names
		 * 		-> [optional] Recognition of other involved players
		 * -> Recognition of a specific action
		 * 		-> Indication of attitude, success and quality (positive / negative)
		 * 		-> Recognition of relationships (who performed this action)
		 */
		
		
		String sampleGermanText = this.entry;
        Annotation germanAnnotation = new Annotation(sampleGermanText);
        Properties germanProperties = StringUtils.argsToProperties(
                new String[]{"-props", "StanfordCoreNLP-german.properties"});
        StanfordCoreNLP pipeline = new StanfordCoreNLP(germanProperties);
        pipeline.annotate(germanAnnotation);
        for (CoreMap sentence : germanAnnotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree sentenceTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            System.out.println(sentenceTree);
        }
        
       return null;
	}

}




