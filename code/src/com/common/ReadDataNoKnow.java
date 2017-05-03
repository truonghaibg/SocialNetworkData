package com.common;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.socialnetwork.constant.GeneralConstant;
import com.socialnetwork.facebook.FacebookUtils;
import com.socialnetwork.model.UserObject;

public class ReadDataNoKnow {
	private final static HashMap<String, UserObject> hashMap = new HashMap<String, UserObject>();

	public static void main(String agr[]) {
		long start = System.currentTimeMillis();
		FacebookClient facebookClient = new DefaultFacebookClient(GeneralConstant.FACEBOOK.ACCESS_TOKEN, Version.VERSION_2_8);
		String dataInput = "facebook/" + "PeopleNotKnow" + "_dataInput.csv";
		System.out.println("Start...");
		FacebookUtils.getInstance().fetchAllFriendsNoKnow(hashMap, "100007923240194", facebookClient);
		try {
			FileWriter writer = new FileWriter(dataInput);
			int i = 0;
			for (Entry<String, UserObject> entry : hashMap.entrySet()) {
				System.out.println(i++);
				StringBuilder sb = new StringBuilder();
				sb.append(entry.getKey()).append(',');
				sb.append(entry.getValue().getName()).append(',');
				sb.append(entry.getValue().getGender()).append(',');
				sb.append("").append("\n");
				writer.write(sb.toString());
			}
			writer.close();
		} catch (IOException e1) {
			long elapsedTimeMillis = System.currentTimeMillis() - start;
			float elapsedTimeMin = elapsedTimeMillis / (60 * 1000F);
			System.out.println("Time: " + elapsedTimeMin + " - End!");
			e1.printStackTrace();
		}
		long elapsedTimeMillis = System.currentTimeMillis() - start;
		float elapsedTimeMin = elapsedTimeMillis / (60 * 1000F);

		System.out.println("Time: " + elapsedTimeMin + " - End!");
	}
}
