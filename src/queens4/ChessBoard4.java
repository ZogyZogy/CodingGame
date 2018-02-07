package queens4;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import queens2.Position2;

public class ChessBoard4 {

	private int boardSize;
	
	/** Table of the squares available to put a new bishop in. */
	private boolean[][] freeSquares;
	
	private Deque<Bishop4> bishopList = new LinkedList<>(); // Deque because we want a LIFO queue
	
	public ChessBoard4(int chessSize) {
		this.boardSize = chessSize;
		freeSquares = new boolean[chessSize][chessSize];
		initFreeSquares();		
	}
	
	public List<Bishop4> getNextPossibleBishops() {
		List<Bishop4> possibleBishops = new ArrayList<>();
		for (int x = 0 ; x < boardSize ; x++) {
			for (int y = 0 ; y < boardSize ; y++) {
				if (freeSquares[x][y] == true && (x+y)%2 != 0) {
					possibleBishops.add(new Bishop4(new Position4(x, y)));
				}
			}
		}
		return possibleBishops;
	}
	
	public void addBishop(Bishop4 bishop) {
		bishopList.addFirst(bishop); // LIFO
		removeNewForbiddenSquares(bishop);
	}
	
	public void removeLastBishop() {
		bishopList.removeFirst();
		recalculateFreeSquares(); // Not efficient at all, all the free squares are recalculated when one bishop is removed. 
		// Might be better to save each view of freeSquares.
	}
	
	public boolean isComplete() {
		return bishopList.size() == boardSize - 1;
	}

	public boolean isNotEmpty() {
		return bishopList.size() != 0;
	}
	
	public String getResult() {
		return bishopList.toString();
	}
	
	// We use a duplicate method and not an override of clone since use of clone is discouraged by the Java team (something to do with bad first implementation and unability to fix it because of the need for retro-compatibility ...)
	public ChessBoard4 duplicate() {
		ChessBoard4 duplicateChessBoard = new ChessBoard4(boardSize);
		Iterator<Bishop4> iterator = bishopList.descendingIterator(); // LIFO so we need descending iterator (algorithm is optimized for bishops added in ascending vertical position). 
		while (iterator.hasNext()) {
			duplicateChessBoard.addBishop(iterator.next());
		}
		return duplicateChessBoard;
	}
	
	private void removeNewForbiddenSquares(Bishop4 bishop) {
		Position4 position = bishop.getPosition();
		// We don't care for removing horizontal squares
		removeSquares(position, (p) -> {p.y--; p.x--;}); // remove diagonal 
		removeSquares(position, (p) -> {p.y--; p.x++;}); // remove anti-diagonal 
		removeSquares(position, (p) -> {p.y++; p.x--;}); // remove anti-diagonal 
		removeSquares(position, (p) -> {p.y++; p.x++;}); // remove diagonal 
	}
	
	private void removeSquares(Position4 bishopPosition, Consumer<Position4> movePosition) {
		Position4 newPosition = new Position4(bishopPosition); // We need to use a different position than that of the bishop
		for(Position4 p = newPosition ; p.isWithinBoard(boardSize) ; movePosition.accept(p)) {
			freeSquares[p.x][p.y] = false;
		}
	}

	private void recalculateFreeSquares() {
		initFreeSquares();
		Iterator<Bishop4> iterator = bishopList.descendingIterator(); // LIFO so we need descending iterator (algorithm is optimized for bishops added in ascending vertical position). 
		while (iterator.hasNext()) {
			removeNewForbiddenSquares(iterator.next());
		}
	}

	private void initFreeSquares() {
		for (int x = 0 ; x < boardSize ; x++) {
			for (int y = 0 ; y < boardSize ; y++) {
				freeSquares[x][y] = true;
			}
		}
	}

	
}
