import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.IntStream;

class Player {

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
		Position kirkPosition;
		List<String> allSquares = new ArrayList<>();
		boolean isMapExplored = false; // Phase 1 - Exploration of the map
		boolean isAlarmTriggered = false; // Phase 2 - Go to Command Room ; Phase 3 - Return to start position 
		Position commandRoomPosition;
		PathPosition commandRoomPath;
		Position startingPosition;
		PathPosition startingPath;

		public Map(int nbOfColumns, int nbOfRows, int alarmTurns) {
			this.width = nbOfColumns;
			this.height = nbOfRows;
			this.alarmTurns = alarmTurns;
			IntStream.range(0, nbOfRows).forEach(i -> allSquares.add(""));
		}

		public void updateKirkPosition(int kirkColum, int kirkRow) {
			kirkPosition = Position.of(kirkColum, kirkRow);
		}

		public void updateRow(int i, String row) {
			allSquares.set(i, row);
		}

		public String getNextMove() {
			Position pos = computeNextMove();
			if (pos.x - kirkPosition.x == 1 && pos.y - kirkPosition.y == 0) {
				return "RIGHT";
			}
			if (pos.x - kirkPosition.x == -1 && pos.y - kirkPosition.y == 0) {
				return "LEFT";
			}
			if (pos.y - kirkPosition.y == 1 && pos.x - kirkPosition.x == 0) {
				return "UP";
			}
			if (pos.y - kirkPosition.y == -1 && pos.x - kirkPosition.x == 0) {
				return "DOWN";
			}
			throw new RuntimeException("Erreur dans la transformation movement en string");
		}

		public Position computeNextMove() {
			if (!isMapExplored) {
				Optional<PathPosition> explorationPath = computeMoveToNearest('?');
				if (explorationPath.isPresent()) {
					Optional<Position> pos = explorationPath.get().computeNextMoveInPathFrom(kirkPosition);
					return pos.get();
				} else {
					isMapExplored = true;
				}
			}
			if (isMapExplored && !isAlarmTriggered) {
				if (commandRoomPosition == null && commandRoomPath == null) {
					computeGoToCommandRoomPath();
				}
				Optional<Position> pos = commandRoomPath.computeNextMoveInPathFrom(kirkPosition);
				if (pos.isPresent()) {
					return pos.get();
				} else {
					isAlarmTriggered = true;
				}
			}
			if (isMapExplored && isAlarmTriggered) {
				if (startingPosition == null && startingPath == null) {
					computeReturnToStartPath();
				}
				Optional<Position> pos = commandRoomPath.computeNextMoveInPathFrom(kirkPosition);
				if (pos.isPresent()) {
					return pos.get();
				} else {
					throw new RuntimeException("Start position already reached, shouldn't happen");
				}
			}
			throw new RuntimeException("This shouldn't be reached in method computeNextMove()");
		}

		private Optional<PathPosition> computeMoveToNearest(char targetChar) {
			PriorityQueue<PathPosition> open = new PriorityQueue<>();
			Set<Position> closed = new HashSet<>();

			open.add(new PathPosition(kirkPosition, 0, null));
			do {
				PathPosition current = open.poll();
				closed.add(current.getPosition());
				if (getCharAtPosition(current.getPosition()) == targetChar) {
					return Optional.of(current);
				}
				for (Position neighbour : current.getPosition().getManhattanNeighbours()) {
					if (isPositionToInvestigate(neighbour, closed)) {
						float heuristics = getAntiSymmetricHeuristics(current.getPosition(), neighbour);
						open.add(new PathPosition(neighbour, heuristics, current));
					}
				}
			} while (!open.isEmpty());

			return Optional.empty();
		}

		private boolean isPositionToInvestigate(Position pos, Set<Position> closed) {
			char charPos = getCharAtPosition(pos);
			return charPos != '#' && charPos != 'C' && !closed.contains(pos);
		}

