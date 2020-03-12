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
	
	private Player actor;
	private Action action;
	public GameSituation() {
		super();
		
	}
	public GameSituation(Player actor, Action action) {
		super();
		this.actor = actor;
		this.action = action;
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
	
	
}
