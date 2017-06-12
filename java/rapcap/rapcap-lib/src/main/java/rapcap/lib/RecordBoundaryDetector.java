package rapcap.lib;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.PriorityQueue;

public class RecordBoundaryDetector {
	private DataInput input;
	private RecordFormat format;
	private byte[] bytes;
	private int max_header_len;
	private int max_packet_len;
	private int bytes_read;

	public void initialize(DataInput input, RecordFormat format) {
		this.input = input;
		this.format = format;

		max_header_len = format.maxRecordHeaderLen();
		max_packet_len = format.maxRecordBodyLen() + max_header_len;
		bytes = new byte[sampleLen()]; // snap len + header + 15 bytes
		bytes_read = 0;
	}

	public int sampleLen() {
		return max_packet_len + max_header_len - 1;
	}
	
	public int lastDetectBytesRead() {
		return bytes_read;
	}
	
	public int detect() throws IOException {
		PriorityQueue<Solution> solutions = new PriorityQueue<Solution>();

		input.readFully(bytes); // throws EOFException if not enough data
		bytes_read = bytes.length;
		
		int max_index = bytes.length - max_header_len;  // don't try to interpret less than 16 bytes as a header
		int furthest_index = 0; // optimizing, so we don't read too many bytes to validate the last remaining solution

		for (int i = 0; i <= max_index; i++) {
			Record record = format.interpretRecord(bytes, i);
			
			if (record != null) {
				Solution s = new Solution(i, i + record.header_len + record.body_len);
				solutions.add(s);
				
				furthest_index = Math.max(furthest_index, s.next_index);
			}
		}

		if (solutions.isEmpty())
			throw new IOException("no solutions");
		
		int offset = 0;
		int len_chunk = bytes.length;
		
		while (len_chunk >= max_header_len) {
			
			while (solutions.peek().next_index <= max_index) {
				Solution s = solutions.remove();

				while (!solutions.isEmpty() && solutions.peek().next_index == s.next_index) {
					Solution a = solutions.remove();

					if (s.last_index != a.last_index)
						s.last_index = s.next_index;
				}

				int header_i = s.next_index - offset;
				
				if (header_i < 0) {
					throw new IOException("s.next_index = " + s.next_index + " offset = " + offset);
				}
				
				Record record = format.interpretRecord(bytes, header_i);
				if (record != null) {
					if (solutions.isEmpty())
						return s.last_index;
					
					s.next_index += record.header_len + record.body_len;
					solutions.add(s);

					furthest_index = Math.max(furthest_index, s.next_index);
				}
			}
//System.out.println("solutions left: " + solutions.size());
			// place last 15 bytes at the beginning for the next iteration
			int carry_len = max_header_len - 1;
			int advance_len = len_chunk - carry_len;
			offset += advance_len;

			for (int i = 0; i < carry_len; i++)
				bytes[i] = bytes[advance_len + i];
			
			// read packet_len more bytes
			// if there is only one solution left, we could just read enough bytes to validate that solution
			int bytes_to_read = solutions.size() == 1 ? solutions.peek().next_index - offset + max_header_len : max_packet_len;
			int read_len = input.read(bytes, carry_len, bytes_to_read);
			bytes_read += read_len;
			
			len_chunk = read_len + carry_len;
			max_index += read_len;
		}

		throw new IOException("parallel solutions");
	}
}
