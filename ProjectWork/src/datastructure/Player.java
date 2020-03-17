package datastructure;

import java.util.ArrayList;

public class Player {
	
	private enum Position {
	    GOALKEEPER,
	    DEFENSE,
	    MIDFIELD,
	    STORM
	  }
	
	private String firstName;
	private String lastName;
	
	private String club;
	private Position position;
	private ArrayList<Action> detectedActions;
	
	private String caseName;
	
	private boolean inDb = false;

	public Player(String lastName) {
		super();
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getClub() {
		return club;
	}

	public void setClub(String club) {
		this.club = club;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public boolean isInDb() {
		return inDb;
	}

	public void setInDb(boolean inDb) {
		this.inDb = inDb;
	}

	public ArrayList<Action> getDetectedActions() {
		return detectedActions;
	}

	public void setDetectedActions(ArrayList<Action> detectedActions) {
		this.detectedActions = detectedActions;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}
	
	
}
