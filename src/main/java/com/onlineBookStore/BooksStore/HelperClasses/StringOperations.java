package com.onlineBookStore.BooksStore.HelperClasses;

public class StringOperations {

	static public String getOnlyUserFirstName(String fullName) {
		return fullName.split(" ")[0];
	}

	static public String getShortDescription(String desc, int wordCount) {
		String str[] = desc.split(" ");
		String formatedStr = "";
		if (str.length <= wordCount)
			return desc;
		for (int i = 0; i < wordCount; i++) {
			formatedStr += str[i] + " ";
		}
		return formatedStr + "...";
	}

}
