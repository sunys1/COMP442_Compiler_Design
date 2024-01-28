package LexicalAnalyzer;

import java.util.HashMap;

public class State {
	private int sid;
	private HashMap<String, Integer> transitions;
	private boolean isFinal;
	private String tokenName;
	private boolean isBacktrack;
	
	public State(int sid, HashMap<String, Integer> transitions) {
		this.sid = sid;
		this.transitions = new HashMap<String, Integer>(transitions);
	}
	public State(int sid, HashMap<String, Integer> transitions, boolean isFinal, String tokenName, boolean isBacktrack) {
		this.sid = sid;
		this.transitions = new HashMap<String, Integer>(transitions);
		this.isFinal = isFinal;
		this.tokenName = tokenName;
		this.isBacktrack = isBacktrack;
	}
	
	public int getStateId() {
		return sid;
	}
	
	public void setStateId(int sid) {
		this.sid = sid;
	}
	
	public HashMap<String, Integer> getTransitions(){
		return new HashMap<String, Integer>(transitions);
	}
	
	public void setTransitions(HashMap<String, Integer> transitions) {
		this.transitions = new HashMap<String, Integer>(transitions);
	}

	public boolean getIsFinal() {
		return isFinal;
	}

	public void setIsFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public String getTokenName() {
		return tokenName;
	}

	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	public boolean getIsBacktrack() {
		return isBacktrack;
	}

	public void setIsBacktrack(boolean isBacktrack) {
		this.isBacktrack = isBacktrack;
	}	
	
}
