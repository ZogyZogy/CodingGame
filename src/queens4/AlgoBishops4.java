package queens4;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlgoBishops4 {

	private static List<ChessBoard4> solvedChessBoards = new ArrayList<>();
	private static int count; 
	
	public static void main(String[] args) {
		AlgoBishops4 algo = new AlgoBishops4();
		for (int size = 4 ; size <= 10 ; size +=2) {
			solvedChessBoards.clear();
			startCalculate(size, algo);
			System.out.println("Size of the chess board : " + size + " - Number of results : " + solvedChessBoards.size());	
			System.out.println("Count " + count);	
		}
		//System.out.println(" Last Result : \n" + solvedChessBoards.get(solvedChessBoards.size()-1).getResult());	
	}
	
	private static void startCalculate(int size, AlgoBishops4 algo) {
		LocalDateTime before = LocalDateTime.now();
		
		ChessBoard4 chessBoard = new ChessBoard4(size);
		algo.tryNextBishop(chessBoard);
		
		LocalDateTime after  = LocalDateTime.now();
		Duration duration = Duration.between(before, after);
		System.out.println("Duree du calcul : " + duration.getSeconds() + "s " + duration.getNano()/1_000_000 + "ms");
	}

	public void tryNextBishop(ChessBoard4 chessBoard) {
		// We have a chess board with x bishops already set
		// First we get all the available squares for the next bishop ;
		List<Bishop4> availableNextBishops = chessBoard.getNextPossibleBishops();
		count += availableNextBishops.size();
		// For each, we add a queen on the square, check if the board is complete, and if not try to put the next queen (recursion)
		for (Bishop4 bishop : availableNextBishops)	{
			chessBoard.addBishop(bishop);
			// If the chess board is complete, we return true;
			if (chessBoard.isComplete()) {
				solvedChessBoards.add(chessBoard.duplicate());
				chessBoard.removeLastBishop(); 
				return;
			}
			tryNextBishop(chessBoard);
			chessBoard.removeLastBishop();
		}
	}
	
}
