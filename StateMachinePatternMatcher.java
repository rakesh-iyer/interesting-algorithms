import java.util.*;

class StateMachinePatternMatcher {
    State start = new State(null, "");
    static class State {
        Map<Character, State> nextStateMap;
        String prefix;
        boolean last;

        boolean isLast() {
            return last;
        }

        void setLast(boolean last) {
            this.last = last;
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
            list.add(nextState);

            currState.nextStateMap.put(pattern.charAt(i), nextState);
            currState = nextState;
        }
        State last = list.get(list.size()-1);
        last.setLast(true);

        for (State state : list) {
            String prefix = state.prefix;
            for (char c = 0; c < 26; c++) {
                char ch = (char)('a' + c);
                String othPrefix = prefix.substring(1, prefix.length()) + String.valueOf(ch);

                // run new prefix through start of state machine to get destination state.
                if (state.nextStateMap.get(ch) == start) {
                    State dest = getDestinationState(othPrefix);
                    state.nextStateMap.put(ch, dest);
                }
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
            currState = currState.nextStateMap.get(c);
            if (currState.isLast()) {
                return true;
            }
        }

        return false;
    }

    public static void main(String args[]) {
        StateMachinePatternMatcher matcher = new StateMachinePatternMatcher();

        matcher.buildStateMachine("abc");
        System.out.println(matcher.matchPattern("ababc"));
    }
}
