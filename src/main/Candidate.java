package main;

/**
 * 
 * The Candidate class represents the people who compete in the electoral
 * process in the Election class, and have different preference rankings
 * assigned by the public.
 * 
 * 
 * @author Carlos J. Pepin Delgado
 * @version 3/15/2023
 * 
 */
public class Candidate {

	/**
	 * Here are the private properties of the class, which is essential information
	 * that a Candidate must have.
	 */
	private int id;
	private boolean active = true;
	private String name;

	/**
	 * 
	 * Constructor that creates a Candidate from the line. The line will have the format
	 * ID#,candidate_name
	 * 
	 * @param line
	 * 
	 */

	public Candidate(String line) {
		String[] data = line.split(",");
		id = Integer.valueOf(data[0]);
		name = data[1];

	}

	/**
	 * sets the candidate active status to false
	 */
	public void erase() {
		active = false;
	}

	/**
	 * 
	 * This method returns the candidate id
	 * 
	 * @return the candidate’s id (int)
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * 
	 * This method returns the candidates name
	 * 
	 * @return the candidates name (String)
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * This method returns whether the candidate is active in the election
	 * 
	 * @return whether the candidate is active in the election (boolean)
	 */
	public boolean isActive() {
		return this.active == true;
	}

}
