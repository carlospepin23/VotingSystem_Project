package main;

import interfaces.List;

public class Ballot {
	/* Creates a ballot based on the line it receives. The format for line is 
	id#,candidate_name . It also receives a List of all the candidates in the 
	elections.*/
	public Ballot(String line, List<Candidate> candidates) {
	}
	// Returns the ballot number
	public int getBallotNum() {
		return 0;
	} 
	

	//Returns the rank for that candidate, if no rank is available return -1
	public int getRankByCandidate(int candidateID) {
		return 0;
	} 
	//Returns the candidate with that rank, if no candidate is available return -1.
	public int getCandidateByRank(int rank) {
		return 0;
	} 
	// Eliminates the candidate with the given id
	public boolean eliminate(int candidateId) {
		return false;
	} 
	/* Returns an integer that indicates if the ballot is: 0 – valid, 1 – blank or 2 -
	invalid */
	public int getBallotType() {
		return 0;
	}
}
