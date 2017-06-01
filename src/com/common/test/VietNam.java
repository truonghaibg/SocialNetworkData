package com.common.test;

import java.util.ArrayList;
import java.util.List;

public class VietNam {
	public static String DATA_FILE_INPUT = "data/dataFacebook_v22.csv";
	public static String DATA_FILE_OUTPUT = "data/dataFacebook_v222.csv";

	public static void main(String agr[]) {
		Integer a = 1, b = 10000;
		System.out.println((double) a / b);
		String firstname1 = "i ee44 a1 1222";
		firstname1 = firstname1.replaceAll("\\d+", " $0 ");
		System.out.println(firstname1);// Prints Sam
		// tokenizer.segment("");
		// tokenizer.tokenize(DATA_FILE_INPUT, DATA_FILE_OUTPUT);
		// DetectLanguage.apiKey = "ef108ab62228dd0d2da7b9a399f3a285";
		// String language = DetectLanguage.simpleDetect("toàn_thân");
		// System.out.println(language);
		// VietTokenizer viet = new VietTokenizer();
		List<String> l1 = new ArrayList<String>();
		String content = "is not like is, but mistakes are common";
		l1.add("is");
		String regex = "\\s*\\bis\\b\\s*";
		content = content.replaceAll(regex, "");
		System.out.println(content);
	}
}
