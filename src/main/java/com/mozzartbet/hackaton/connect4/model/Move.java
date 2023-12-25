package com.mozzartbet.hackaton.connect4.model;

import static com.mozzartbet.hackaton.connect4.model.GameConsts.*;

public class Move {

	final int counter;
	final int row;
	final int col;

	public Move(int counter, int row, int col) {
		if (!(counter == 1 || counter == 2)) {
			throw new IllegalArgumentException("Invalid counter: " + counter);
		}
		this.counter = counter;
		
		if (row < 0 || row >= ROWS) {
			throw new IllegalArgumentException("Invalid row: " + row);
		}
		this.row = row;
		
		if (col < 0 || col >= COLUMNS) {
			throw new IllegalArgumentException("Invalid col: " + col);
		}
		this.col = col;
	}
	
	public int getCounter() {
		return counter;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	@Override
	public String toString() {
		return String.format("Move [counter=%s, row=%s, col=%s]", counter, row, col);
	}

}
