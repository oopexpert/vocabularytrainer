package de.oopexpert.vocabulary.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import de.oopexpert.oopdi.Scope;
import de.oopexpert.oopdi.annotation.Injectable;
import de.oopexpert.vocabulary.model.VocabularySet.BadgeTime;
import de.oopexpert.vocabulary.model.exception.NoVocabulary;
import de.oopexpert.vocabulary.model.exception.NoVocabularySet;

@Injectable(scope=Scope.GLOBAL)
public class Questionnaire {

	private Timer timer;	
	
	public static abstract class State {
		
		protected Questionnaire questionnaire;

		public State(Questionnaire questionnaire) {
			this.questionnaire = questionnaire;
		}

		protected void requestJoker() {}

		protected void provideAnswer(String answer) {}

		protected void setupQuestionaire(VocabularySet vocabularyset, boolean switchLanguage) {}

		protected void continueQuestionnaire() {}

		protected void abortQuestionnaire() {}
		
		protected void requestHint() {}

		protected void requestBrain() {};
		
		protected void acceptFinished() {};
		
		public abstract void accept(StateVisitor stateVisitor);

	}
	
	public static interface StateVisitor {

		void handle(InitState initState);

		void handle(CorrectAnswerState correctAnswerState);

		void handle(WrongAnswerState wrongAnswerState);

		void handle(QuestionState questionState);

		void handle(FinishedState finishedState);
		
	}

	private boolean switchLanguage;
	
	private long questionnaireEnd;

	public static class InitState extends State {

		public InitState(Questionnaire questionnaire) {
			super(questionnaire);
		}

