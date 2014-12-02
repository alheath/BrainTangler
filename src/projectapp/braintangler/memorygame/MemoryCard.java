package projectapp.braintangler.memorygame;

import projectapp.braintangler.R;

public class MemoryCard {
	public static final int FACE_DOWN = 0;
	public static final int FACE_UP = 1;
	public static final int SOLVED = 2;
	
	private int state;
	private int face_up_img_id;
	private int face_down_img_id;
	private int row;
	private int col;
	
	public MemoryCard(int img_id, int row, int col) {
		this.state = FACE_DOWN;
		this.face_up_img_id = img_id;
		this.face_down_img_id = 18; //18 -> card back
		this.row = row;
		this.col = col;
	}
	
	public int getState() { return state; }
	public void setState(int state) { this.state = state; }

	public int getCurrentImage() { return state==FACE_DOWN ? face_down_img_id : face_up_img_id; }
	
	public int getFaceUpImage() { return face_up_img_id; };
	public void setFaceUpImage(int img_id) { this.face_up_img_id = img_id; }
	
	public int getRow() { return row; };
	public void setRow(int row) { this.row = row; }

	public int getCol() { return col; };
	public void setCol(int col) { this.col = col; }
	
	public boolean equals(MemoryCard mc) { return mc.getFaceUpImage() == this.getFaceUpImage(); }

}
