package rapcap.lib;

public class SolutionSet {
	PriorityQueue minheap;
	
	SolutionSet(int num_values) {
		long values[] = new long[num_values];

		for (int i = 0; i < num_values; i++)
			values[i] = ((long)i << 32) + i;

		minheap = new PriorityQueue(values);
	}

	int size() {
		return minheap.size();
	}
	
	void pop(Solution s) {
		long value = minheap.pop();
		s.last_index = (int)((value << 32) >> 32);
		s.next_index = (int)(value >> 32);
//		System.out.println("popping " + s);
	}

	void push(Solution s) {
//		long value = ((long)s.next_index << 32) + s.last_index;
//		System.out.println("PUSHING " + s);
		minheap.push(((long)s.next_index << 32) + s.last_index);
	}
}
