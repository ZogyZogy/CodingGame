package algos.skyscrapers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import algos.skyscrapers.SkyScrapersGenerator.EndException;
import algos.skyscrapers.SkyScrapersGenerator.Position;
import algos.skyscrapers.SkyscrapersCheckIfMultipleSolutions.Table;

/** 
 * Optimized version : with already placed values and isComplete determined 
 * when position is set instead of when each new possible value is tested in Line class 
 * */

public class SkyscrapersRemoveClues {

	static Integer SIZE = 7;

	static boolean solvePuzzle (int size, int[] clues) {
		SIZE = size;
		return new SkyscrapersRemoveClues().removeMaxClues(clues);
	}

	boolean isSolutionFound = false;

	Table table;


	private boolean removeMaxClues(int[] clues) {
		try {
			//solve(clues);
			while (solve(clues)) {
				//System.out.println(Arrays.toString(clues));
				int cluePosition = (int) (Math.random() * (4*SIZE + 1));
				clues[cluePosition] = 0;
			}
			return true;
		} catch (EndException e) {
			System.out.println(e);
			System.out.println(Arrays.toString(clues));
			//return clues;
			return false;
		}
	}

	/**
	 * High-level method which initializes the table object and start the recursion.  
	 * @param clues
	 * @return the solution
	 * @throws EndException 
	 */
	
	private boolean solve(int[] clues) throws EndException {
		table = new Table(clues);
		Optional<Position> initPosition = table.getNextPosition();
		iter(initPosition.get());
		if (isSolutionFound) {
			return true;
		}
		throw new RuntimeException("No solution found");
	}

	//	if (!initPosition.isPresent() || iter(initPosition.get())) {
	//			return true;
	//		} else {
	//			if (isSolutionFound) {
	//				return true;
	//			}
	//			throw new RuntimeException("No solution found");
	//		}

	/**
	 * Core of the Algorithm. <br>
	 * For a given position, we get all the possible values compatible with the rules. 
	 * Then we take the first one and call the same method for the next position.<br>
	 * If the method returns true, we return true to end the recursion.
	 * If there are no next positions, it means we have a solution and thus return true 
	 * (for the last position, the first value is the right one).
	 * If the method returns false, it means the value is wrong : we reset the value 
	 * (and the next position) and try with the next possible value.
	 * If there are no possible values or no possible value returns true, we return false 
	 * as it means we're in a dead-end  and a former value is wrong. 
	 *  
	 * @param position
	 * @return true : if there is no next position or possible value returns true.<br>
	 * 			false : if no possible value returns true
	 * @throws EndException 
	 */

	private void positionNotFound(Position position) throws EndException {
		System.out.println(table);
		if (!isSolutionFound) {
			isSolutionFound = true;
			table.resetValue(position);
		} else {
			throw new EndException("Plusieurs Solutions");
		}
	}
	
	private boolean iter(Position position) throws EndException {
		Set<Integer> possibleValues = table.getPossibleValuesForPosition(position);
		for (Integer possibleValue : possibleValues) {
			table.setValueForPosition(position, possibleValue);
			Optional<Position> nextPosition = table.getNextPosition();
			if (!nextPosition.isPresent()) {
				positionNotFound(position);
				continue;
			}
			if(iter(nextPosition.get())) {
				return true;
			} else {
				table.resetPosition(nextPosition.get());
				table.resetValue(position);
			}
		}
		return false;
	}

	/**
	 * Custom exception thrown when iteration is over
	 */
	@SuppressWarnings("serial")
	public static class EndException extends Exception {
		public EndException(String message) {
			super(message);
		}
	}

	/**
	 * Class representing the 2d position (pretty self-explanatory)
	 */
	public static class Position {
		// Integers so that Position is immutable (always a good practice)
		Integer x;
		Integer y;

		Position(Integer x, Integer y) {
			this.x = x;
			this.y = y;
		}

		public Integer getX() {
			return x;
		}

		public Integer getY() {
			return y;
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}

		@Override
		public boolean equals(Object object) {
			if (!(object instanceof Position)) {
				return false;
			}
			Position p2 = (Position) object;
			return x == p2.x && y == p2.y;
		}

	}

	/**
	 * Table is an abstraction of the table, and of the lines it contains. 
	 * This is where we can initialize the table with the given clues, and where we can get the relevant positions
	 * and values for the iteration process. 
	 */
	public static class Table {

		int[][] table = new int[SIZE][SIZE];
		Line[] horizontalLines = new Line[SIZE];
		Line[] verticalLines = new Line[SIZE];
		Deque<Position> positionsQueue = new LinkedList<>();
		int[] clues;

