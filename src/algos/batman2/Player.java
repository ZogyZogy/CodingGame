package algos.batman2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Player {

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int widthBuilding = in.nextInt(); // width of the building.
		int heightBuilding = in.nextInt(); // height of the building.
		int numberOfTurns = in.nextInt(); // maximum number of turns before game
											// over.
		int initialX = in.nextInt();
		int initialY = in.nextInt();

		Building building = new Building(widthBuilding, heightBuilding);
		Position oldPos = new Position(initialX, initialY);
		Position newPos = oldPos;
		Position nextPos = null;
		// game loop
		while (true) {
			String bombDir = in.next(); // Current distance to the bomb compared
										// to previous distance (COLDER, WARMER,
										// SAME or UNKNOWN)
			System.err.println("Max number of turns : " + numberOfTurns);
			if (!"UNKNOWN".equals(bombDir)) {
				building.pruneBuilding(oldPos, newPos, bombDir);
			}
			nextPos = building.getNextPosition(oldPos, newPos, bombDir);
			System.err.println("Final Position : " + nextPos);

			System.out.println(String.format("%s %s", nextPos.x, nextPos.y));
			oldPos = newPos;
			newPos = nextPos;
		}
	}

	public static class Position {

		private final int x;
		private final int y;

		public static Position of(int x, int y) {
			return new Position(x, y);
		}

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
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

	/**
	 * Building of max width and max height of 10_000
	 *
	 */
	public static class Building {
		private final int width;
		private final int height;

		List<LineDelimitation> lines = new ArrayList<>();

		List<Position> vertices;
		LinkedList<Position> edges;
		Set<Position> visitedPositions = new HashSet<>();

		public Building(int width, int height) {
			this.width = width;
			this.height = height;
			vertices = getInitialVertices();
			edges = getInitialEdges();
		}

		private List<Position> getInitialVertices() {
			List<Position> list = new ArrayList<>();
			list.add(Position.of(0, 0));
			list.add(Position.of(0, height - 1));
			list.add(Position.of(width - 1, height - 1));
			list.add(Position.of(width - 1, 0));
			return list;
		}

		/**
		 * Increasing x (= same order as when we add new edges).
		 * 
		 * @return
		 */
		private LinkedList<Position> getInitialEdges() {
			LinkedList<Position> list = new LinkedList<>();
			for (int x = 0; x < width; x++) {
				list.add(Position.of(x, 0));
			}
			for (int y = 1; y < height; y++) {
				list.add(Position.of(width - 1, y));
			}
			for (int x = width - 2; x >= 0; x--) {
				list.add(Position.of(x, height - 1));
			}
			for (int y = height - 2; y >= 0; y--) {
				list.add(Position.of(0, y));
			}
			return list;
		}

		/**
		 * Compute next position starting from oldPos if "colder" or newPos if
		 * "warmer" (= from the closest one). if "same" whichever is ok.
		 * 
		 * @param oldPosition
		 * @param newPos
		 * @param bombDir
		 * @return
		 */
		public Position getNextPosition(Position oldPosition, Position newPos, String bombDir) {
			visitedPositions.add(newPos);
			if ("COLDER".equals(bombDir)) {
				return getNextPosition(oldPosition, 0, 0);
			} else {
				return getNextPosition(newPos, 0, 0);
			}
		}

		/***
		 * Computes next position from previous position
		 * 
		 * @param newPos
		 * @return
		 */
		private Position getNextPosition(Position previousPosition, int weight, int iteration) {
			Position center = getCenter();
			System.err.println("Previous position : " + previousPosition);
			System.err.println("Center : " + center);

			Position testPosition = null;
			System.err.println("Comparing if test position is ok for the remaining building");
			testPosition = getBarycentre(Arrays.asList(center, previousPosition), Arrays.asList(2, -1));

			//				testPosition = center;
			System.err.println("Test position : " + testPosition);
			int randomX = new Random().nextInt(2 * weight + 1) - weight;
			int randomY = new Random().nextInt(2 * weight + 1) - weight;
			testPosition = testPosition.addCoordinates(randomX, randomY);
			System.err.println("Test position : " + testPosition);

			if (!isInBuilding(testPosition) || visitedPositions.contains(testPosition)) {
				iteration += 1;
				if (iteration == (2 * weight + 1) * (2 * weight + 1)) {
					weight += 1;
				}
				return getNextPosition(previousPosition, weight, iteration);
			}
			//			testPosition = center;

			//System.err.println("visitedPositions : " + visitedPositions);

			//			while (visitedPositions.contains(testPosition)) {
			//				int randomX = new Random().nextInt(3) - 1;
			//				int randomY = new Random().nextInt(3) - 1;
			//				testPosition = testPosition.addCoordinates(randomX, randomY);
			//				System.err.println("new testPosition since already visited: " + testPosition);
			//
			//			}

			return testPosition;
		}

		private Position getBarycentre(List<Position> positions, List<Integer> weights) {
			assert positions.size() == weights.size();
			int sumX = 0;
			int sumY = 0;
			int sumWeight = 0;
			for (int index = 0; index < positions.size(); index++) {
				Position pos = positions.get(index);
				int weight = weights.get(index);
				sumX += weight * pos.x;
				sumY += weight * pos.y;
				sumWeight += weight;
			}
			//			System.err.println(String.format("sumX = %s ; sumY = %s ; sumWeight = %s ", sumX, sumY, sumWeight));
			return Position.of(sumX / sumWeight, sumY / sumWeight);
		}

		private Position getCenter() {
			List<Integer> weights = new ArrayList<>();
			for (Position pos : vertices) {
				weights.add(1);
			}
			return getBarycentre(vertices, weights);
		}

		private boolean isInBuilding(Position pos) {
			if (pos.x < 0 || pos.y < 0 || pos.x >= width || pos.y >= height) {
				return false;
			}
			for (LineDelimitation line : lines) {
				if (!line.isPositionInDelimitation(pos)) {
					return false;
				}
			}
			return true;
		}

		public void pruneBuilding(Position oldPosition, Position newPosition, String bombDir) {
			if (oldPosition.equals(newPosition)) {
				throw new SamePositionException();//"New position same as old position : infinity loop"
			}
			try {
				LineDelimitation line = new LineDelimitation(oldPosition, newPosition, bombDir);
				System.err.println(line);
				lines.add(line);
				removeObsoleteVertices(line);
				int indexOfNewVertice = removeObsoleteEdges(line);
				addNewVertices(indexOfNewVertice);
				System.err.println("New Vertices : " + vertices);
				addNewEdges(line, indexOfNewVertice);
				System.err.println("New edges : " + edges);
				System.err.println("indexOfNewVertice : " + indexOfNewVertice);
			} catch (NoEdgeRemovedException e) {
				System.err.println(e);
			} catch (Exception e) {
				System.err.println(e);
			}
		}

		private void removeObsoleteVertices(LineDelimitation line) {
			ListIterator<Position> iter = vertices.listIterator(0);
			while (iter.hasNext()) {
				Position pos = iter.next();
				if (!line.isPositionInDelimitation(pos)) {
					iter.remove();
				}
			}
		}

		/**
		 * We get the index of the first edge that is still in the delimitation
		 * after iterating over (and removing) the edges that aren't anymore (if
		 * no index is found, then it is necessarily 0). This index and the one
		 * just before (once the edges are removed) are both new vertices, and
		 * the new edges are added between those two.
		 * 
		 * @param line
		 * @throws NoEdgeRemovedException
		 */
		private int removeObsoleteEdges(LineDelimitation line) throws NoEdgeRemovedException {
			boolean isLastPositionInDelimitation = line.isPositionInDelimitation(edges.getFirst());
			boolean isItemRemoved = false;
			Integer indexOfNewVertice = null;
			ListIterator<Position> iter = edges.listIterator(0);
			while (iter.hasNext()) {
				Position pos = iter.next();
				if (!line.isPositionInDelimitation(pos)) {
					isLastPositionInDelimitation = false;
					iter.remove();
					isItemRemoved = true;
				} else if (indexOfNewVertice == null && !isLastPositionInDelimitation) {
					indexOfNewVertice = iter.previousIndex();
				}
			}
			if (indexOfNewVertice == null) {
				if (isItemRemoved) {
					indexOfNewVertice = 0;
				} else {
					throw new NoEdgeRemovedException();
				}
			}
			return indexOfNewVertice;
		}

		private void addNewVertices(int indexOfNewVertice) {
			int previousIndex = getPreviousIndexOfNewVertice(indexOfNewVertice);
			vertices.add(edges.get(indexOfNewVertice));
			vertices.add(edges.get(previousIndex));
			cleanVertices();
		}

		private int getPreviousIndexOfNewVertice(int indexOfNewVertice) {
			if (indexOfNewVertice == 0) {
				return edges.size() - 1;
			} else {
				return indexOfNewVertice - 1;
			}
		}

		private void addNewEdges(LineDelimitation line, int indexOfNewVertice) {
			int previousIndex = getPreviousIndexOfNewVertice(indexOfNewVertice);
			int indexX = edges.get(indexOfNewVertice).x;
			int previousIndexX = edges.get(previousIndex).x;
			if (indexX > previousIndexX) {
				for (int x = indexX - 1; x > previousIndexX; x--) {
					Position pos = Position.of(x, line.getYFromX(x));
					edges.add(indexOfNewVertice, pos);
				}
			} else {
				for (int x = indexX + 1; x < previousIndexX; x++) {
					Position pos = Position.of(x, line.getYFromX(x));
					edges.add(indexOfNewVertice, pos);
				}
			}
		}

		private void cleanVertices() {
			Iterator<Position> iter = vertices.iterator();
			Position previousPos = iter.next();
			while (iter.hasNext()) {
				Position newPos = iter.next();
				if (newPos.equals(previousPos)) {
					iter.remove();
				}
				previousPos = newPos;
				// Check si y a pas un pb en cas de remove sur l'affectation previousPos
			}
		}

	}

	public static class NoEdgeRemovedException extends Exception {
	}

	public static class SamePositionException extends RuntimeException {
	}

	/**
	 * Tells if position over, on or under y = a*x + b
	 *
	 */
	public static class LineDelimitation {
		float a;
		float b;
		RelativePosition relativePosition;

		public LineDelimitation(Position oldPos, Position newPos, String bombDir) {
			this.a = getA(oldPos, newPos);
			this.b = getB(oldPos, newPos);
			this.relativePosition = getRelativePosition(oldPos, newPos, bombDir);
			System.err.println("oldPos : " + oldPos);
			System.err.println("newPos : " + newPos);
			System.err.println("bombDir : " + bombDir);
		}

		public float getA(Position pos1, Position pos2) {
			if (pos2.y - pos1.y != 0) {
				return -(pos2.getFloatX() - pos1.getFloatX()) / (pos2.getFloatY() - pos1.getFloatY());
			}
			throw new IllegalArgumentException("Ligne x = qqch detectée");
		}

		public float getB(Position pos1, Position pos2) {
			return (pos2.getFloatY() + pos1.getFloatY()) / 2 - a * (pos2.getFloatX() + pos1.getFloatX()) / 2;
		}

		public RelativePosition getRelativePosition(Position oldPos, Position newPos, String bombDir) {
			if ("SAME".equals(bombDir)) {
				throw new IllegalArgumentException("SAME detecté");
			} else if ("COLDER".equals(bombDir)) {
				return getRelativePosition(oldPos);
			} else {
				return getRelativePosition(newPos);
			}
		}

		private RelativePosition getRelativePosition(Position pos) {
			if (pos.y > a * pos.x + b) {
				return RelativePosition.OVER;
			} else {
				return RelativePosition.UNDER;
			}
		}

		public int getYFromX(int x) {
			if (RelativePosition.OVER.equals(relativePosition)) {
				return (int) (a * x + b + 1);
			} else if (RelativePosition.UNDER.equals(relativePosition)) {
				return (int) (a * x + b - 1);
			} else if (RelativePosition.ON.equals(relativePosition)) {
				return (int) (a * x + b);
			}
			throw new IllegalArgumentException();

		}

		public boolean isPositionInDelimitation(Position pos) {
			if (RelativePosition.OVER.equals(relativePosition)) {
				return pos.getFloatY() > pos.getFloatX() * a + b;
			} else if (RelativePosition.UNDER.equals(relativePosition)) {
				return pos.getFloatY() < pos.getFloatX() * a + b;
			} else if (RelativePosition.ON.equals(relativePosition)) {
				return pos.getFloatY() == pos.getFloatX() * a + b;
			}
			throw new IllegalArgumentException();
		}

		@Override
		public String toString() {
			return String.format(" Line : a*x + b : %s*x + %s ; position : %s", a, b, relativePosition);
		}

	}

	public static enum RelativePosition {
		OVER, UNDER, ON;
	}

}
