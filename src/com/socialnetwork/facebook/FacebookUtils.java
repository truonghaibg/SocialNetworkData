package com.socialnetwork.facebook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.NonSparseToSparse;

import com.common.CommonUtils;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.CategorizedFacebookType;
import com.restfb.types.Comment;
import com.restfb.types.Post;
import com.restfb.types.Post.Comments;
import com.restfb.types.Post.MessageTag;
import com.restfb.types.User;
import com.socialnetwork.constant.GeneralConstant;
import com.socialnetwork.model.FacebookObject;
import com.socialnetwork.model.MessageObject;
import com.socialnetwork.model.UserObject;

public class FacebookUtils {
	private static FacebookUtils INSTANCE = new FacebookUtils();

	public static FacebookUtils getInstance() {
		return FacebookUtils.INSTANCE;
	}

	public static void ngram(Map<String, Integer> wordMap, String str, String gramType) {
		// System.out.println(str);
		String[] words = str.split("\\s+");
		int start = 0, end = 0, size = words.length;
		for (int i = 0; i < size; i++) {
			// System.out.println(words[i]);
			FacebookUtils.getInstance().putWordIntoMap(wordMap, words[i]);
			if (!GeneralConstant.UNI_GRAM.equals(gramType)) {
				if (FacebookUtils.getInstance().isOnlySpecialCharacter(words[i]) || i == size) {
					end = i;
					for (int j = start; j < end - 1; j++) {
						String tmp = words[j] + "_" + words[j + 1];
						// System.out.println(tmp);
						FacebookUtils.getInstance().putWordIntoMap(wordMap, tmp);
					}
					if (!GeneralConstant.BI_GRAM.equals(gramType)) {
						for (int j = start; j < end - 2; j++) {
							String tmp = words[j] + "_" + words[j + 1] + "_" + words[j + 2];
							// System.out.println(tmp);
							FacebookUtils.getInstance().putWordIntoMap(wordMap, tmp);
						}
					}
					start = end + 1;
				}
			}

		}
	}

