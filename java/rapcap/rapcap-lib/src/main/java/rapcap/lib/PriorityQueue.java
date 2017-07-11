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

		if (zero == 0)
			throw new ArrayIndexOutOfBoundsException();

		int change = (priorityQueue.length - zero) / 2;
		
		while (change > 0) {


			// determine if we should insert at this position
			if (pos == (priorityQueue.length - 1)
				|| ((priorityQueue[pos]   >> 32) < value_shifted
						&& (priorityQueue[pos+1] >> 32) > value_shifted))
				break;

			if (value > priorityQueue[pos]) {
				// go right
				pos = pos + change - (change >> 1);
				//(int)(priorityQueue.length - pos) / 2;
			}
			else {
				// go left
				pos = pos - change + (change >> 1); //(int)(pos - zero + 1) / 2;
			}

			change >>= 1;
			// handle equality, update without insertion
			if ((priorityQueue[pos] >> 32) == value_shifted) {
				// update
				priorityQueue[pos] = value;
				return;
			}
		}

		// add space for new value
		zero--;

		// shift values before pos left by 1
		for (int i = zero; i < pos; i++)
			priorityQueue[i] = priorityQueue[i+1];
		
		// insert the value
		priorityQueue[pos] = value;
//		if (priorityQueue[pos-1] >= priorityQueue[pos] || priorityQueue[pos] >= priorityQueue[pos+1])
//			System.out.println(pos + " " + (priorityQueue.length - zero) + "\n" + (priorityQueue[pos-1]>>32) + "\n" + (priorityQueue[pos]>>32) + "\n" + (priorityQueue[pos+1]>>32));
//		if ((priorityQueue[pos] >> 32) == (priorityQueue[pos+1 >> 32]) || (priorityQueue[pos] >> 32) == (priorityQueue[pos-1] >> 32))
//			System.out.println("Duplicate entries found.");
	}
}
