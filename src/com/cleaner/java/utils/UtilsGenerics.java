package com.cleaner.java.utils;

import java.util.List;

public class UtilsGenerics {
	public static boolean any(List<Boolean> list) {
		for (int i = 0; i < list.size(); i++) {
			Boolean each = list.get(i);
			if (each)
				return true;
		}
		return false;
	}
}
