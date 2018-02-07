package algos.batman2;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.math.*;

public class Batman2 {

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
		Position newPos = null;
		// game loop
		while (true) {
			String bombDir = in.next(); // Current distance to the bomb compared
										// to previous distance (COLDER, WARMER,
										// SAME or UNKNOWN)
			if (!"UNKNOWN".equals(bombDir)) {
				building.pruneBuilding(oldPos, newPos, bombDir);
			}
			newPos = building.getNextPosition(oldPos, newPos, bombDir);

			System.out.println(String.format("%s %s", newPos.x, newPos.y));
		}
	}

	public static class Position {
		private final int x;
		private final int y;

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object object) {
			// !!!!!
			return false;
		}

		public boolean equalsOneDirection(Position pos) {
			// !!!!!
			return false;
		}

		public Position getSymmetricBy(Position middlePos) {
			return new Position(2 * middlePos.x - this.x, 2 * middlePos.y - this.y);
		}

		public Position addCoordinates(int x, int y) {
			return new Position(this.x + x, this.y + y);
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

		int maxEffectiveWidth;
		int minEffectiveWidth = 0;
		int maxEffectiveHeight;
		int minEffectiveHeight = 0;

		public Building(int width, int height) {
			this.width = width;
			this.height = height;
			this.maxEffectiveWidth = width - 1;
			this.maxEffectiveHeight = height - 1;
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
			if ("COLDER".equals(bombDir)) {
				return getNextPosition(oldPosition);
			} else {
				return getNextPosition(newPos);
			}
		}

		/***
		 * Computes next position from previous position
		 * 
		 * @param newPos
		 * @return
		 */
		private Position getNextPosition(Position previousPosition) {
			previousPosition.getSymmetricBy(getMiddleEffectiveBuilding());
			int randomX = ThreadLocalRandom.current().nextInt(0, width-1);
			int randomY = ThreadLocalRandom.current().nextInt(0, width-1);
			Position testPosition = new Position(randomX, randomY);
			if (!isInBuilding(testPosition)){
				getNextPosition(previousPosition);
			}
			return testPosition;
		}

		private boolean isInBuilding(Position pos) {
			for (LineDelimitation line : lines) {
				if (!line.isInLineDelimitation(pos)) {
					return false;
				}
			}
			return true;
		}

		public void pruneBuilding(Position oldPosition, Position newPosition, String bombDir) {
			if (oldPosition.equals(newPosition)) {
				throw new IllegalArgumentException();
			}
			try {
				lines.add(new LineDelimitation(oldPosition, newPosition, bombDir));
				calculateNewEffectiveMinimax();
			} catch (Exception e) {
				System.err.println(e);
			}
		}

		private void calculateNewEffectiveMinimax() {
			LineDelimitation lastLine = lines.get(lines.size() - 1);
			int minX = 0, minY = 0, maxX = 0, maxY = 0;
			for (int x = minEffectiveWidth; x <= maxEffectiveWidth; x++) {
				Position pos = new Position(x, lastLine.getYFromX(x));
				if (minX == 0 && isInBuilding(pos)) {
					minX = pos.x;
				}
				if (minX != 0 && !isInBuilding(pos)) {
					maxX = pos.x;
				}
			}
			if (lastLine.a > 0) {
				minY = lastLine.getYFromX(minX);
				maxY = lastLine.getYFromX(maxX);
			} else {
				minY = lastLine.getYFromX(maxX);
				maxY = lastLine.getYFromX(minX);
			}
			

		}

		public Position getMiddleEffectiveBuilding() {
			return new Position((maxEffectiveWidth - minEffectiveWidth) / 2,
					(minEffectiveHeight - minEffectiveHeight) / 2);
		}

	}

	/**
	 * Tells if position over, on or under y = a*x + b
	 *
	 */
	public static class LineDelimitation {
		int a;
		int b;
		RelativePosition relativePosition;

		public LineDelimitation(Position oldPos, Position newPos, String bombDir) {
			this.a = getA(oldPos, newPos);
			this.b = getB(oldPos, newPos);
			this.relativePosition = getRelativePosition(oldPos, newPos, bombDir);
		}

		public int getYFromX(int x) {
			if (RelativePosition.OVER.equals(relativePosition)) {
				return a * x + b + 1;
			} else if (RelativePosition.UNDER.equals(relativePosition)) {
				return a * x + b - 1;
			} else if (RelativePosition.ON.equals(relativePosition)) {
				return a * x + b;
			}
			throw new IllegalArgumentException();

		}

		public boolean isInLineDelimitation(Position pos) {
			if (RelativePosition.OVER.equals(relativePosition)) {
				return pos.y > pos.x * a + b;
			} else if (RelativePosition.UNDER.equals(relativePosition)) {
				return pos.y < pos.x * a + b;
			} else if (RelativePosition.ON.equals(relativePosition)) {
				return pos.y == pos.x * a + b;
			}
			throw new IllegalArgumentException();
		}

		public int getA(Position pos1, Position pos2) {
			if (pos2.y - pos1.y != 0) {
				return -(pos2.x - pos1.x) / (pos2.y - pos1.y);
			}
			throw new IllegalArgumentException("Ligne x = qqch detectée");
		}

		public int getB(Position pos1, Position pos2) {
			return (pos2.y + pos1.y) / 2 - a * (pos2.x + pos1.x) / 2;
		}

		public RelativePosition getRelativePosition(Position oldPos, Position newPos, String bombDir) {
			if ("SAME".equals(bombDir)) {
				return RelativePosition.ON;
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

	}

	public static enum RelativePosition {
		OVER, UNDER, ON;
	}

}
