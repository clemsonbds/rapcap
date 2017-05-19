package rapcap.lib;

import java.io.IOException;

public class Solution implements Comparable<Solution> {
	public int last_index;
	public int next_index;

	public Solution(int last, int next) throws IOException {
		last_index = last;
		next_index = next;
	}
		
	public int compareTo(Solution o) {
		long diff = this.next_index - o.next_index;
		return diff < 0 ? -1 : diff > 0 ? 1 : 0;
	}
	
	public String toString() {
		return "(" + last_index + ", " + next_index + ")";
	}
}
