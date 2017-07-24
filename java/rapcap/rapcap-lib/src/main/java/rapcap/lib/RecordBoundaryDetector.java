package rapcap.lib;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class RecordBoundaryDetector {
	private BufferedInputStream input;
	private RecordFormat format;

	public abstract long getRecordStartOffset();
	
	public void initialize(InputStream input, RecordFormat format) {
		this.input = new BufferedInputStream(input);
		this.format = format;
	}

	public int detect() throws IOException {
		int index = 0;
		Solution solution = new Solution();
		Record record = new Record();
		byte header_buf[] = new byte[format.MAX_HEADER_LEN];
		
		// don't try to interpret less than 16 bytes as a header
		SolutionSet solutions = new SolutionSet(format.MAX_BODY_LEN + format.MAX_HEADER_LEN);

		while (solutions.size() > 0) {   //does not check pushed values

			solutions.pop(solution);

			// don't stop if the last remaining solution is an untested original solution
			if (solutions.size() == 0 && solution.next_index != solution.last_index)
				break;

			// here we need to check error conditions such as number of bytes skipped or available() to see if
			// we will run out of data before validating all solutions, resulting in parallel solutions
			
			for (long to_skip = solution.next_index - index; to_skip > 0; )
				to_skip -= input.skip(to_skip);

			index = solution.next_index;

			input.mark(format.MAX_HEADER_LEN);
			input.read(header_buf, 0, format.MAX_HEADER_LEN);
			input.reset();
			
			if (format.interpretRecordHeader(header_buf, record)) {
				solution.last_index = index;
				solution.next_index = index + record.header_len + record.body_len;
				solutions.push(solution);
			}
			else if (solutions.size() == 0)
				throw new IOException("no solutions");
		}

		// s.last_index contains the only unambiguous solution
		return solution.last_index;
	}
}
