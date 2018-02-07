package queens3;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlgoQueens3 {

	private static List<ChessBoard3> solvedChessBoards = new ArrayList<>();
	private static int count; 
	
	public static void main(String[] args) {
		AlgoQueens3 algo = new AlgoQueens3();
		for (int size = 8 ; size <= 13 ; size ++) {
			solvedChessBoards.clear();
			startCalculate(size, algo);
			System.out.println("Size of the chess board : " + size + " - Number of results : " + solvedChessBoards.size());	
			System.out.println("Count " + count);	
		} 
		String s = "";
		
		//System.out.println(" Last Result : \n" + solvedChessBoards.get(solvedChessBoards.size()-1).getResult());	
	}
	
	private static void startCalculate(int size, AlgoQueens3 algo) {
		LocalDateTime before = LocalDateTime.now();
		
		ChessBoard3 chessBoard = new ChessBoard3(size);
		algo.tryNextQueen(chessBoard);
		
		LocalDateTime after  = LocalDateTime.now();
		Duration duration = Duration.between(before, after);
		System.out.println("Duree du calcul : " + duration.getSeconds() + "s " + duration.getNano()/1_000_000 + "ms");
	}

	public void tryNextQueen(ChessBoard3 chessBoard) {
		// We have a chess board with x queens already set
		// First we get all the available squares for the next queen (on the next line);
		List<Queen3> availableNextQueens = chessBoard.getNextPossibleQueens();
		count += availableNextQueens.size();
		// For each, we add a queen on the square, check if the board is complete, and if not try to put the next queen (recursion)
		for (Queen3 queen : availableNextQueens)	{
			chessBoard.addQueen(queen);
			// If the chess board is complete, we return true;
			if (chessBoard.isComplete()) {
				solvedChessBoards.add(chessBoard.duplicate());
				chessBoard.removeLastQueen(); 
				return;
			}
			tryNextQueen(chessBoard);
			chessBoard.removeLastQueen();
		}
	}
	
}
