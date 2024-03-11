package main;

import data_structures.ArrayList;
import interfaces.List;

public class Ballot {
	
	//private properties
	private int ballot_number;
	private List<Integer> candidate_ids=new ArrayList<Integer>();
	private List<Integer> candidate_ranks=new ArrayList<Integer>();
	private List<Candidate> ballot_candidates=new ArrayList<Candidate>();
	
	
	/* Creates a ballot based on the line it receives. The format for line is 
	id#,candidate_name . It also receives a List of all the candidates in the 
	elections.*/
	public Ballot(String line, List<Candidate> candidates) {
		//Ballot ballot = new Ballot("2,7:1,5:2,1:3,9:4,4:5,2:6,8:7,6:8,10:9,3:10", candidates);
		String[] line_parts=line.split(",");
		
		//Se guarda numero de ballot
		this.ballot_number=Integer.parseInt(line_parts[0]);
		//con el string limpio separa los ids de los ranks
		String[] parsed_range=line.substring(line_parts[0].length() + 1).split(",");
		//anade ids y ranks en listas ordenadas
		for(String pair:parsed_range) {
			String[] ids_ranks=pair.split(":");
			
			candidate_ids.add((Integer.parseInt(ids_ranks[0])));
			candidate_ranks.add((Integer.parseInt(ids_ranks[1])));
		}
		//guarda los candidatos
		for(int i=0;i<candidates.size();i++) {
			this.ballot_candidates.add(candidates.get(i));
		}
		
		
	}
	// Returns the ballot number
	public int getBallotNum() {
		return this.ballot_number;
	} 
	

	//Returns the rank for that candidate, if no rank is available return -1
	public int getRankByCandidate(int candidateID) {
		//itera por la lista de candidato para ver si lo encuentra y devolver el rank
		for(int i=0;i<candidate_ids.size();i++) {
			if(candidateID==candidate_ids.get(i)){
				return candidate_ranks.get(i);
			}
		}
		return -1;
	} 
	//Returns the candidate with that rank, if no candidate is available return -1.
	//itera por la lista de rank para ver si lo encuentra y devolver el candidato
	public int getCandidateByRank(int rank) {
		for(int i=0;i<candidate_ranks.size();i++) {
			if(rank==candidate_ranks.get(i)){
				return candidate_ids.get(i);
			}
		}
		return -1;
	} 
	// Eliminates the candidate with the given id
	
	public boolean eliminate(int candidateId) {
		//primero verifica si la lista contiene el candidato
		if(!candidate_ids.contains(candidateId)) return false;
		
		//si contiene al candidato, lo elimina de la lista de rank, id y de los candidatos activos
		for(int i=0;i<candidateId;i++) {
			if((candidate_ids.get(i))==candidateId) {
				candidate_ids.remove(i);
				candidate_ranks.remove(i);
			}
		}
		for(Candidate c:ballot_candidates) {
			if(c.getId()==candidateId) {
				c.erase();
			}
		}
		return true;
	} 
	/* Returns an integer that indicates if the ballot is: 0 – valid, 1 – blank or 2 -
	invalid */
	public int getBallotType() {
		return this.ballot_number;
	}

}
