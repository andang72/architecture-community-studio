package architecture.community.tag;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LockUtils {

	private static final ConcurrentMap<String, InternHolder> map = new ConcurrentHashMap<String, InternHolder>();

	public static void doCleanup(long highWaterMark, long minLRUTime) {
		if ((long) map.size() <= highWaterMark)
			return;
		ArrayList<InternHolder> holderList = new ArrayList<InternHolder>(map.values());
		Collections.sort(holderList, new InternHolderComparator());
		long now = System.currentTimeMillis();
		Iterator<InternHolder> iter = holderList.iterator();
		do {
			if (!iter.hasNext())
				break;
			InternHolder holder = (InternHolder) iter.next();
			if (minLRUTime >= now - holder.lastAccessTime)
				break;
			map.remove(holder.string);
		} while (true);
	}

	public static String internFallBack(String s) {
		return s.intern();
	}

	public static String intern(String key) {
		InternHolder result = map.get(key);
		if (result == null) {
			InternHolder holder = new InternHolder(key);
			result = map.putIfAbsent(key, holder);
			if (result == null)
				result = holder;
		}
		result.updateAccessTime();
		return result.string;
	}

	public void printDetails(PrintStream stream) {
		stream.println("----Strings interned Start----");
		String key;
		for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); stream.println((map.get(key)).toString()))
			key = iter.next();
		stream.println("----Strings interned End----");
	}

	public static long internedCount() {
		return (long) map.size();
	}

	public static long internedSize() {
		long size = 0L;
		for (Iterator<InternHolder> iter = map.values().iterator(); iter.hasNext();) {
			InternHolder internHolder = iter.next();
			size += internHolder.string.length();
		}

		return size;
	}

	static class InternHolder {

		public void updateAccessTime() {
			lastAccessTime = System.currentTimeMillis();
		}

		public String toString() {
			return (new StringBuilder()).append("Key:").append(string).append(", lastAccessTime:")
					.append(lastAccessTime).toString();
		}

		public final String string;
		public volatile long lastAccessTime;

		InternHolder(String s) {
			string = s;
			lastAccessTime = System.currentTimeMillis();
		}
	}

	static class InternHolderComparator implements Comparator<InternHolder> {

		public int compare(InternHolder o1, InternHolder o2) {
			return o1.lastAccessTime <= o2.lastAccessTime ? o1.lastAccessTime != o2.lastAccessTime ? -1 : 0 : 1;
		}

		InternHolderComparator() {
		}

	}

}
