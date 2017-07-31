package rapcap.hadoop.mr1.lzo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.hadoop.compression.lzo.LzoIndex;
import com.hadoop.compression.lzo.LzoInputFormatCommon;

import net.ripe.hadoop.pcap.io.PcapInputFormat;
import rapcap.lib.RecordBoundaryDetector;
import rapcap.lib.lzo.LzoBoundaryDetector;

/**
 * An {@link InputFormat} for lzop compressed text files. Files are broken into
 * lines. Either linefeed or carriage-return are used to signal end of line.
 * Keys are the position in the file, and values are the line of text.
 * <p>
 * See {@link LzoInputFormatCommon} for a description of the boolean property
 * <code>lzo.text.input.format.ignore.nonlzo</code> and how it affects the
 * behavior of this input format.
 */
public class LzoTextInputFormat extends com.hadoop.mapreduce.LzoTextInputFormat {
    public static final Log LOG = LogFactory.getLog(PcapInputFormat.class);

	@Override
	protected boolean isSplitable(JobContext context, Path filename) {
		if (LzoInputFormatCommon.isLzoFile(filename.toString())) {
			return true;
			//      LzoIndex index = indexes.get(filename);
			//      return !index.isEmpty();
		} else {
			// Delegate non-LZO files to the TextInputFormat base class.
			return super.isSplitable(context, filename);
		}
	}

	@Override
	public List<InputSplit> getSplits(JobContext job) throws IOException {
		List<InputSplit> splits = super.getSplits(job);
		List<InputSplit> result = new ArrayList<InputSplit>();

		Map<Path, LzoIndex> indexes = null;

		try {
			Class<?> clazz = getClass().getSuperclass();
			Field field = clazz.getDeclaredField("indexes");
			field.setAccessible(true);
			indexes = (Map<Path, LzoIndex>) field.get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<Path, List<FileSplit>> unsplittable = new HashMap<Path, List<FileSplit>>();

		// to avoid excessive reading of a file's global header, group the splits by their path
		for (InputSplit genericSplit : splits) {
			FileSplit fileSplit = (FileSplit)genericSplit;
			Path file = ((FileSplit)genericSplit).getPath();

			if (indexes.get(file).isEmpty()) {
				if (!unsplittable.containsKey(file))
					unsplittable.put(file, new ArrayList<FileSplit>());

				unsplittable.get(file).add(fileSplit);
			}
			else
				result.add(genericSplit);
		}

		for (Entry<Path, List<FileSplit>> entry : unsplittable.entrySet()) {
			List<FileSplit> fileSplits = entry.getValue();
			Path path = fileSplits.get(0).getPath();

			// to avoid excessive calls to the boundary detector, sort the splits by their byte offset
			Collections.sort(entry.getValue(), new Comparator<FileSplit>() {
				public int compare(FileSplit lhs, FileSplit rhs) {
					return ((Long)lhs.getStart()).compareTo(rhs.getStart());
				}
			});

			FSDataInputStream baseStream = path.getFileSystem(job.getConfiguration()).open(path);
			InputStream stream = baseStream;

			baseStream.seek(0);
			// reads the global header and some of the first block header
			RecordBoundaryDetector boundaryDetector = new LzoBoundaryDetector(stream);

			long start, next_start = 0;

			for (int split_num = 0; split_num < fileSplits.size(); split_num++) {
				if (split_num == 0) {
					start = boundaryDetector.getRecordStartOffset();
				}
				else {
					start = next_start;
				}

				FileSplit split = fileSplits.get(split_num);

				if (split_num == fileSplits.size()-1) {
					next_start = split.getStart() + split.getLength();
				}
				else {
					baseStream.seek(split.getStart());
					next_start = split.getStart() + boundaryDetector.detect();
				}

				LOG.info("computed boundaries for " + path + "." +split_num+ " at [" + start + ", " + next_start + ")");
				result.add(new FileSplit(path, start, next_start - 1, split.getLocations()));
			}

			stream.close();
		}

		return result;
	}
}
