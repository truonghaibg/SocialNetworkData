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

import com.socialnetwork.constant.GeneralConstant;
import com.socialnetwork.model.FacebookObject;

public class CountWordOnFacebook {

	public static void main(String agr[]) throws IOException {
		// processNotVnTokenzierFile(GeneralConstant.FULL_STATUS_FILTER, false,
		// true);
		processVnTokenzierFile(GeneralConstant.VN_TOKENIZER_STATUS, false, false, false, false);

		// handleFileFake(GeneralConstant.TEST_FILE);

	}

	public static void processVnTokenzierFile(String nameFile, boolean isWeka, boolean isTfIdf, boolean isBinary, boolean isNgram) throws IOException {
		Map<String, FacebookObject> hashMap = new LinkedHashMap<String, FacebookObject>();
		Map<String, Integer> wordMap = new HashMap<String, Integer>();
		BufferedReader buffReader = null;
		// int i = 0;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(nameFile)), GeneralConstant.ENCODING_UTF8));
			String line;
			while ((line = buffReader.readLine()) != null) {
				String[] user = line.split(",", 5);
				if (user.length == 5) {
					FacebookUtils.getInstance().feebWordFromStringScanner(wordMap, user[4]);
					FacebookObject temp = new FacebookObject(user[2].trim(), user[3].trim(), user[4].trim());
					hashMap.put(user[3].trim(), temp);
				}
				// i++;
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
				if (key.split("_").length == 1 && value >= 5) {
					writer.write(key + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

		// Filter words
		Iterator<Entry<String, Integer>> iterator = wordMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Integer> obj = iterator.next();
			if (FacebookUtils.getInstance().removeWord(obj)) {
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
		// ===========================================================
		// Create file
		if (isWeka) {
			FacebookUtils.getInstance().createFileWeka(wordMap, results);
		}
		if (isTfIdf) {
			FacebookUtils.getInstance().createLibSVMFileWithTFIDF(wordMap, results);
		}
		if (isBinary) {
			FacebookUtils.getInstance().createBinaryFile(wordMap, results);
		}
		if (isNgram) {
			FacebookUtils.getInstance().create1GramFile(wordMap, results);
			FacebookUtils.getInstance().create2GramFile(results);
			FacebookUtils.getInstance().create3GramFile(results);
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
					FacebookUtils.getInstance().feebWordFromStringScanner(wordMap, user[4]);
					FacebookObject temp = new FacebookObject(user[2].trim(), user[3].trim(), user[4].trim());
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
					FacebookUtils.getInstance().feebWordFromStringScanner(wordMap, user[4]);
					FacebookObject temp = new FacebookObject(user[2].trim(), user[3].trim(), user[4].trim());
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
}
