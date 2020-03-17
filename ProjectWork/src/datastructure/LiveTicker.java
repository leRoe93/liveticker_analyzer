package datastructure;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LiveTicker {
	
	// Additional HashMap for line up would be nice
	String teamOne;
	String teamTwo;
	String league;
	ArrayList<String> tickerEntries = new ArrayList<String>();

	public LiveTicker(String tickerUrl) throws IOException {
		// ?subpage=aufstellung does not work due to java script generated content
		Document doc = Jsoup.connect(tickerUrl).get();

		Elements links = doc.select(".rowBright");
		
		Element header = doc.select("#mcHeaderleft").get(0);
		String leaguePart = header.select("strong").html();
		String league = header.select("strong").html().substring(leaguePart.indexOf(" ") + 1);

		String teamsString = header.html().substring(header.html().indexOf("</strong>") +  10, header.html().indexOf(", "));
		
		this.league = league;
		this.teamOne = teamsString.split(" - ")[0];
		this.teamTwo = teamsString.split(" - ")[1];
		
		System.out.println(this.teamOne + " vs. " + this.teamTwo + " in league: " + this.league);
		String content = "";

		for (Element link : links) {
			// Removing the inner html block for the minute text, because it is not needed
			// in this application
			content = link.getElementsByClass("text").html().replaceAll("<[^>]*>", "");
			// Line begings with a digit and therefore is a minute text
			if (Character.isDigit(content.charAt(0))) {
				this.tickerEntries.add(content);
			}

		}
	}
	public String getTeamOne() {
		return teamOne;
	}
	public void setTeamOne(String teamOne) {
		this.teamOne = teamOne;
	}
	public String getTeamTwo() {
		return teamTwo;
	}
	public void setTeamTwo(String teamTwo) {
		this.teamTwo = teamTwo;
	}
	public String getLeague() {
		return league;
	}
	public void setLeague(String league) {
		this.league = league;
	}
	public ArrayList<String> getTickerEntries() {
		return tickerEntries;
	}
	public void setTickerEntries(ArrayList<String> tickerEntries) {
		this.tickerEntries = tickerEntries;
	}
	
	

}
