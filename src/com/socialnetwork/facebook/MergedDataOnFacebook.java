package com.socialnetwork.facebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map.Entry;

import com.socialnetwork.constant.GeneralConstant;
import com.socialnetwork.model.FacebookObject;

public class MergedDataOnFacebook {
	public static void main(String agr[]) throws IOException {
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
}
