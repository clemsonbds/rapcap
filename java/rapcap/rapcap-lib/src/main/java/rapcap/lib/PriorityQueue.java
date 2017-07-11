package rapcap.lib;

public class Minheap {
	long heap[];
	int zero = 0;
	
	Minheap(long values[]) {
		heap = new long[values.length];
		System.arraycopy(values, 0, heap, 0, values.length);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < heap.length; i++) {
			sb.append(i);
			sb.append(" ");
			sb.append(heap[i]);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	int size() {
		return heap.length - zero;
	}
	
	long pop() {
		return heap[zero++];
	}

	void push(long value) {
		// figure out where to insert
		long value_shifted = value >> 32;
		int pos = (heap.length - zero) / 2 + zero;
		int change;

		if (zero == 0)
			throw new ArrayIndexOutOfBoundsException();
		
		while (true) {
			if ((heap[pos] >> 32) == value_shifted) {
				// update
				heap[pos] = value;
				return;
			}

			if (heap[pos] > value) {
				// go right
				change = (heap.length - pos) / 2;
			}
			else {
				// go left
				change = 0 - (pos - zero + 1) / 2;
			}
			
			if (change == 0)
				break;
			
			pos += change;
		}

		// add space for new value
		zero--;

		// shift values before pos left by 1
		for (int i = zero; i < pos; i++)
			heap[i] = heap[i+1];
		
		// insert the value
		heap[pos] = value;
	}
}
