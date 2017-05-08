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
import com.socialnetwork.model.FacebookObject;

public class MergedDataOnFacebook {
	public static void main(String agr[]) throws IOException {
		merged();
		String inputFile = "source/vn_tokenizer_status.csv";
		String compareFile = "source/result.txt";
		// merged(inputFile, compareFile);
		// filter(inputFile, compareFile);
	}

	public static void filter(String file, String compareFile) throws IOException {
		List<String> results = new ArrayList<String>();
		List<String> stts = new ArrayList<String>();
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(compareFile)), GeneralConstant.ENCODING_UTF8));
			String line;
			while ((line = buffReader.readLine()) != null) {
				results.add(Integer.parseInt(line) == 1 ? GeneralConstant.CLASSIFY.MALE : GeneralConstant.CLASSIFY.FEMALE);
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

		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file)), GeneralConstant.ENCODING_UTF8));
			String line;
			int i = 0;
			while ((line = buffReader.readLine()) != null) {
				String[] split = line.split(",");
				if (!split[2].equals(results.get(i)) && split[2].equals(GeneralConstant.CLASSIFY.MALE) && split[4].length() < 40) {
					// stts.add(split[3]);
				} else {
					stts.add(line);
				}
				i++;
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
			writer = new FileWriter(file);
			// Object predictList = null;
			for (String stt : stts) {
				writer.write(stt + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static void merged() throws IOException {
		HashMap<String, FacebookObject> users = new HashMap<String, FacebookObject>();
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(GeneralConstant.VN_TOKENIZER_STATUS)),
					GeneralConstant.ENCODING_UTF8));
			String line;
			while ((line = buffReader.readLine()) != null) {
				String[] user = line.split(",", 5);
				if (user.length == 5) {
					FacebookObject temp = users.get(user[0].trim());
					if (temp == null) {
						temp = new FacebookObject();
						temp.setId(user[0].trim());
						temp.setName(user[1].trim());
						temp.setGender(user[2].trim());
						temp.setMessageId(user[3].trim());
						temp.setFullMessage(user[4].trim());
					} else {
						temp.mergedMessage(user[4].trim());
					}
					users.put(user[0].trim(), temp);
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
		System.out.println("Size of user: " + users.size());
		FileWriter writer = null;
		try {
			writer = new FileWriter(GeneralConstant.VN_TOKENIZER_STATUS_MERGED);
			for (Entry<String, FacebookObject> entry : users.entrySet()) {
				FacebookObject value = entry.getValue();
				writer.write(value.getId() + "," + value.getName() + "," + value.getGender() + "," + value.getId() + "," + value.getFullMessage() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static void merged(String inputFile, String compareFile) throws IOException {
		List<String> results = new ArrayList<String>();
		List<String> stts = new ArrayList<String>();
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(compareFile)), GeneralConstant.ENCODING_UTF8));
			String line;
			while ((line = buffReader.readLine()) != null) {
				results.add(Integer.parseInt(line) == 1 ? GeneralConstant.CLASSIFY.MALE : GeneralConstant.CLASSIFY.FEMALE);
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

		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputFile)), GeneralConstant.ENCODING_UTF8));
			String line;
			int i = 0;
			while ((line = buffReader.readLine()) != null) {
				String[] split = line.split(",");
				if (split[2].equals(results.get(i))) {
					stts.add(line);
				}
				i++;
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
			writer = new FileWriter("source/vn_tokenizer_status.csv");
			// Object predictList = null;
			for (String stt : stts) {
				writer.write(stt + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
