package queens2;

import java.util.function.Consumer;

public class Position2 {
	
	public int x;
	public int y;
	
	public Position2(int x, int y) {
		this.x = x;
		this.y = y;		
	}
	
	public Position2(Position2 pos) {
		this.x = pos.x;
		this.y = pos.y;		
	}
	
	public void applyBiConsumer(Consumer<Position2> consumer) {
		consumer.accept(this);
	}

	@Override
	public String toString() {
		return ("horizontal Position = " + (x + 1) + " ; vertical Position = " + (y + 1) + "\n");
	}
	
	public boolean isWithinBoard(int boardSize) {
		return x < boardSize && y < boardSize && x >= 0 && y >= 0;
	}

}