		@Override
		protected void setupQuestionaire(VocabularySet vocabularyset, boolean switchLanguage) {
			if (vocabularyset == null) throw new NoVocabularySet();
			if (vocabularyset.count() == 0) throw new NoVocabulary();
			questionnaire.switchLanguage = switchLanguage;
			questionnaire.vocabularySet = vocabularyset;
			questionnaire.selectNextWord();
			questionnaire.initStatistics();
			questionnaire.state = new QuestionState(questionnaire);
			questionnaire.notifyAllOnSetupQuestionaire();
			questionnaire.questionnaireStart = System.currentTimeMillis();
			questionnaire.timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					questionnaire.notifyOnTimerTick();
				}
			}, 0, 200);
		}

		@Override
		public void accept(StateVisitor stateVisitor) {
			stateVisitor.handle(this);
		}
		
	}

	public static class FinishedState extends State {

		public FinishedState(Questionnaire questionnaire) {
			super(questionnaire);
		}

		@Override
		protected void acceptFinished() {
			questionnaire.init();
			questionnaire.notifyAllOnAcceptFinished();
		}
		
		@Override
		public void accept(StateVisitor stateVisitor) {
			stateVisitor.handle(this);
		}
		
	}

	public static class QuestionState extends State {

		private boolean hintRequested;
		private boolean jokerRequested;
		private boolean brainRequested;
		
		public QuestionState(Questionnaire questionnaire) {
			super(questionnaire);
			this.hintRequested = false;
			this.jokerRequested = false;
			this.brainRequested = false;
		}

		@Override
		protected void provideAnswer(String answer) {
			String word = questionnaire.getCurrentWordLang2();
			if (answer.equals(word)) {
				questionnaire.reduceQueryProbability();
				if (brainRequested) {
					questionnaire.reduceQueryProbability();
					questionnaire.brains.add(questionnaire.getCurrentWord());
				}
				
				questionnaire.state = new CorrectAnswerState(questionnaire);
				questionnaire.notifyAllOnCorrectAnswer(questionnaire.calculatePercentage());

				if (questionnaire.calculatePercentage() >= 100.0) {
					questionnaire.state = new FinishedState(questionnaire);
					questionnaire.stopQuestionnaire();
					questionnaire.notifyAllOnFinished();
				}
				
			} else {
				if (!jokerRequested) {
					questionnaire.increaseQueryProbability();
				}
				questionnaire.state = new WrongAnswerState(questionnaire);
				questionnaire.notifyAllOnWrongAnswer(word, questionnaire.calculatePercentage());
			}
		}

		@Override
		protected void requestHint() {
			if (!hintRequested && questionnaire.hintRequests > 0) {
				hintRequested = true;
				questionnaire.hintRequests--;
				questionnaire.listeners.forEach(l -> l.notifyAllOnHintGranted(maskString(questionnaire.getCurrentWordLang2()), questionnaire.hintRequests));
			}
		}

		@Override
		protected void requestJoker() {
			if (!jokerRequested && questionnaire.jokerRequests > 0) {
				jokerRequested = true;
				questionnaire.jokerRequests--;
				questionnaire.listeners.forEach(l -> l.notifyAllOnJokerGranted(questionnaire.jokerRequests));
			}
		}

		@Override
		protected void requestBrain() {
			if (!brainRequested && questionnaire.brainRequests > 0) {
				brainRequested = true;
				questionnaire.brainRequests--;
				questionnaire.listeners.forEach(l -> l.notifyAllOnBrainGranted(questionnaire.brainRequests));
			}
		}

		public static String maskString(String input) {
		    StringBuilder result = new StringBuilder();
		    boolean inWord = false;
		    for (int i = 0; i < input.length(); i++) {
		        char c = input.charAt(i);
		        if (Character.isLetter(c)) {
		            if (!inWord) {
		                inWord = true;
		                result.append(c);
		            } else {
		                result.append('?');
		            }
		        } else if (c == ' ') {
		            inWord = false;
		            result.append(' ');
		        } else {
		            result.append('?');
		        }
		    }
		    return result.toString();
		}

		@Override
		protected void abortQuestionnaire() {
			questionnaire.abortQuestionnaireInternally();
		}

		@Override
		public void accept(StateVisitor stateVisitor) {
			stateVisitor.handle(this);
		}
		
		public String getWord1() {
			return questionnaire.getCurrentWordLang1();
		}

		public String getDescription1() {
			return questionnaire.getCurrentDescriptionLang1();
		}

		public int getBrainsLeft() {
			return questionnaire.brainRequests;
		}

		public int getHintsLeft() {
			return questionnaire.hintRequests;
		}

		public int getJokerLeft() {
			return questionnaire.jokerRequests;
		}

	}

	public static abstract class ResolutionState extends State {

		public ResolutionState(Questionnaire questionnaire) {
			super(questionnaire);
		}

		@Override
		protected void continueQuestionnaire() {
			questionnaire.currentPosition = calculateNewPosition();
			questionnaire.state = new QuestionState(questionnaire);
			questionnaire.notifyAllOnContinueQuestionnaire();
		}

		private int calculateNewPosition() {
			int newPosition = questionnaire.currentPosition;
			while (newPosition == questionnaire.currentPosition || questionnaire.brains.contains(questionnaire.vocabularySet.get(newPosition))) {
				newPosition = questionnaire.calculateNewPosition();
			}
			return newPosition;
		}

		@Override
		protected void abortQuestionnaire() {
			questionnaire.abortQuestionnaireInternally();
		}

	}

	private void stopQuestionnaire() {
		questionnaireEnd = System.currentTimeMillis();
		timer.cancel();
		Statistic.saveStatistic(this);
	}


	public static class WrongAnswerState extends ResolutionState {

		public WrongAnswerState(Questionnaire questionnaire) {
			super(questionnaire);
		}

		@Override
		public void accept(StateVisitor stateVisitor) {
			stateVisitor.handle(this);
		}
		
		public String getCorrectAnswer() {
			return questionnaire.getCurrentWordLang2();
		}

		public double getPercentage() {
			return questionnaire.calculatePercentage();
		}

	}
	
	public static class CorrectAnswerState extends ResolutionState {

		public CorrectAnswerState(Questionnaire questionnaire) {
			super(questionnaire);
		}

		@Override
		public void accept(StateVisitor stateVisitor) {
			stateVisitor.handle(this);
		}
		
		@Override
		protected void continueQuestionnaire() {
			double hintChance = questionnaire.random.nextDouble();
			if (hintChance < 0.07) {
				questionnaire.addHint();
			}
			double jokerChance = questionnaire.random.nextDouble();
			if (jokerChance < 0.05) {
				questionnaire.addJoker();
			}
			super.continueQuestionnaire();
		}


		public double getPercentage() {
			return questionnaire.calculatePercentage();
		}

	}
	
	public static interface Listener {

		default void onSetupQuestionnaire(String word1, String description1, int hintsLeft, int jokerLeft, int brainsLeft) {};

		default void notifyAllOnWrongAnswer(String correctAnswer, double percentage) {};
		
		default void notifyAllOnHintGained(int freeHintRequests) {};
		
		default void notifyAllOnHintGranted(String hint, int freeHintRequests) {};

		default void notifyAllOnCorrectAnswer(double percentage) {};

		default void notifyAllOnContinueQuestionnaire(String word1, String description1, int hintsLeft, int jokerLeft, int brainsLeft) {};

		default void notifyAllOnAbortQuestionnaire() {};
		
		default void notifyAllOnRegisteredAsListener(State state) {}

		default void notifyAllOnJokerGained(int jokerRequests) {};

		default void notifyAllOnJokerGranted(int jokersLeft) {};
		
		default void notifyAllOnBrainGranted(int brainsLeft) {};
		
		default void notifyAllOnTimerProgress(BadgeTime currentValue) {};
		
		default void notifyAllOnFinished() {}

		default void notifyAllOnAcceptFinished() {};

	}

	private Random random;
	private VocabularySet vocabularySet;
	private int currentPosition;
	private State state;
	private Set<Listener> listeners;
	private Map<Integer,Integer> statistic;
	private Set<Word> brains;
	
	private int getCurrentProbability() {
		return getProbability(currentPosition);
	}
	
	public void notifyAllOnAcceptFinished() {
		this.listeners.forEach(l -> l.notifyAllOnAcceptFinished());
	}

	public void notifyAllOnFinished() {
		this.listeners.forEach(l -> l.notifyAllOnFinished());
	}

	private void clearStatistic() {
		statistic.clear();
	}

	public UUID getVocabularySetId() {
		return vocabularySet.getId();
	}

	private void selectNextWord() {
		currentPosition = random.nextInt(vocabularySet.count());
	}

	private void initStatistics() {
		vocabularySet.forEachWord((i, word) -> statistic.put(i, getMaxProbability()));
	}

	private int hintRequests;

	void addHint() {
		this.hintRequests++;
		this.listeners.forEach(l -> l.notifyAllOnHintGained(this.hintRequests));
	}

	private int jokerRequests;
	
	void addJoker() {
		this.jokerRequests++;
		this.listeners.forEach(l -> l.notifyAllOnJokerGained(this.jokerRequests));
	}

	double calculatePercentage() {
		
		double p = 0.0;
		
		double base = vocabularySet.count();
		
		Set<Entry<Integer, Integer>> entrySet = statistic.entrySet();
		
		for (Entry<Integer, Integer> entry : entrySet) {
			if (entry.getValue() != getMaxProbability()) {
				p = p + ((1.0 / ((double) entry.getValue())) / base);
			}
		}
		
		return p * 100.0 > 100.0 ? 100.0 : p * 100.0;
	}

	private int getMaxProbability() {
		return (vocabularySet.count() * 2) + 1;
	}

	private int calculateNewPosition() {
		
		double randomValue = random.nextDouble();
		
		int selectedValue = (int)Math.floor(((double)getStatisticSum()) * randomValue);
		
		List<Word> words = vocabularySet.getWords();
		
		int selectedWord = 0;
		int currentValue = 0;
		
		for (Word word : words) {
			selectedWord = words.indexOf(word);
			currentValue = currentValue + this.statistic.get(selectedWord);
			if (currentValue > selectedValue) break;
		}
		
		return selectedWord;
	}

	private int getProbability(int position) {
		return statistic.get(position);
	}

	private void reduceQueryProbability() {
		Integer integer = getCurrentProbability();
		if (integer != 1) {
			integer = integer - vocabularySet.count();
		}
		statistic.put(currentPosition, integer);
	}

	private void increaseQueryProbability() {
		Integer integer = getCurrentProbability();
		if (integer != getMaxProbability()) {
			integer = integer + vocabularySet.count();
		}
		statistic.put(currentPosition, integer);
	}
	
	private int getStatisticSum() {
		int sum = 0;
		Collection<Integer> values = this.statistic.values();
		for (Integer value : values) {
			sum = sum + value;
		}
		return sum;
	}
	
	private int brainRequests;

	public Questionnaire() {
		this.random = new Random(System.currentTimeMillis());
		this.listeners = new HashSet<>();
		this.statistic = new HashMap<>();
		this.brains = new HashSet<>();
		init();
	}

	private void init() {
		this.timer = new Timer();
		this.vocabularySet = null;
		this.currentPosition = -1;
		this.hintRequests = 0;
		this.jokerRequests = 0;
		this.brainRequests = 3;
		this.clearStatistic();
		this.brains.clear();
		this.state = new InitState(this);
	}
	
	private void notifyAllOnAbortQuestionnaire() {
		this.listeners.forEach(l -> l.notifyAllOnAbortQuestionnaire());
	}

	private void notifyAllOnContinueQuestionnaire() {
		this.listeners.forEach(l -> l.notifyAllOnContinueQuestionnaire(getCurrentWordLang1(), getCurrentDescriptionLang1(), hintRequests, jokerRequests, brainRequests));
	}

	private void notifyAllOnWrongAnswer(String correctAnswer, double percentage) {
		this.listeners.forEach(l -> l.notifyAllOnWrongAnswer(correctAnswer, percentage));
	}

	private void notifyAllOnCorrectAnswer(double percentage) {
		this.listeners.forEach(l -> l.notifyAllOnCorrectAnswer(percentage));
	}
	
	
	private long questionnaireStart;
	
	
	private void notifyOnTimerTick() {
		long timeElapsed = System.currentTimeMillis() - questionnaireStart;
		final BadgeTime badgeTime = VocabularySet.getBadgeTime(timeElapsed, vocabularySet);
		this.listeners.forEach(l -> l.notifyAllOnTimerProgress(badgeTime));
	}

	private void notifyAllOnSetupQuestionaire() {
		this.listeners.forEach(l -> l.onSetupQuestionnaire(getCurrentWordLang1(), getCurrentDescriptionLang1(), hintRequests, jokerRequests, brainRequests));
	}

	private String getCurrentWordLang2() {
		return getCurrentWord().getWord2(switchLanguage);
	}

	private String getCurrentWordLang1() {
		return getCurrentWord().getWord1(switchLanguage);
	}

	private String getCurrentDescriptionLang1() {
		return getCurrentWord().getDescription1(switchLanguage);
	}

	private Word getCurrentWord() {
		return this.vocabularySet.get(currentPosition);
	}

	public static class QuestionaireSetup {
		public final VocabularySet vocabularySet;
		public final boolean switchLanguage;
		public QuestionaireSetup(VocabularySet vocabularySet, boolean switchLanguage) {
			this.vocabularySet = vocabularySet;
			this.switchLanguage = switchLanguage;
		}
	}
	
	public void setupQuestionnaire(QuestionaireSetup setup) {
		this.state.setupQuestionaire(setup.vocabularySet, setup.switchLanguage);
	}
	
	public void provideAnswer(String answer) {
		this.state.provideAnswer(answer);
	}

	public void continueQuestionnaire() {
		this.state.continueQuestionnaire();
	}

	public void abortQuestionnaire() {
		this.state.abortQuestionnaire();
	}
	
	public void addListener(Listener questionaireListener) {
		this.listeners.add(questionaireListener);
		questionaireListener.notifyAllOnRegisteredAsListener(this.state);
	}

	public void acceptFinished() {
		this.state.acceptFinished();
	}
	
	public void requestHint() {
		this.state.requestHint();
	}

	public void requestJoker() {
		this.state.requestJoker();
	}

	public void requestBrain() {
		this.state.requestBrain();
	}

	public int getDifficulty() {
		return this.vocabularySet.getDecayTime();
	}

	public VocabularySet getVocabularySet() {
		return this.vocabularySet;
	}

	protected boolean getSwitchLanguage() {
		return switchLanguage;
	}

	protected long getQuestionnaireEnd() {
		return questionnaireEnd;
	}

	protected long getQuestionnaireStart() {
		return questionnaireStart;
	}

	private void abortQuestionnaireInternally() {
		stopQuestionnaire();
		init();
		notifyAllOnAbortQuestionnaire();
	}
	
}
