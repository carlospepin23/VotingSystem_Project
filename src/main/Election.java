package main;

import interfaces.List;

public class Election {
	/* Constructor that implements the election logic using the files candidates.csv 
	and ballots.csv as input. (Default constructor) */ 
	public Election() {
	}
	/* Constructor that receives the name of the candidate and ballot files and applies 
	the election logic. Note: The files should be found in the input folder. */
	public Election(String candidates_filename, String ballot_filename) {
	}
	// returns the name of the winner of the election 
	public String getWinner() {
		return null;
	} 
	// returns the total amount of ballots submitted
	public int getTotalBallots() {
		return 0;
	} 
	// returns the total amount of invalid ballots
	public int getTotalInvalidBallots() {
		return 0;
	} 
	// returns the total amount of blank ballots
	public int getTotalBlankBallots() {
		return 0;
	}
	// returns the total amount of valid ballots
	public int getTotalValidBallots() {
		return 0;
	}
	/* List of names for the eliminated candidates with the numbers of 1s they had, 
	must be in order of elimination. Format should be <candidate name>-<number of 1s 
	when eliminated>*/
	public List<String> getEliminatedCandidates() {
		return null;
	}
	
}
