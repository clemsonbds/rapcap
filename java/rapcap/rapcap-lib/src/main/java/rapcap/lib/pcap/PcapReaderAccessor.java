package rapcap.lib.pcap;

import net.ripe.hadoop.pcap.PcapReader;
import org.apache.commons.lang3.reflect.FieldUtils;

/* This is an awful hack because the writers of the RIPE-NCC PCAP library didn't have a
 * reason to make snapLen and the byte order available to users.  Hopefully this will
 * go away in a future version.
 */

public class PcapReaderAccessor {
	public static int getSnapLen(PcapReader reader) throws IllegalAccessException {
		return (Integer) FieldUtils.readField(reader, "snapLen", true);
	}

	public static boolean isReverseHeaderByteOrder(PcapReader reader) throws IllegalAccessException {
		return (Boolean) FieldUtils.readField(reader, "reverseHeaderByteOrder", true);
	}
}
