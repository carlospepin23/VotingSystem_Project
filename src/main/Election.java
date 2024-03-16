package main;

import interfaces.List;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

import data_structures.ArrayList;
import data_structures.DoublyLinkedList;

/**
 * 
 * The Election class represents the electoral process that is carried out to
 * determine the winning Candidate() determined by their respective Ballots().
 * 
 * @author Carlos J. Pepin Delgado
 * @version 3/15/2023
 *
 * 
 */

public class Election {

	/**
	 * Here are the private properties of the class, which is essential information
	 * that an Election must have.
	 * -----------------------------------------------------------------------------
	 * In next three lists ArrayLists below are used because it is essential
	 * constantly use the class method get() and in ArrayList the time complexity of
	 * this class method is constant O(1).
	 */
	private List<Candidate> candidates = new ArrayList<Candidate>();
	private List<ArrayList<Ballot>> board = new ArrayList<ArrayList<Ballot>>(); // 2D LIST
	private List<String> eliminated_candidates = new ArrayList<String>();

	/**
	 * DoublyLinkedList is used for this list because its add() class method at the
	 * end of the list has a constant O(1) time complexity, and that is its main
	 * functionality in this code.
	 */
	private List<Candidate> survivors = new DoublyLinkedList<Candidate>();

	private int total_ballots = 0, valid_ballots = 0, blank_ballots = 0, invalid_ballots = 0, rip_votes = 0,
			winner_votes = 0;

	/**
	 * Default constructor that creates an election based on the candidates in
	 * "candidates.csv" and the ballots "ballots.csv".
	 */
	public Election() {
		this("candidates.csv", "ballots.csv");
	}

