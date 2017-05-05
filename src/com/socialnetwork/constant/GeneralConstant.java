package com.socialnetwork.constant;

public final class GeneralConstant {
	public class FACEBOOK {
		public static final String MY_NAME = "Truong Hai";
		public static final String MY_ID = "100001092470878";
		public static final String ACCESS_TOKEN = "EAACEdEose0cBAGx85us0oTd1CgEz644nPzW8mm1gwkvdAlXZBrJGeZCZAoLIcqRBJJjxWeVBZAEDp714F67ZAoVX5wAevQGWRR1RfU7ZCZBdlgSPHkOyGay7AVZCjKMNv8DHt5FlfmBPz2vGFXYpED4iz9jkBKQ0VgqGSeikZBulmZCXat7gzExoqJSZC5DRD9mBm4ZD";
		public static final String APP_ID = "908282755970407";
		public static final String APP_SECRET = "568ac13a32514e0cfe7d552199d457e9";
	}

	public class SYMBOL {
		public static final String DOTS = ".";
		public static final String SPACE = " ";
		public static final String UNDERSCORE = "_";
		public static final String SLASH = "/";
	}

	public class CLASSIFY {
		public static final String MALE = "male";
		public static final String MALE_VALUE = "+1";
		public static final String FEMALE = "female";
		public static final String FEMALE_VALUE = "-1";
	}

	public static final String ENCODING_UTF8 = "UTF-8";

	// public static String DATA_FILE_CSV =
	// "D:/Personal/workspace/SocialNetworkData/data/dataFacebook_v3.csv";
	public static String DATA_FILE_CSV = "E:/Project_J2EE/workspace/SocialNetworkData/data/vn_tokenizer_dataFacebook_x33.csv";
	public static String FORDER = "facebook_v3";
	public static String CLASS = "#predict_class#";
	public static String FILE_WEKA_NAME = "FacebookData";
	public static String FILE_WEKA_NAME_ARFF = "FacebookData.arff";
	public static String FULL_STATUS = "source/full_status.csv";
	public static String FULL_STATUS_FILTER = "source/full_status_filter.csv";
	public static String MALE_FILE = "source/full_male.csv";
	public static String ID_FILE = "source/list_ID.csv";
	public static String FEMALE_FILE = "source/full_female.csv";
	public static String FILTER_FILE = "source/filter.data";
	public static String BLACK_LIST_FILE = "source/black_list.data";
	public static String FILTER_WORD = "source/filter_word.csv";
	public static String ORIGIN_WORD = "source/origin_word.csv";
	public static String VN_TOKENIZER_STATUS = "source/vn_tokenizer_status.csv";
	public static String VN_TOKENIZER_STATUS_MERGED = "source/vn_tokenizer_status_merged.csv";
	public static String LIBSVM_FILE_FINAL = "source/facebook_status.libsvm";
	public static String LIBSVM_FILE_FINAL_WINDOWS_DEFAULT_TEST = "C:/libsvm-3.21/tools/facebook_default_test.libsvm";

	public static String LIBSVM_FILE_FINAL_WINDOWS_TFIDF = "C:/libsvm-3.21/tools/facebook_tfidf.libsvm";
	public static String LIBSVM_FILE_FINAL_WINDOWS_BINARY = "C:/libsvm-3.21/tools/facebook_binary.libsvm";
	public static String LIBSVM_FILE_FINAL_WINDOWS_1GRAM = "C:/libsvm-3.21/tools/facebook_1gram.libsvm";
	public static String LIBSVM_FILE_FINAL_WINDOWS_2GRAM = "C:/libsvm-3.21/tools/facebook_2gram.libsvm";
	public static String LIBSVM_FILE_FINAL_WINDOWS_3GRAM = "C:/libsvm-3.21/tools/facebook_3gram.libsvm";

	public static String DIGIT = "#digit";
	public static String SIGN_CHAR = "#signchar";
	public static String HASH_TAG = "#hashtag";
	public static String LETTER_LONG = "#letterlong";
	public static String TIME = "#time";
	public static String MONEY = "#money";

	public static String INPUT_FILE_VNTOKENIZER = "D:/Personal/workspace/SocialNetworkData/source/full_status_filter.csv";
	public static String OUTPUT_FILE_VNTOKENIZER = "D:/Personal/workspace/SocialNetworkData/source/vn_tokenizer_status.csv";
	public static String TEST_FILE = "source/test_file_fake.csv";

}
