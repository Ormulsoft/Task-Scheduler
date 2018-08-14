package util;

import java.util.concurrent.atomic.AtomicLong;

public class FIFOEntry<E extends Comparable<? super E>> implements Comparable<FIFOEntry<E>> {
	final static AtomicLong seq = new AtomicLong();
	final long seqNum;
	final E entry;

	public FIFOEntry(E entry) {
		seqNum = seq.getAndIncrement();
		this.entry = entry;
	}

	public E getEntry() {
		return entry;
	}

	public int compareTo(FIFOEntry<E> other) {
		int res = entry.compareTo(other.entry);
		if (res == 0 && other.entry != this.entry)
			res = (seqNum < other.seqNum ? -1 : 1);
		return res;
	}
}