		Table(int[] clues) {
			this.clues = clues;
			initLinesWithClues(clues);
			initPositionsIterationWithClues(clues);
		}

		private void initPositionsIterationWithClues(int[] clues) {
			for (int clueValue = SIZE-1 ; clueValue >= 0 ; clueValue--) {
				for (int i = 0 ; i < 4*SIZE ; i++) {
					if (clues[i] == clueValue) {
						addAllPositionsOfLine(i);
					}
				}
			}
		}

		private void addAllPositionsOfLine(int i) {
			if (i < SIZE) { // First Line
				for (int y = 0 ; y < SIZE ; y++) {
					addIfNotPresent(new Position(i, y));
				}
			} else if (i < 2*SIZE) {
				for (int x = 0 ; x < SIZE ; x++) {
					addIfNotPresent(new Position(SIZE-1-x, i-SIZE));
				}

			} else if (i < 3*SIZE) {
				for (int y = 0 ; y < SIZE ; y++) {
					addIfNotPresent(new Position(3*SIZE-1-i, SIZE-1-y));
				}
			} else {
				for (int x = 0 ; x < SIZE ; x++) {
					addIfNotPresent(new Position(x, 4*SIZE-1-i));
				}
			}
		}

		private void addIfNotPresent(Position position) {
			if (!positionsQueue.contains(position)) {
				positionsQueue.add(position);
			}	
		}

		/** Initialization of the horizontal and vertical lines of the table, and resolve 'init' clues. 
		 * @param clues
		 */
		private void initLinesWithClues(int[] clues) {
			IntStream.range(0, SIZE).forEach(x -> verticalLines[x] = new Line(clues[x], clues[3*SIZE-1-x]));
			IntStream.range(0, SIZE).forEach(y -> horizontalLines[y] = new Line(clues[4*SIZE-1-y], clues[SIZE+y]));
			IntStream.range(0, SIZE).forEach(i -> verticalLines[i].resolveInitClues());
			synchTableWithVerticalLines();
			IntStream.range(0, SIZE).forEach(i -> horizontalLines[i].resolveInitClues());
			synchTableWithHorizontalLines();
		}

		/**
		 * A bit ugly, the problem lies in the fact that the arrays in Line class are not backed by the 2d array in Table class.
		 */
		private void synchTableWithHorizontalLines() {
			for (int x = 0 ; x < SIZE ; x++) {
				for (int y = 0 ; y < SIZE ; y++) {
					setValueForPosition(new Position(x,y), horizontalLines[y].line[x]);
				}
			}		
		}

		private void synchTableWithVerticalLines() {
			for (int x = 0 ; x < SIZE ; x++) {
				for (int y = 0 ; y < SIZE ; y++) {
					setValueForPosition(new Position(x,y), verticalLines[x].line[y]);
				}
			}
		}

		public void resetPosition(Position position) {
			positionsQueue.addFirst(position);
		}

		public void resetValue(Position position) {
			int value = getValueForPosition(position);
			setValueForPosition(position, 0);
			int x = position.getX();
			int y = position.getY();
			verticalLines[x].alreadyPlacedValues.remove(value);
			horizontalLines[y].alreadyPlacedValues.remove(value);
			verticalLines[x].isComplete = false;
			horizontalLines[y].isComplete = false;
		}

		/**
		 * Getter for the position (x, y). Helps because x and y are reversed, and we shouldn't care about that 
		 * (also prevents from making mistakes as reversing x and y is pretty error-prone).
		 * @param x
		 * @param y
		 * @return
		 */
		private Integer getValueForPosition(int x, int y) {
			return table[y][x]; 	// y and x are inverted so that table[0] gives the first row (simple transposition) 
		}

		private Integer getValueForPosition(Position p) {
			return getValueForPosition(p.getX(), p.getY());  
		}

		/**
		 * Updates value in the table, and updates the relevant lines to ensure consistency of the model.
		 * Like the getter, hides the transposition from the user (it only appear once in the getter and once in the setter).
		 * @param position : position in the table
		 * @param value : value to update with
		 */
		public void setValueForPosition(Position position, int value) {
			int x = position.getX();
			int y = position.getY();
			table[y][x] = value;  	// y and x are inverted so that table[0] gives the first row (simple transposition) 
			verticalLines[x].line[y] = value; 
			horizontalLines[y].line[x] = value; 
			if (value != 0) {
				verticalLines[x].alreadyPlacedValues.add(value);
				horizontalLines[y].alreadyPlacedValues.add(value);
				verticalLines[x].checkIsComplete();
				horizontalLines[y].checkIsComplete();
			} 
		}

