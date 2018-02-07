package algos.skyscrapers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** 
 * Optimized version : with already placed values and isComplete determined 
 * when position is set instead of when each new possible value is tested in Line class 
 * */

public class SkyscrapersCheckIfMultipleSolutions {

	static Integer SIZE = 7;
	static boolean LOG;

	static boolean solvePuzzle (int[] clues, boolean log) {
		SIZE = clues.length/4;
		LOG = log;
		return new SkyscrapersCheckIfMultipleSolutions().checkIfMultipleSolutions(clues);
	}

	Random rand = new Random();
    boolean isSolutionFound = false;
	Table table;
	long dur;
	LocalDateTime before = LocalDateTime.now();
	
	private boolean checkIfMultipleSolutions(int[] clues) {
		try {
			solve(clues);
			return true;
		} catch (EndException e) {
			if (LOG) {
				System.out.println(e + Arrays.toString(clues));	
			}
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
		System.out.println("Duree 1 : " + table.dur/1_000_000 + "ms");
		System.out.println("Duree 2 : " + table.dur2/1_000_000 + "ms");
		System.out.println("Duree 3 : " + table.dur3/1_000_000 + "ms");
		System.out.println("nb de values non null testées : " + table.nbNonNullValuesSet);

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
		if (LOG) {
			System.out.println(table);
		}
		if (!isSolutionFound) {
			isSolutionFound = true;
			table.resetValue(position);
		} else {
			throw new EndException("Plusieurs Solutions");
		}
	}

	private boolean iter(Position position) throws EndException {
		List<Integer> possibleValues = table.getPossibleValuesForPosition(position);
		int size = possibleValues.size();
		for (int i = 0; i < size; i++) {
	        int randomIndex = rand.nextInt(possibleValues.size());
	        Integer possibleValue = possibleValues.get(randomIndex);
	        possibleValues.remove(randomIndex);
	    
			table.setValueForPosition(position, possibleValue);
			Optional<Position> nextPosition = table.getNextPosition();
			if (!nextPosition.isPresent()) {
				positionNotFound(position);
				LocalDateTime after = LocalDateTime.now();
				Duration duration = Duration.between(before, after);
				System.out.println("First Solution : "+ duration.getSeconds() + "s " + duration.getNano()/1_000_000 + "ms");
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
		long dur, dur2, dur3;
		int nbNonNullValuesSet;
		Random rand = new Random();
		
		Table(int[] clues) {
			this.clues = clues;
			initLinesWithClues(clues);
			initPositionsIterationWithClues(clues);
		}

		private void initPositionsIterationWithClues(int[] clues) {
			for (int clueValue = SIZE-1 ; clueValue >= 0 ; clueValue--) {
				List<Integer> cluesForGivenValue = new ArrayList<>();
				for (int i = 0 ; i < 4*SIZE ; i++) {
					if (clues[i] == clueValue) {
						cluesForGivenValue.add(i);
					}
				}
				int numberOfElements = cluesForGivenValue.size();
				for (int i = 0; i < numberOfElements; i++) {
			        int randomIndex = rand.nextInt(cluesForGivenValue.size());
			        int randomElement = cluesForGivenValue.get(randomIndex);
			        cluesForGivenValue.remove(randomIndex);
			        addAllPositionsOfClue(randomElement);
			    } 

			}
		}

		private void addAllPositionsOfClue(int i) {
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
					if(horizontalLines[y].line[x] != 0 && getValueForPosition(x, y) == 0 ) {
						setValueForPosition(new Position(x,y), horizontalLines[y].line[x]);
					}
				}
			}		
		}

		private void synchTableWithVerticalLines() {
			for (int x = 0 ; x < SIZE ; x++) {
				for (int y = 0 ; y < SIZE ; y++) {
					if(verticalLines[x].line[y] != 0 && getValueForPosition(x, y) == 0 ) {
						setValueForPosition(new Position(x,y), verticalLines[x].line[y]);
					}
				}
			}
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

		public void resetPosition(Position position) {
			positionsQueue.addFirst(position);
		}

		public void resetValue(Position position) {
			Integer value = getValueForPosition(position);
			setValueForPosition(position, 0);
			int x = position.getX();
			int y = position.getY();
			verticalLines[x].alreadyPlacedValues[value-1] = false;
			horizontalLines[y].alreadyPlacedValues[value-1] = false;
			verticalLines[x].numberOfValuesPlaced--;
			horizontalLines[y].numberOfValuesPlaced--;
			verticalLines[x].isComplete = false;
			horizontalLines[y].isComplete = false;
		}

		/**
		 * Updates value in the table, and updates the relevant lines to ensure consistency of the model.
		 * Like the getter, hides the transposition from the user (it only appear once in the getter and once in the setter).
		 * @param position : position in the table
		 * @param value : value to update with
		 */
		public void setValueForPosition(Position position, int value) {
//			LocalDateTime before = LocalDateTime.now();
			int x = position.getX();
			int y = position.getY();
			table[y][x] = value;  	// y and x are inverted so that table[0] gives the first row (simple transposition) 
			verticalLines[x].line[y] = value; 
			horizontalLines[y].line[x] = value; 
			if (value != 0) {
				nbNonNullValuesSet++;
//				LocalDateTime after2 = LocalDateTime.now();
				verticalLines[x].alreadyPlacedValues[value-1] = true;
				horizontalLines[y].alreadyPlacedValues[value-1] = true;
				verticalLines[x].numberOfValuesPlaced++;
				horizontalLines[y].numberOfValuesPlaced++;
//				LocalDateTime after3 = LocalDateTime.now();
//				Duration duration2 = Duration.between(after2, after3);
				verticalLines[x].checkIsComplete();
				horizontalLines[y].checkIsComplete();
//				LocalDateTime after4 = LocalDateTime.now();
//				Duration duration3 = Duration.between(after3, after4);
//				dur2 += duration2.getNano();
//				dur3 += duration3.getNano();
			} 
//			LocalDateTime after = LocalDateTime.now();
//			Duration duration = Duration.between(before, after);
//			dur += duration.getNano();

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
		public List<Integer> getPossibleValuesForPosition(Position position) {
			int x = position.getX();
			int y = position.getY();
			//			LocalDateTime before = LocalDateTime.now();
			List<Integer> possibleValuesForHorizontalLine = horizontalLines[y].getPossibleValuesForPosition(x); 
			//			LocalDateTime after = LocalDateTime.now();
			if (!possibleValuesForHorizontalLine.isEmpty()) {
				List<Integer> possibleValuesForVerticalLine = verticalLines[x].getPossibleValuesForPosition(y);
				possibleValuesForHorizontalLine.retainAll(possibleValuesForVerticalLine);
			}
			//			LocalDateTime after2 = LocalDateTime.now();
			//			Duration duration = Duration.between(before, after);
			//			Duration duration2 = Duration.between(after, after2);
			//			dur += duration.getNano();
			//			dur2 += duration2.getNano();
			return possibleValuesForHorizontalLine;
		}

		/** 
		 * Initialization of the table with null values. Didn't find a shorter way of doing it.
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("\n");
			for (int i = 0 ; i < SIZE ; i++) {
				sb.append(Arrays.toString(table[i]));
				sb.append("\n");
			}
			sb.append("Clues : ");
			sb.append(Arrays.toString(clues));
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

		boolean isComplete;

		boolean[] alreadyPlacedValues = new boolean[SIZE];
		int numberOfValuesPlaced;
		static Set<Integer> possibleValues = IntStream.rangeClosed(1, SIZE).boxed().collect(Collectors.toSet());

		/**
		 * Non init clues are clues that must be checked against at end step.
		 */
		private Set<Integer> allNonInitClues = IntStream.rangeClosed(2, SIZE-1).boxed().collect(Collectors.toSet());

		Line(int topClue, int bottomClue) {
			this.topClue = topClue;
			this.bottomClue = bottomClue;
		}

		private void checkIsComplete() {
			if (numberOfValuesPlaced == SIZE-1) {
				isComplete = true;
			}
			//			if (isNearlyComplete()) {
			//				isComplete = true;
			//			}	
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
		public List<Integer> getPossibleValuesForPosition(Integer positionInLine) {
			if(line[positionInLine] != 0) {
				throw new IllegalArgumentException("Fatal Error : Trying to discover already discovered value !!"
						+ "\n position = " + positionInLine + "\n line : " + this);
			}
			//Set<Integer> alreadyPlacedValues = getAlreadyPlacedValues(positionInLine);
			List<Integer> possibleValues = new ArrayList<>();
			for (int i = 0 ; i < SIZE ; i++) {
				if (alreadyPlacedValues[i] == false && isCompatibleWithClues(i+1, positionInLine)) {
					possibleValues.add(i+1);
				}
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
			//checkIsComplete();
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
			{ 0,2,3,0,2,0,0, 5,0,4,5,0,4,0, 0,4,2,0,0,0,6, 0,2,2,2,2,4,1},
			{ 	2, 2, 1, 3,
                2, 2, 3, 1,
                1, 2, 2, 3,
                3, 2, 1, 3}
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

		boolean clues1 = SkyscrapersCheckIfMultipleSolutions.solvePuzzle(clues[1], true);
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