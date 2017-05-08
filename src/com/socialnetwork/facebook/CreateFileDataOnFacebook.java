package com.socialnetwork.facebook;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.common.CommonUtils;
import com.socialnetwork.constant.GeneralConstant;
import com.socialnetwork.model.MessageObject;
import com.socialnetwork.model.UserObject;

public class CreateFileDataOnFacebook {

	public static void main(String agr[]) throws IOException {
		HashMap<String, UserObject> hashMap = new HashMap<String, UserObject>();
		List<String> listString = new ArrayList<String>();

		List<File> files = new ArrayList<File>();
		CommonUtils.readAllFileInFolder(new File(GeneralConstant.FORDER + GeneralConstant.SYMBOL.SLASH), files);
		BufferedReader buffReader = null;
		for (File file : files) {
			try {
				buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), GeneralConstant.ENCODING_UTF8));
				String line;
				while ((line = buffReader.readLine()) != null) {
					listString.add(line);
					String[] user = line.split(",", 5);
					if (user.length == 5) {
						if (!hashMap.containsKey(user[3])) {
							UserObject temp = new UserObject(user[0], user[1], user[2]);
							temp.addMessage(user[3], user[4]);
							hashMap.put(user[3], temp);
						}
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
		}
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(GeneralConstant.FULL_STATUS), GeneralConstant.ENCODING_UTF8));
			for (Entry<String, UserObject> entry : hashMap.entrySet()) {
				UserObject user = entry.getValue();
				List<MessageObject> feeds = user.getMessage();
				for (MessageObject feed : feeds) {
					String msg = feed.getMessage();
					msg = CommonUtils.formatString(msg);
					msg = Normalizer.normalize(msg, Normalizer.Form.NFKC);
					msg = msg.toLowerCase();
					if (msg == null || "".equals(msg.trim()) || CommonUtils.isRemoveStatus(msg)) {
						continue;
					}
					StringBuilder sb = new StringBuilder();
					sb.append(user.getId()).append(',');
					sb.append(user.getName()).append(',');
					sb.append(user.getGender()).append(',');
					sb.append(feed.getId()).append(',');
					sb.append(" " + msg + " ").append("\n");
					writer.write(sb.toString());
				}
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
