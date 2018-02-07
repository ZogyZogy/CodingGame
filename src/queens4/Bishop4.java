package queens4;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Bishop4 {

	private Position4 position; // would be cool if it was immutable
	
	public List<Consumer<Position4>> movePossibilities = getMovePossibilities();

	public Bishop4(Position4 position) {
		this.position = position;
	}

	private List<Consumer<Position4>> getMovePossibilities() {
		List<Consumer<Position4>> movePossibilities = new ArrayList<>();
		movePossibilities.add(p -> {p.x++; p.y--;}); // anti-diagonal
		movePossibilities.add(p -> {p.x--; p.y--;}); // diagonal
		movePossibilities.add(p -> {p.x++; p.y++;}); // diagonal
		movePossibilities.add(p -> {p.x--; p.y++;}); // anti-diagonal
		return movePossibilities;
	}

	public Position4 getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		return "Bishop : " + position.toString();
	}
	
	

}