	public void createLibSVMFileWithTFIDF(Map<String, Integer> wordMap, List<FacebookObject> results) throws IOException {
		Integer allSize = results.size();
		// create the LibSVM file
		BufferedWriter writer = new BufferedWriter(new FileWriter(GeneralConstant.LIBSVM_FILE_FINAL_WINDOWS_TFIDF));

		// insert data into the LibSVM file
		for (FacebookObject result : results) {
			Integer sttSize = result.getTotalWord();
			Map<String, Integer> treeCateMap = new TreeMap<String, Integer>(result.getCountByWords());
			if (GeneralConstant.CLASSIFY.FEMALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.FEMALE_VALUE);
			} else if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.MALE_VALUE);
			}
			int i = 1;
			for (Entry<String, Integer> entry : wordMap.entrySet()) {
				if (treeCateMap.containsKey(entry.getKey())) {
					Integer wordSize = treeCateMap.get(entry.getKey());
					writer.write(" " + i + ":" + FacebookUtils.INSTANCE.TF_IDF(allSize, sttSize, wordSize));
				}
				i++;
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
	}

	public final void createLibSVMFileDefault(Map<String, Integer> wordMap, List<FacebookObject> results) throws IOException {
		// create the LibSVM file
		BufferedWriter writer = new BufferedWriter(new FileWriter(GeneralConstant.LIBSVM_FILE_FINAL_WINDOWS_1GRAM));

		// insert data into the LibSVM file
		for (FacebookObject result : results) {
			Map<String, Integer> treeCateMap = new TreeMap<String, Integer>(result.getCountByWords());
			if (GeneralConstant.CLASSIFY.FEMALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.FEMALE_VALUE);
			} else if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.MALE_VALUE);
			}
			int i = 1;
			for (Entry<String, Integer> entry : wordMap.entrySet()) {
				if (treeCateMap.containsKey(entry.getKey())) {
					writer.write(" " + i + ":" + treeCateMap.get(entry.getKey()));
				}
				i++;
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
	}

	public final void createLibSVMFileDefaultTwoFile(Map<String, Integer> wordMap, List<FacebookObject> results) throws IOException {
		// create the LibSVM file
		BufferedWriter trainingFile = new BufferedWriter(new FileWriter(GeneralConstant.LIBSVM_FILE_FINAL_WINDOWS_1GRAM));
		BufferedWriter testFile = new BufferedWriter(new FileWriter(GeneralConstant.LIBSVM_FILE_FINAL_WINDOWS_DEFAULT_TEST));

		// Split list object

		int size = results.size();
		int large = 10000;

		List<FacebookObject> trainingResults = results.subList(0, size - large);
		List<FacebookObject> testResults = results.subList(size - large, size);

		// insert data into the LibSVM file
		for (FacebookObject result : trainingResults) {
			Map<String, Integer> treeCateMap = new TreeMap<String, Integer>(result.getCountByWords());
			if (GeneralConstant.CLASSIFY.FEMALE.equalsIgnoreCase(result.getGender())) {
				trainingFile.write(GeneralConstant.CLASSIFY.FEMALE_VALUE);
			} else if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(result.getGender())) {
				trainingFile.write(GeneralConstant.CLASSIFY.MALE_VALUE);
			}
			int i = 1;
			for (Entry<String, Integer> entry : wordMap.entrySet()) {
				if (treeCateMap.containsKey(entry.getKey())) {
					trainingFile.write(" " + i + ":" + treeCateMap.get(entry.getKey()));
				}
				i++;
			}
			trainingFile.write("\n");
		}
		trainingFile.flush();
		trainingFile.close();

		// insert data into the LibSVM file
		for (FacebookObject result : testResults) {
			Map<String, Integer> treeCateMap = new TreeMap<String, Integer>(result.getCountByWords());
			if (GeneralConstant.CLASSIFY.FEMALE.equalsIgnoreCase(result.getGender())) {
				testFile.write(GeneralConstant.CLASSIFY.FEMALE_VALUE);
			} else if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(result.getGender())) {
				testFile.write(GeneralConstant.CLASSIFY.MALE_VALUE);
			}
			int i = 1;
			for (Entry<String, Integer> entry : wordMap.entrySet()) {
				if (treeCateMap.containsKey(entry.getKey())) {
					testFile.write(" " + i + ":" + treeCateMap.get(entry.getKey()));
				}
				i++;
			}
			testFile.write("\n");
		}
		testFile.flush();
		testFile.close();
	}

	public final void createBinaryFile(Map<String, Integer> wordMap, List<FacebookObject> results) throws IOException {
		// create the Binary file
		BufferedWriter writer = new BufferedWriter(new FileWriter(GeneralConstant.LIBSVM_FILE_FINAL_WINDOWS_BINARY));

		// insert data into the Binary file
		for (FacebookObject result : results) {
			Map<String, Integer> treeCateMap = new TreeMap<String, Integer>(result.getCountByWords());
			if (GeneralConstant.CLASSIFY.FEMALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.FEMALE_VALUE);
			} else if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.MALE_VALUE);
			}
			int i = 1;
			for (Entry<String, Integer> entry : wordMap.entrySet()) {
				if (treeCateMap.containsKey(entry.getKey())) {
					writer.write(" " + i + ":" + 1);
				}
				i++;
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
	}

	public final void create1GramFile(Map<String, Integer> wordMap, List<FacebookObject> results) throws IOException {
		// create the LibSVM file
		BufferedWriter writer = new BufferedWriter(new FileWriter(GeneralConstant.LIBSVM_FILE_FINAL_WINDOWS_1GRAM));

		// insert data into the LibSVM file
		for (FacebookObject result : results) {
			Map<String, Integer> treeCateMap = new TreeMap<String, Integer>(result.getCountByWords());
			if (GeneralConstant.CLASSIFY.FEMALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.FEMALE_VALUE);
			} else if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.MALE_VALUE);
			}
			int i = 1;
			for (Entry<String, Integer> entry : wordMap.entrySet()) {
				if (treeCateMap.containsKey(entry.getKey())) {
					writer.write(" " + i + ":" + treeCateMap.get(entry.getKey()));
				}
				i++;
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
	}

	public final void create2GramFile(List<FacebookObject> results) throws IOException {
		// create the Binary file
		BufferedWriter writer = new BufferedWriter(new FileWriter(GeneralConstant.LIBSVM_FILE_FINAL_WINDOWS_2GRAM));

		Map<String, Integer> twoGram = new LinkedHashMap<String, Integer>();
		// insert data into the Binary file
		for (FacebookObject result : results) {
			Map<String, Integer> twoGramTemp = new LinkedHashMap<String, Integer>();
			String mgs = result.getMessage().trim();
			mgs = mgs.replaceAll("\\d+", GeneralConstant.DIGIT);
			mgs = CommonUtils.formatString(mgs);

			String[] words = mgs.split("\\s+");
			int length = words.length;
			for (int i = 0; i < length - 1; i++) {
				String word = words[i].trim() + " " + words[i + 1].trim();
				Integer count = twoGramTemp.get(word);
				if (count == null) {
					twoGramTemp.put(word, 1);
				} else {
					twoGramTemp.put(word, (count + 1));
				}
				count = twoGram.get(word);
				if (count == null) {
					twoGram.put(word, 1);
				} else {
					twoGram.put(word, (count + 1));
				}
			}
			result.setTwoGram(twoGramTemp);
		}
		System.out.println("Size of 2gram: " + twoGram.size());
		int i = 1;
		// Begin: remove words
		FacebookUtils.getInstance().removeWord(twoGram);
		// End: remove words
		for (FacebookObject result : results) {
			if (GeneralConstant.CLASSIFY.FEMALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.FEMALE_VALUE);
			} else if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.MALE_VALUE);
			}
			i = 1;
			for (Entry<String, Integer> entry : twoGram.entrySet()) {
				if (result.getTwoGram().containsKey(entry.getKey())) {
					writer.write(" " + i + ":" + result.getTwoGram().get(entry.getKey()));
				}
				i++;
			}
			writer.write("\n");
		}

		writer.flush();
		writer.close();
	}

	public final void create3GramFile(List<FacebookObject> results) throws IOException {
		// create the Binary file
		BufferedWriter writer = new BufferedWriter(new FileWriter(GeneralConstant.LIBSVM_FILE_FINAL_WINDOWS_3GRAM));

		Map<String, Integer> threeGram = new LinkedHashMap<String, Integer>();
		// insert data into the Binary file
		for (FacebookObject result : results) {
			Map<String, Integer> threeGramTemp = new LinkedHashMap<String, Integer>();
			String mgs = result.getMessage().trim();
			String[] words = mgs.split("\\s+");
			int length = words.length;
			for (int i = 0; i < length; i++) {
				if (isOnlyDigits(words[i])) {
					words[i] = GeneralConstant.DIGIT;
				}
			}
			for (int i = 0; i < length - 2; i++) {
				String word = words[i].trim() + " " + words[i + 1].trim() + " " + words[i + 2].trim();
				Integer count = threeGramTemp.get(word);
				if (count == null) {
					threeGramTemp.put(word, 1);
				} else {
					threeGramTemp.put(word, (count + 1));
				}
				count = threeGram.get(word);
				if (count == null) {
					threeGram.put(word, 1);
				} else {
					threeGram.put(word, (count + 1));
				}
			}
			result.setThreeGram(threeGramTemp);
		}
		// Begin: remove words
		FacebookUtils.getInstance().removeWord(threeGram);
		// End: remove words
		System.out.println("Size of 3gram: " + threeGram.size());
		int i = 1;
		for (FacebookObject result : results) {
			if (GeneralConstant.CLASSIFY.FEMALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.FEMALE_VALUE);
			} else if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.MALE_VALUE);
			}
			i = 1;
			for (Entry<String, Integer> entry : threeGram.entrySet()) {
				if (result.getThreeGram().containsKey(entry.getKey())) {
					writer.write(" " + i + ":" + result.getThreeGram().get(entry.getKey()));
				}
				i++;
			}
			writer.write("\n");
		}

		writer.flush();
		writer.close();
	}

	public final void create1GramAnd2Gram(List<FacebookObject> results) throws IOException {
		// create the Binary file
		BufferedWriter writer = new BufferedWriter(new FileWriter(GeneralConstant.LIBSVM_FILE_FINAL_WINDOWS_1GRAM_2GRAM));

		Map<String, Integer> mergedGram = new LinkedHashMap<String, Integer>();
		// insert data into the Binary file
		for (FacebookObject result : results) {
			Map<String, Integer> mergedGramTemp = new LinkedHashMap<String, Integer>();
			String mgs = result.getMessage().trim();
			String[] words = mgs.split("\\s+");
			int length = words.length;
			for (int i = 0; i < length; i++) {
				if (isOnlyDigits(words[i])) {
					words[i] = GeneralConstant.DIGIT;
				}
			}
			int i = 0;
			for (i = 0; i < length - 1; i++) {
				String word = words[i].trim() + " " + words[i + 1].trim();
				Integer count = mergedGramTemp.get(word);
				if (count == null) {
					mergedGramTemp.put(word, 1);
				} else {
					mergedGramTemp.put(word, (count + 1));
				}
				count = mergedGram.get(word);
				if (count == null) {
					mergedGram.put(word, 1);
				} else {
					mergedGram.put(word, (count + 1));
				}

				word = words[i].trim();
				count = mergedGramTemp.get(word);
				if (count == null) {
					mergedGramTemp.put(word, 1);
				} else {
					mergedGramTemp.put(word, (count + 1));
				}
				count = mergedGram.get(word);
				if (count == null) {
					mergedGram.put(word, 1);
				} else {
					mergedGram.put(word, (count + 1));
				}
			}
			result.setMergedGram(mergedGramTemp);
		}
		// Begin: remove words
		FacebookUtils.getInstance().removeWord(mergedGram);
		// End: remove words
		System.out.println("Size of 1gram + 2gram: " + mergedGram.size());
		int i = 1;
		for (FacebookObject result : results) {
			if (GeneralConstant.CLASSIFY.FEMALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.FEMALE_VALUE);
			} else if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.MALE_VALUE);
			}
			i = 1;
			for (Entry<String, Integer> entry : mergedGram.entrySet()) {
				if (result.getThreeGram().containsKey(entry.getKey())) {
					writer.write(" " + i + ":" + result.getThreeGram().get(entry.getKey()));
				}
				i++;
			}
			writer.write("\n");
		}

		writer.flush();
		writer.close();
	}

	public void fetchAllFriends(List<UserObject> friends, String idOwn, FacebookClient facebookClient) {
		Connection<User> fetchFriends = facebookClient.fetchConnection(idOwn + "/friends", User.class, Parameter.with("limit", Integer.valueOf(5000)),
				Parameter.with("fields", "id,name,gender"));
		if (fetchFriends != null && !fetchFriends.getData().isEmpty()) {
			List<User> listFriend = fetchFriends.getData();
			for (User user : listFriend) {
				if (user.getGender() == null) {
					continue;
				}
				UserObject friend = new UserObject(user.getId(), user.getName(), user.getGender());
				friends.add(friend);
			}
		} else {
			return;
		}
	}

	public void fetchAllFriendsNoKnow(HashMap<String, UserObject> friends, String idOwn, FacebookClient facebookClient) {
		Connection<User> fetchFriends = null;
		try {
			fetchFriends = facebookClient.fetchConnection(idOwn + "/friends", User.class, Parameter.with("limit", Integer.valueOf(5000)),
					Parameter.with("fields", "id,name,gender"));
		} catch (Exception e) {
			return;
		}
		if (fetchFriends != null && !fetchFriends.getData().isEmpty()) {
			List<User> listFriend = fetchFriends.getData();
			for (User user : listFriend) {

				if (user.getGender() == null || friends.containsKey(user.getId())) {
					continue;
				}
				UserObject friend = new UserObject(user.getId(), user.getName(), user.getGender());
				friends.put(user.getId(), friend);
				System.out.println(friends.size() + " - " + user.getId());
				if (friends.size() < 10) {
					FacebookUtils.getInstance().fetchAllFriendsNoKnow(friends, user.getId(), facebookClient);
				}
			}
		}
	}

	// TODO
	public void fetchAllFeedWithUrl(List<MessageObject> feeds, String idOwn, String url, FacebookClient facebookClient) {
		Connection<Post> fetchFeeds = null;
		if (url != null && !"".equals(url)) {
			fetchFeeds = facebookClient.fetchConnectionPage(url, Post.class);
		} else {
			fetchFeeds = facebookClient.fetchConnection(idOwn + "/posts", Post.class, Parameter.with("limit", Integer.valueOf(5000)),
					Parameter.with("fields", "message_tags,message"));
		}
		if (fetchFeeds != null && !fetchFeeds.getData().isEmpty()) {
			String nextPage = fetchFeeds.getNextPageUrl();
			if (nextPage != null && !"".equals(nextPage)) {
				FacebookUtils.getInstance().fetchAllFeedWithUrl(feeds, idOwn, fetchFeeds.getNextPageUrl(), facebookClient);
			}
			List<Post> listFeed = fetchFeeds.getData();
			for (Post feed : listFeed) {
				String id = feed.getId();
				if (id.indexOf(idOwn) == -1) {
					continue;
				}
				List<MessageTag> messageTags = feed.getMessageTags();
				if (!messageTags.isEmpty()) {
					continue;
				}
				String msg = feed.getMessage();
				if (msg == null || "".equals(msg)) {
					continue;
				}
				feeds.add(new MessageObject(id, msg));
			}
		}
	}

	public String fetchAllFeedString(String idOwn, FacebookClient facebookClient) {
		StringBuilder strAll = new StringBuilder();
		List<MessageObject> feeds = new ArrayList<MessageObject>();
		FacebookUtils.getInstance().fetchAllFeedWithUrl(feeds, idOwn, null, facebookClient);
		for (MessageObject feed : feeds) {
			strAll.append(" ").append(feed.getMessage());
		}
		return CommonUtils.removeUrl(strAll.toString());
	}

	public List<MessageObject> fetchAllFeedList(String idOwn, FacebookClient facebookClient, int min) {
		List<MessageObject> feeds = new ArrayList<MessageObject>();
		FacebookUtils.getInstance().fetchAllFeedWithUrl(feeds, idOwn, null, facebookClient);
		int size = feeds.size();
		if (size < min) {
			return new ArrayList<MessageObject>();
		}
		System.out.println("Size: " + size);
		return feeds;
	}

	public String fetchAllFeedAndComment(String idUser, FacebookClient facebookClient) {
		StringBuilder strAll = new StringBuilder();
		List<MessageObject> feeds = new ArrayList<MessageObject>();
		FacebookUtils.getInstance().fetchAllFeedWithUrl(feeds, idUser, null, facebookClient);
		for (MessageObject feed : feeds) {
			strAll.append(" ").append(feed.getMessage());
			strAll.append(" ").append(FacebookUtils.getInstance().fetchAllComments(idUser, feed.getId(), facebookClient));
		}
		return CommonUtils.removeUrl(strAll.toString());
	}

	public String fetchAllComments(String idUser, String idFeed, FacebookClient facebookClient) {
		if (idFeed == null || idFeed.isEmpty()) {
			return "";
		}
		StringBuilder strAll = new StringBuilder();
		Post detailPost = null;
		try {
			detailPost = facebookClient.fetchObject(idFeed, Post.class, Parameter.with("fields", "comments.limit(5000){comments.limit(5000),from,message}"));
			Comments comments = detailPost.getComments();
			if (comments != null) {
				List<Comment> parents = comments.getData();
				for (Comment dataTempLevel1 : parents) {
					CategorizedFacebookType fromLevel1 = dataTempLevel1.getFrom();
					if (idUser.equals(fromLevel1.getId())) {
						strAll.append(" ").append(dataTempLevel1.getMessage());
					}
					// com.restfb.types.Comment.Comments child =
					// dataTempLevel1.getComments();
					// if (child != null) {
					// List<Comment> listLevel2 = child.getData();
					// for (Comment dataTempLevel2 : listLevel2) {
					// CategorizedFacebookType fromLevel2 =
					// dataTempLevel2.getFrom();
					// if (idUser.equals(fromLevel2.getId())) {
					// strAll.append(" ").append(dataTempLevel2.getMessage());
					// }
					// }
					// }
				}
			}
		} catch (Exception e) {
			return "";
		}
		return strAll.toString();
	}

	public void main(String agr[]) {
		FacebookClient facebookClient = new DefaultFacebookClient(GeneralConstant.FACEBOOK.ACCESS_TOKEN, Version.VERSION_2_8);
		String str = FacebookUtils.getInstance().fetchAllFeedString("me", facebookClient);
		System.out.println(str);
		try {
			FileWriter writer = new FileWriter(new File("test.csv"));
			writer.write(str);
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void feebWordFromString(Map<String, Integer> wordMap, String str) throws IOException {
		str = CommonUtils.formatString(str);
		String[] list = str.split(" ");
		int size = list.length;
		for (int i = 0; i < size; i++) {
			String next = list[i];
			next = Normalizer.normalize(next, Normalizer.Form.NFKC);
			if (!(FacebookUtils.getInstance().isOnlyDigits(next) || FacebookUtils.getInstance().isOnlyStrings(next) || FacebookUtils.getInstance()
					.isOnlySpecialCharacter(next))) {
				String subNext = FacebookUtils.getInstance().splitString(next);
				String abc = CommonUtils.formatString(subNext);
				String[] subList = abc.split(" ");
				int subSize = subList.length;
				for (int ii = 0; ii < subSize; ii++) {
					putWordIntoMap(wordMap, subList[ii]);
				}
			} else {
				putWordIntoMap(wordMap, next);
			}
		}
	}

	@SuppressWarnings("resource")
	public void feebWordFromStringScanner(Map<String, Integer> wordMap, String str) throws IOException {
		Scanner scanner = new Scanner(str);
		while (scanner.hasNext()) {
			String next = scanner.next();
			putWordIntoMap(wordMap, next);
		}
	}

	public void putWordIntoMap(Map<String, Integer> wordMap, String word) {
		// if (isOnlyDigits(word)) {
		// word = GeneralConstant.DIGIT;
		// }
		// if (isHashTag(word)) {
		// word = GeneralConstant.HASH_TAG;
		// }
		Integer count = wordMap.get(word);
		if (count == null) {
			wordMap.put(word, 1);
		} else {
			wordMap.put(word, (count + 1));
		}
	}

	public void createFileWeka(Map<String, Integer> wordMap, List<FacebookObject> results) throws IOException {
		ArrayList<Attribute> atts;
		ArrayList<String> attVals;
		Instances data;
		double[] vals;
		// 1. set up attributes
		atts = new ArrayList<Attribute>();
		for (Entry<String, Integer> entry : wordMap.entrySet()) {
			atts.add(new Attribute(entry.getKey()));
		}
		attVals = new ArrayList<String>();
		attVals.add(GeneralConstant.CLASSIFY.MALE);
		attVals.add(GeneralConstant.CLASSIFY.FEMALE);
		atts.add(new Attribute(GeneralConstant.CLASS, attVals));
		data = new Instances(GeneralConstant.FILE_WEKA_NAME, atts, 0);

		BufferedWriter writer = new BufferedWriter(new FileWriter(GeneralConstant.FILE_WEKA_NAME_ARFF));
		writer.write(data.toString());
		writer.flush();
		writer.close();

		// Reopen write weka file
		BufferedWriter out = null;
		int count = 0;
		try {
			FileWriter fstream = new FileWriter("FacebookData.arff", true);
			out = new BufferedWriter(fstream);
			for (FacebookObject result : results) {
				System.out.println(count++);
				int i = 0;
				Map<String, Integer> word = result.getCountByWords();
				vals = new double[wordMap.size()];
				for (Entry<String, Integer> entry : wordMap.entrySet()) {
					if (word.containsKey(entry.getKey())) {
						vals[i] = word.get(entry.getKey());
					} else {
						vals[i] = 0;
					}
					i++;
				}
				DenseInstance dense = new DenseInstance(1.0, vals);
				out.write(dense.toString() + "," + result.getGender() + "\n");
			}
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public void createFileWekaDefault(Map<String, Integer> wordMap, List<FacebookObject> results) throws IOException {
		ArrayList<Attribute> atts;
		ArrayList<String> attVals;
		Instances dataSet;
		double[] vals;
		// 1. set up attributes
		atts = new ArrayList<Attribute>();
		for (Entry<String, Integer> entry : wordMap.entrySet()) {
			atts.add(new Attribute(entry.getKey()));
		}
		attVals = new ArrayList<String>();
		attVals.add(GeneralConstant.CLASSIFY.MALE);
		attVals.add(GeneralConstant.CLASSIFY.FEMALE);
		atts.add(new Attribute(GeneralConstant.CLASS, attVals));

		// 2. create Instances object
		dataSet = new Instances(GeneralConstant.FILE_WEKA_NAME, atts, 0);
		// 3. fill with data
		for (FacebookObject result : results) {
			int i = 0;
			Map<String, Integer> word = result.getCountByWords();
			vals = new double[dataSet.numAttributes()];
			for (Entry<String, Integer> entry : wordMap.entrySet()) {
				if (word.containsKey(entry.getKey())) {
					vals[i] = word.get(entry.getKey());
				} else {
					vals[i] = 0;
				}
				i++;
			}
			if (GeneralConstant.CLASSIFY.FEMALE.equalsIgnoreCase(result.getGender())) {
				vals[i] = attVals.indexOf(GeneralConstant.CLASSIFY.FEMALE);
			} else if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(result.getGender())) {
				vals[i] = attVals.indexOf(GeneralConstant.CLASSIFY.MALE);
			}
			dataSet.add(new DenseInstance(1.0, vals));
		}
		// 4. export file ARFF
		try {
			NonSparseToSparse nonSTS = new NonSparseToSparse();
			nonSTS.setInputFormat(dataSet);
			Instances sparseDataset = Filter.useFilter(dataSet, nonSTS);
			ArffSaver arffSaver = new ArffSaver();
			arffSaver.setInstances(sparseDataset);
			arffSaver.setFile(new File(GeneralConstant.FILE_WEKA_NAME_ARFF));
			arffSaver.writeBatch();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Kiểm tra a string chỉ chứa digits không
	public boolean isOnlyDigits(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public boolean isNumberInWord(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	// Kiểm tra a string chỉ chứa moneys không
	public boolean isMoney(String s) {
		int size = s.length();
		if (s.charAt(size - 1) != 'k' || s.charAt(size - 1) != 'm') {
			return false;
		}
		for (int i = 0; i < s.length() - 1; i++) {
			if (!Character.isDigit(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	// Kiểm tra string chỉ chứa các letters không
	public boolean isOnlyStrings(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isLetter(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	// Kiểm tra string chỉ chứa các special chars không
	public boolean isOnlySpecialCharacter(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isLetter(s.charAt(i)) || Character.isDigit(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	// Kiểm tra string la HashTag không?
	public boolean isHashTag(String s) {
		if (s == null || s.length() == 1 || s.charAt(0) != '#') {
			return false;
		}
		return true;
	}

	public String splitString(String s) {
		if (s == null || "".equals(s.trim()) || s.trim().length() <= 2) {
			return s;
		}
		StringBuilder result = new StringBuilder();
		int i = 0, j = 0;
		int length = s.length();
		for (i = 0; i < length; i++) {
			char charAt = s.charAt(i);
			result.append(charAt);
			if (Character.isLetter(charAt)) {
				for (j = i + 1; j < length; j++) {
					if (Character.isLetter(s.charAt(j))) {
						result.append(s.charAt(j));
					} else {
						result.append(" ");
						i = j - 1;
						break;
					}
				}
				i = j - 1;
			} else if (Character.isDigit(charAt)) {
				for (j = i + 1; j < length; j++) {
					if (Character.isDigit(s.charAt(j))) {
						result.append(s.charAt(j));
					} else {
						result.append(" ");
						i = j - 1;
						break;
					}
				}
				i = j - 1;
			} else {
				for (j = i + 1; j < length; j++) {
					if (!Character.isLetter(s.charAt(j)) && !Character.isDigit(s.charAt(j))) {
						result.append(s.charAt(j));
					} else {
						result.append(" ");
						i = j - 1;
						break;
					}
				}
				i = j - 1;
			}
		}
		return result.toString();
	}

	public String removeDiacriticalMarks(String string) {
		return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	public boolean removeWord(Entry<String, Integer> entry) {
		String key = entry.getKey();
		Integer value = entry.getValue();
		if (value < 5) {
			return true;
		}
		if (key.length() <= 1 || FacebookUtils.getInstance().isNumberInWord(key)) {
			return true;
		}

		// if (key.split("_").length == 1) {
		// System.out.println(key);
		// }

		return false;
	}

	public void removeWord(Map<String, Integer> wordMap) {
		Iterator<Entry<String, Integer>> iterator = wordMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Integer> entry = iterator.next();
			if (FacebookUtils.getInstance().removeWord(entry)) {
				iterator.remove();
			}
		}
	}

	// Kiểm tra một word chỉ bao gồm letter có các char duplicate không
	// Nếu word = null || độ dài <5 || có letter gần nhau duplicate thì
	// return true
	// Else return false
	@SuppressWarnings({ "unused", "null" })
	public boolean isDuplicateLetter(String s) {
		if (s == null && s.length() < 5) {
			return false;
		}
		char start = s.charAt(0);
		for (int i = 1; i < s.length(); i++) {
			if (s.charAt(i) != start) {
				start = s.charAt(i);
			}
			return true;

		}
		return false;
	}

	// Remove các char đứng cạnh nhau duplicate
	// Example: ahhi => return ahi
	public String removeDuplicateCharactersFromWord(String word) {
		if (word == null || word.length() == 0) {
			return "";
		}
		StringBuilder result = new StringBuilder("");
		char start = word.charAt(0);
		result.append(start);
		for (int i = 1; i < word.length(); i++) {
			if (word.charAt(i) != start) {
				start = word.charAt(i);
				result.append(start);
			}
		}
		return result.toString();
	}

	public double TF(Integer sttSize, Integer wordSize) {
		if (sttSize == 0 || wordSize == 0) {
			return 0;
		}
		Integer.valueOf(sttSize);
		return (double) wordSize / sttSize;
	}

	public double IDF(Integer allSize, Integer wordSize) {
		if (allSize == 0 || wordSize == 0) {
			return 0;
		}
		return Math.log10((double) allSize / wordSize);
	}

	public double TF_IDF(Integer allSize, Integer sttSize, Integer wordSize) {
		double temp = TF(sttSize, wordSize) * IDF(allSize, wordSize);
		temp = Math.round(temp * 1000);
		temp = temp / 1000;
		return temp;
	}

	public void handleLibSVMFake(Map<String, Integer> wordMap, List<FacebookObject> results) throws IOException {
		Map<String, Integer> treeCateOrder = new TreeMap<String, Integer>(wordMap);
		Integer allSize = results.size();
		// create the LibSVM file
		BufferedWriter writer = new BufferedWriter(new FileWriter("C:/libsvm-3.21/tools/test_file_fake.libsvm"));
		BufferedWriter ifidfWriter = new BufferedWriter(new FileWriter("C:/libsvm-3.21/tools/test_file_fake_ifidf.libsvm"));
		BufferedWriter binary = new BufferedWriter(new FileWriter("C:/libsvm-3.21/tools/test_file_fake_binary.libsvm"));
		FacebookUtils.getInstance().create2GramFile(results);
		// insert data into the LibSVM file
		for (FacebookObject result : results) {
			Integer sttSize = result.getTotalWord();
			Map<String, Integer> treeCateMap = new TreeMap<String, Integer>(result.getCountByWords());
			if (GeneralConstant.CLASSIFY.FEMALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.FEMALE_VALUE);
				ifidfWriter.write(GeneralConstant.CLASSIFY.FEMALE_VALUE);
				binary.write(GeneralConstant.CLASSIFY.FEMALE_VALUE);
			} else if (GeneralConstant.CLASSIFY.MALE.equalsIgnoreCase(result.getGender())) {
				writer.write(GeneralConstant.CLASSIFY.MALE_VALUE);
				ifidfWriter.write(GeneralConstant.CLASSIFY.MALE_VALUE);
				binary.write(GeneralConstant.CLASSIFY.MALE_VALUE);
			}
			int i = 1;
			for (Entry<String, Integer> entry : treeCateOrder.entrySet()) {
				if (treeCateMap.containsKey(entry.getKey())) {
					writer.write(" " + i + ":" + treeCateMap.get(entry.getKey()));
					Integer wordSize = treeCateMap.get(entry.getKey());
					ifidfWriter.write(" " + i + ":" + FacebookUtils.INSTANCE.TF_IDF(allSize, sttSize, wordSize));
					binary.write(" " + i + ":" + 1);
				}
				i++;
			}
			writer.write("\n");
			ifidfWriter.write("\n");
			binary.write("\n");
		}
		writer.flush();
		writer.close();
		ifidfWriter.flush();
		ifidfWriter.close();
		binary.flush();
		binary.close();
	}
}
