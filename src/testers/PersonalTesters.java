package testers;

import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

//import data_structures.ArrayList;
//import interfaces.List;
//import main.Ballot;
//import main.Candidate;
import main.Election;

public class PersonalTesters {

	@Nested
	@DisplayName("Personal Tests")
	public class StudentTests {

		@Nested
		@DisplayName("Election Tests")
		public class ElectionTests {
			
			@Test
			@DisplayName("Creating Election 3") 
			public void testConstructor3() {
				Election election = new Election("candidates.csv", "ballots2.csv");
				
				assertAll(
						() -> assertTrue(election.getWinner().equals("Pucho Avellanet"), "Didn't get Correct winner."),
						() -> assertTrue(election.getTotalBallots() == 15,"Didn't count correct amount of ballots"),
						() -> assertTrue(election.getTotalBlankBallots() == 2, "Didn't count correct amount of blank ballots"),
						() -> assertTrue(election.getTotalInvalidBallots() == 3, "Didn't count correct amount of invalid ballots"),
						() -> assertTrue(election.getTotalBlankBallots() == 2, "Didn't count correct amount of blank ballots"),
						() -> assertTrue(election.getTotalValidBallots() == 10, "Didn't count correct amount of valid ballots"),
						() -> assertTrue(election.getEliminatedCandidates().get(0).equals("Pardeep Kumar-1"), "Didn't return correct eliminated candidate and/or count for this position"),
						() -> assertTrue(election.getEliminatedCandidates().get(1).equals("Juan Lopez-1"), "Didn't return correct eliminated candidate and/or count for this position"),
						() -> assertTrue(election.getEliminatedCandidates().get(2).equals("Lola Mento-3"), "Didn't return correct eliminated candidate and/or count for this position")
						);
				
			}	
			
			@Test
			@DisplayName("Creating Election 5") //Empate entre 2 candidatos
			public void testConstructor5() {
				Election election = new Election("candidates2.csv", "ballots4.csv");
				
				assertAll(
						() -> assertTrue(election.getWinner().equals("Juan Lopez"), "Didn't get Correct winner."),
						() -> assertTrue(election.getTotalBallots() == 4,"Didn't count correct amount of ballots"),
						() -> assertTrue(election.getTotalBlankBallots() == 0, "Didn't count correct amount of blank ballots"),
						() -> assertTrue(election.getTotalInvalidBallots() == 0, "Didn't count correct amount of invalid ballots"),
						() -> assertTrue(election.getTotalBlankBallots() == 0, "Didn't count correct amount of blank ballots"),
						() -> assertTrue(election.getTotalValidBallots() == 4, "Didn't count correct amount of valid ballots"),
						() -> assertTrue(election.getEliminatedCandidates().get(0).equals("Pepe Perez-0"), "Didn't return correct eliminated candidate and/or count for this position"),
						() -> assertTrue(election.getEliminatedCandidates().get(1).equals("Lola Mento-2"), "Didn't return correct eliminated candidate and/or count for this position")
						);
				
	
			}
			
			
			@Test
			@DisplayName("Creating Election 6") //Empate entre 3 o mas candidatos (Se intentara 4 personas)
			public void testConstructor6() {
				Election election = new Election("candidates.csv", "ballots5.csv");
				
				assertAll(
						() -> assertTrue(election.getWinner().equals("Pepe Perez"), "Didn't get Correct winner."),
						() -> assertTrue(election.getTotalBallots() == 4,"Didn't count correct amount of ballots"),
						() -> assertTrue(election.getTotalBlankBallots() == 0, "Didn't count correct amount of blank ballots"),
						() -> assertTrue(election.getTotalInvalidBallots() == 0, "Didn't count correct amount of invalid ballots"),
						() -> assertTrue(election.getTotalBlankBallots() == 0, "Didn't count correct amount of blank ballots"),
						() -> assertTrue(election.getTotalValidBallots() == 4, "Didn't count correct amount of valid ballots"),
						() -> assertTrue(election.getEliminatedCandidates().get(0).equals("Lola Mento-0"), "Didn't return correct eliminated candidate and/or count for this position"),
						() -> assertTrue(election.getEliminatedCandidates().get(1).equals("Pucho Avellanet-1"), "Didn't return correct eliminated candidate and/or count for this position"),
						() -> assertTrue(election.getEliminatedCandidates().get(2).equals("Juan Lopez-1"), "Didn't return correct eliminated candidate and/or count for this position"),
						() -> assertTrue(election.getEliminatedCandidates().get(3).equals("Pardeep Kumar-2"), "Didn't return correct eliminated candidate and/or count for this position")
						);
				
	
			}
			
			
			
		}
	}

}
