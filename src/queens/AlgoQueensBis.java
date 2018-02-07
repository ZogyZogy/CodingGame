package queens;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlgoQueensBis {

	private static List<ChessBoard> solvedChessBoards = new ArrayList<>();
	
	public static void main(String[] args) {
		AlgoQueensBis algo = new AlgoQueensBis();
		startCalculate(8, algo);
		System.out.println("Number of results : " + solvedChessBoards.size());	
		System.out.println(" Last Result : \n" + solvedChessBoards.get(solvedChessBoards.size()-1).getResult());	
	}
	
	private static void startCalculate(int size, AlgoQueensBis algo) {
		LocalDateTime before = LocalDateTime.now();
		
		ChessBoard chessBoard = new ChessBoard(size);
		algo.tryNextQueen(chessBoard);
		
		LocalDateTime after  = LocalDateTime.now();
		Duration duration = Duration.between(before, after);
		System.out.println("Duree du calcul : " + duration.getSeconds() + "s " + duration.getNano()/1_000_000 + "ms");
	}

	public void tryNextQueen(ChessBoard chessBoard) {
		// We have a chess board with x queens already set
		// First we get all the available squares for the next queen (on the next line);
		List<Queen> availableNextQueens = chessBoard.getNextPossibleQueens();
		// For each, we add a queen on the square, check if the board is complete, and if not try to put the next queen (recursion)
		for (Queen queen : availableNextQueens)	{
			chessBoard.addQueen(queen);
			// If the chess board is complete, we return true;
			if (chessBoard.isComplete()) {
				solvedChessBoards.add(chessBoard.duplicate());
				chessBoard.removeLastQueen(); // Real tricky - we need to remove the queen from this method
				break; // Tricky - not a return, we need to remove the queen from the previous method
			}
			tryNextQueen(chessBoard);
		}
		// If no square is ok, we remove the last queen and return false
		if (chessBoard.isNotEmpty()) { // Tricky - unless we have already tested all the possibilities
			chessBoard.removeLastQueen();
		}
	}
	
	
	
}