	/**
	 * Constructor that receives the name of the candidate and ballot files and
	 * applies the election logic. Note: The files should be found in the input
	 * folder.
	 */
	public Election(String candidates_filename, String ballot_filename) {

		// INPUT DATA
		// utilize BufferedReader & FileReader to read the csv files containing the data
		BufferedReader candidates_Reader = null, ballots_Reader = null;
		try {
			candidates_Reader = new BufferedReader(new FileReader("inputFiles/" + candidates_filename));
			ballots_Reader = new BufferedReader(new FileReader("inputFiles/" + ballot_filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String candidate_line, ballot_line;
		// generates list of candidates
		try {
			while ((candidate_line = candidates_Reader.readLine()) != null) {
				candidates.add(new Candidate(candidate_line));
			}

			/*
			 * The reason for this list is that get() class method is widely used,and in
			 * ArrayList its complexity is O(1).
			 */
			List<Ballot> ballots = new ArrayList<Ballot>();

			// generates lists of ballots
			while ((ballot_line = ballots_Reader.readLine()) != null) {
				Ballot temp = new Ballot(ballot_line, candidates);

				total_ballots++;
				if (temp.getBallotType() == 0) {
					ballots.add(temp);
					valid_ballots++;
				} else if (temp.getBallotType() == 1) {
					blank_ballots++;
				} else if (temp.getBallotType() == 2) {
					invalid_ballots++;
				}
			}

			for (Candidate c : candidates) { // per each candidate
				ArrayList<Ballot> t = new ArrayList<Ballot>();

				for (Ballot b : ballots) { // for each candidate's ballot paper concerned
					t.add(b);
				}

				board.add(t);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// OUTPUT DATA
		outputTxtWriter();

		// Bonus Functions:
		System.out.println();
		printCandidates((c) -> c.getId() + " " + c.getName() + "" + ((c.isActive()) ? "" : "+"));
		System.out.println();

		countBallots((b) -> b.getRankByCandidate(1) == 2);
		/*
		 * Returns count of how many ballot have candidate 1 as rank 2
		 */
		countBallots((b) -> b.getRankByCandidate(2) == 2);
		/*
		 * Returns count of how many ballot have candidate 2 as rank 2
		 */
		countBallots((b) -> b.eliminate(3));
		/*
		 * Returns count of how many ballots eliminated candidate 3.
		 */

		System.out.println();

	}

	/**
	 * This class method returns the name of the winning candidate consists of three
	 * steps. First, it checks that there is no current candidate with more than 50%
	 * of the votes. Second, if not found, it proceeds to eliminate the candidate
	 * with the worst ranking, and to update the rankings of the other candidates.
	 * Third, it reclassifies the remaining candidates, to go back to step 1, until
	 * it finds the winner and return it.
	 * 
	 * @return the name of the election winner
	 */
	public String getWinner() {

		// if the candidate has more than 50% (lucky_num) of the ballots, the candidate
		// wins
		// STEP 1
		int lucky_num = valid_ballots / 2;
		boolean not_found = true;

		while (not_found) {
			printBallotDistribution(); // print out the updated voting information
			int i = 0;
			for (Candidate c : candidates) {

				if (c.isActive()) {
					ArrayList<Ballot> b = board.get(i); // candidate for each corresponding position
					if (rank_Counter(c, b, 1) > lucky_num) { // counts #1's ranks of candidate
						winner_votes = rank_Counter(c, b, 1);
						return c.getName();
					}
					i++;
				}
			}
			// STEP 2
			eliminate_Lowest_Candidate();

			// STEP 3
			reclassification();

			// REPEAT
		}
		return null;
	}

	/**
	 * 
	 * This method returns total amount of ballots
	 * 
	 * @return total amount of ballots (int)
	 */
	public int getTotalBallots() {
		return total_ballots;
	}

	/**
	 *
	 * This method returns total amount of invalid ballots
	 * 
	 * @return total amount of invalid ballots (int) [Ballot type: 2]
	 */
	public int getTotalInvalidBallots() {
		return invalid_ballots;

	}

	/**
	 * 
	 * This method returns total amount of blank ballots
	 * 
	 * @return total amount of blank ballots (int) (Ballot type: 1)
	 */
	public int getTotalBlankBallots() {
		return blank_ballots;
	}

	/**
	 * 
	 * This method returns total amount of valid ballots
	 * 
	 * @return total amount of valid ballots (int) (Ballot type: 0)
	 */
	public int getTotalValidBallots() {
		return valid_ballots;
	}

	/**
	 * This method returns total amount of ballots the list of names for the eliminated candidates with the numbers of 1s they had,
	 * in order of elimination.
	 * 
	 * @return list of eliminated candidates
	 */
	public List<String> getEliminatedCandidates() {

		List<String> e = new ArrayList<String>(eliminated_candidates.size());
		for (String n : eliminated_candidates) {
			if (n != null)
				e.add(n);
		}
		return e;

	}

	/**
	 * 
	 * This method returns the winner #1's votes
	 * 
	 * @return winner #1's votes (int)
	 */
	public int getWinnerVotes() {
		return winner_votes;

	}

	/**
	 * 
	 */
	public void eliminate_Lowest_Candidate() {
		int r = 1;
		survivors.clear();
		for (Candidate c : candidates) {
			if (c.isActive()) {
				survivors.add(c);// Adds the active candidates in the current Round.
			}

		}

		// valid_ballots would be the max possible ballot rank that can be used to find
		// a losing candidate.
		while (r <= valid_ballots) {
			Candidate loser;
			if (r < valid_ballots) {
				// ranks_eliminatory is in order of candidates
				ArrayList<Integer> ranks_eliminatory = new ArrayList<Integer>();
				for (int i = 0; i < board.size(); i++) {
					/*
					 * The lists of ranks uses rank_Counter() to add the candidates selected rank
					 * total count.
					 */
					ranks_eliminatory.add(rank_Counter(survivors.get(i), board.get(i), r));
				}
				/*
				 * The candidate with the lowest number of votes who should be eliminated is
				 * obtained here.
				 */
				loser = min_Candidate(ranks_eliminatory);

			} else
				/*
				 * if the maximum number of ranks has been reached and no losing candidate has
				 * been found, assume a tie and proceed to a tie-breaker.
				 */
				loser = tie_Breaker(survivors.get(0));

			if (loser != null) { // If a losing candidate was found, it is inactivated.
				for (int j = 0; j < board.get(0).size(); j++) {
					board.get(0).get(j).eliminate(loser.getId());
				}

				candidates.get(candidates.firstIndex(loser)).erase();

				eliminated_candidates.add(loser.getName() + "-" + Integer.toString(rip_votes));
				break;
			}

			/*
			 * If no losing candidate has been found, a lower preference rank will be used
			 * to count.
			 */
			r++;
		}
	}

	/**
	 * 
	 * This class method returns the selected rank count on all valid ballots.
	 * 
	 * @param candidate
	 * @param candidate_ballots
	 * @param rank
	 * @return candidate selected rank count on ballots (int)
	 */
	public int rank_Counter(Candidate candidate, ArrayList<Ballot> candidate_ballots, int rank) { // funciona
		int counter = 0;
		for (int i = 0; i < candidate_ballots.size(); i++) {
			if (candidate_ballots.get(i).getRankByCandidate(candidate.getId()) == rank)
				counter++;
		}

		return counter;
	}

	/**
	 * This method reclassifies the remaining active candidates in the 2D List
	 * keeping the electoral process working until a winner is found.
	 */
	public void reclassification() {
		/*
		 * The ballots' list for each candidate is the same so the first one can be
		 * reused.
		 */
		ArrayList<Ballot> ballots = board.get(0);
		board.clear();
		for (Candidate c : candidates) {
			if (c.isActive()) {
				board.add(ballots);
			}
		}
	}

	/**
	 * 
	 * Searches for the candidate with the fewest votes of a rank already counted by
	 * the rank_Counter() class.
	 * 
	 * @param ranks_of_candidates
	 * @return Candidate with the fewest votes to be eliminated (Candidate)
	 */
	public Candidate min_Candidate(ArrayList<Integer> ranks_of_candidates) {

		/*
		 * The reason for this list is that get() class method is widely used,and in
		 * ArrayList its complexity is O(1).
		 */

		List<Integer> to_remove = new ArrayList<Integer>();
		// Stores the position of candidates that need to be removed,when a tie occurs.
		int min_pos = 0;
		int tie_pos = 0;
		int tieCounter = 0;

		// Searches the position of the candidate with fewer votes
		for (int i = 1; i < ranks_of_candidates.size(); i++) {
			if (ranks_of_candidates.get(i) < ranks_of_candidates.get(min_pos)) {
				min_pos = i;

			} else if (ranks_of_candidates.get(i).equals(ranks_of_candidates.get(min_pos))) {

				tie_pos = min_pos;
				tieCounter++;
			}

		}
		/*
		 * When a tie of ranks occurs between certain lowest candidates, they are
		 * isolated for posterior management
		 */
		if ((tie_pos == min_pos) && (tieCounter != ranks_of_candidates.size() - 1)) {

			for (int r = 0; r < ranks_of_candidates.size(); r++) {

				if (!ranks_of_candidates.get(min_pos).equals(ranks_of_candidates.get(r))) {
					to_remove.add(r);

				}
			}

			for (int k = to_remove.size() - 1; k >= 0; k--) {
				board.remove(to_remove.get(k));
				survivors.remove(to_remove.get(k));
			}

			return null;

			/*
			 * If a tie of ranks occurs for all candidates, the candidate w/ largest ID is
			 * removed.
			 */
		} else if ((tie_pos == min_pos) && (tieCounter == ranks_of_candidates.size() - 1)) {
			// System.err.println("Eliminated by rank");
			return tie_Breaker(survivors.get(0));
		}

		rip_votes = rank_Counter(survivors.get(min_pos), board.get(min_pos), 1); // The loser votes are stored
		return survivors.get(min_pos); // Returns Election loser for elimination
	}

	/**
	 * 
	 * This method breaks a tie between candidates w/ by using candidates ids.
	 * 
	 * @param loser (Candidate for tie break)
	 * @return loser (Election loser for elimination) (Candidate)
	 */

	public Candidate tie_Breaker(Candidate loser) {
		rip_votes = rank_Counter(loser, board.get(0), 1);
		for (int i = 1; i < survivors.size(); i++) {
			if (survivors.get(i).getId() > loser.getId()) {
				loser = survivors.get(i);
				rip_votes = rank_Counter(loser, board.get(i), 1);
			}
		}
		return loser;
	}

	/**
	 * 
	 * BONUS METHOD #1 This method takes a lambda function that receives a Candidate
	 * as a parameter and returns a String. The objective of the method is that it
	 * iterates over the list of candidates and prints the output of the lambda
	 * function for each candidate.
	 * 
	 * @param func (lamda function) lamda=election#.printCandidates((c) -> c.getId()
	 *             + " " + c.getName() + "" + ((c.isActive()) ? "" : "+"));
	 * 
	 */
	public void printCandidates(Function<Candidate, String> func) {
		for (Candidate c : candidates) {
			System.out.println(func.apply(c));
		}

	}

	/**
	 * 
	 * BONUS METHOD #2 This method takes a lambda function that receives a ballot
	 * and returns a Boolean. This method iterated through all the valid ballots and
	 * return how many ballots comply with a given condition.
	 * 
	 * @param func (lamda function) lambas: countBallots((b) ->
	 *             b.getRankByCandidate(1) == 2); countBallots((b) ->
	 *             b.getRankByCandidate(2) == 2); countBallots((b) ->
	 *             b.eliminate(3));
	 */
	public int countBallots(Function<Ballot, Boolean> func) {
		int counter = 0;
		for (Ballot b : board.get(0)) {
			if (func.apply(b)) {
				counter++;
			}
		}
		System.out.println(Integer.toString(counter));
		return counter;

	}

	/**
	 * This helper method prints all the general information about the election as
	 * well as a table with the vote distribution. Meant for helping in the
	 * debugging process.
	 */
	public void printBallotDistribution() {
		System.out.println("Total ballots:" + getTotalBallots());
		System.out.println("Total blank ballots:" + getTotalBlankBallots());
		System.out.println("Total invalid ballots:" + getTotalInvalidBallots());
		System.out.println("Total valid ballots:" + getTotalValidBallots());
		System.out.println(getEliminatedCandidates());
		int i = 0;
		for (Candidate c : candidates) {

			if (c.isActive()) {
				System.out.print(c.getName().substring(0, c.getName().indexOf(" ")) + "\t");

				for (int j = 0; j < board.get(i).size(); j++) {
					if (c.isActive()) {
						Ballot b = board.get(i).get(j);
						int rank = b.getRankByCandidate(c.getId());
						String tableline = "| " + ((rank != -1) ? rank : " ") + " ";
						System.out.print(tableline);
					}
				}
				System.out.println("|");
				i++;
			}

		}
	}

	/**
	 * This method writes information related to the current election process in a
	 * separate generated <winner>-<winnerVotes>.txt file located in output. It
	 * includes number of total ballots, blank ballots, invalid ballots, eliminated
	 * candidates w/ #1's, and the election winner.
	 */
	public void outputTxtWriter() {
		String name = this.getWinner().replace(" ", "_").toLowerCase();

		try (BufferedWriter writer = new BufferedWriter(
				new FileWriter("outputFiles/" + name + getWinnerVotes() + ".txt"))) {
			writer.write("Number of ballots: " + getTotalBallots() + "\n");
			writer.write("Number of blank ballots: " + getTotalBlankBallots() + "\n");
			writer.write("Number of invalid ballots: " + getTotalInvalidBallots() + "\n");

			for (int i = 0; i < getEliminatedCandidates().size(); i++) {
				String n = getEliminatedCandidates().get(i);
				String[] eliminated_data = n.split("-");
				writer.write("Round " + (i + 1) + ": " + eliminated_data[0] + " was eliminated with "
						+ eliminated_data[1] + " #1's\n");

			}
			writer.write("Winner: " + getWinner() + " wins with " + getWinnerVotes() + " #1's\n");
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main method that starts the project by creating an Election() Object.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Election election1 = new Election();
		Election election2 = new Election("candidates.csv", "ballots.csv");
		Election election3 = new Election("candidates.csv", "ballots2.csv");
		Election election5 = new Election("candidates2.csv", "ballots4.csv");
		Election election6 = new Election("candidates.csv", "ballots5.csv");

	}
}
