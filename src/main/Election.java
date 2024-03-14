package main;

import interfaces.List;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
	
	int total_b=0,valid_b=0,blank_b=0,invalid_b=0,rip_v=0,winner_v=0;
	
	
	public Election() {
		this("candidates.csv","ballots.csv");
			    	   
	}
	
	/* Constructor that receives the name of the candidate and ballot files and applies 
	the election logic. Note: The files should be found in the input folder. */
	public Election(String candidates_filename, String ballot_filename) {
		
		//utilize BufferedReader & FileReader to read the csv files containing the data
		BufferedReader candidates_Reader = null,ballots_Reader=null;
		try {
			candidates_Reader=new BufferedReader(new FileReader("inputFiles/"+candidates_filename));
			ballots_Reader=new BufferedReader(new FileReader("inputFiles/"+ballot_filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String c_line,b_line;
		//genera lista de candidatos
	    try {
			while ((c_line = candidates_Reader.readLine()) != null) {
				candidates.add(new Candidate(c_line));
			}
			//se usa mucho get, y en arrlist su complejidad es o(1).
			List<Ballot> ballots=new ArrayList<Ballot>();
			
			//genera lista de ballots
		    while ((b_line = ballots_Reader.readLine()) != null) {
		    	Ballot temp=new Ballot(b_line, candidates);
		    	//if(temp!=null) {
		    		total_b++;
			    	if(temp.getBallotType()==0) {
			    		ballots.add(temp);
			    		valid_b++;
			    	}
			    	else if(temp.getBallotType()==1) {
			    		blank_b++;
			    	}
			    	else if(temp.getBallotType()==2) {
			    		invalid_b++;
			    	}
		    	//}
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

	}
	// returns the name of the winner of the election 
	public String getWinner() {
		
		//if the candidate has more than 50% (lucky_num) of the ballots, the candidate wins
		int lucky_num=valid_b/2;
		boolean not_found=true;
		
		while(not_found) {
			printBallotDistribution();
			for(int i=0;i<board.size();i++) {
				ArrayList<Ballot>b=board.get(i); //candidato de cada posicion correspondiente
				Candidate c=candidates.get(i);
				if(rank_Counter(c,b,1)>lucky_num) {
					winner_v=rank_Counter(c,b,1);
					return candidates.get(i).getName();
				}
			}
			
			eliminate_Lowest_Candidate();
			reclassification();	
		
		}
		return null;
	}
	// returns the total amount of winner #1 votes
	public int getWinnerVotes() {
		return winner_v;
		
	}
	// returns the total amount of ballots submitted
	public int getTotalBallots() {
		return total_b;
	} 
	// returns the total amount of invalid ballots
	public int getTotalInvalidBallots() {
		return invalid_b;

	} 
	// returns the total amount of blank ballots
	public int getTotalBlankBallots() {
		return blank_b;
	}
	// returns the total amount of valid ballots
	public int getTotalValidBallots() {
		return valid_b;
	}
	
	/* List of names for the eliminated candidates with the numbers of 1s they had, 
	must be in order of elimination. Format should be <candidate name>-<number of 1s 
	when eliminated>*/
	public List<String> getEliminatedCandidates() {
		return eliminated_candidates;
	}
	
	public void reclassification() {
		ArrayList<Ballot> ballots=board.get(0);
		board.clear();
	    //genera lista de listas [candidato1[1,2,3], candidato2[4,5,6]]
	    for(Candidate c:candidates) { //por cada candidato
	    	board.add(ballots);
	    }
	}
	
	public void eliminate_Lowest_Candidate() {
		int r=1;
		survivors.clear();
		for(Candidate c:candidates) {
			survivors.add(c);
		}
		
		while(r<=valid_b) { //valid_b would be the max possible ballot rank
			Candidate loser;
			if(r<valid_b) {
				ArrayList<Integer> ranks_eliminatory=new ArrayList<Integer>(); //esta en orden con los candidatos
				for(int i=0;i<board.size();i++) {
					ranks_eliminatory.add(rank_Counter(survivors.get(i),board.get(i), r));   //c1:4,c2:3,c3:0
				}
			
				loser=min_Candidate(ranks_eliminatory); //esto obtiene la pos del candidato con menos votos
				
			}else {
				loser=survivors.get(0);
				rip_v=rank_Counter(loser,board.get(0), 1);
				for(int i=1;i<survivors.size();i++) {
					if(survivors.get(i).getId()>loser.getId()) {
						loser=survivors.get(i);
						rip_v=rank_Counter(loser,board.get(i), 1);
					}
					
				}
				
			}

			//error es que despues de encontrar el empate de los mas bajitos,busca el 2 para todos
			if(loser!=null) { //si hay un min 

					for(int j=0;j<board.get(0).size();j++) {
						board.get(0).get(j).eliminate(loser.getId());
					}
				
				candidates.remove(loser); // 
				eliminated_candidates.add(loser.getName()+"-"+Integer.toString(rip_v));
				
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
	public Candidate min_Candidate(ArrayList<Integer> ranks_of_candidates) {		//funciona
		//se utiliza get en esta lista, y es mas conveniente arraylist
		List<Integer> to_remove=new ArrayList<Integer>();
		int min_pos=0;
		for(int i=1;i<ranks_of_candidates.size();i++) {
			if(ranks_of_candidates.get(i)<ranks_of_candidates.get(min_pos)) {
				min_pos=i;
				
			//if theres two equal mins, theres need to be a tie breaker
			}else if (ranks_of_candidates.get(i).equals(ranks_of_candidates.get(min_pos))) {
				
				//desempate		
				for(int r=0;r<ranks_of_candidates.size();r++) {
					if(!ranks_of_candidates.get(r).equals(ranks_of_candidates.get(i))) {	
						to_remove.add(r);
						
					}
				}
				
				for(int k=to_remove.size()-1;k>=0;k--) {
					board.remove(to_remove.get(k));
					survivors.remove(to_remove.get(k));
				}
					
				return null;
				
			}
		}
		
		rip_v=rank_Counter(survivors.get(min_pos),board.get(min_pos), 1);
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
		for(int i=0;i<candidates.size();i++) {
			Candidate c=candidates.get(i);
			System.out.print(c.getName().substring(0, c.getName().indexOf(" ")) + "\t");
			for(int j=0;j<board.get(i).size();j++) {
				Ballot b=board.get(i).get(j);
				int rank = b.getRankByCandidate(c.getId());
				String tableline = "| " + ((rank != -1) ? rank: " ") + " ";
				System.out.print(tableline); 
			}
			System.out.println("|");
		}
	}


	
	public static void outputTxtWriter(Election election) {
	    String name = election.getWinner().replace(" ", "_").toLowerCase();

	    try (BufferedWriter writer = new BufferedWriter(new FileWriter("outputFiles/" + name + election.getWinnerVotes() + ".txt"))) {
	        writer.write("Number of ballots: " + election.getTotalBallots()+"\n");
	        writer.write("Number of blank ballots: " + election.getTotalBlankBallots()+"\n");
	        writer.write("Number of invalid ballots: " + election.getTotalInvalidBallots()+"\n");

	        for (int i=0;i<election.getEliminatedCandidates().size(); i++) {
	            String n = election.getEliminatedCandidates().get(i);
	            String[] eliminated_data = n.split("-");
	            writer.write("Round "+(i+1)+": "+eliminated_data[0]+" was eliminated with "+eliminated_data[1]+" #1's\n");
	           
	        }
	        writer.write("Winner: "+election.getWinner()+" wins with "+election.getWinnerVotes()+" #1's\n");

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public static void main(String[] args) {
		//Election election1 = new Election();
	    //Election election2 = new Election("candidates.csv","ballots.csv");
	    Election election3 = new Election("candidates.csv","ballots2.csv"); //<--AVERIGUAR PORQUE HAY ESPACIOS EN BLANCO
		//Election election4 = new Election("candidates2.csv","ballots4.csv");
		//System.out.println(election5.getWinner());
	    //outputTxtWriter(election1);
	    //outputTxtWriter(election2);
	    outputTxtWriter(election3);
		//outputTxtWriter(election4);

	}
}

