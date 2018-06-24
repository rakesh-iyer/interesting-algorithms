import java.util.*;

class ShuntingYard {

	static boolean anyMath(char ch) {
		return anyMult(ch) || anyAdd(ch);
	}

	static boolean anyMult(char ch) {
		return ch == '*' || ch == '/';
	}

	static boolean anyAdd(char ch) {
		return ch == '+' || ch == '-';
	}

	static boolean anyParens(char ch) {
		return ch == '(' || ch == ')';
	}

	static String infixToPostfix(String expr) {
		Deque<Character> operandQueue = new LinkedList<>();
		Deque<Character> operatorStack = new LinkedList<>();

		char[] arr = expr.toCharArray();

		for (char ch : arr) {
			if (anyMath(ch) || anyParens(ch)) {
				switch (ch) {
					case '+' :
					case '-' :
						while (operatorStack.peek() != null && anyMath(operatorStack.peek())) {
							Character next = operatorStack.pop();
							operandQueue.add(next);
						}
						operatorStack.push(ch);
						break;
					case '*':
					case '/':
						while (operatorStack.peek() != null && anyMult(operatorStack.peek())) {
							Character next = operatorStack.pop();
							operandQueue.add(next);
						}
						operatorStack.push(ch);
						break;
					case '(':
						operatorStack.push(ch);
						break;
					case ')':
						while (operatorStack.peek() != '(') {
							Character next = operatorStack.pop();
							operandQueue.add(next);
						}
						operatorStack.pop();
						break;
				}
			} else {
				operandQueue.add(ch);
			}
		}

		Character ch;
		while ((ch = operatorStack.poll()) != null) {
			operandQueue.add(ch);
		}

		StringBuilder sb = new StringBuilder();
		while ((ch = operandQueue.poll()) != null) {
			sb.append(ch);
		}

		return sb.toString();	
	}

	public static void main(String args[]) {
		String postfix = infixToPostfix("(a+b)*c");

		System.out.println(postfix);
	}
}