		/**
		 * Gets the next null position in the table, with x ascending then y ascending.
		 * @param the current position
		 * @return the next position
		 * @throws EndException : when there is no next position
		 */
		public Optional<Position> getNextPosition() {
			Position nextPosition;
			do {
				nextPosition = positionsQueue.poll();
				if (nextPosition == null) {
					return Optional.empty();
				}
			} while (getValueForPosition(nextPosition) != 0);
			return Optional.of(nextPosition);
		}

		/**
		 * Gets the possible values for the given position, by taking the intersection of the possible values 
		 * for the horizontal line and the vertical line.
		 * @param position
		 * @return
		 */
		public Set<Integer> getPossibleValuesForPosition(Position position) {
			int x = position.getX();
			int y = position.getY();
			Set<Integer> possibleValuesForHorizontalLine = horizontalLines[y].getPossibleValuesForPosition(x); 
			if (!possibleValuesForHorizontalLine.isEmpty()) {
				Set<Integer> possibleValuesForVerticalLine = verticalLines[x].getPossibleValuesForPosition(y);
				possibleValuesForHorizontalLine.retainAll(possibleValuesForVerticalLine);
			}
			return possibleValuesForHorizontalLine;
		}

		/** 
		 * Initialization of the table with null values. Didn't find a shorter way of doing it.
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0 ; i < SIZE ; i++) {
				sb.append(Arrays.toString(table[i]));
				sb.append("\n");
			}
			sb.append("Clues : ");
			sb.append(Arrays.toString(clues));
			sb.append("\n");
			return sb.toString();
		}
	}

	/**
	 * Line is an abstraction that represents a horizontal or vertical line.
	 * It is the adequate (at least to my mind) object in which we can enforce the rules of the game, 
	 * namely a different value for each position in the line and the number of skyCrapers seen from each side of the line.
	 */
	public static class Line {
		int[] line = new int[SIZE];
		int topClue;
		int bottomClue;

		boolean isComplete = false;

		Set<Integer> alreadyPlacedValues = new TreeSet<>();
		static Set<Integer> possibleValues = IntStream.rangeClosed(1, SIZE).boxed().collect(Collectors.toSet());

		/**
		 * Non init clues are clues that must be checked against at end step.
		 */
		private Set<Integer> allNonInitClues = IntStream.rangeClosed(2, SIZE-1).boxed().collect(Collectors.toSet());

		Line(int topClue, int bottomClue) {
			this.topClue = topClue;
			this.bottomClue = bottomClue;
		}

		public void checkIsComplete() {
			if (isNearlyComplete()) {
				isComplete = true;
			}	
		}

		/**
		 * Init clues are clues that enforce specific values at - and only at - initialization step.
		 * In detail, if clue = 1 then first value is SIZE, if clue = SIZE, then line is [1,2,...,SIZE]
		 */
		private void resolveInitClues() {
			if (topClue == 1) {
				line[0] = SIZE;
			}
			if (bottomClue == 1) {
				line[SIZE-1] = SIZE;
			}
			if (topClue == SIZE) {
				IntStream.range(0, SIZE).forEach(i -> line[i] = i + 1);
			}
			if (bottomClue == SIZE) {
				IntStream.range(0, SIZE).forEach(i -> line[SIZE-1-i] = i + 1);
			}
		}

		/**
		 * Returns list of possible values for given position.
		 * Technically, it is all the possible values [1,...,SIZE] minus the values already placed on the line,
		 * and minus the values that don't check against the clues (clues are only checked when placing last value in the line)
		 * @param positionInLine : given position
		 * @return list of possible values
		 */
		public Set<Integer> getPossibleValuesForPosition(Integer positionInLine) {
			//LocalDateTime before = LocalDateTime.now();
			if(line[positionInLine] != 0) {
				throw new IllegalArgumentException("Fatal Error : Trying to discover already discovered value !!"
						+ "\n position = " + positionInLine + "\n line : " + this);
			}
			//Set<Integer> alreadyPlacedValues = getAlreadyPlacedValues(positionInLine);
			Set<Integer> possibleValues = new TreeSet<>(Line.possibleValues);
			possibleValues.removeAll(alreadyPlacedValues); 
			if (!possibleValues.isEmpty()) {
				possibleValues.removeIf(valueToTest -> !isCompatibleWithClues(valueToTest, positionInLine));
			}
			return possibleValues;
		}

		/**
		 * Checks if line is complete (returns true if not), and then checks if whole line is consistent with clues.
		 * @param valueToTest
		 * @param position
		 * @return
		 */
		private boolean isCompatibleWithClues(Integer valueToTest, int position) {
			line[position] = valueToTest;
			boolean result = true;
			if(this.isComplete) {
				result = this.isCompatibleWithClues(); 
			}
			line[position] = 0;
			return result;
		}

