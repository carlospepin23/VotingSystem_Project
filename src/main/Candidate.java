package main;

public class Candidate {
	

	//private properties
	private int id;
	private boolean active=true;
	private String name;
	
	
	/* Creates a Candidate from the line. The line will have the format ID#,candidate_name */
	public Candidate(String line) {
		String[]data=line.split(",");
		id=Integer.valueOf(data[0]);
		name=data[1];
		
		
	}
	// returns the candidate’s id
	public int getId() {
		return this.id;
	} 
	// Whether the candidate is still active in the election
	public boolean isActive() {
		return this.active==true;
	} 
	// return the candidates name
	public String getName() {
		return this.name;
	}
	//added methods
	//eliminates candidate and it sets active to false
	public void erase() {
		active=false;
	}
	
}
