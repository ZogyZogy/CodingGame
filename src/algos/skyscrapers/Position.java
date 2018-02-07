package algos.skyscrapers;

public class Position {

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
