package com.common.test;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.socialnetwork.facebook.FacebookUtils;

public class Test {
	public static void main(String agr[]) throws Exception {
		List<String> al = new ArrayList<String>();
		al.add("11");
		al.add("21");
		al.add("21");
		al.add("3");
		al.add("41");
		al.add("41");

		findDuplicates(al);
		String str = "Phát điên vì lũ chuột wa ko cho mình ngủ chu điên mat thui";
		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		System.out.println(str);
		if (FacebookUtils.getInstance().isOnlyStrings(str) && FacebookUtils.getInstance().isDuplicateLetter(str)) {
			str = FacebookUtils.getInstance().removeDiacriticalMarks(str);
		}
		System.out.println(str);
		str = "週の金曜は祝日だそうですac日だそ hải tôi";

		Pattern p = Pattern.compile("[\\p{L}\\p{M}&&[^\\p{Alpha}]]");
		Matcher m = p.matcher(str);
		while (m.find()) {

			String ss = m.group();
			System.out.println(ss);
			str = str.replaceAll(ss, "");
		}
		System.out.println(str);
	}

	public static String removeDiacriticalMarks(String string) {
		return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	public static Set<String> findDuplicates(List<String> listContainingDuplicates) {

		final Set<String> setToReturn = new HashSet<String>();
		final Set<String> set1 = new HashSet<String>();

		for (String yourInt : listContainingDuplicates) {
			if (!set1.add(yourInt)) {
				setToReturn.add(yourInt);
			}
		}
		return setToReturn;
	}

}
