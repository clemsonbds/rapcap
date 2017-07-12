package rapcap.lib;

public class Solution implements Comparable<Solution> {
	public int last_index; // p
	public int next_index; // q

	public Solution() {
		last_index = 0;
		next_index = 0;
	}
	
	public Solution(long value) {
		next_index = (int)(value >> 32);
		last_index = (int)((value << 32) >> 32);
	}
	
	public int compareTo(Solution o) {
		long diff = this.next_index - o.next_index;
		return diff < 0 ? -1 : diff > 0 ? 1 : 0;
	}
	
	public String toString() {
		return "(last=" + last_index + ", next=" + next_index + ")";
	}
}
