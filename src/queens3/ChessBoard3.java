package queens3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import queens2.Queen2;

public class ChessBoard3 {

	private int boardSize;
	
	/** Table of the squares available to put a new queen in. */
	private boolean[][] freeSquares;
	
	private Deque<boolean[][]> freeSquaresList = new LinkedList<>(); // Deque because we want a LIFO queue
	private Deque<Queen3> queenList = new LinkedList<>(); // Deque because we want a LIFO queue
	
	public ChessBoard3(int chessSize) {
		this.boardSize = chessSize;
		initFreeSquares();		
	}
	
	public List<Queen3> getNextPossibleQueens() {
		List<Queen3> possibleQueens = new ArrayList<>(boardSize);
		int y = queenList.size();
		for (int x = 0 ; x < boardSize ; x++) {
			if (freeSquares[x][y] == true) {
				possibleQueens.add(new Queen3(new Position3(x, y)));
			}
		}
		return possibleQueens;
	}
	
	public void addQueen(Queen3 queen) {
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
	
	// We use a duplicate method and not an override of clone since use of clone is discouraged by the Java team (something to do with bad first implementation and unability to fix it because of the need for retro-compatibility ...)
	public ChessBoard3 duplicate() {
		ChessBoard3 duplicateChessBoard = new ChessBoard3(boardSize);
		Iterator<Queen3> iterator = queenList.descendingIterator(); // LIFO so we need descending iterator (algorithm is optimized for queens added in ascending vertical position). 
		while (iterator.hasNext()) {
			duplicateChessBoard.addQueen(iterator.next());
		}
		return duplicateChessBoard;
	}
	
	private void removeNewForbiddenSquares(Queen3 queen) {
		// Note : impressively bad performance using this for loop : around twice as long
		for(Consumer<Position3> movePossibility : queen.movePossibilities) {
			removeSquares(queen.getPosition(), movePossibility);
		}
	}
	
	private void removeSquares(Position3 queenPosition, Consumer<Position3> movePosition) {
		Position3 newPosition = new Position3(queenPosition); // We need to use a different position than that of the queen
		movePosition.accept(newPosition); // We don't care for the current position
		for(Position3 p = newPosition ; p.isWithinBoard(boardSize) ; movePosition.accept(p)) {
			freeSquares[p.x][p.y] = false;
		}
	}

	private void recalculateFreeSquares() {
		freeSquares = freeSquaresList.removeFirst();
//		initFreeSquares();
//		Iterator<Queen3> iterator = queenList.descendingIterator(); // LIFO so we need descending iterator (algorithm is optimized for queens added in ascending vertical position). 
//		while (iterator.hasNext()) {
//			removeNewForbiddenSquares(iterator.next());
//		}

	}

	private void initFreeSquares() {
		freeSquares = new boolean[boardSize][boardSize];
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
