package de.oopexpert.vocabulary.model;

public class Word {

	private String word1;
	private String description1;
	
	private String word2;
	private String description2;
	
	public Word(String word1, String description1, String word2, String description2) {
		this.word1 = word1;
		this.description1 = description1;
		this.word2 = word2;
		this.description2 = description2;
	}

	public String getWord1(boolean switchLanguage) {
		return switchLanguage?word1:word2;
	}
	
	public String getDescription1(boolean switchLanguage) {
		return switchLanguage?description1:description2;
	}
	
	public String getWord2(boolean switchLanguage) {
		return switchLanguage?word2:word1;
	}
	
	public String getDescription2(boolean switchLanguage) {
		return switchLanguage?description2:description1;
	}

}
