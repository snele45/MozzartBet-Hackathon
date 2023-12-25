package com.mozzartbet.hackaton.connect4.Michigan;

import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.GameConsts;
import com.mozzartbet.hackaton.connect4.model.Player;

import java.util.*;

public class Bot extends Player {

    protected final int HUMAN = 1;
    protected final int BOT = 2;
    private GameBoard gameBoard = new GameBoard();

    private boolean isMoveValid(int col){
        return gameBoard.getBoard()[0][col] == 0;
    }


    private static void dropPiece(int[][] board, int col, int player) {
        for (int row = GameConsts.ROWS - 1; row >= 0; row--) {
            if (board[row][col] == 0) {
                board[row][col] = player;
                return;
            }
        }
    }
    private boolean hasHorizontalSeries(int[][] board, int player) {
        for (int row = 0; row < GameConsts.ROWS; row++) {
            for (int col = 0; col < GameConsts.COLUMNS - 3; col++) {
                if (board[row][col] == player &&
                        board[row][col + 1] == player &&
                        board[row][col + 2] == player &&
                        board[row][col + 3] == player) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasVerticalSeries(int[][] board, int player) {
        for (int col = 0; col < GameConsts.COLUMNS; col++) {
            for (int row = 0; row < GameConsts.ROWS - 3; row++) {
                if (board[row][col] == player &&
                        board[row + 1][col] == player &&
                        board[row + 2][col] == player &&
                        board[row + 3][col] == player) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasDiagonalSeries(int[][] board, int player) {
        // Check positive diagonal
        for (int row = 0; row < GameConsts.ROWS - 3; row++) {
            for (int col = 0; col < GameConsts.COLUMNS - 3; col++) {
                if (board[row][col] == player &&
                        board[row + 1][col + 1] == player &&
                        board[row + 2][col + 2] == player &&
                        board[row + 3][col + 3] == player) {
                    return true;
                }
            }
        }

        // Check negative diagonal
        for (int row = 3; row < GameConsts.ROWS; row++) {
            for (int col = 0; col < GameConsts.COLUMNS - 3; col++) {
                if (board[row][col] == player &&
                        board[row - 1][col + 1] == player &&
                        board[row - 2][col + 2] == player &&
                        board[row - 3][col + 3] == player) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<int[][]> getNextStates(int[][] board, int player) {
        List<int[][]> states = new ArrayList<>();
        for (int col = 0; col < GameConsts.COLUMNS ; col++) {
            if (isMoveValid(col)) {
                int[][] nextBoard = copyBoard(board);
                dropPiece(nextBoard, col, player);
                states.add(nextBoard);
            }
        }
        return states;
    }

    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[GameConsts.ROWS][GameConsts.COLUMNS];
        for (int i = 0; i < GameConsts.ROWS; i++) {
            copy[i] = Arrays.copyOf(board[i], GameConsts.COLUMNS);
        }
        return copy;
    }

    private static int getNextOpenRow(int[][] board, int col) {
        for (int row = GameConsts.ROWS - 1; row >= 0; row--) {
            if (board[row][col] == 0) {
                return row;
            }
        }
        return -1; // Should not reach here if the move is valid
    }

    private int getBestMove(int player) {
        Set<Integer> validLocations = getValidLocations(gameBoard.getBoard());
        int bestScore = Integer.MIN_VALUE;
        Set<Integer> bestMoves = new HashSet<>();

        // Attempt to find the optimal move, avoiding opening in the corners
        Set<Integer> optimalMoves = new HashSet<>(validLocations);
        optimalMoves.removeAll(Arrays.asList(0, GameConsts.COLUMNS - 1));

        if (!optimalMoves.isEmpty()) {
            bestMoves.add(getRandomElement(optimalMoves));
        } else {
            bestMoves.add(getRandomElement(validLocations));
        }

        for (int col : validLocations) {
            int row = getNextOpenRow(gameBoard.getBoard(), col);
            int[][] tempBoard = copyBoard(gameBoard.getBoard());
            dropPiece(tempBoard, col, player);
            int score = min(tempBoard, Integer.MIN_VALUE, Integer.MAX_VALUE, 3, 0, 0, 0);

            if (score > bestScore) {
                bestMoves.clear();
                bestMoves.add(col);
                bestScore = score;
            } else if (score == bestScore) {
                bestMoves.add(col);
            }
        }

        // Random selection among the best moves
        return getRandomElement(bestMoves);
    }


    private int min(int[][] currentState, int alpha, int beta, int depth, int seriesDepth, int seriesCount, int winCount) {
        if (isTerminalNode(currentState) || depth == 0) {
            return evaluateBoard(currentState, HUMAN, depth, seriesDepth, seriesCount, winCount);
        }

        int minEval = Integer.MAX_VALUE;

        for (int[][] nextState : getNextStates(currentState, HUMAN)) {
            int evalVal = max(nextState, alpha, beta, depth - 1, seriesDepth, seriesCount, winCount);
            minEval = Math.min(minEval, evalVal);
            beta = Math.min(beta, evalVal);
            if (beta <= alpha) {
                break;
            }
        }

        return minEval;
    }

    private int max(int[][] currentState, int alpha, int beta, int depth, int seriesDepth, int seriesCount, int winCount) {
        if (isTerminalNode(currentState) || depth == 0) {
            return evaluateBoard(currentState, BOT, depth, seriesDepth, seriesCount, winCount);
        }

        int maxEval = Integer.MIN_VALUE;

        for (int[][] nextState : getNextStates(currentState, BOT)) {
            int evalVal = min(nextState, alpha, beta, depth - 1, seriesDepth, seriesCount, winCount);
            maxEval = Math.max(maxEval, evalVal);
            alpha = Math.max(alpha, evalVal);
            if (beta <= alpha) {
                break;
            }
        }

        return maxEval;
    }


    private int evaluateBoard(int[][] board, int player, int depth, int seriesDepth, int seriesCount, int winCount) {

        int score = 0;

        // Check for winning positions
        if (winningMove(board, HUMAN)) {
            return -1000;
        } else if (winningMove(board, BOT)) {
            return 1000;
        }

        score += depth;

        score += seriesCount * 10;

        score += seriesDepth;

        score += winCount * 100;

        score += evaluateClosing(board, player);

        // Heuristic 1: Prevent opponent's winning move
        for (int col : getValidLocations(board)) {
            int row = getNextOpenRow(board, col);
            int[][] tempBoard = copyBoard(board);
            dropPiece(tempBoard, col, player);
            if (winningMove(tempBoard, player)) {
                score -= 800;  // Priority on preventing opponent's winning move
            }
        }
        int[][] tempBoard = copyBoard(board);

        // Heuristic 2: Block opponent's horizontal series
        if (hasHorizontalSeries(board, HUMAN)) {
            score -= 500;  // Priority on blocking opponent's horizontal series
        }

        // Heuristic 3: Block opponent's vertical series
        if (hasVerticalSeries(board, HUMAN)) {
            score -= 1000;  // Priority on blocking opponent's vertical series
        }

        // Heuristic 4: Block opponent's diagonal series
        if (hasDiagonalSeries(board, HUMAN)) {
            score -= 300;  // Priority on blocking opponent's diagonal series
        }

        return score;
    }


    private int evaluateClosing(int[][] board, int player) {

        Set<Integer> validLocations = getValidLocations(board);

        // Heuristic 1: Winning moves
        for (int col : validLocations) {
            int row = getNextOpenRow(board, col);
            int[][] tempBoard = copyBoard(board);
            dropPiece(tempBoard, col, BOT);
            if (winningMove(tempBoard, HUMAN)) {
                return 1000;  // Priority on winning move
            }
        }

        // Heuristic 2: Blocking the opponent
        for (int col : validLocations) {
            int row = getNextOpenRow(board, col);
            int[][] tempBoard = copyBoard(board);
            dropPiece(tempBoard, col, BOT);
            if (winningMove(tempBoard, HUMAN)) {
                return 900;  // Priority on blocking the opponent
            }
        }

        // Heuristic 3: Attacks with more options
        int maxPlayerOptions = 0;
        for (int col : validLocations) {
            int row = getNextOpenRow(board, col);
            int[][] tempBoard = copyBoard(board);
            dropPiece(tempBoard, col, HUMAN);
            int playerOptions = getValidLocations(tempBoard).size();
            if (playerOptions > maxPlayerOptions) {
                maxPlayerOptions = playerOptions;
            }
        }

        // Heuristic 4: Blocking opponent's options
        int maxOpponentOptions = 0;
        for (int col : validLocations) {
            int row = getNextOpenRow(board, col);
            int[][] tempBoard = copyBoard(board);
            dropPiece(tempBoard, col, HUMAN);
            int opponentOptions = getValidLocations(tempBoard).size();
            if (opponentOptions > maxOpponentOptions) {
                maxOpponentOptions = opponentOptions;
            }
        }


        return maxPlayerOptions * 10 - maxOpponentOptions * 5;
    }


    private boolean isTerminalNode(int[][] board) {
        return winningMove(board, HUMAN) || winningMove(board, BOT) || getValidLocations(board).isEmpty();
    }

    private boolean winningMove(int[][] board, int player) {
        for (int col = 0; col < GameConsts.COLUMNS - 3; col++) {
            for (int row = 0; row < GameConsts.ROWS; row++) {
                if (board[row][col] == player &&
                        board[row][col + 1] == player &&
                        board[row][col + 2] == player &&
                        board[row][col + 3] == player) {
                    return true;
                }
            }
        }

        // Check vertical
        for (int col = 0; col < GameConsts.COLUMNS ; col++) {
            for (int row = 0; row < GameConsts.ROWS - 3; row++) {
                if (board[row][col] == player &&
                        board[row + 1][col] == player &&
                        board[row + 2][col] == player &&
                        board[row + 3][col] == player) {
                    return true;
                }
            }
        }

        // Check positive diagonal
        for (int col = 0; col < GameConsts.COLUMNS - 3; col++) {
            for (int row = 0; row < GameConsts.ROWS - 3; row++) {
                if (board[row][col] == player &&
                        board[row + 1][col + 1] == player &&
                        board[row + 2][col + 2] == player &&
                        board[row + 3][col + 3] == player) {
                    return true;
                }
            }
        }

        // Check negative diagonal
        for (int col = 0; col < GameConsts.COLUMNS - 3; col++) {
            for (int row = 3; row < GameConsts.ROWS; row++) {
                if (board[row][col] == player &&
                        board[row - 1][col + 1] == player &&
                        board[row - 2][col + 2] == player &&
                        board[row - 3][col + 3] == player) {
                    return true;
                }
            }
        }

        return false;
    }


    private Set<Integer> getValidLocations(int[][] board) {
        Set<Integer> validLocations = new HashSet<>();
        for (int col = 0; col < GameConsts.COLUMNS; col++) {
            if (isMoveValid(col)) {
                validLocations.add(col);
            }
        }
        return validLocations;
    }


    private static int getRandomElement(Set<Integer> set) {
        int size = set.size();
        int item = new Random().nextInt(size);
        int i = 0;
        for (int col : set) {
            if (i == item) {
                return col;
            }
            i++;
        }
        return 1; // Should not reach here
    }

    @Override
    public void configure(long timeoutMillis) {

    }

    @Override
    public void move() {
        move = getBestMove(BOT);
        gameBoard.placeCounter(move, BOT);
    }

    @Override
    public void stop() {

    }

    @Override
    public void opponentMove(int move) {
        gameBoard.placeCounter(move, HUMAN);
    }

    @Override
    public void finished(int winner) {

    }
}
