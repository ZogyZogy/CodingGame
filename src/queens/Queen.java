package queens;

public class Queen {
	
	private int horizontalPosition;
	private int verticalPosition;

	public Queen(int horizontalPosition, int verticalPosition) {
		this.horizontalPosition = horizontalPosition;
		this.verticalPosition = verticalPosition;
	}
	
	public int getHorizontalPosition() {
		return horizontalPosition;
	}

	public int getVerticalPosition() {
		return verticalPosition;
	}
	
	@Override
	public String toString() {
		return ("Queen : horizontal Position = " + (horizontalPosition + 1) + " ; vertical Position = " + (verticalPosition + 1) + "\n");
	}

	
}
