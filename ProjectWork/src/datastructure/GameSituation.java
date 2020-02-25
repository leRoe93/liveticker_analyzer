package datastructure;


// Angelehnt aus Ontologie der BA
public class GameSituation {
	
	private enum Attitude {
		DEFENSIVE,
		OFFENSIVE,
		NEUTRAL
	}
	
	private enum Action {
		DUEL,
		GOAL_SHOT,
		FOUL,
		PASS,	
	}
	
	private Player actor;
	private Player opponent;
	private Player teamMember;
	private boolean success;
	private boolean isPositive;
	private Attitude attitude;
	private Action specificAction;

}
