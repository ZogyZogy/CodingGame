package queens2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class ChessBoard2 {

	private int boardSize;
	
	/** Table of the squares available to put a new queen in. */
	private boolean[][] freeSquares;
	
	private Deque<boolean[][]> freeSquaresList = new LinkedList<>(); // Deque because we want a LIFO queue
	private Deque<Queen2> queenList = new LinkedList<>(); // Deque because we want a LIFO queue
	
	public ChessBoard2(int chessSize) {
		this.boardSize = chessSize;
		freeSquares = new boolean[chessSize][chessSize];
		initFreeSquares();		
	}
	
	public List<Queen2> getNextPossibleQueens() {
		List<Queen2> possibleQueens = new ArrayList<>(boardSize);
		int y = queenList.size();
		for (int x = 0 ; x < boardSize ; x++) {
			if (freeSquares[x][y] == true) {
				possibleQueens.add(new Queen2(new Position2(x, y)));
			}
		}
		return possibleQueens;
	}
	
	public void addQueen(Queen2 queen) {
		freeSquaresList.addFirst(deepCopy());
		queenList.addFirst(queen); // LIFO
		removeNewForbiddenSquares(queen);
	}
	
	public void removeLastQueen() {
		queenList.removeFirst();
		recalculateFreeSquares(); // Not efficient at all, all the free squares are recalculated when one queen is removed. 
		// Might be better to save each view of freeSquares.
	}
	
	public boolean isComplete() {
		return queenList.size() == boardSize;
	}

	public boolean isNotEmpty() {
		return queenList.size() != 0;
	}
	
	public String getResult() {
		return queenList.toString();
	}
	
	public ChessBoard2 duplicate() {
		ChessBoard2 duplicateChessBoard = new ChessBoard2(boardSize);
		Iterator<Queen2> iterator = queenList.descendingIterator(); // LIFO so we need descending iterator (algorithm is optimized for queens added in ascending vertical position). 
		while (iterator.hasNext()) {
			duplicateChessBoard.addQueen(iterator.next());
		}
		return duplicateChessBoard;
	}
	
	private void removeNewForbiddenSquares(Queen2 queen) {
		Position2 position = queen.getPosition();
		// We don't care for removing horizontal squares
		removeSquares(position, (p) -> {p.y++;});  // remove vertical
		removeSquares(position, (p) -> {p.y++; p.x--;}); // remove anti-diagonal 
		removeSquares(position, (p) -> {p.y++; p.x++;}); // remove diagonal 
	}
	
	private void removeSquares(Position2 queenPosition, Consumer<Position2> movePosition) {
		Position2 newPosition = new Position2(queenPosition); // We need to use a different position than that of the queen
		movePosition.accept(newPosition); // We don't care for the current position
		for(Position2 p = newPosition ; p.isWithinBoard(boardSize) ; movePosition.accept(p)) {
			freeSquares[p.x][p.y] = false;
		}
	}

	private void recalculateFreeSquares() {
		freeSquares = freeSquaresList.removeFirst();
//		initFreeSquares();
//		Iterator<Queen2> iterator = queenList.descendingIterator(); // LIFO so we need descending iterator (algorithm is optimized for queens added in ascending vertical position). 
//		while (iterator.hasNext()) {
//			removeNewForbiddenSquares(iterator.next());
//		}
	}

	private void initFreeSquares() {
		for (int x = 0 ; x < boardSize ; x++) {
			for (int y = 0 ; y < boardSize ; y++) {
				freeSquares[x][y] = true;
			}
		}
	}

	public static boolean[][] deepCopy(boolean[][] original) {
	    final boolean[][] result = new boolean[original.length][];
	    for (int i = 0; i < original.length; i++) {
	        result[i] = Arrays.copyOf(original[i], original[i].length);
	    }
	    return result;
	}
	
	private boolean[][] deepCopy() {
		boolean[][] copy = new boolean[boardSize][boardSize];
		for (int x = 0 ; x < boardSize ; x++) {
			for (int y = 0 ; y < boardSize ; y++) {
				copy[x][y] = freeSquares[x][y];
			}
		}
		return copy;
	}
	
	
}
