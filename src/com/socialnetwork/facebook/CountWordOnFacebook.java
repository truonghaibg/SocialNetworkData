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

	public static void processVnTokenzierFile(String nameFile, boolean isWeka, boolean isTfIdf, boolean isBinary, boolean isCount) throws IOException {
		Map<String, FacebookObject> hashMap = new LinkedHashMap<String, FacebookObject>();
		Map<String, Integer> unigramMap = new HashMap<String, Integer>();
		Map<String, Integer> bigramMap = new HashMap<String, Integer>();
		Map<String, Integer> trigramMap = new HashMap<String, Integer>();
		writerRemoveFile(nameFile);
		List<String> wordRemove = loadWordInFile();

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
					mgs = removeInString(mgs, wordRemove);
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
		// FacebookUtils.getInstance().removeWord(unigramMap);
		// FacebookUtils.getInstance().removeWord(bigramMap);
		// FacebookUtils.getInstance().removeWord(trigramMap);
		// End: remove words

		// System.out.println("Filter unigram size: " + unigramMap.size());
		// System.out.println("Filter bigram size: " + bigramMap.size());
		// System.out.println("Filter trigram size: " + trigramMap.size());

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
		System.out.println("Total (male + female): " + (countMale + countFemale));
		System.out.println("Count male: " + countMale);
		System.out.println("Count female: " + countFemale);
		// ===========================================================
		// Create file
		if (isWeka) {
			FacebookUtils.getInstance().createFileWekaDefault(unigramMap, results);
		}
		if (isCount) {
			// FacebookUtils.getInstance().createCountFile(unigramMap, results,
			// GeneralConstant.UNI_GRAM);
			// FacebookUtils.getInstance().createCountFile(bigramMap, results,
			// GeneralConstant.BI_GRAM);
			// FacebookUtils.getInstance().createCountFile(trigramMap, results,
			// GeneralConstant.TRI_GRAM);
		}
		if (isTfIdf) {
			FacebookUtils.getInstance().createTfIdfFile(unigramMap, results, GeneralConstant.UNI_GRAM);
			FacebookUtils.getInstance().createTfIdfFile(bigramMap, results, GeneralConstant.BI_GRAM);
			FacebookUtils.getInstance().createTfIdfFile(trigramMap, results, GeneralConstant.TRI_GRAM);
		}
		if (isBinary) {
			// FacebookUtils.getInstance().createBinaryFile(unigramMap, results,
			// GeneralConstant.UNI_GRAM);
			// FacebookUtils.getInstance().createBinaryFile(bigramMap, results,
			// GeneralConstant.BI_GRAM);
			// FacebookUtils.getInstance().createBinaryFile(trigramMap, results,
			// GeneralConstant.TRI_GRAM);
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

	public static void ngram1(Map<String, Integer> wordMap, String str, String gramType) {
		// System.out.println(str);
		String[] words = str.split("\\s+");
		int size = words.length;
		if (GeneralConstant.UNI_GRAM.equals(gramType)) {
			for (int i = 0; i < size; i++) {
				FacebookUtils.getInstance().putWordIntoMap(wordMap, words[i]);
			}
		} else if (GeneralConstant.BI_GRAM.equals(gramType)) {
			for (int i = 0; i < size - 1; i++) {
				String tmp = words[i] + "_" + words[i + 1];
				FacebookUtils.getInstance().putWordIntoMap(wordMap, tmp);
			}
		} else if (GeneralConstant.TRI_GRAM.equals(gramType)) {
			for (int i = 0; i < size - 2; i++) {
				String tmp = words[i] + "_" + words[i + 1] + "_" + words[i + 2];
				FacebookUtils.getInstance().putWordIntoMap(wordMap, tmp);
			}
		}
	}

	public static List<String> loadWordInFile() throws IOException {
		List<String> words = new ArrayList<String>();
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("source/remove_word.csv")), GeneralConstant.ENCODING_UTF8));
			String line;
			while ((line = buffReader.readLine()) != null) {
				String word = line.trim();
				if (word.length() > 0) {
					words.add(word);
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
		return words;
	}

	public static void writerRemoveFile(String nameFile) throws IOException {
		List<String> wordRemove = new ArrayList<String>();
		BufferedReader buffReader = null;
		Map<String, Integer> unigramMap = new HashMap<String, Integer>();
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(nameFile)), GeneralConstant.ENCODING_UTF8));
			String line;
			while ((line = buffReader.readLine()) != null) {
				String[] user = line.split(",", 5);
				if (user.length == 5) {
					String mgs = user[4];
					mgs = CommonUtils.formatString(mgs);
					mgs = mgs.replaceAll("\\d+", GeneralConstant.DIGIT);
					ngram(unigramMap, mgs, GeneralConstant.UNI_GRAM);
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
		FacebookUtils.getInstance().removeWord(unigramMap, wordRemove);
		FileWriter writer = null;
		try {
			writer = new FileWriter("source/remove_word.csv");
			for (String word : wordRemove) {
				writer.write(word + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static String removeInString(String str, List<String> list) throws IOException {
		// System.out.println(str);
		String[] words = str.split("\\s+");
		int size = words.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			if (!list.contains(words[i])) {
				sb.append(words[i]).append(" ");
			}
		}
		// System.out.println(sb.toString().trim());
		return sb.toString().trim();
	}
}
