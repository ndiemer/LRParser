import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;
import java.util.Queue;

public class LRParser {

	Stack<String> stack = new Stack<String>();
	Queue<String> inputQueue = new LinkedList<String>();

	// Table containing all the actions per token and state combination
	private String[][] actionTable = { 
			{ "s5", null, null, "s4", null, null }, 
			{ null, "s6", null, null, null, "acc" },
			{ null, "r2", "s7", null, "r2", "r2" }, 
			{ null, "r4", "r4", null, "r4", "r4" },
			{ "s5", null, null, "s4", null, null }, 
			{ null, "r6", "r6", null, "r6", "r6" },
			{ "s5", null, null, "s4", null, null }, 
			{ "s5", null, null, "s4", null, null },
			{ null, "s6", null, null, "s11", null }, 
			{ null, "r1", "s7", null, "r1", "r1" },
			{ null, "r3", "r3", null, "r3", "r3" }, 
			{ null, "r5", "r5", null, "r5", "r5" } };
	
	// the GoTo table from the example
	private String[][] goToTable = { 
			{ "1", "2", "3" }, 
			{ null, null, null }, 
			{ null, null, null },
			{ null, null, null }, 
			{ "8", "2", "3" }, 
			{ null, null, null }, 
			{ null, "9", "3" }, 
			{ null, null, "10" },
			{ null, null, null }, 
			{ null, null, null }, 
			{ null, null, null }, 
			{ null, null, null } };
	
	// Constructor
	public LRParser(String in) {
		String[] input = in.split("(?<=op)|(?=op)".replace("op", "[$+*()]"));
		for (int i = 0; i < input.length; i++) {
			this.inputQueue.add(input[i]); // adds values to queue for formatting
		}
		this.stack.push("0");
	}
	
	// Checks if the sequential form is valid
	public void leftRightAlgorithm() {
		String top;
		String action = "", currState;
		try {
			
			// Table Header
			System.out.printf("%-55s %-30s %15s", "Stack", "Input", "Action\n");

			do { // Will execute at least once 
				currState = String.format("  %-55s %-30s", stack, inputQueue);
				top = stack.peek();
				stack.push(inputQueue.peek());

				switch (stack.peek()) {
				case "id":
					execAction(actionTable[Integer.parseInt(top)][0]);
					action = String.format("%s", actionTable[Integer.parseInt(top)][0]);
					System.out.printf("%-85s %-15s\n", currState, action);
					break;
				case "+":
					execAction(actionTable[Integer.parseInt(top)][1]);
					action = String.format("%s", actionTable[Integer.parseInt(top)][1]);
					System.out.printf("%-85s %-15s\n", currState, action);
					break;
				case "*":
					execAction(actionTable[Integer.parseInt(top)][2]);
					action = String.format("%s", actionTable[Integer.parseInt(top)][2]);
					System.out.printf("%-85s %-15s\n", currState, action);
					break;
				case "(":
					execAction(actionTable[Integer.parseInt(top)][3]);
					action = String.format("%s", actionTable[Integer.parseInt(top)][3]);
					System.out.printf("%-85s %-15s\n", currState, action);
					break;
				case ")":
					execAction(actionTable[Integer.parseInt(top)][4]);
					action = String.format("%s", actionTable[Integer.parseInt(top)][4]);
					System.out.printf("%-85s %-15s\n", currState, action);
					break;
				case "$":
					execAction(actionTable[Integer.parseInt(top)][5]);
					action = String.format("%s", actionTable[Integer.parseInt(top)][5]);
					System.out.printf("%-85s %-15s\n", currState, action);
					break;
				}
			} while (!inputQueue.isEmpty());// out of the loop when $ or incomplete statement

			if (!action.equals("acc")) {
				System.out.printf("  --DO NOT ACCEPT--");
			} else {
				System.out.println("  --ACCEPT--");
			}
		} catch (Exception e) { // handles unexpected grammar
			System.out.println("--ERROR! IMPROPER GRAMMAR!");
		}
	}

	// accepts a value in the Action table and executes the correct action
	public void execAction(String action) {
		int num;
		;
		if (action.charAt(0) == 's') { // shift action
			stack.push(action.substring(1));
			inputQueue.remove();
		} else if (action.charAt(0) == 'r') { // reduce action
			num = Integer.parseInt(action.substring(1));
			reduce(num);
		} else if (action.equals("acc")) { // accept action
			inputQueue.remove();
		}
	}

	// Checks which grammar rule to use and pops the stack until the correct Value is on top of the stack. 
	// It also pushes a new value from the GoTo table onto the stack. 
	public void reduce(int num) {
		int prevTop;
		switch (num) {
		case 1:
			prevTop = reducePop("E");
			stack.push("E");
			useGoTo(prevTop, stack.peek());
			break;
		case 2:
			prevTop = reducePop("T");
			stack.push("E");
			useGoTo(prevTop, stack.peek());
			break;
		case 3:
			prevTop = reducePop("T");
			stack.push("T");
			useGoTo(prevTop, stack.peek());
			break;
		case 4:
			prevTop = reducePop("F");
			stack.push("T");
			useGoTo(prevTop, stack.peek());
			break;
		case 5:
			prevTop = reducePop("(");
			stack.push("F");
			useGoTo(prevTop, stack.peek());
			break;
		case 6:
			prevTop = reducePop("id");
			stack.push("F");
			useGoTo(prevTop, stack.peek());
			break;
		}
	}
	
	// Pops values off the stack until it finds the correct String
	public int reducePop(String popTo) {
		String prev, curr;
		do {
			prev = stack.pop();
		} while (!prev.equals(popTo));
		curr = stack.peek();
		return Integer.parseInt(curr);
	}
	
	// Pushes GoTo value to stack
	public void useGoTo(int row, String col) {
		if (col.equals("E")) {
			stack.push(goToTable[row][0]);
		} else if (col.equals("T")) {
			stack.push(goToTable[row][1]);
		} else if (col.equals("F")) {
			stack.push(goToTable[row][2]);
		}
	}

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Bottom-up LR Parser by Nick Diemer\n");
		System.out.println("enter your sequential form with no spaces ending in a $:");
		String sent = scan.nextLine();
		LRParser parser = new LRParser(sent);
		parser.leftRightAlgorithm();
		scan.close();
	}

}