		private char getCharAtPosition(Position pos) {
			return allSquares.get(pos.y).charAt(pos.x);
		}

		private void computeGoToCommandRoomPath() {
			commandRoomPosition = computeMoveToNearest('C').get().getPosition();
			commandRoomPath = computePathForEnd(commandRoomPosition);
		}

		private void computeReturnToStartPath() {
			startingPosition = computeMoveToNearest('S').get().getPosition();
			startingPath = computePathForEnd(commandRoomPosition);
		}

		private PathPosition computePathForEnd(Position targetPosition) {
			PriorityQueue<PathPosition> open = new PriorityQueue<>();
			Set<Position> closed = new HashSet<>();

			open.add(new PathPosition(kirkPosition, 0, null));
			do {
				PathPosition current = open.poll();
				closed.add(current.getPosition());
				if (targetPosition.equals(current.getPosition())) {
					return current;
				}
				for (Position neighbour : current.getPosition().getManhattanNeighbours()) {
					if (isPositionToInvestigate(neighbour, closed)) {
						float heuristics = getHeuristics(neighbour, targetPosition);
						open.add(new PathPosition(neighbour, heuristics, current));
					}
				}
			} while (!open.isEmpty());

			throw new RuntimeException("Target position " + targetPosition + " not found !");
		}

		private float getAntiSymmetricHeuristics(Position current, Position next) {
			// TODO : TESTER si la conversion en float marche correctement
			if (next.x - current.x < 0) {
				return (float) (-1.0 / width);
			}
			if (next.y - current.y < 0) {
				return (float) (-1.0 / height);
			}
			return 0;
		}

		private float getHeuristics(Position neighbour, Position targetPosition) {
			int distanceX = Math.abs(targetPosition.x - neighbour.x);
			int distanceY = Math.abs(targetPosition.y - neighbour.y);
			float antiSymmetry = getAntiSymmetricHeuristics(neighbour, targetPosition);
			return antiSymmetry + distanceX + distanceY;
		}

	}

	public static class PathPosition implements Comparable<PathPosition> {

		private final PathPosition parent;
		private final Position position;
		private final float total; // equals to f in A*
		private final int cost; // equals to g in A*

		public PathPosition(Position position, float heuristics, PathPosition parent) {
			this.position = position;
			this.parent = parent;
			if (parent != null) {
				this.cost = parent.cost + 1;
				this.total = this.cost + heuristics;
			} else {
				this.cost = 0;
				this.total = 0;
			}
		}

		public Optional<Position> computeNextMoveInPathFrom(Position currentPosition) {
			System.err.println("Current Pos : " + currentPosition);
			System.err.println("Target Pos : " + this);
			if (currentPosition.equals(this.position)) {
				return Optional.empty();
			}
			PathPosition tempPathPosition = this;
			while (!currentPosition.equals(tempPathPosition.parent.getPosition())) {
				tempPathPosition = this.parent;
				System.err.println("Previous Pos : " + currentPosition);
			}

			return Optional.of(tempPathPosition.getPosition());
		}

		public Position getPosition() {
			return position;
		}

		@Override
		public String toString() {
			return position.toString() + " ; cost : " + cost;
		}

		@Override
		public int compareTo(PathPosition other) {
			return Float.compare(this.total, other.total);
		}

		@Override
		public int hashCode() {
			return (int) (this.cost * 17 + 73);
		}

		@Override
		public boolean equals(Object object) {
			if (object == null) {
				return false;
			}
			PathPosition pos = (PathPosition) object;
			return pos.cost == this.cost;
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

		public List<Position> getManhattanNeighbours() {
			List<Position> list = new ArrayList<>();
			list.add(this.addCoordinates(1, 0));
			list.add(this.addCoordinates(-1, 0));
			list.add(this.addCoordinates(0, 1));
			list.add(this.addCoordinates(0, -1));
			return list;
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
