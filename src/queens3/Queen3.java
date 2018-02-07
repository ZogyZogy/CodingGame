package queens3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import queens2.Position2;


/** 
 * Note : the two fields are mutable and have getters, so there's actually no use 
 * to make them private since everyone can modify them.
 * 
 */
public class Queen3 {
	
	private Position3 position; // would be cool if it was immutable
	
	public List<Consumer<Position3>> movePossibilities = getMovePossibilities();

	public Queen3(Position3 position) {
		this.position = position;
	}
	
	public Position3 getPosition() {
		return position;
	}

	// Note : no use for y-- move possibilities and horizontal
	private List<Consumer<Position3>> getMovePossibilities() {
		List<Consumer<Position3>> movePossibilities = new ArrayList<>();
		movePossibilities.add(p -> p.y++); // vertical
		movePossibilities.add(p -> {p.x++; p.y++;}); // diagonal
		movePossibilities.add(p -> {p.x--; p.y++;}); // anti-diagonal
		return movePossibilities;
	}
	
	// Je veux passer le comportement de dire d'aller chercher y+1, et de dérouler tous les x
	// Je sais pas comment faire ...
	
	@Override
	public String toString() {
		return "Queen : " + position.toString();
	}
	
	
	
}
