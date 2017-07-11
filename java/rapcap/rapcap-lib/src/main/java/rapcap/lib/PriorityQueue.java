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
		// figure out where to insert
		long value_shifted = (value >> 32);
		int pos = ((int)priorityQueue.length - zero) / 2 + zero;
		int change = 0;

		if (zero == 0)
			throw new ArrayIndexOutOfBoundsException();
		
		while (true) {
			if ((priorityQueue[pos] >> 32) == value_shifted) {
				// update
				priorityQueue[pos] = value;
				return;
			}


			if (priorityQueue[pos] > value) {
				// go right
				change = (int)(priorityQueue.length - pos) / 2;
				System.out.println("Going right.");
			}
			else {
				// go left
				change = 0 - (int)(pos - zero + 1) / 2;
				System.out.println("Going left.");
			}
			
			if (change == 0)
				break;
			
			pos += change;
			System.out.println("I am stuck in the PQ Loop.");
		}

		// add space for new value
		zero--;

		// shift values before pos left by 1
		for (int i = zero; i < pos; i++)
			priorityQueue[i] = priorityQueue[i+1];
		
		// insert the value
		priorityQueue[pos] = value;
	}
}
