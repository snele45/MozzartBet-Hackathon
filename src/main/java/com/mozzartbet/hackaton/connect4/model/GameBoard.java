package com.mozzartbet.hackaton.connect4.model;

import static com.mozzartbet.hackaton.connect4.model.Direction.*;
import static com.mozzartbet.hackaton.connect4.model.GameConsts.*;

import java.util.Stack;

public class GameBoard {

	protected final int maxRows = GameConsts.ROWS;
	protected final int maxCols = GameConsts.COLUMNS;

	protected final int[][] board;

	protected Stack<Move> moves;
	protected int countersPlaced;
	protected int lastCounterPlaced;

	protected boolean gameOver;
	protected int winner;

	public GameBoard() {
		board = new int[maxRows][maxCols];
		initializeBoard();

		moves = new Stack<>();
		countersPlaced = 0;
		lastCounterPlaced = 2;

		gameOver = false;
		winner = 0;
	}

	public GameBoard deepCopy() {
		GameBoard copy = new GameBoard();
		copyBoard(copy.board, this.board);

		copyMoves(copy.moves, this.moves);
		copy.countersPlaced = this.countersPlaced;
		copy.lastCounterPlaced = this.lastCounterPlaced;

		copy.gameOver = this.gameOver;
		copy.winner = this.winner;
		return copy;
	}

	private void initializeBoard() {
		for (int i = 0; i < maxRows; i++) {
			for (int j = 0; j < maxCols; j++) {
				board[i][j] = 0;
			}
		}
	}

	private void copyBoard(int[][] destination, int[][] source) {
		for (int row = 0; row < maxRows; row++) {
			for (int col = 0; col < maxCols; col++) {
				destination[row][col] = source[row][col];
			}
		}
	}

	private void copyMoves(Stack<Move> destination, Stack<Move> source) {
		for (Move move : source) {
			destination.add(new Move(move.getCounter(), move.getRow(), move.getCol()));
		}
	}

	//

	public void reset() {
		gameOver = false;
		winner = 0;
		countersPlaced = 0;
		lastCounterPlaced = 2;

		moves.removeAllElements();

		initializeBoard();
	}

	public void undoMove() {
		if (countersPlaced > 0) {
			Move m = moves.pop();
			countersPlaced--;

			board[m.getRow()][m.getCol()] = 0;
			gameOver = false;
			winner = 0;

			lastCounterPlaced = 3 - lastCounterPlaced;
		}
	}

	public boolean placeCounter(int col, int counter) {
		if ((col < 0) || (col > maxCols - 1)) {
			return false;
		}

		int row = findDepth(col);
		if (row == -1) {
			for (int i = 0; i < maxCols; i++) {
				row = findDepth(i);
				if (row != -1) {
					col = i;
					break;
				}
			}
			if (row == -1) {
				return false;
			}
		}

		board[row][col] = counter;
		lastCounterPlaced = counter;

		moves.push(new Move(counter, row, col));
		countersPlaced++;

		if (checkWin(counter)) {
			winner = counter;
			gameOver = true;
		} else if (countersPlaced == maxRows * maxCols) {
			gameOver = true;
		}

		return true;
	}

	public int findDepth(int col) {
		int depth = 0;
		while (depth < maxRows && board[depth][col] == 0) {
			depth++;
		}
		return depth - 1;
	}

	private boolean checkWin(int counter) {
		int maxCount = IN_A_ROW - 1;

		int row = moves.peek().getRow();
		int col = moves.peek().getCol();

		int count = countConnected(row + 1, col, S, counter);
		if (count >= maxCount) {
			return true;
		}

		count = countConnected(row, col + 1, E, counter)
				+ countConnected(row, col - 1, W, counter);
		if (count >= maxCount) {
			return true;
		}

		count = countConnected(row - 1, col + 1, NE, counter)
				+ countConnected(row + 1, col - 1, SW, counter);
		if (count >= maxCount) {
			return true;
		}

		count = countConnected(row - 1, col - 1, NW, counter)
				+ countConnected(row + 1, col + 1, SE, counter);
		if (count >= maxCount) {
			return true;
		}

		return false;
	}

	public int countConnected(int row, int col, Direction dir, int counter) {
		if (row < maxRows && row > -1 && col < maxCols && col > -1
				&& board[row][col] == counter) {
			switch (dir) {
			case N:
				return 1 + countConnected(row - 1, col, dir, counter);
			case S:
				return 1 + countConnected(row + 1, col, dir, counter);
			case E:
				return 1 + countConnected(row, col + 1, dir, counter);
			case W:
				return 1 + countConnected(row, col - 1, dir, counter);
			case NE:
				return 1 + countConnected(row - 1, col + 1, dir, counter);
			case NW:
				return 1 + countConnected(row - 1, col - 1, dir, counter);
			case SE:
				return 1 + countConnected(row + 1, col + 1, dir, counter);
			case SW:
				return 1 + countConnected(row + 1, col - 1, dir, counter);
			default:
				return 0;
			}
		} else {
			return 0;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("\n");
		sb.append("   ");
		for (int i = 0; i < maxCols; i++) {
			sb.append(i).append(" ");
		}
		sb.append("\n");

		sb.append("   ");
		for (int i = 0; i < maxCols; i++) {
			sb.append("--");
		}
		sb.append("\n");

		for (int i = 0; i < maxRows; i++) {
			sb.append(Integer.toString(i)).append("| ");
			for (int j = 0; j < maxCols; j++) {
				sb.append(Integer.toString(board[i][j])).append(" ");
			}
			sb.append("\n");
		}

		return sb.toString();
	}

	//

	public int[][] getBoard() {
		return board;
	}

	public int getCountersPlaced() {
		return countersPlaced;
	}

	public int getLastCounterPlaced() {
		return lastCounterPlaced;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public int getWinner() {
		return winner;
	}

	public Stack<Move> getMoves() {
		return moves;
	}

	public Move getLastMove() {
		return moves != null ? moves.peek() : null;
	}
}
