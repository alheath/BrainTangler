package projectapp.braintangler.memorygame;

public class GameGrid {
	
	private final int rows;
	private final int cols;
	private final MemoryCard[][] data;
	
	private int selected_row;
	private int selected_col;
	
	public static final GameGrid BLANK = new GameGrid(0, 0);
	
	public GameGrid(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.data = new MemoryCard[rows][cols];
		selected_row = -1;
		selected_col = -1;
	}
	
	public int getRows() { return rows; }
	public int getCols() { return cols; }
	
	public MemoryCard getCardAt(int row, int col) {
		if ((row >= 0 && row < rows) && (col >= 0 && col < cols)) { return data[row][col]; }
		return null;
	}
	
	public void setCardAt(MemoryCard card, int row, int col) {
		if ((row >= 0 && row < rows) && (col >= 0 && col < cols)) {
			card.setRow(row);
			card.setCol(col);
			data[row][col] = card;
		}
	}
	
	public void selectCard(MemoryCard card) {
		card.setState(MemoryCard.FACE_UP);
		this.selected_row = card.getRow();
		this.selected_col = card.getCol();
	}

	public void clearSelectedCard() {
		MemoryCard mCard = getSelectedCard();
		if (mCard != null) {
			mCard.setState(MemoryCard.FACE_DOWN);
		}
		this.selected_row = -1;
		this.selected_col = -1;
	}
	
	public MemoryCard getSelectedCard() {
		return getCardAt(selected_row, selected_col);
	}
}
