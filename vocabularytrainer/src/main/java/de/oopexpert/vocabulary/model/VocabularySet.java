package de.oopexpert.vocabulary.model;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public class VocabularySet {

	public interface Observer {
		void wordAdded(Word word);
		void wordRemoved(Word word);
	}
	
	private final Set<Observer> observers = new HashSet<>();
	
	private final List<Word> words = new ArrayList<>();

	private String name;
	private String source;

	private int maxScore2;
	private int maxScore1;
	
	private int decayTime;

	public void add(Word w) {
		words.add(w);
		observers.forEach(o -> o.wordAdded(w));
	}

	public void remove(Word w) {
		words.remove(w);
		observers.forEach(o -> o.wordRemoved(w));
	}

	public int position(Word word) {
		return words.indexOf(word);
	}
	
	public int count() {
		return words.size();
	}

	public Word get(int index) {
		return this.words.get(index);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setSource(String filePath) {
		this.source = filePath;
	}

	public String getSource() {
		return source;
	}

	public List<Word> getWords() {
		return words;
	}

	public void forEachWord(BiConsumer<Integer, Word> handle) {
		for (int i = 0; i < words.size(); i++) {
			handle.accept(i, words.get(i));
		}
	}
	
	public UUID getId() {
		return UUID.fromString(getSource());
	}

	public int getMaxScore1() {
		return maxScore1;
	}

	public int getMaxScore2() {
		return maxScore2;
	}

	public void setMaxScore2(int maxScore2) {
		this.maxScore2 = maxScore2;
	}

	public void setMaxScore1(int maxScore1) {
		this.maxScore1 = maxScore1;
	}
	
	public int getCurrentScore1() {
		double effectivePercentage1 = getStatistic().calculateEffectivePercentage1(getDecayTime());
		if (getStatistic().getPercentage1() == 100.0) {
			effectivePercentage1 = Math.max(effectivePercentage1, getBadge1().limit());
		}
		return (int) Math.round((getMaxScore1() * effectivePercentage1 / 100.0));
	}

	public int getCurrentScore2() {
		double effectivePercentage2 = getStatistic().calculateEffectivePercentage2(getDecayTime());
		if (getStatistic().getPercentage2() == 100.0) {
			effectivePercentage2 = Math.max(effectivePercentage2, getBadge2().limit());
		}
		return (int) Math.round((getMaxScore2() * effectivePercentage2 / 100.0));
	}

	public Badge getBadge1() {
		if (getStatistic().getBadgePercentage1() >= 100.0) {
			return getBadgeTime1().badge();
		}
		return Badge.NONE;
	}

	public Badge getBadge2() {
		if (getStatistic().getBadgePercentage2() >= 100.0) {
			return getBadgeTime2().badge();
		}
		return Badge.NONE;
	}

	public Statistic getStatistic() {
		try {
			return  Statistic.loadStatistic(this);
		} catch (FileNotFoundException e) {
			Statistic statistic = new Statistic();
			return statistic;
		}
	}

	public int getDecayTime() {
		return this.decayTime;
	}

	protected void setDecayTime(int decayTime) {
		this.decayTime = decayTime;
	}

	public static BadgeTime getBadgeTime(long timeElapsed, VocabularySet vocabularySet) {
		long goldBadgePeriod = 1000l * (vocabularySet.decayTime * 8l);

		long value = (timeElapsed / (goldBadgePeriod / 100));
		Badge badge = Badge.GOLD;
		if (value > 100) {
			long silverBadgePeriod = 1000l * (vocabularySet.decayTime * 2l);
			value = ((timeElapsed - goldBadgePeriod) / (silverBadgePeriod / 100));
			badge = Badge.SILVER;
			if (value > 100) {
				long bronzeBadgePeriod = 1000l * (vocabularySet.decayTime * 2l);
				value = ((timeElapsed - goldBadgePeriod - silverBadgePeriod) / (bronzeBadgePeriod / 100));
				badge = Badge.BRONZE;
				if (value > 100) {
					value = 100;
					badge = Badge.NONE;
				}
			}
		}
		return new BadgeTime(100 - (int) value, badge);
	}

	public static record BadgeTime(int value, Badge badge) {}
	
	
	public BadgeTime getBadgeTime1() {
		return getBadgeTime(this.getStatistic().lastBadgeTryStart1, this.getStatistic().lastBadgeTryEnd1, this);
	}

	public BadgeTime getBadgeTime2() {
		return getBadgeTime(this.getStatistic().lastBadgeTryStart2, this.getStatistic().lastBadgeTryEnd2, this);
	}

	private static BadgeTime getBadgeTime(long lastBadgeTryStart, long lastBadgeTryEnd, VocabularySet vocabularySet) {
		return getBadgeTime(lastBadgeTryEnd - lastBadgeTryStart, vocabularySet);
	}
	
}
