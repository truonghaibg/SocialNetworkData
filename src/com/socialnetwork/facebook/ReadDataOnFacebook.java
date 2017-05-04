package com.socialnetwork.facebook;

import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.common.CommonUtils;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.User;
import com.socialnetwork.constant.GeneralConstant;
import com.socialnetwork.model.MessageObject;
import com.socialnetwork.model.UserObject;

public class ReadDataOnFacebook {
	private final static List<UserObject> friends = new ArrayList<UserObject>();

	public static void main(String agr[]) {
		// String access_token =
		// JOptionPane.showInputDialog("Nhập Access token:");
		// if (access_token == null) {
		// System.exit(1);
		// }
		String access_token = "EAACEdEose0cBAGx85us0oTd1CgEz644nPzW8mm1gwkvdAlXZBrJGeZCZAoLIcqRBJJjxWeVBZAEDp714F67ZAoVX5wAevQGWRR1RfU7ZCZBdlgSPHkOyGay7AVZCjKMNv8DHt5FlfmBPz2vGFXYpED4iz9jkBKQ0VgqGSeikZBulmZCXat7gzExoqJSZC5DRD9mBm4ZD";
		long start = System.currentTimeMillis();
		FacebookClient facebookClient = new DefaultFacebookClient(access_token, Version.VERSION_2_8);
		User me = facebookClient.fetchObject("me", User.class);
		String dataInput = GeneralConstant.FORDER + GeneralConstant.SYMBOL.SLASH + me.getId() + "_" + me.getName() + "_dataInput.csv";
		System.out.println("Start...");
		FacebookUtils.getInstance().fetchAllFriends(friends, "me", facebookClient);
		HashSet<UserObject> uniqueFriends = new HashSet<UserObject>(friends);
		try {
			FileWriter writer = new FileWriter(dataInput);
			int count = 0;
			for (UserObject user : uniqueFriends) {
				List<MessageObject> feeds = new ArrayList<MessageObject>();
				try {
					feeds = FacebookUtils.getInstance().fetchAllFeedList(user.getId(), facebookClient, 50);
				} catch (Exception e) {
					continue;
				}
				if (feeds.isEmpty()) {
					continue;
				}
				System.out.println(count++);
				for (MessageObject feed : feeds) {
					String msg = CommonUtils.formatString(feed.getMessage());
					msg = Normalizer.normalize(msg, Normalizer.Form.NFKC);
					if (msg == null || "".equals(msg) || CommonUtils.isRemoveStatus(msg)) {
						continue;
					}
					String name = user.getName();
					name = CommonUtils.formatString(name);
					name = Normalizer.normalize(name, Normalizer.Form.NFKC);
					StringBuilder sb = new StringBuilder();
					sb.append(user.getId()).append(',');
					sb.append(name).append(',');
					sb.append(user.getGender()).append(',');
					sb.append(feed.getId()).append(',');
					sb.append(msg).append("\n");
					writer.write(sb.toString());
				}
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
