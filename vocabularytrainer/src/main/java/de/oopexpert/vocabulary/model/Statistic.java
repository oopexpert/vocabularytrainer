package de.oopexpert.vocabulary.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

public class Statistic {

	private static final String FOLDER_STATISTICS = "statistics";
	private double percentage1;
	long lastQuestionnaire1;
	long lastQuestionnaireEnd1;
	long lastBadgeTryStart1;
	long lastBadgeTryEnd1;
	private double badgePercentage1;

	private double percentage2;
	long lastQuestionnaire2;
	long lastQuestionnaireEnd2;
	long lastBadgeTryStart2;
	long lastBadgeTryEnd2;
	private double badgePercentage2;

	public Statistic() {
		this.percentage1 = 0.0;
		this.lastQuestionnaire1 = 0;
		this.lastQuestionnaireEnd1 = Long.MAX_VALUE;
		this.lastBadgeTryStart1 = 0;
		this.lastBadgeTryEnd1 = Long.MAX_VALUE;
		this.percentage2 = 0.0;
		this.lastQuestionnaire2 = 0;
		this.lastQuestionnaireEnd2 = Long.MAX_VALUE;
		this.lastBadgeTryStart2 = 0;
		this.lastBadgeTryEnd2 = Long.MAX_VALUE;
	}
	
	public double getPercentage1() {
		return percentage1;
	}


	public void setPercentage1(double percentage1) {
		this.percentage1 = percentage1;
	}

	public long getLastQuestionnaire1() {
		return lastQuestionnaire1;
	}

	public void setLastQuestionnaire1(long lastQuestionnaire1) {
		this.lastQuestionnaire1 = lastQuestionnaire1;
	}

	public double getPercentage2() {
		return percentage2;
	}

	public void setPercentage2(double percentage2) {
		this.percentage2 = percentage2;
	}

	public long getLastQuestionnaire2() {
		return lastQuestionnaire2;
	}

	public void setLastQuestionnaire2(long lastQuestionnaire2) {
		this.lastQuestionnaire2 = lastQuestionnaire2;
	}

	public double calculateEffectivePercentage1(int decayTime) {
	    return calculatePercentage(getLastQuestionnaire1(), getPercentage1(), decayTime);
	}
	
	public double calculateEffectivePercentage2(int decayTime) {
	    return calculatePercentage(getLastQuestionnaire2(), getPercentage2(), decayTime);
	}
	
	private double calculatePercentage(long lastQuestionnaire, double originalPercentage, int decayTime) {
		long currentTime = System.currentTimeMillis();
		long timeDifference = currentTime - lastQuestionnaire;
	    double decayRate = originalPercentage / (decayTime * 24l * 60l * 60l * 1000l); // decay per millisecond

	    double newPercentage = originalPercentage - (decayRate * timeDifference);

	    if (newPercentage < 0.0) {
	        return 0.0;
	    } else {
	        return newPercentage;
	    }
	}

	public static Statistic loadStatistic(VocabularySet vocabularySet) throws FileNotFoundException {
		Statistic statistic = null;
		var fileReader = new FileReader(new File(FOLDER_STATISTICS, vocabularySet.getId().toString()));
		Gson gson = new Gson();
		statistic = gson.fromJson(fileReader, Statistic.class);
		return statistic;
	}

	
	private static void saveStatistic(File file, Statistic statistic) {
		try (var writer = new FileWriter(file)) {
			Gson gson = new Gson();
			gson.toJson(statistic, writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveStatistic(Questionnaire questionnaire) {
		File file = new File(FOLDER_STATISTICS, questionnaire.getVocabularySetId().toString());
		
		Statistic statistic = null;
				
		if (file.exists()) {
			try {
				statistic = Statistic.loadStatistic(questionnaire.getVocabularySet());
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		} else {
			statistic = new Statistic();
		}

		update(statistic, questionnaire);
		
		saveStatistic(file, statistic);
	}

	private static void update(Statistic statistic, Questionnaire questionnaire) {
		boolean switchLanguage = questionnaire.getSwitchLanguage();
		if (switchLanguage) {
			if (questionnaire.calculatePercentage() > statistic.calculateEffectivePercentage2(questionnaire.getDifficulty())) {
				statistic.setLastQuestionnaire2(questionnaire.getQuestionnaireStart());
				statistic.setLastQuestionnaireEnd2(questionnaire.getQuestionnaireEnd());
				statistic.setPercentage2(questionnaire.calculatePercentage());
			}
			statistic.setLastBadgeTryStart2(questionnaire.getQuestionnaireStart());
			statistic.setLastBadgeTryEnd2(questionnaire.getQuestionnaireEnd());
			statistic.setBadgePercentage2(questionnaire.calculatePercentage());
		} else {
			if (questionnaire.calculatePercentage() > statistic.calculateEffectivePercentage1(questionnaire.getDifficulty())) {
				statistic.setLastQuestionnaire1(questionnaire.getQuestionnaireStart());
				statistic.setLastQuestionnaireEnd1(questionnaire.getQuestionnaireEnd());
				statistic.setPercentage1(questionnaire.calculatePercentage());
			}
			statistic.setLastBadgeTryStart1(questionnaire.getQuestionnaireStart());
			statistic.setLastBadgeTryEnd1(questionnaire.getQuestionnaireEnd());
			statistic.setBadgePercentage1(questionnaire.calculatePercentage());
		}
	}

	private void setLastQuestionnaireEnd1(long lastQuestionnaireEnd1) {
		this.lastQuestionnaireEnd1 = lastQuestionnaireEnd1;
	}

	private void setLastQuestionnaireEnd2(long lastQuestionnaireEnd2) {
		this.lastQuestionnaireEnd2 = lastQuestionnaireEnd2;
	}

	private void setLastBadgeTryStart1(long lastBadgeTryStart1) {
		this.lastBadgeTryStart1 = lastBadgeTryStart1;
	}

	private void setLastBadgeTryEnd1(long lastBadgeTryEnd1) {
		this.lastBadgeTryEnd1 = lastBadgeTryEnd1;
	}

	private void setLastBadgeTryStart2(long lastBadgeTryStart2) {
		this.lastBadgeTryStart2 = lastBadgeTryStart2;
	}

	private void setLastBadgeTryEnd2(long lastBadgeTryEnd2) {
		this.lastBadgeTryEnd2 = lastBadgeTryEnd2;
	}

	public double getBadgePercentage1() {
		return badgePercentage1;
	}

	private void setBadgePercentage1(double badgePercentage1) {
		this.badgePercentage1 = badgePercentage1;
	}

	public double getBadgePercentage2() {
		return badgePercentage2;
	}

	private void setBadgePercentage2(double badgePercentage2) {
		this.badgePercentage2 = badgePercentage2;
	}

}
