package com.mozzartbet.hackaton.connect4.view;

import java.util.List;

public interface MatchProvider {

	public List<MatchInfo> availableMatches();
	
	public MatchInfo refresh(MatchInfo info);

}
