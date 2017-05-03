package com.common;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
	public static String removeUrl(String str) {
		if (str == null || str == "") {
			return str;
		}
		str = str.replaceAll("\\b((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])", " ");
		return CommonUtils.formatString(str);
	}

	public static boolean isRemoveStatus(String str) {
		if (isHaveUrl(str) || isHaveHashtag(str)) {
			return true;
		}
		return false;
	}

	public static boolean isHaveUrl(String str) {
		if (str == null || str == "") {
			return false;
		}
		Pattern MY_PATTERN = Pattern.compile("\\b((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");
		Matcher matcher = MY_PATTERN.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static boolean isHaveHashtag(String str) {
		if (str == null || str == "") {
			return false;
		}
		Pattern MY_PATTERN = Pattern.compile("#(\\S+)");
		Matcher matcher = MY_PATTERN.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static String formatString(String result) {
		if (result == null || result == " ") {
			return result;
		}
		result = result.replaceAll(",", " ").replaceAll("\\.", " ");
		result = result.replaceAll("[\\n\\r\\t]+", " ").replaceAll("\\s+", " ").trim();
		return result;
	}

	public static void readAllFileInFolder(File folder, List<File> files) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				readAllFileInFolder(fileEntry, files);
			} else {
				files.add(fileEntry);
			}
		}
	}

	public static void main(String agr[]) {
		String mgs = "cảm,,$ xúc. http://www.youtube.com/watch?v=M0au4ptU4U4 http://w";
		mgs = CommonUtils.removeUrl(mgs);
		System.out.print(mgs);
	}
}
