package main;

import interfaces.List;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;

import data_structures.ArrayList;
import data_structures.DoublyLinkedList;


public class Election {
	//Se esta usando arraylist para las 3 listas de abajo porque para todas se usa mucho la funcion get
	//y en arraylist la complejidad de get es o(1).
	private List<Candidate> candidates=new ArrayList<Candidate>();
	private List<ArrayList<Ballot>> board=new ArrayList<ArrayList<Ballot>>();
	private List<String> eliminated_candidates=new ArrayList<String>();
	
	//porque no se hace shifting, y esta lista solo esta para anadir,
	//valores, y removerselos, y en doublylinkedlist la complejidad es o(1);
	List<Candidate> survivors=new DoublyLinkedList<Candidate>();
	
	int total_ballots=0,valid_ballots=0,blank_ballots=0,invalid_ballots=0,rip_votes=0,winner_votes=0;
	
	
	public Election() {
		this("candidates.csv","ballots.csv");
			    	   
	}
	
	/* Constructor that receives the name of the candidate and ballot files and applies 
	the election logic. Note: The files should be found in the input folder. */
	public Election(String candidates_filename, String ballot_filename) {
		
		//INPUT
		//utilize BufferedReader & FileReader to read the csv files containing the data
		BufferedReader candidates_Reader = null,ballots_Reader=null;
		try {
			candidates_Reader=new BufferedReader(new FileReader("inputFiles/"+candidates_filename));
			ballots_Reader=new BufferedReader(new FileReader("inputFiles/"+ballot_filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String candidate_line,ballot_line;
		//genera lista de candidatos
	    try {
			while ((candidate_line = candidates_Reader.readLine()) != null) {
				candidates.add(new Candidate(candidate_line));
			}
			//se usa mucho get, y en arrlist su complejidad es o(1).
			List<Ballot> ballots=new ArrayList<Ballot>();
			
			//genera lista de ballots
		    while ((ballot_line = ballots_Reader.readLine()) != null) {
		    	Ballot temp=new Ballot(ballot_line, candidates);

		    		total_ballots++;
			    	if(temp.getBallotType()==0) {
			    		ballots.add(temp);
			    		valid_ballots++;
			    	}
			    	else if(temp.getBallotType()==1) {
			    		blank_ballots++;
			    	}
			    	else if(temp.getBallotType()==2) {
			    		invalid_ballots++;
			    	}
			  }
		  
		    	
		    for(Candidate c:candidates) { //por cada candidato
		    	ArrayList<Ballot>t=new ArrayList<Ballot>(); //candidato i
		    	
		    	for(Ballot b:ballots) { //por cada papeleta de los candidatos correspondiente
		    		t.add(b);
		    	}
	    		
		    	board.add(t);
		    }
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    //OUTPUT
	    outputTxtWriter();

	}
	
// returns the name of the winner of the election 
public String getWinner() {
		
		//if the candidate has more than 50% (lucky_num) of the ballots, the candidate wins
		int lucky_num=valid_ballots/2;
		boolean not_found=true;
		
		while(not_found) {
			printBallotDistribution();
			int i=0;
			for(Candidate c:candidates) {
				
				if(c.isActive()) {
					ArrayList<Ballot>b=board.get(i); //candidato de cada posicion correspondiente
					if(rank_Counter(c,b,1)>lucky_num) {
						winner_votes=rank_Counter(c,b,1);
						return c.getName();	
					}
					i++;		
				}	
			}
			
			eliminate_Lowest_Candidate();
			reclassification();	
		
		}
		return null;
	}

	// returns the total amount of winner #1 votes
	public int getWinnerVotes() {
		return winner_votes;
		
	}
	// returns the total amount of ballots submitted
	public int getTotalBallots() {
		return total_ballots;
	} 
	// returns the total amount of invalid ballots
	public int getTotalInvalidBallots() {
		return invalid_ballots;

	} 
	// returns the total amount of blank ballots
	public int getTotalBlankBallots() {
		return blank_ballots;
	}
	// returns the total amount of valid ballots
	public int getTotalValidBallots() {
		return valid_ballots;
	}
	
	/* List of names for the eliminated candidates with the numbers of 1s they had, 
	must be in order of elimination. Format should be <candidate name>-<number of 1s 
	when eliminated>*/
	public List<String> getEliminatedCandidates() {
		
		List<String> e=new ArrayList<String>(eliminated_candidates.size());
		for(String n:eliminated_candidates) {
			if(n!=null) e.add(n);
		}
		return e;

	}
	
	public void reclassification() {
		ArrayList<Ballot> ballots=board.get(0);
		board.clear();
		for(Candidate c:candidates) {
			if(c.isActive()) {
				board.add(ballots);
			}
		}
	}
	
	public void eliminate_Lowest_Candidate() {
		int r=1;
		survivors.clear();
		for(Candidate c:candidates) {
			if(c.isActive()) {
				survivors.add(c);
			}
			
		}
		
		while(r<=valid_ballots) { //valid_b would be the max possible ballot rank
			Candidate loser;
			if(r<valid_ballots) {
				ArrayList<Integer> ranks_eliminatory=new ArrayList<Integer>(); //esta en orden con los candidatos
				for(int i=0;i<board.size();i++) {
					ranks_eliminatory.add(rank_Counter(survivors.get(i),board.get(i), r));   //c1:4,c2:3,c3:0
				}
			
				loser=min_Candidate(ranks_eliminatory); //esto obtiene la pos del candidato con menos votos
				
			}else loser=tie_Breaker(survivors.get(0));
			
			if(loser!=null) { //si hay un min 
				for(int j=0;j<board.get(0).size();j++) {
					board.get(0).get(j).eliminate(loser.getId());
				}
				
				candidates.get(candidates.firstIndex(loser)).erase();

				eliminated_candidates.add(loser.getName()+"-"+Integer.toString(rip_votes));
				break;
			}
			r++;	
		}
	}
	
	//returns the count of the selected rank for a single candidate
	public int rank_Counter(Candidate candidate, ArrayList<Ballot> candidate_ballots,int rank) { //funciona
		int counter=0;
		for(int i=0;i<candidate_ballots.size();i++) {
			if(candidate_ballots.get(i).getRankByCandidate(candidate.getId())==rank) counter++;
		}
		
		return counter;
	}
	
	//returns position
		public Candidate min_Candidate(ArrayList<Integer> ranks_of_candidates) {
			//se utiliza get en esta lista, y es mas conveniente arraylist
			List<Integer> to_remove=new ArrayList<Integer>();
			int min_pos=0;
			int tie_pos=0;
			int tieCounter=0;
			
			for(int i=1;i<ranks_of_candidates.size();i++) {
				if(ranks_of_candidates.get(i)<ranks_of_candidates.get(min_pos)) {
					min_pos=i; 

				}else if(ranks_of_candidates.get(i).equals(ranks_of_candidates.get(min_pos))) {

					tie_pos=min_pos;
					tieCounter++;
				}
				
			}
			
			if((tie_pos==min_pos)&&(tieCounter!=ranks_of_candidates.size()-1)) {
				//desempate		
				for(int r=0;r<ranks_of_candidates.size();r++) {

					if(!ranks_of_candidates.get(min_pos).equals(ranks_of_candidates.get(r))) {	
						to_remove.add(r);
					
					}
				}
			
				for(int k=to_remove.size()-1;k>=0;k--) {
					board.remove(to_remove.get(k));
					survivors.remove(to_remove.get(k));
				}
				
				return null;
				
				
			}else if((tie_pos==min_pos)&&(tieCounter==ranks_of_candidates.size()-1)) {
				//System.err.println("Eliminated by rank");
				return tie_Breaker(survivors.get(0));
			}
			
			rip_votes=rank_Counter(survivors.get(min_pos),board.get(min_pos), 1);
			return survivors.get(min_pos);
		}
	
	/**
	* Prints all the general information about the election as well as a 
	* table with the vote distribution.
	* Meant for helping in the debugging process.
	*/
	
	public void printBallotDistribution() {
		System.out.println("Total ballots:" + getTotalBallots());
		System.out.println("Total blank ballots:" + getTotalBlankBallots());
		System.out.println("Total invalid ballots:" + getTotalInvalidBallots());
		System.out.println("Total valid ballots:" + getTotalValidBallots());
		System.out.println(getEliminatedCandidates());
		int i=0;
		for(Candidate c:candidates) {
			
			if(c.isActive()) {
				System.out.print(c.getName().substring(0, c.getName().indexOf(" ")) + "\t");
			
				for(int j=0;j<board.get(i).size();j++) {
					if(c.isActive()) {
						Ballot b=board.get(i).get(j);
						int rank = b.getRankByCandidate(c.getId());
						String tableline = "| " + ((rank != -1) ? rank: " ") + " ";
						System.out.print(tableline); 
					}
				}
				System.out.println("|");
				i++;
			}
			
			
		}
	}
	
	public Candidate tie_Breaker(Candidate loser) {
		rip_votes=rank_Counter(loser,board.get(0), 1);
		for(int i=1;i<survivors.size();i++) {
			if(survivors.get(i).getId()>loser.getId()) {
				loser=survivors.get(i);
				rip_votes=rank_Counter(loser,board.get(i), 1);
			}	
		}
		return loser;
	}
	
	public void printCandidates(Function<Candidate,String> func) {
		for (Candidate c : candidates) {
            System.out.println(func.apply(c));
        }
		
	}
	
	public int countBallots(Function<Ballot,Boolean> func) {
		int counter=0;
		for (Ballot b : board.get(0)) {
			if(func.apply(b)){
				counter++;
			}
        }
		System.out.println(Integer.toString(counter));
		return counter;
		
	}


	public void outputTxtWriter() {
	    String name = this.getWinner().replace(" ", "_").toLowerCase();

	    try (BufferedWriter writer = new BufferedWriter(new FileWriter("outputFiles/" + name + getWinnerVotes() + ".txt"))) {
	        writer.write("Number of ballots: " + getTotalBallots()+"\n");
	        writer.write("Number of blank ballots: " + getTotalBlankBallots()+"\n");
	        writer.write("Number of invalid ballots: " + getTotalInvalidBallots()+"\n");

	        for (int i=0;i<getEliminatedCandidates().size(); i++) {
	            String n = getEliminatedCandidates().get(i);
	            String[] eliminated_data = n.split("-");
	            writer.write("Round "+(i+1)+": "+eliminated_data[0]+" was eliminated with "+eliminated_data[1]+" #1's\n");
	           
	        }
	        writer.write("Winner: "+getWinner()+" wins with "+getWinnerVotes()+" #1's\n");
	        writer.close();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public static void main(String[] args) {
		Election election1 = new Election();
	    Election election2 = new Election("candidates.csv","ballots.csv");
	    Election election3 = new Election("candidates.csv","ballots2.csv");
		Election election5 = new Election("candidates2.csv","ballots4.csv");
		Election election6 = new Election("candidates.csv","ballots5.csv");
		

	    //Bonus Functions:
		System.out.println();
		
		election1.printCandidates((c)-> c.getId() + " " + c.getName() + "" + ((c.isActive()) ? "": "+"));
		
		System.out.println();
	
		election1.countBallots((b) -> b.getRankByCandidate(1) == 2); 
		/* Returns count 
		of how many ballot have candidate 1 as rank 2 */
		election1.countBallots((b) -> b.getRankByCandidate(2) == 2); 
	    /* Returns count 
		of how many ballot have candidate 2 as rank 2 */
		election1.countBallots((b) -> b.eliminate(3)); 
	    /* Returns count of how many 
		ballots eliminated candidate 3. */

	}
}

