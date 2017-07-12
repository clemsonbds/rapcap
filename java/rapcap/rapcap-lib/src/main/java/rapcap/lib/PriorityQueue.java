package rapcap.lib;

public class PriorityQueue {
	long priorityQueue[];
	int zero = 0;

	PriorityQueue(long values[]) {
		priorityQueue = new long[values.length];
		System.arraycopy(values, 0, priorityQueue, 0, values.length);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < priorityQueue.length; i++) {
			sb.append(i);
			sb.append(" ");
			sb.append(priorityQueue[i]);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	int size() {
		return priorityQueue.length - zero;
	}
	
	long pop() {
		return priorityQueue[zero++];
	}

	void push(long value) {
		if (zero == 0)
			throw new ArrayIndexOutOfBoundsException();

		int min = zero;
		int max = priorityQueue.length - 1;
		int pos = 0;

		long value_shifted = (value >> 32);

		while (true) {
			pos = (max + min) / 2;
			
			// handle equality, update without insertion
			if ((priorityQueue[pos] >> 32) == value_shifted) {
				// update
				priorityQueue[pos] = value;
				return;
			}

			if (max < min) // insert here
				break;
			
			if (value > priorityQueue[pos]) {
				// go right
				min = pos + 1;
			}
			else {
				// go left
				max = pos - 1;
			}
		}

		// add space for new value
		zero--;

		// shift values before pos left by 1
		for (int i = zero; i < pos; i++)
			priorityQueue[i] = priorityQueue[i+1];
		
		// insert the value
		priorityQueue[pos] = value;

		boolean ooo = false;
		boolean dup = false;
		
		if (pos > zero) {
			if (priorityQueue[pos-1] > priorityQueue[pos])
				ooo = true;
			if (priorityQueue[pos-1] == priorityQueue[pos])
				dup = true;
		}
		if (pos < priorityQueue.length-1) {
			if (priorityQueue[pos] > priorityQueue[pos+1])
				ooo = true;
			if (priorityQueue[pos] == priorityQueue[pos+1])
				dup = true;
		}

		if (dup)
			System.out.println("duplicates");
		if (ooo)
			System.out.println("out of order");
		if (dup || ooo)
			System.out.println(
				((pos > zero) ? (new Solution(priorityQueue[pos-1]) + "\n") : "") +
				new Solution(priorityQueue[pos]) +
				((pos < priorityQueue.length-1) ? ("\n" + new Solution(priorityQueue[pos+1])) : ""));
	}
}
