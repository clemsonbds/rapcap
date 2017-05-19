package rapcap.lib;

public class Record {
	public int header_len;
	public int body_len;

	public Record(int header_len, int body_len) {
		this.header_len = header_len;
		this.body_len = body_len;
	}
}
