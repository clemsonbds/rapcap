package rapcap.lib;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.PriorityQueue;

@SuppressWarnings("unused")
public class RecordBoundaryDetector {
	private DataInputStream input;
	private RecordFormat format;
	private int max_packet_len;

	public void initialize(BufferedInputStream input, RecordFormat format) {
		this.input = new DataInputStream(input);
		this.format = format;

		max_packet_len = format.maxRecordBodyLen() + format.maxRecordHeaderLen();
	}
/*
	public int sampleLen() {
		final int len = max_packet_len + max_header_len - 1;
		return len;
	}
*/	
	public int detect() throws IOException {
		int index = 0;
		Solution s = new Solution();
		Record record = new Record();
		
		Solution real = new Solution(0);
		
		// don't try to interpret less than 16 bytes as a header
		SolutionSet solutions = new SolutionSet(max_packet_len);
//System.out.println(solutions.minheap);
		while (solutions.size() > 0) {   //does not check pushed values

			solutions.pop(s);

			if (s.last_index == real.next_index) {
//				System.out.println("found real solution " + s);
				real.next_index = s.next_index;
			}
			
			//System.out.println("reading byte " + index + ", s = " + s);
			// don't stop if the last remaining solution is an untested original solution
			if (solutions.size() == 0 && s.next_index != s.last_index)
				break;

			// here we need to check error conditions such as number of bytes skipped or available() to see if
			// we will run out of data before validating all solutions, resulting in parallel solutions
			
			for (long to_skip = s.next_index - index; to_skip > 0; )
				to_skip -= input.skip(to_skip);

			index = s.next_index;

			// LZOP needs to decompress the block starting with the header, so we do two mark/resets:
			// one before/after reading the header to test the assertion that this could be a header
			// candidate, and one before/after the decompression of the block.
			input.mark(format.maxRecordHeaderLen());
			format.testRecordHeader(input, record);
			input.reset();
			//int test = input.readInt();
			
			input.mark(max_packet_len); // allow interpretRecord to read the entire record
			format.getRecordLength(input, record);
			input.reset();

			if (record.valid) {
				s.last_index = index;
				s.next_index = index + record.header_len + record.body_len;
				solutions.push(s);
//				System.out.println("# solutions: " + solutions.size());
			}
			else if (solutions.size() == 0)
				throw new IOException("no solutions");
		}

		// s.last_index contains the only unambiguous solution
		return s.last_index;
	}
}