		private boolean isNearlyComplete() {
			return IntStream.range(0, SIZE).filter(i -> line[i] == 0).count() == 1;

		}

		/**
		 * Checks if the line is completely filled
		 * @return
		 */
		private boolean isComplete() {
			return IntStream.range(0, SIZE).allMatch(i -> line[i] != 0);
		}

		/**
		 * Checks if the filled line is consistent with the non init clues 
		 * @return
		 */
		private boolean isCompatibleWithClues() {
			boolean isNotCompatibleWithClues = 
					(allNonInitClues.contains(topClue) && getNumberOfSkyCrapersOnLine(true) != topClue) ||
					(allNonInitClues.contains(bottomClue) && getNumberOfSkyCrapersOnLine(false) != bottomClue);
			return !isNotCompatibleWithClues;
		}

		/** 
		 * Returns the number of SkyCrapers seen on the line. 
		 * isAcending tells if we're looking from lower to higher position (true) or higher to lower (false).
		 * @param isAscending
		 * @return
		 */
		private int getNumberOfSkyCrapersOnLine(boolean isAscending) {
			int result = 0;
			int maxValue = 0;
			for (int i = 0 ; i < SIZE ; i++) {
				int positionInLine = isAscending ? i : SIZE-1-i; 
				if (line[positionInLine] > maxValue) {
					maxValue = line[positionInLine];
					result++;
				}		
			}
			return result;
		}

		/** 
		 * Returns a set of all the values already placed elsewhere on the line.
		 * @param positionInLine
		 * @return
		 */
		private Set<Integer> getAlreadyPlacedValues(int positionInLine) {
			Set<Integer> alreadyPlacedValues = new HashSet<Integer>();
			for (int i = 0 ; i < SIZE ; i ++) {
				if (i == positionInLine) { // Skip for target square
					continue;
				}
				if (line[i] != 0) { // If there's already a value, we remove it from possibilities on the line
					alreadyPlacedValues.add(line[i]);
				}
			}
			return alreadyPlacedValues;
		}
	}

	//	@Override
	//	public String toString() {
	//		return "\n line : " + Arrays.toString(line) + "\n topClue = " + topClue + " ; bottomClue = " + bottomClue 
	//				;
	//		//		"\n verticalLines : " + Arrays.toString(verticalLines) +
	//		//	"\n horizontalLines : " + Arrays.toString(horizontalLines) +
	//		//"table = " + Arrays.toString(table[0]) + Arrays.toString(table[1]) + 
	//		//Arrays.toString(table[2]) + Arrays.toString(table[3]) ;
	//	}

	private static int clues[][] = {
			{ 7,0,0,0,2,2,3, 0,0,3,0,0,0,0, 3,0,3,0,0,5,0, 0,0,0,0,5,0,4},
			{ 		1, 2, 3, 3, 4, 2, 1, 2, 2, 1, 2, 4, 3, 3, 2, 1},
			{ 	7,6,5,4,3,2,1,
				1,2,2,2,2,2,2,
				2,2,2,2,2,2,1,
				1,2,3,4,5,6,7}
	};

	public static void printTable(int[][] table) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < SIZE ; i++) {
			sb.append(Arrays.toString(table[i]));
			sb.append("\n");
		}
		System.out.println(sb);
	}

	public static void main(String...args) {

		LocalDateTime before = LocalDateTime.now();

		boolean clues1 = SkyscrapersRemoveClues.solvePuzzle(7, clues[0]);
		//int[][] table1 = Skyscrapers2.solvePuzzle(clues[1]);
		//printTable(table1);

		LocalDateTime after  = LocalDateTime.now();
		Duration duration = Duration.between(before, after);
		System.out.println("Duree du calcul : " + duration.getSeconds() + "s " + duration.getNano()/1_000_000 + "ms");

		//Line(int[] line, int topClue, int bottomClue) {
		//		Line line = new Line(new int[] {4,2,0,0}, 1 ,2);
		//		Set<Integer> set = line.getPossibleValuesForPosition(2);
		//		System.out.println(line);

		//System.out.println(Arrays.toString(Kata.solvePuzzle(clues[0])));


		//int[] input = new int[] { 1, 2, 3, 4, 5 };
		//	System.out.println(foldArray(input, 2));
		//System.out.println(zeros(100_000));
		//System.out.println(zeros(100_005));
		//System.out.println(zeros2(20_000));
		//System.out.println(zeros2(99_995));
		//System.out.println(zeros2(100_000));
		//System.out.println(zeros2(100_005));
	}

	// System.out.println(x);
}