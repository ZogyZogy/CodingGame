package queens;

import java.util.List;

public class AlgoQueens {

	public static void main(String[] args) {
		AlgoQueens algo = new AlgoQueens();
		ChessBoard chessBoard = new ChessBoard(8);
		algo.tryNextQueen(chessBoard);
		System.out.println(chessBoard.getResult());	
	}
	
	public boolean tryNextQueen(ChessBoard chessBoard) {
		// We have a chess board with x queens already set
		// First we get all the available squares for the next queen (on the next line);
		List<Queen> availableNextQueens = chessBoard.getNextPossibleQueens();
		// For each, we add a queen on the square, check if the board is complete, and if not try to put the next queen (recursion)
		for (Queen queen : availableNextQueens)	{
			chessBoard.addQueen(queen);
			// If the chess board is complete, we return true;
			if (chessBoard.isComplete()) {
				return true;
			}
			if(tryNextQueen(chessBoard)) {
				// If the next queen is ok, we return true to end the recursion
				return true;
			} else {
				// If the next queen is not possible, we remove the added queen
				chessBoard.removeLastQueen();
			}
		}
		//  If no square is ok, we return false
		return false;
	}
	
	
	
}
