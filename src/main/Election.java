package main;

import interfaces.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import data_structures.ArrayList;
import main.Candidate;
import main.Ballot;


public class Election {
	private List<Candidate> candidates=new ArrayList<Candidate>();
	private List<Ballot> ballots=new ArrayList<Ballot>();
	private List<ArrayList<Integer>> tournament=new ArrayList<ArrayList<Integer>>();
	
	/* Constructor that implements the election logic using the files candidates.csv 
	and ballots.csv as input. (Default constructor) */ 
	public Election() {
	}
	/* Constructor that receives the name of the candidate and ballot files and applies 
	the election logic. Note: The files should be found in the input folder. */
	public Election(String candidates_filename, String ballot_filename) throws IOException {

		//utilize BufferedReader & FileReader to read the csv files containing the data
		BufferedReader candidates_Reader=new BufferedReader(new FileReader("inputFiles/"+candidates_filename));
		BufferedReader ballots_Reader=new BufferedReader(new FileReader("inputFiles/"+ballot_filename));

		
		String c_line,b_line;
		//genera lista de candidatos
	    while ((c_line = candidates_Reader.readLine()) != null) {
	    	candidates.add(new Candidate(c_line));
	    }
	    //genera lista de ballots
	    while ((b_line = ballots_Reader.readLine()) != null) {
	    	ballots.add(new Ballot(b_line, candidates));
	    }
	    
	    //genera lista de listas [candidato1[1,2,3], candidato2[4,5,6]]
	    for(int i=0;i<candidates.size();i++) { //por cada candidato
	    	ArrayList<Integer>t=new ArrayList<Integer>(); //candidato i
	    	
	    	for(int j=0;j<ballots.size();j++) { //por cada papeleta
	    		if(ballots.get(j).getBallotType()==0) {
	    			Integer b=ballots.get(j).getRankByCandidate(candidates.get(i).getId()); //papeleta j
	    			t.add(b);
	    		}else {
	    			continue;
	    		}
	    		
	    	}
    		
	    	tournament.add(t);
	    	
	    }

	}
	// returns the name of the winner of the election 
	public String getWinner() {
		return "Na";
	} 
	// returns the total amount of ballots submitted
	public int getTotalBallots() {
		return ballots.size();
	} 
	// returns the total amount of invalid ballots
	public int getTotalInvalidBallots() {
		int counter=0;
		for(Ballot b:ballots) {
			if(b.getBallotType()==2) counter++;
			
		}
		
		return counter;
	} 
	// returns the total amount of blank ballots
	public int getTotalBlankBallots() {
		int counter=0;
		for(Ballot b:ballots) {
			if(b.getBallotType()==1) counter++;
			
		}
		
		return counter;
	}
	// returns the total amount of valid ballots
	public int getTotalValidBallots() {
		int counter=0;
		for(Ballot b:ballots) {
			if(b.getBallotType()==0) counter++;
			
		}
		
		return counter;
	}
	/* List of names for the eliminated candidates with the numbers of 1s they had, 
	must be in order of elimination. Format should be <candidate name>-<number of 1s 
	when eliminated>*/
	public List<String> getEliminatedCandidates() {
		return null;
	}
	
//	public void elimination() {
//		
//	}
	
	public static void main(String[] args) throws IOException {
			Election election = new Election("candidates.csv", "ballots.csv");
			
			assertAll(
					() -> assertTrue(election.getWinner().equals("Pepe Perez"), "Didn't get Correct winner."),
					() -> assertTrue(election.getTotalBallots() == 15,"Didn't count correct amount of ballots"),
					() -> assertTrue(election.getTotalBlankBallots() == 3, "Didn't count correct amount of blank ballots"),
					() -> assertTrue(election.getTotalInvalidBallots() == 2, "Didn't count correct amount of invalid ballots"),
					() -> assertTrue(election.getTotalBlankBallots() == 3, "Didn't count correct amount of blank ballots"),
					() -> assertTrue(election.getTotalValidBallots() == 10, "Didn't count correct amount of valid ballots"),
					() -> assertTrue(election.getEliminatedCandidates().get(0).equals("Lola Mento-1"), "Didn't return correct eliminated candidate and/or count for this position"),
					() -> assertTrue(election.getEliminatedCandidates().get(1).equals("Juan Lopez-1"), "Didn't return correct eliminated candidate and/or count for this position"),
					() -> assertTrue(election.getEliminatedCandidates().get(2).equals("Pucho Avellanet-3"), "Didn't return correct eliminated candidate and/or count for this position")
					);
			

		
	}
}
