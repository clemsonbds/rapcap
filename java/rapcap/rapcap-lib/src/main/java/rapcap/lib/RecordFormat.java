package rapcap.lib;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

public interface RecordFormat {
	public int maxRecordBodyLen();
	public int maxRecordHeaderLen();
	public ByteOrder byteOrder();

//	public Record interpretRecord(byte[] bytes, int offset) throws IOException;
	public void testRecordHeader(DataInputStream input, Record record) throws IOException;
	public void getRecordLength(DataInputStream input, Record record) throws IOException;
}
