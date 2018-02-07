package queens4;

import java.util.function.Consumer;

public class Position4 {
	
	public int x;
	public int y;
	
	public Position4(int x, int y) {
		this.x = x;
		this.y = y;		
	}
	
	public Position4(Position4 pos) {
		this.x = pos.x;
		this.y = pos.y;		
	}
	
	public void applyBiConsumer(Consumer<Position4> consumer) {
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
