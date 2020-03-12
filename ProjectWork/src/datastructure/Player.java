package datastructure;

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
	

}
