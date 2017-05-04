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
import java.util.List;
import java.util.Map.Entry;

import com.socialnetwork.constant.GeneralConstant;

public class BeforeCountWordOnFacebook {
	public static String DATA_FILE = "other.csv";
	public static List<String> noHandler = new ArrayList<String>();

	public static void main(String agr[]) throws IOException {
		HashMap<String, Integer> wordMap = new HashMap<String, Integer>();
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(DATA_FILE)), GeneralConstant.ENCODING_UTF8));
			String line;
			while ((line = buffReader.readLine()) != null) {
				String str = FacebookUtils.getInstance().splitString(line);
				FacebookUtils.getInstance().feebWordFromString(wordMap, str.toLowerCase());
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
			writer = new FileWriter("OriginOther.csv");
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
	}

	public void test() throws IOException {
		List<String> listResult = new ArrayList<String>();
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("source/result.txt")), GeneralConstant.ENCODING_UTF8));
			String line;
			while ((line = buffReader.readLine()) != null) {
				listResult.add(Integer.parseInt(line) == 1 ? GeneralConstant.CLASSIFY.MALE : GeneralConstant.CLASSIFY.FEMALE);
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
		FileWriter writer = null;
		try {
			writer = new FileWriter("source/training.csv");
			// Object predictList = null;
			// for (String aaa : predictList) {
			// writer.write(aaa + "\n");
			// }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
