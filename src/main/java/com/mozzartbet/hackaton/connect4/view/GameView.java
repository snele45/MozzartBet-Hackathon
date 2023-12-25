package com.mozzartbet.hackaton.connect4.view;

import static java.awt.BorderLayout.*;
import static javax.swing.JFrame.*;
import static javax.swing.JOptionPane.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.mozzartbet.hackaton.connect4.bot.example.DummyPlayer;
import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.Player;
import com.mozzartbet.hackaton.connect4.Michigan.Bot;

public class GameView {

	final GameBoard board;
	final DisplayedBoard displayedBoard;

	final Player computer = new Bot();

	//

	JButton resetButton;
	JButton undoButton;
	JButton compMoveButton;

	//

	public GameView() {
		computer.configure(1000);
		
		board = new GameBoard();
		displayedBoard = new DisplayedBoard(board);

		final JFrame frame = new JFrame("Connect Four");
		frame.setLocation(310, 130);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

		//

		JPanel topRow = new JPanel();
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.reset();
				computer.configure(1000);
				counter = 1;
				displayedBoard.refresh();
			}
		});
		topRow.add(resetButton);

		undoButton = new JButton("Undo");
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (board.getCountersPlaced() > 0) {
					board.undoMove();
					toggleCounter();
				}
				displayedBoard.refresh();
			}
		});
		topRow.add(undoButton);

		//

		compMoveButton = new JButton("Computer");
		compMoveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				computer.move();
				int move = computer.getMove();

				board.placeCounter(move, toggleCounter());
				displayedBoard.refresh();

				toggleButtons(false);
				checkGameStatus();
			}
		});
		topRow.add(compMoveButton);

		JPanel optionButtons = new JPanel();
		optionButtons.setLayout(new BorderLayout());
		optionButtons.add(topRow, NORTH);

		//

		displayedBoard.addColumnHandler(move -> {
			if (compMoveButton.isEnabled() && board.getCountersPlaced() > 0) {
				return;
			}

			board.placeCounter(move, toggleCounter());
			computer.opponentMove(move);
			
			displayedBoard.refresh();

			toggleButtons(true);
			checkGameStatus();
		});

		displayedBoard.setPreferredSize(new Dimension(598, 516));

		displayedBoard.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				displayedBoard.repaint();
			}
		});

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		mainPanel.add(displayedBoard, CENTER);
		mainPanel.add(optionButtons, SOUTH);

		frame.add(mainPanel, CENTER);

		frame.pack();
		frame.setVisible(true);
	}

	int counter = 1;

	private int toggleCounter() {
		int result = counter;
		counter = (counter == 1) ? 2 : 1;
		return result;
	}

	void checkGameStatus() {
		if (board.isGameOver()) {
			displayGameOverMessage(board.getWinner());

			board.reset();
			displayedBoard.refresh();
		}
	}

	void displayGameOverMessage(int winner) {
		showMessageDialog(null, (winner == 0) ? "DRAW" : (winner == 1) ? "Red wins!" : "Yellow wins!",
				"Game Over", INFORMATION_MESSAGE);
	}

	private void toggleButtons(boolean comp) {
		compMoveButton.setEnabled(comp);
		displayedBoard.setEnabled(!comp);
	}

	//

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GameView();
			}
		});
	}

}
