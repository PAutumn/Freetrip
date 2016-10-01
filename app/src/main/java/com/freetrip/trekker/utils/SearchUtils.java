package com.freetrip.trekker.utils;

public class SearchUtils {
	public static int calculateSimilarity(String content, String regular) {
		int length = regular.length();
		if (content.contains(regular)) {
			// 完全匹配
			return length;
		} else {
			if (length > 1) {
				String regular1 = regular.substring(0, length - 1);
				String regular2 = regular.substring(1, length);
				int x = calculateSimilarity(content, regular1);
				int y = calculateSimilarity(content, regular1);
				return x > y ? x : y;
			}
			return 0;
		}

	}
}
