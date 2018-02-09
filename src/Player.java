import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

public class Player {

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int nbOfRows = in.nextInt();
		int nbOfColumns = in.nextInt();
		int alarmTurns = in.nextInt(); // number of rounds between the time the alarm countdown is activated and the time the alarm goes off.

		Map map = new Map(nbOfColumns, nbOfRows, alarmTurns);

		// game loop
		while (true) {
			int kirkRow = in.nextInt(); // row where Kirk is located.
			int kirkColum = in.nextInt(); // column where Kirk is located.
			map.updateKirkPosition(kirkColum, kirkRow);

			for (int i = 0; i < nbOfRows; i++) {
				String ROW = in.next(); // C of the characters in '#.TC?' (i.e. one line of the ASCII maze).
				map.updateRow(i, ROW);
			}

			// Write an action using System.out.println()
			// To debug: System.err.println("Debug messages...");

			System.out.println(map.getNextMove()); // Kirk's next move (UP DOWN LEFT or RIGHT).
		}
	}

	public static class Map {

		public static final int MAX_FUEL = 1200;
		int width;
		int height;
		int fuelUsed = 0;
		int alarmTurns;
		Position kirkPos;
		List<String> allSquares = new ArrayList<>();
		boolean isMapExplored = false; // Phase 1 - Exploration of the map
		boolean isAlarmTriggered = false; // Phase 2 - Go to Command Room ; Phase 3 - Return to start position 

		public Map(int nbOfColumns, int nbOfRows, int alarmTurns) {
			this.width = nbOfColumns;
			this.height = nbOfRows;
			this.alarmTurns = alarmTurns;
		}

		public void updateKirkPosition(int kirkColum, int kirkRow) {
			kirkPos = Position.of(kirkColum, kirkRow);
		}

		public void updateRow(int i, String row) {
			allSquares.set(i, row);
		}

		public String getNextMove() {
			Position pos = computeNextMove();
			if (pos.x - kirkPos.x == 1 && pos.y - kirkPos.y == 0) {
				return "RIGHT";
			}
			if (pos.x - kirkPos.x == -1 && pos.y - kirkPos.y == 0) {
				return "LEFT";
			}
			if (pos.y - kirkPos.y == 1 && pos.x - kirkPos.x == 0) {
				return "UP";
			}
			if (pos.y - kirkPos.y == -1 && pos.x - kirkPos.x == 0) {
				return "DOWN";
			}
			throw new RuntimeException("Erreur dans la transformation movement en string");
		}

		public Position computeNextMove() {
			if (!isMapExplored) {
				Optional<Position> pos = computeNextExplorationMove();
				if (pos.isPresent()) {
					return pos.get();
				} else {
					isMapExplored = true;
				}
			}
			if (!isAlarmTriggered) {
				Optional<Position> pos = computeGoToCommandRoomMove();
				if (pos.isPresent()) {
					return pos.get();
				} else {
					isAlarmTriggered = true;
				}
			}
			return computeReturnToStartMove();
		}

		private Optional<Position> computeNextExplorationMove() {
			PriorityQueue<AlgoPosition> open = new PriorityQueue<>();
			Set<Position> closed = new HashSet<>();
			// init
			do {
				AlgoPosition current = open.poll();
				closed.add(current);
				if(getCharAtPosition(current) == '?') {
					return getFirstMoveOfEndPosition(current);
				} 
				for (AlgoPosition neighbour : getNeighbours(current)) {
					char charPos = getCharAtPosition(neighbour);
					if (charPos != '#' && charPos != 'C' && !closed.contains(neighbour)) {
						neighbour.cost = getCost(current, neighbour);
						neighbour.parent = current;
						open.add(neighbour);
					}
				}

			} while (!open.isEmpty());
			return null;
		}

		private Optional<Position> getFirstMoveOfEndPosition(AlgoPosition current) {
			// TODO Auto-generated method stub
			return null;
		}

		private float getCost(AlgoPosition current, AlgoPosition neighbour) {
			// TODO : TESTER si la conversion en float marche correctement
			if (neighbour.x - current.x == -1) {
				return (float) (current.cost + 1 - 1.0 / width);
			}
			if (neighbour.y - current.y == -1) {
				return (float) (current.cost + 1 - 1.0 / height);
			}
			return current.cost + 1;
		}

		private List<AlgoPosition> getNeighbours(AlgoPosition current) {
			List<AlgoPosition> list = new ArrayList<>();
			list.add(new AlgoPosition(current.addCoordinates(1, 0)));
			list.add(new AlgoPosition(current.addCoordinates(-1, 0)));
			list.add(new AlgoPosition(current.addCoordinates(0, 1)));
			list.add(new AlgoPosition(current.addCoordinates(0, -1)));
			return list;
		}

		private char getCharAtPosition(Position pos) {
			return allSquares.get(pos.x).charAt(pos.y);
		}

		private Optional<Position> computeGoToCommandRoomMove() {
			// If already on CommandRoom, return null;
			return null;
		}

		private Position computeReturnToStartMove() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class AlgoPosition extends Position implements Comparable<AlgoPosition> {

		private AlgoPosition parent;
		private float cost;

		public AlgoPosition(Position position) {
			super(position);
		}

		public AlgoPosition(Position position, float cost, AlgoPosition parent) {
			super(position);
			this.cost = cost;
			this.parent = parent;
		}

		@Override
		public int compareTo(AlgoPosition other) {
			return Float.compare(this.cost, other.cost);
		}

	}

	public static class Position {

		protected final int x;
		protected final int y;

		public static Position of(int x, int y) {
			return new Position(x, y);
		}

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public Position(Position pos) {
			this.x = pos.x;
			this.y = pos.y;
		}

		public float getFloatX() {
			return (float) x;
		}

		public float getFloatY() {
			return (float) y;
		}

		public Position getSymmetricBy(Position middlePos) {
			return new Position(2 * middlePos.x - this.x, 2 * middlePos.y - this.y);
		}

		public Position addCoordinates(int x, int y) {
			return new Position(this.x + x, this.y + y);
		}

		@Override
		public String toString() {
			return "P : " + x + " " + y;
		}

		@Override
		public int hashCode() {
			return this.x * 17 + this.y * 31 + 73;
		}

		@Override
		public boolean equals(Object object) {
			if (object == null) {
				return false;
			}
			Position pos = (Position) object;
			return pos.x == this.x && pos.y == this.y;
		}

	}

}
