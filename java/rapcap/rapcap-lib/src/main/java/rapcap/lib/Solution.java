package rapcap.lib;

public class Solution implements Comparable<Solution> {
	public int last_index; // p
	public int next_index; // q

	public int compareTo(Solution o) {
		long diff = this.next_index - o.next_index;
		return diff < 0 ? -1 : diff > 0 ? 1 : 0;
	}
	
	public String toString() {
		return "(" + last_index + ", " + next_index + ")";
	}
}
