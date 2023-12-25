package com.mozzartbet.hackaton.connect4.model;

public abstract class Player {

	protected volatile int move;

	public final int getMove() {
		return move;
	}

	public abstract void configure(long timeoutMillis);

	public abstract void move();

	public abstract void stop();
	
	public abstract void opponentMove(int move);
	
	public abstract void finished(int winner);

	
	@Override
	public String toString() {
		return "[" + getClass().getName() + "]";
	}
	
}
