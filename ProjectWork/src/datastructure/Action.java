package datastructure;

public class Action {
	
	private String identifier;
	private String affectedAttribute;

	public Action(String identifier, String affectedAttribute) {
		super();
		this.identifier = identifier;
		this.affectedAttribute = affectedAttribute;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getAffectedAttribute() {
		return affectedAttribute;
	}

	public void setAffectedAttribute(String affectedAttribute) {
		this.affectedAttribute = affectedAttribute;
	}
	
	

}
