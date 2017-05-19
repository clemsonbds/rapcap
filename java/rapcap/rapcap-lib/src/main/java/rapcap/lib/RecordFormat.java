package rapcap.lib;

import java.nio.ByteOrder;

public interface RecordFormat {
	public int maxRecordBodyLen();
	public int maxRecordHeaderLen();
	public ByteOrder byteOrder();

	public Record interpretRecord(byte[] bytes, int offset);
}
