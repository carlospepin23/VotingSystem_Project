package main;

import data_structures.ArrayList;
import data_structures.DoublyLinkedList;
import interfaces.List;

/**
 * 
 * The Ballot class pretends to be what it stands for, ballots belonging to
 * candidates in electoral processes in class Election.
 * 
 * @author Carlos J. Pepin Delgado
 * @version 3/15/2023
 *
 * 
 */
public class Ballot {

	/**
	 * Here are the private properties of the class, which is essential information
	 * that a Ballot must have.
	 */
	private int ballot_number;
	private boolean blank_ballot = false;

	/**
	 * The candidate_ids & candidates_ranks lists are ArrayList because the get() 
	 * class method is constantly used, and the time complexity for this
	 * class method is constant O(1).
	 */

	private List<Integer> candidate_ids = new ArrayList<Integer>();
	private List<Integer> candidate_ranks = new ArrayList<Integer>();

	/**
	 * The ballot_candidates list is DoublyLinkedList because it is the list that
	 * best suits it given that objects are constantly being added at the end, and
	 * the add() time complexity in DoublyLinkedList is constant O(1).
	 */

	private List<Candidate> ballot_candidates = new DoublyLinkedList<Candidate>();

	/**
	 * Constructor that creates a ballot based on the line it receives. The format for line is
	 * id#,candidate_name . It also receives a List of all the candidates in the
	 * elections.
	 * 
	 * @param line (CSV File line to be parsed)
	 * @param candidates
	 * 
	 */

	public Ballot(String line, List<Candidate> candidates) {
		String[] line_parts = line.split(",");

		// Stores ballot number
		this.ballot_number = Integer.parseInt(line_parts[0]);

		try {
			// With clean string separates ids from ranks
			String[] parsed_range = line.substring(line_parts[0].length() + 1).split(",");
			// Adds ids and ranks in sorted lists
			for (String pair : parsed_range) {
				String[] ids_ranks = pair.split(":");

				candidate_ids.add((Integer.parseInt(ids_ranks[0])));
				candidate_ranks.add((Integer.parseInt(ids_ranks[1])));
			}
		} catch (Exception e) {
			this.blank_ballot = true;
		}

		// Stores candidates
		for (int i = 0; i < candidates.size(); i++) {
			this.ballot_candidates.add(candidates.get(i));
		}

	}

	/**
	 * 
	 * This method returns if the candidate with the given id was eliminated
	 * 
	 * @param candidateId
	 * @return if the candidate with the given id was eliminated (boolean)
	 */

	public boolean eliminate(int candidateId) {
		// first checks if the list contains the candidate
		if (!candidate_ids.contains(candidateId))
			return false;

		int erased_rank_val = -1;
		int erased_rank_pos = -1;

		// if it contains the candidate, removes it from the list of rank, id and active
		// candidates
		for (int i = 0; i < candidate_ids.size(); i++) {
			if ((candidate_ids.get(i)) == candidateId) {
				candidate_ids.remove(i);
				erased_rank_val = candidate_ranks.get(i);
				erased_rank_pos = i;
				candidate_ranks.remove(i);
				break;
			}
		}

		ballot_candidates.remove(erased_rank_pos);

		// updates ranks equal or greater by reducing 1 rank
		for (int i = 0; i < candidate_ranks.size(); i++) {
			if (candidate_ranks.get(i) >= erased_rank_val) {
				candidate_ranks.set(i, candidate_ranks.get(i) - 1);
			}
		}
		return true;
	}

	/**
	 * 
	 * This method returns the ballot number
	 * 
	 * @return the ballot number (int)
	 */

	public int getBallotNum() {
		return this.ballot_number;
	}

	/**
	 * 
	 * This method returns the ballot type
	 * 
	 * @return an integer that indicates if the ballot is: 0 – valid, 1 – blank or 2 - invalid (int)
	 */
	public int getBallotType() {

		if (blank_ballot) {
			return 1;
		}

		for (int rank : candidate_ranks) {
			// checks that there are no ranks greater than the number of candidates.
			if (rank > ballot_candidates.size()) {
				return 2;
			}
			// verifies for rank duplicates
			if (candidate_ranks.firstIndex(rank) != candidate_ranks.lastIndex(rank)) {
				return 2;
			}
		}

		// verifies for candidates duplicates
		for (int id : candidate_ids) {
			if (candidate_ids.firstIndex(id) != candidate_ids.lastIndex(id)) {
				return 2;
			}
		}

		// verifies for skip ranks
		List<Integer> total_ranks = new ArrayList<Integer>(candidate_ranks.size());
		for (int i = 0; i < candidate_ranks.size(); i++) {
			total_ranks.add(i + 1);
		}

		for (int r : total_ranks) {
			if (!candidate_ranks.contains(r)) {
				return 2;
			}
		}

		// if it passes the requirements, then the ballot is valid.
		return 0;
	}

	/**
	 * 
	 * This method returns the candidate id with that rank, if no candidate is available return -1.
	 * 
	 * @param rank
	 * @return the candidate id with that rank, if no candidate is available return -1. (int)
	 */
	public int getCandidateByRank(int rank) {

		// iterates through the rank list to see if it finds it and returns the
		// candidate.
		for (int i = 0; i < candidate_ranks.size(); i++) {
			if (rank == candidate_ranks.get(i)) {
				return candidate_ids.get(i);
			}
		}
		return -1;
	}

	/**
	 * 
	 * This method returns the rank for the candidate id, if no rank is available return -1
	 * 
	 * @param candidateID
	 * @return the rank for the candidate id, if no rank is available return -1 (int)
	 */

	public int getRankByCandidate(int candidateID) {

		// Iterates through the candidate list to see if it finds it and returns the
		// rank.
		for (int i = 0; i < candidate_ids.size(); i++) {
			if (candidateID == candidate_ids.get(i)) {
				return candidate_ranks.get(i);
			}
		}
		return -1;
	}

}
