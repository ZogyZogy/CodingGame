package queens;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChessBoard {

	private int chessSize;
	
	private boolean[][] freeSquares;
	
	private Deque<Queen> queenList; // Deque because we want LIFO
	
	public ChessBoard(int chessSize) {
		this.chessSize = chessSize;
		queenList = new LinkedList<>();
		freeSquares = new boolean[chessSize][chessSize];
		initFreeSquares();		
	}
	
	public List<Queen> getNextPossibleQueens() {
		List<Queen> possibleQueens = new ArrayList<>(chessSize);
		int verticalPosition = queenList.size();
		for (int horizontalPosition = 0 ; horizontalPosition < chessSize ; horizontalPosition++) {
			if (freeSquares[horizontalPosition][verticalPosition] == true) {
				possibleQueens.add(new Queen(horizontalPosition, verticalPosition));
			}
		}
		return possibleQueens;
	}
	
	public void addQueen(Queen queen) {
		queenList.addFirst(queen); // LIFO
		removeNewForbiddenSquares(queen);
	}
	
	public void removeLastQueen() {
		queenList.removeFirst();
		recalculateFreeSquares(); 
		// Not efficient at all, all the free squares are recalculated when one queen is removed. Might be better to save each view of freeSquares.
	}
	
	public boolean isComplete() {
		return queenList.size() == chessSize;
	}

	public boolean isNotEmpty() {
		return queenList.size() != 0;
	}
	
	public String getResult() {
		return queenList.toString();
	}
	
	public ChessBoard duplicate() {
		ChessBoard duplicateChessBoard = new ChessBoard(chessSize);
		Iterator<Queen> iterator = queenList.descendingIterator(); // LIFO so we need descending iterator
		while (iterator.hasNext()) {
			duplicateChessBoard.addQueen(iterator.next());
		}
		return duplicateChessBoard;
	}
	
	private void removeNewForbiddenSquares(Queen queen) {
		int verticalPosition = queen.getVerticalPosition();
		int horizontalPosition = queen.getHorizontalPosition();
		removeVerticalSquares(horizontalPosition, verticalPosition);
		removeHorizontalSquares(horizontalPosition, verticalPosition);
		removeDiagonalSquares(horizontalPosition, verticalPosition);
	}

	private void removeDiagonalSquares(int horizontalPosition, int verticalPosition) {
		for (int x = horizontalPosition, y = verticalPosition ; y < chessSize && x >= 0; y++, x--) {
			freeSquares[x][y] = false;
		}
		for (int x = horizontalPosition, y = verticalPosition ; y < chessSize && x < chessSize; y++, x++) {
			freeSquares[x][y] = false;
		}
		
	}

	private void removeHorizontalSquares(int horizontalPosition, int verticalPosition) {
		for (int x = 0 ; x < chessSize ; x++) {
			freeSquares[x][verticalPosition] = false;
		}
		
	}

	private void removeVerticalSquares(int horizontalPosition, int verticalPosition) {
		for (int y = verticalPosition + 1 ; y < chessSize ; y++) {
			freeSquares[horizontalPosition][y] = false;
		}
	}

	private void recalculateFreeSquares() {
		initFreeSquares();
		Iterator<Queen> iterator = queenList.descendingIterator(); // LIFO so we need descending iterator
		while (iterator.hasNext()) {
			removeNewForbiddenSquares(iterator.next());
		}
	}

	private void initFreeSquares() {
		for (int horizontalPosition = 0 ; horizontalPosition < chessSize ; horizontalPosition++) {
			for (int verticalPosition = 0 ; verticalPosition < chessSize ; verticalPosition++) {
				freeSquares[horizontalPosition][verticalPosition] = true;
			}
		}
	}

	
	
	
}
