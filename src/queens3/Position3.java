package queens3;

import java.util.function.Consumer;

public class Position3 {
	
	public int x;
	public int y;
	
	public Position3(int x, int y) {
		this.x = x;
		this.y = y;		
	}
	
	public Position3(Position3 pos) {
		this.x = pos.x;
		this.y = pos.y;		
	}
	
	public void applyBiConsumer(Consumer<Position3> consumer) {
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
