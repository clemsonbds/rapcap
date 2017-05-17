# Hadoop RAPCAP Library

A Hadoop library for FileInputFormats that use RAPCAP, an algorithm for reliably detecting record boundaries in non-delimited variable length record storage formats.  Certain file formats such as PCAP are unsplittable without record detection, and therefore must be read by a single Map task.  This library addresses that problem.

RAPCAP
------

Sequential record storage formats such as the network trace storage format Packet CAPture (PCAP) are designed to be read sequentially - they consist of a records appended to the file tail without using indices or delimiters.  Each record contains a pointer to the start of the next record.  As such, they are considered "unsplittable" - it is difficult to determine a record start position from an arbitrary access point.
RAPCAP is an algorithm for reliably finding the start of a record by building an initial set of candidate positions and refining the solution space to a single outcome.  The algorithm and analysis were published in the proceedings of the 2nd IFIP/IEEE International Workshop on Analytics for Network and Service Management, and is available at:

http://tigerprints.clemson.edu/computing_pubs/29/

Please use the following information when citing this work:

Anderson, Jason, Christopher Gropp, Linh Ngo, and Amy Apon. 2017. "Random Access in Nondelimited Variable-Length Record Collections for Parallel Reading with Hadoop". In Proceedings of the 2nd IFIP/IEEE International Workshop on Analytics for Network and Service Management (Annet‘17), 965-970. IFIP/IEEE.

Repository
----------

	<repositories>
	  <repository>
	    <id>hadoop-rapcap</id>
	    <url>http://???/hadoop-rapcap</url>
	  </repository>
	</repositories>


License
-------

This library is distributed under the Apache License 2.0.
https://raw.github.com/clemsonbds/hadoop-rapcap/blob/master/LICENSE

