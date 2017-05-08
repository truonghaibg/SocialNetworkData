package com.common.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.CommonUtils;
import com.socialnetwork.constant.GeneralConstant;
import com.socialnetwork.facebook.FacebookUtils;
import com.socialnetwork.model.FacebookObject;

public class FakeData {

	public static void main(String agr[]) throws IOException {

		Map<String, FacebookObject> hashMap = new HashMap<String, FacebookObject>();
		Map<String, Integer> wordMap = new LinkedHashMap<String, Integer>();
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:/Users/tchai/Desktop/dataset/fake_vntokenizer.csv")),
					GeneralConstant.ENCODING_UTF8));
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
			FacebookObject dataTemp = new FacebookObject();
			String msg = facebookObject.getMessage();
			FacebookUtils.getInstance().feebWordFromStringScanner(tempWordMap, msg);

			dataTemp.setId(facebookObject.getId());
			dataTemp.setGender(facebookObject.getGender());
			if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(facebookObject.getGender())) {
				countMale++;
			} else {
				countFemale++;
			}
			dataTemp.setCountByWords(tempWordMap);
			results.add(dataTemp);
		}
		System.out.println("Count male: " + countMale);
		System.out.println("Count female: " + countFemale);
		// ===========================================================
		// Create file
		// handleFileFake(wordMap, GeneralConstant.TEST_FILE);
		FacebookUtils.getInstance().createLibSVMFileWithTFIDF(wordMap, results);
		FacebookUtils.getInstance().createLibSVMFileDefault(wordMap, results);
		FacebookUtils.getInstance().createBinaryFile(wordMap, results);

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
			FacebookObject dataTemp = new FacebookObject();
			String msg = facebookObject.getMessage();
			FacebookUtils.getInstance().feebWordFromStringScanner(tempWordMap, msg);

			dataTemp.setId(facebookObject.getId());
			dataTemp.setGender(facebookObject.getGender());
			dataTemp.setCountByWords(tempWordMap);
			results.add(dataTemp);

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
