package com.mozzartbet.hackaton.connect4.view;

import static com.mozzartbet.hackaton.connect4.model.GameConsts.*;
import static java.awt.RenderingHints.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mozzartbet.hackaton.connect4.model.GameBoard;

public class DisplayedBoard extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Circle RED_CIRCLE = new Circle(new Color(255, 0, 0));
	private static final Circle YELLOW_CIRCLE = new Circle(new Color(255, 255, 0));

	private final Field[][] fields = new Field[ROWS][COLUMNS];
	
	private GameBoard board;

	//
	
	public DisplayedBoard(GameBoard board) {
		this.setLayout(new GridLayout(ROWS, COLUMNS));
		for (int i = 0; i < fields.length; i++) {
			for (int j = 0; j < fields[i].length; j++) {
				add(fields[i][j] = new Field(i, j));
				fields[i][j].setEnabled(false);
			}
		}
		this.board = board;
	}
	
	public void setBoard(GameBoard board) {
		this.board = board;
		refresh();
	}

	public void refresh() {
		if (board != null) {
			for (int i = 0; i < fields.length; i++) {
				for (int j = 0; j < fields[i].length; j++) {
					if (board.getBoard()[i][j] == 1) {
						fields[i][j].setCircle(RED_CIRCLE);
						fields[i][j].repaint();
					} else if (board.getBoard()[i][j] == 2) {
						fields[i][j].setCircle(YELLOW_CIRCLE);
						fields[i][j].repaint();
					} else {
						fields[i][j].setCircle(null);
						fields[i][j].repaint();
					}
				}

			}
		}
	}
	
	Consumer<Integer> columnHandler;
	
	void addColumnHandler(Consumer<Integer> columnHandler) {
		this.columnHandler = columnHandler;
	}
	
	//
	
	static class Circle {
		final Color color;

		public Circle(Color color) {
			this.color = color;
		}

		public void draw(Graphics2D g2d, int w, int h) {
			g2d.setColor(color);
			drawCenteredCircle(g2d, w / 2, h / 2, 2 * h / 3);
		}

		private void drawCenteredCircle(Graphics2D g, int x, int y, int r) {
			x = x - (r / 2);
			y = y - (r / 2);
			g.fillOval(x, y, r, r);
		}
	}

	class Field extends JTextField {
		private static final long serialVersionUID = 1L;

		private Circle c;

		public Field(int row, int col) {
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (columnHandler != null) {
						columnHandler.accept(col);
					}
				}
			});
		}
		
		public void setCircle(Circle c) {
			this.c = c;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			if (c != null) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
				c.draw(g2d, getWidth(), getHeight());
			}
		}

	}

}
