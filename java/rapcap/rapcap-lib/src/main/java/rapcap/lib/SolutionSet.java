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
	}

	void push(Solution s) {
		minheap.push(((long)s.next_index << 32) + s.last_index);
		long value = ((long)s.next_index << 32) + s.last_index;
		System.out.println("pushing last=" + s.last_index + ", s.next=" + s.next_index + ", " + value);
	}
}
