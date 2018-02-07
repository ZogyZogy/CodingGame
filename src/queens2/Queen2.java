package queens2;

public class Queen2 {
	
	private Position2 position;

	public Queen2(Position2 position) {
		this.position = position;
	}
	
	public Position2 getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return "Queen : " + position.toString();
	}

	
}
