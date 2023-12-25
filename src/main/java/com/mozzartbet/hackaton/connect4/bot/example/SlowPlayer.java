package com.mozzartbet.hackaton.connect4.bot.example;

import static com.mozzartbet.hackaton.connect4.model.GameConsts.*;
import static com.mozzartbet.hackaton.connect4.util.ThreadHelper.*;

import java.util.concurrent.ThreadLocalRandom;

import com.mozzartbet.hackaton.connect4.model.Player;

public class SlowPlayer extends Player {

	long timeoutMillis = 0;
	int opponentMoves = 0;

	@Override
	public void configure(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	@Override
	public void move() {
		move = ThreadLocalRandom.current().nextInt(COLUMNS);
		
		if (opponentMoves % 2 == 0) {
			sleep(3 * timeoutMillis);
		}
	}

	@Override
	public void stop() {
	}

	@Override
	public void opponentMove(int move) {
		opponentMoves++;
	}

	@Override
	public void finished(int winner) {
	}

}
