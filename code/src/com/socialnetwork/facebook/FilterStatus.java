package com.socialnetwork.facebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.socialnetwork.constant.GeneralConstant;

public class FilterStatus {
	public static void main(String agr[]) throws IOException {
		List<String> strList = new ArrayList<String>();
		Set<String> blackList = new HashSet<String>(FilterStatus.loadWordInFile(GeneralConstant.BLACK_LIST_FILE));
		List<String> lists = FilterStatus.loadWordInFile(GeneralConstant.FILTER_FILE);

		FileWriter writer = null;
		int dem = 0;
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(GeneralConstant.FULL_STATUS)), GeneralConstant.ENCODING_UTF8));
			String line;

			while ((line = buffReader.readLine()) != null) {
				String[] user = line.split(",", 5);
				if (blackList.contains(user[0])) {
					continue;
				}
				if (user.length == 5 && !isContant(lists, user[4]) && checkLength(user[4], 5, 255) && checkNumberWord(user[4])) {
					strList.add(line);
				} else {
					dem++;
					System.out.println(user[4]);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println(dem);
			if (buffReader != null) {
				buffReader.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
		exportFile(strList);
	}

	public static void exportFile(List<String> strList) throws IOException {
		FileWriter writer = null;
		Map<String, String> statusMap = new HashMap<String, String>();
		Map<String, String> idPerson = new HashMap<String, String>();
		List<String> al = new ArrayList<String>();

		for (String stt : strList) {
			String[] user = stt.split(",", 5);
			idPerson.put(user[0], user[0] + "," + user[1] + "," + user[2]);
			al.add(user[4]);
			statusMap.put(user[3], stt);
		}
		Set<String> duplicates = findDuplicates(al);
		try {
			writer = new FileWriter(GeneralConstant.ID_FILE);
			for (Entry<String, String> entry : idPerson.entrySet()) {
				writer.write(entry.getValue() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		System.out.println("Before filer duplicate: " + statusMap.size());
		for (int i = 0; i < strList.size(); i++) {
			String stt = strList.get(i);
			String[] user = stt.split(",", 5);
			if (duplicates.contains(user[4])) {
				statusMap.remove(user[3]);
			}
		}
		System.out.println("After filer duplicate: " + statusMap.size());

		try {
			writer = new FileWriter(GeneralConstant.FULL_STATUS_FILTER);
			for (Entry<String, String> entry : statusMap.entrySet()) {
				writer.write(entry.getValue() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

		//
		// if (females != null) {
		// try {
		// writer = new FileWriter(GeneralConstant.FEMALE_FILE);
		// for (String entry : females) {
		// writer.write(entry + "\n");
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// } finally {
		// if (writer != null) {
		// writer.close();
		// }
		// }
		// }
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

	public static List<String> loadWordInFile(String nameFile) throws IOException {
		List<String> words = new ArrayList<String>();
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(nameFile)), GeneralConstant.ENCODING_UTF8));
			String line;
			while ((line = buffReader.readLine()) != null) {
				String[] wordInLines = line.split(",");
				for (String wordInLine : wordInLines) {
					if ("".equalsIgnoreCase(wordInLine.trim())) {
						continue;
					}
					words.add(wordInLine.trim());
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
		// words.add(" a ");
		words.add(" ă ");
		words.add(" â ");
		words.add(" b ");
		// words.add(" c ");
		words.add(" d ");
		words.add(" đ ");
		// words.add(" e ");
		words.add(" ê ");
		words.add(" g ");
		words.add(" h ");
		words.add(" i ");
		words.add(" k ");
		words.add(" l ");
		words.add(" m ");
		words.add(" n ");
		words.add(" o ");
		words.add(" p ");
		words.add(" q ");
		words.add(" r ");
		words.add(" s ");
		words.add(" t ");
		words.add(" u ");
		words.add(" ư ");
		words.add(" v ");
		words.add(" x ");
		words.add("đ ");
		words.add("ki ");

		return words;
	}

	public static List<String> loadBlackList(String nameFile) throws IOException {
		List<String> blackList = new ArrayList<String>();
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(nameFile)), GeneralConstant.ENCODING_UTF8));
			String line;
			while ((line = buffReader.readLine()) != null) {
				blackList.add(line.trim());
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
		return blackList;
	}

	public static boolean isContant(List<String> list, String str) {
		for (int i = 0; i < list.size(); i++) {
			if (str.toLowerCase().indexOf(list.get(i).toLowerCase()) != -1) {
				System.out.print(list.get(i) + " - ");
				return true;
			}
		}
		return false;
	}

	public static boolean checkLength(String str, int min, int max) {
		int length = str.split("\\s+").length;
		if (length >= min && length <= max) {
			return true;
		}
		return false;
	}

	public static boolean checkNumberWord(String str) {

		String[] words = str.split("\\s+");
		int letter = 0;
		int other = 0;
		for (String word : words) {
			word = word.trim();
			if (FacebookUtils.getInstance().isOnlyStrings(word)) {
				letter++;
			} else {
				other++;
			}
		}
		if (letter > other * 2) {
			return true;
		}
		return false;
	}

	public static void checkString(String str) {
		String[] list = str.split(" ");
		if (list != null && list.length != 0) {
			for (String ls : list) {
				String clean = Normalizer.normalize(ls, Form.NFKC).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
				boolean valid = clean.matches("\\p{L}+");
				if (valid) {
					System.out.println(clean);
				}
			}
		}
	}
}
