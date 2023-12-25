package com.mozzartbet.hackaton.connect4.bot.example;

import static com.mozzartbet.hackaton.connect4.model.GameConsts.*;
import static com.mozzartbet.hackaton.connect4.util.ThreadHelper.*;

import java.util.concurrent.ThreadLocalRandom;

import com.mozzartbet.hackaton.connect4.model.Player;

public class DummyPlayer extends Player {

	long timeoutMillis = 100;
	int opponentMoves = 0;

	@Override
	public void configure(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	@Override
	public void move() {
		ThreadLocalRandom rnd = ThreadLocalRandom.current();

		move = rnd.nextInt(COLUMNS);

		int half = (int) (timeoutMillis / 2);
		sleep(half + rnd.nextInt(half));
	}

	@Override
	public void stop() {
	}

	@Override
	public void opponentMove(int move) {

	}

	@Override
	public void finished(int winner) {
	}

}
