package datastructure;

import java.util.ArrayList;

// Angelehnt an Ontologie der BA
public class GameSituation {
	
	/*
	 * private enum Attitude { DEFENSIVE, OFFENSIVE, NEUTRAL }
	 * 
	 * private enum Action { DUEL, GOAL_SHOT, FOUL, PASS, }
	 * 
	 * private Player actor; private Player opponent; private Player teamMember;
	 * private boolean success; private boolean isPositive; private Attitude
	 * attitude; private Action specificAction;
	 */
	
	
	private ArrayList<String> playerProposals;
	private ArrayList<String> actionProposals;
	
	
	
	public ArrayList<String> getPlayerProposals() {
		return playerProposals;
	}
	public void setPlayerProposals(ArrayList<String> playerProposals) {
		this.playerProposals = playerProposals;
	}
	public ArrayList<String> getActionProposals() {
		return actionProposals;
	}
	public void setActionProposals(ArrayList<String> actionProposals) {
		this.actionProposals = actionProposals;
	}
}
