package rapcap.lib;

import java.io.IOException;

public abstract class RecordFormat {
	public int MAX_HEADER_LEN;
	public int MAX_BODY_LEN;
	
	public abstract boolean interpretRecordHeader(byte[] header_buf, Record record) throws IOException;
}
