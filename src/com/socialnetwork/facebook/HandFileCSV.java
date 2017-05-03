package com.socialnetwork.facebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.socialnetwork.constant.GeneralConstant;

public class HandFileCSV {
	public static String DATA_FILE = "data/dataFacebook_v22.csv";

	public static void main(String agr[]) throws IOException {

		FileWriter writer = null;
		List<String> strList = new ArrayList<String>();
		BufferedReader buffReader = null;
		try {
			buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(DATA_FILE)), GeneralConstant.ENCODING_UTF8));
			String line;

			while ((line = buffReader.readLine()) != null) {
				strList.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buffReader != null) {
				buffReader.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
		int oder = 1;
		int n = strList.size();
		int start = 0, ii;
		while (start < n) {
			try {
				writer = new FileWriter("data/dataFacebook_" + oder + ".csv");
				for (ii = 0; ii < 100000 && start < n; ii++) {
					writer.write(strList.get(start) + "\n");
					start++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
			oder++;
		}

	}
}
