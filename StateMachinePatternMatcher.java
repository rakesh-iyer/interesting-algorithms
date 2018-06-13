import java.util.*;

class StateMachinePatternMatcher {
	State start = new State(null, "");
	static class State {
		Map<Character, State> nextStateMap;
		String prefix;
		boolean isFinal;

		boolean isFinal() {
			return isFinal;
		}

		State(State start, String prefix) {
			if (start == null) {
				start = this;
			}
			nextStateMap = new HashMap<>();
			// TODO: optimize the trie into a graph that is discovered based on input.
			for (char c = 0; c < 26; c++) {
				nextStateMap.put((char)('a' + c), start);
			}
			this.prefix = prefix;
		}
	}

	void buildStateMachine(String pattern) {
		List<State> list = new ArrayList<>();
		State currState = start;

		for (int i = 0; i < pattern.length(); i++) {
			State nextState = new State(start, pattern.substring(0, i+1));
			currState.nextStateMap.put(pattern.charAt(i), nextState);
			currState = nextState;
			list.add(nextState);
		}

		for (State state : list) {
			for (char c = 0; c < 26; c++) {
				String prefix = state.prefix;
				String othPrefix = prefix.substring(1, prefix.length()) + String.valueOf('a' + c);
			
				// run new prefix through start of state machine to get destination state.
				State dest = getDestinationState(othPrefix);
				state.nextStateMap.put((char)('a' + c), dest);
			}
		}
	}

	State getDestinationState(String prefix) {
		State currState = start;
		State nextState = null;

		for (char c : prefix.toCharArray()) {
			nextState = currState.nextStateMap.get(c);
		}

		return nextState;
	}

	boolean matchPattern(String src) {
		State currState = start;

		for (char c : src.toCharArray()) {
			State nextState = currState.nextStateMap.get(c);
			if (nextState.isFinal()) {
				return true;
			}
		}

		return false;
	}

	public static void main(String args[]) {
		StateMachinePatternMatcher matcher = new StateMachinePatternMatcher();

		matcher.buildStateMachine("abc");
		System.out.println(matcher.matchPattern("abcd"));
	}
}
