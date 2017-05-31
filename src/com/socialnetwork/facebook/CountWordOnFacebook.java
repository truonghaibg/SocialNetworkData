package com.socialnetwork.facebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.common.CommonUtils;
import com.socialnetwork.constant.GeneralConstant;
import com.socialnetwork.model.FacebookObject;

public class CountWordOnFacebook {

	public static void main(String agr[]) throws IOException {
		// processNotVnTokenzierFile(GeneralConstant.FULL_STATUS_FILTER, false,
		// true);
		processVnTokenzierFile(GeneralConstant.VN_TOKENIZER_STATUS, false, false, false, false);

		// handleFileFake(GeneralConstant.TEST_FILE);

	}

	public static void processVnTokenzierFile(String nameFile, boolean isWeka, boolean isTfIdf, boolean isBinary, boolean count) throws IOException {
		Map<String, FacebookObject> hashMap = new LinkedHashMap<String, FacebookObject>();
		Map<String, Integer> unigramMap = new HashMap<String, Integer>();
		Map<String, Integer> bigramMap = new HashMap<String, Integer>();
		Map<String, Integer> trigramMap = new HashMap<String, Integer>();
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(nameFile)), GeneralConstant.ENCODING_UTF8));
			String line;
			while ((line = buffReader.readLine()) != null) {
				String[] user = line.split(",", 5);
				if (user.length == 5) {
					Map<String, Integer> uniGramTemp = new HashMap<String, Integer>();
					Map<String, Integer> biGramTemp = new HashMap<String, Integer>();
					Map<String, Integer> triGramTemp = new HashMap<String, Integer>();
					String mgs = user[4];
					mgs = mgs.replaceAll("\\d+", GeneralConstant.DIGIT);
					mgs = CommonUtils.formatString(mgs);
					ngram(unigramMap, mgs, GeneralConstant.UNI_GRAM);
					ngram(bigramMap, mgs, GeneralConstant.BI_GRAM);
					ngram(trigramMap, mgs, GeneralConstant.TRI_GRAM);

					ngram(uniGramTemp, mgs, GeneralConstant.UNI_GRAM);
					ngram(biGramTemp, mgs, GeneralConstant.BI_GRAM);
					ngram(triGramTemp, mgs, GeneralConstant.TRI_GRAM);

					FacebookObject temp = new FacebookObject(user[2].trim(), user[3].trim(), mgs.trim());
					temp.setUniGram(uniGramTemp);
					temp.setBiGram(biGramTemp);
					temp.setTriGram(triGramTemp);
					hashMap.put(user[3].trim(), temp);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buffReader != null) {
				buffReader.close();
			}
		}
		// Count word
		System.out.println("Origin Size: " + unigramMap.size());
		System.out.println("Bigram Size: " + bigramMap.size());
		System.out.println("Trigram Size: " + trigramMap.size());
		FileWriter writer = null;
		// try {
		// writer = new FileWriter(GeneralConstant.ORIGIN_WORD);
		// for (Entry<String, Integer> entry : unigramMap.entrySet()) {
		// Integer value = entry.getValue();
		// String key = entry.getKey();
		// writer.write(key + "\n");
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// } finally {
		// if (writer != null) {
		// writer.close();
		// }
		// }

		// Begin: remove words
		FacebookUtils.getInstance().removeWord(unigramMap);
		FacebookUtils.getInstance().removeWord(bigramMap);
		FacebookUtils.getInstance().removeWord(trigramMap);
		// End: remove words
		System.out.println("Filter unigram size: " + unigramMap.size());
		System.out.println("Filter bigram size: " + bigramMap.size());
		System.out.println("Filter trigram size: " + trigramMap.size());
		// try {
		// writer = new FileWriter(GeneralConstant.FILTER_WORD);
		// for (Entry<String, Integer> entry : unigramMap.entrySet()) {
		// Integer value = entry.getValue();
		// String key = entry.getKey();
		// writer.write(value + ", " + key + "\n");
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// } finally {
		// if (writer != null) {
		// writer.close();
		// }
		// }
		// Count of male/female number
		int countMale = 0;
		int countFemale = 0;

		// Count words
		List<FacebookObject> results = new ArrayList<FacebookObject>();
		for (Map.Entry<String, FacebookObject> entry : hashMap.entrySet()) {
			FacebookObject facebookObject = entry.getValue();
			HashMap<String, Integer> tempWordMap = new HashMap<String, Integer>();
			FacebookUtils.getInstance().feebWordFromStringScanner(tempWordMap, facebookObject.getMessage());

			facebookObject.setCountByWords(tempWordMap);
			results.add(facebookObject);

			if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(facebookObject.getGender())) {
				countMale++;
			} else {
				countFemale++;
			}
		}
		System.out.println("Count male: " + countMale);
		System.out.println("Count female: " + countFemale);
		System.out.println("Total: " + (countMale + countFemale));
		// ===========================================================
		// Create file
		if (isWeka) {
			FacebookUtils.getInstance().createFileWekaDefault(unigramMap, results);
		}
		if (count) {
			FacebookUtils.getInstance().create1GramFile(unigramMap, results);
			FacebookUtils.getInstance().create2GramFile(results);
			FacebookUtils.getInstance().create3GramFile(results);
		}
		if (isTfIdf) {
			FacebookUtils.getInstance().createLibSVMFileWithTFIDF(unigramMap, results);
		}
		if (isBinary) {
			FacebookUtils.getInstance().createBinaryFile(unigramMap, results, GeneralConstant.UNI_GRAM);
		}

	}

	public static void processNotVnTokenzierFile(String nameFile, boolean isCreateFileWeka, boolean isCreateFileLib) throws IOException {
		HashMap<String, FacebookObject> hashMap = new HashMap<String, FacebookObject>();
		HashMap<String, Integer> wordMap = new HashMap<String, Integer>();
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(nameFile)), GeneralConstant.ENCODING_UTF8));
			String line;
			while ((line = buffReader.readLine()) != null) {
				String[] user = line.split(",", 5);
				if (user.length == 5) {
					String mgs = user[4].trim();
					mgs = mgs.replaceAll("\\d+", GeneralConstant.DIGIT);
					mgs = CommonUtils.formatString(mgs);
					FacebookUtils.getInstance().feebWordFromStringScanner(wordMap, mgs);
					FacebookObject temp = new FacebookObject(user[2].trim(), user[3].trim(), mgs);
					hashMap.put(user[3].trim(), temp);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buffReader != null) {
				buffReader.close();
			}
		}
		// Count word
		System.out.println("Origin Size: " + wordMap.size());
		FileWriter writer = null;
		try {
			writer = new FileWriter(GeneralConstant.ORIGIN_WORD);
			for (Entry<String, Integer> entry : wordMap.entrySet()) {
				Integer value = entry.getValue();
				String key = entry.getKey();
				writer.write(value + "," + key + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

		// Filter words
		Map<String, Integer> otherWord = new TreeMap<String, Integer>();
		Iterator<Entry<String, Integer>> iterator = wordMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Integer> obj = iterator.next();
			String str = obj.getKey();
			if (FacebookUtils.getInstance().isOnlyStrings(str) && FacebookUtils.getInstance().isDuplicateLetter(str)) {
				str = FacebookUtils.getInstance().removeDuplicateCharactersFromWord(str);
			}
			if (FacebookUtils.getInstance().removeWord(obj)) {
				otherWord.put(obj.getKey(), obj.getValue());
				iterator.remove();
			}
		}
		System.out.println("Filter Size: " + wordMap.size());
		try {
			writer = new FileWriter(GeneralConstant.FILTER_WORD);
			for (Entry<String, Integer> entry : wordMap.entrySet()) {
				Integer value = entry.getValue();
				String key = entry.getKey();
				writer.write(value + ", " + key + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		// Count words
		List<FacebookObject> results = new ArrayList<FacebookObject>();
		for (Map.Entry<String, FacebookObject> entry : hashMap.entrySet()) {
			FacebookObject facebookObject = entry.getValue();
			HashMap<String, Integer> tempWordMap = new HashMap<String, Integer>();
			FacebookUtils.getInstance().feebWordFromStringScanner(tempWordMap, facebookObject.getMessage());

			facebookObject.setCountByWords(tempWordMap);
			results.add(facebookObject);
		}
		// ===========================================================
		// Create ARFF
		if (isCreateFileWeka) {
			FacebookUtils.getInstance().createFileWeka(wordMap, results);
		}
		if (isCreateFileLib) {
			FacebookUtils.getInstance().createLibSVMFileDefault(wordMap, results);
		}
	}

	public static void handleFileFake(Map<String, Integer> words, String nameFile) throws IOException {
		Map<String, FacebookObject> hashMap = new LinkedHashMap<String, FacebookObject>();
		Map<String, Integer> wordMap = new HashMap<String, Integer>();
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(nameFile)), GeneralConstant.ENCODING_UTF8));
			String line;
			while ((line = buffReader.readLine()) != null) {
				String[] user = line.split(",", 5);
				if (user.length == 5) {
					String mgs = user[4].trim();
					mgs = mgs.replaceAll("\\d+", GeneralConstant.DIGIT);
					mgs = CommonUtils.formatString(mgs);
					FacebookUtils.getInstance().feebWordFromStringScanner(wordMap, mgs);
					FacebookObject temp = new FacebookObject(user[2].trim(), user[3].trim(), mgs);
					hashMap.put(user[3].trim(), temp);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buffReader != null) {
				buffReader.close();
			}
		}
		int countMale = 0;
		int countFemale = 0;
		// Count words
		List<FacebookObject> results = new ArrayList<FacebookObject>();
		for (Map.Entry<String, FacebookObject> entry : hashMap.entrySet()) {
			FacebookObject facebookObject = entry.getValue();
			HashMap<String, Integer> tempWordMap = new HashMap<String, Integer>();
			FacebookUtils.getInstance().feebWordFromStringScanner(tempWordMap, facebookObject.getMessage());

			facebookObject.setCountByWords(tempWordMap);
			results.add(facebookObject);

			if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(facebookObject.getGender())) {
				countMale++;
			} else {
				countFemale++;
			}
		}
		System.out.println("Count male: " + countMale);
		System.out.println("Count female: " + countFemale);

		System.out.println("Size: " + wordMap.size());
		// ===========================================================
		// Create LibSVM
		FacebookUtils.getInstance().handleLibSVMFake(words, results);
	}

	public static void ngram(Map<String, Integer> wordMap, String str, String gramType) {
		// System.out.println(str);
		String[] words = str.split("\\s+");
		int start = 0;
		int end = 0, size = words.length;
		for (int i = 0; i < size; i++) {
			// System.out.println(words[i]);
			FacebookUtils.getInstance().putWordIntoMap(wordMap, words[i]);
			if (!GeneralConstant.UNI_GRAM.equals(gramType)) {
				if (FacebookUtils.getInstance().isOnlySpecialCharacter(words[i]) || (i + 1) == size) {
					end = i;
					if ((i + 1) == size && !FacebookUtils.getInstance().isOnlySpecialCharacter(words[i])) {
						end = end + 1;
					}
					for (int j = start; j < end - 1; j++) {
						String tmp = words[j] + "_" + words[j + 1];
						// System.out.println(tmp);
						FacebookUtils.getInstance().putWordIntoMap(wordMap, tmp);
					}
					if (!GeneralConstant.BI_GRAM.equals(gramType)) {
						for (int j = start; j < end - 2; j++) {
							String tmp = words[j] + "_" + words[j + 1] + "_" + words[j + 2];
							// System.out.println(tmp);
							FacebookUtils.getInstance().putWordIntoMap(wordMap, tmp);
						}
					}
					start = end + 1;
				}
			}

		}
	}

}
