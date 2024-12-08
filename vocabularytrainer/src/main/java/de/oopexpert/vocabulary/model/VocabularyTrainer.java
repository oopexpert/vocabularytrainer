package de.oopexpert.vocabulary.model;

import java.util.HashSet;
import java.util.Set;

import de.oopexpert.oopdi.OOPDI;
import de.oopexpert.oopdi.Scope;
import de.oopexpert.oopdi.annotation.InjectInstance;
import de.oopexpert.oopdi.annotation.Injectable;
import de.oopexpert.oopdi.annotation.PostConstruct;
import de.oopexpert.vocabulary.model.Selector.OnDeselectObserver;
import de.oopexpert.vocabulary.model.Selector.OnSelectObserver;
import de.oopexpert.vocabulary.model.Selector.OnSelectionChangedObserver;

@Injectable(scope=Scope.GLOBAL)
public class VocabularyTrainer {
	
	@InjectInstance
	private VocabularyLibrary vocabularyLibrary;
	
	@InjectInstance
	private VocabularyEditor editor;
	
	@InjectInstance
	private Questionnaire questionnaire;

	@InjectInstance
	private RewardManager rewardManager;
	
	@InjectInstance
	private BackgroundImageRotation backgroundImageRotation;

	private Selector<VocabularySet> vocabularySetSelector;

	private boolean switchLanguage;
	
	public static interface Listener {
		
		default void onRegisteredAsListener(int currentScore, State state) {};

		default void onAbortQuestionnaire(int currentScore) {};

		default void onQuestionnaire(VocabularySet vocabularySet, boolean switchLanguage) {}

		default void onRewards() {};

		default void onAbortRewards() {};

	}
	
	private Set<Listener> listeners;
	
	
	public static abstract class State {
		
		private VocabularyTrainer vocabularyTrainer;

		public State(VocabularyTrainer vocabularyTrainer) {
			this.vocabularyTrainer = vocabularyTrainer;
		}

		protected VocabularyTrainer getVocabularyTrainer() {
			return vocabularyTrainer;
		}
		
		public abstract void accept(StateVisitor stateVisitor);
		
		public abstract void gotoQuestionnaire(VocabularySet vocabularySet, boolean switchLanguage);
		public abstract void abortQuestionnaiere();
		public abstract void gotoEditor();
		
		protected void setState(State state) {
			getVocabularyTrainer().state = state;
		}

		protected abstract void switchLanguage(boolean switchLanguage);

		protected abstract void gotoRewards();

		protected abstract void abortRewards();
		
	}
	
	public static class StateQuestionnaire extends State {

		public StateQuestionnaire(VocabularyTrainer vocabularyTrainer) {
			super(vocabularyTrainer);
		}

		@Override
		public void accept(StateVisitor stateVisitor) {
			stateVisitor.handle(this);
		}

		@Override
		public void gotoQuestionnaire(VocabularySet vocabularySet, boolean switchLanguage) {
		}

		@Override
		public void abortQuestionnaiere() {
			setState(new StateMenu(getVocabularyTrainer()));
			getVocabularyTrainer().listeners.forEach(l -> l.onAbortQuestionnaire(getVocabularyTrainer().vocabularyLibrary.getCurrentScore()));
		}

		@Override
		public void gotoEditor() {
		}

		@Override
		protected void switchLanguage(boolean switchLanguage) {
		}

		@Override
		protected void gotoRewards() {
		}

		@Override
		protected void abortRewards() {
			// TODO Auto-generated method stub
			
		}
		
	}

	public static class StateMenu extends State {

		public StateMenu(VocabularyTrainer vocabularyTrainer) {
			super(vocabularyTrainer);
		}

		@Override
		public void accept(StateVisitor stateVisitor) {
			stateVisitor.handle(this);
		}

		@Override
		public void gotoQuestionnaire(VocabularySet vocabularySet, boolean switchLanguage) {
			setState(new StateQuestionnaire(getVocabularyTrainer()));
			getVocabularyTrainer().listeners.forEach(l -> l.onQuestionnaire(vocabularySet, switchLanguage));
		}

		@Override
		public void abortQuestionnaiere() {
		}

		@Override
		public void gotoEditor() {
		}

		@Override
		protected void switchLanguage(boolean switchLanguage) {
			getVocabularyTrainer().switchLanguage = switchLanguage;
		}

		@Override
		protected void gotoRewards() {
			setState(new StateRewards(getVocabularyTrainer()));
			getVocabularyTrainer().listeners.forEach(l -> l.onRewards());
		}

		@Override
		protected void abortRewards() {
			// TODO Auto-generated method stub
			
		}
		
	}

	public static class StateEditor extends State {

		public StateEditor(VocabularyTrainer vocabularyTrainer) {
			super(vocabularyTrainer);
		}

		@Override
		public void accept(StateVisitor stateVisitor) {
			stateVisitor.handle(this);
		}

		@Override
		public void gotoQuestionnaire(VocabularySet vocabularySet, boolean switchLanguage) {
		}

		@Override
		public void abortQuestionnaiere() {
		}

		@Override
		public void gotoEditor() {
		}

		@Override
		protected void switchLanguage(boolean switchLanguage) {
		}

		@Override
		protected void gotoRewards() {
		}

		@Override
		protected void abortRewards() {
			// TODO Auto-generated method stub
			
		}
		
	}

	public static class StateRewards extends State {

		public StateRewards(VocabularyTrainer vocabularyTrainer) {
			super(vocabularyTrainer);
		}

		@Override
		public void accept(StateVisitor stateVisitor) {
			stateVisitor.handle(this);
		}

		@Override
		public void gotoQuestionnaire(VocabularySet vocabularySet, boolean switchLanguage) {
		}

		@Override
		public void abortQuestionnaiere() {
		}

		@Override
		public void gotoEditor() {
		}

		@Override
		protected void switchLanguage(boolean switchLanguage) {
		}

		@Override
		protected void gotoRewards() {
		}

		@Override
		protected void abortRewards() {
			setState(new StateMenu(getVocabularyTrainer()));
			getVocabularyTrainer().listeners.forEach(l -> l.onAbortRewards());
		}
		
	}

	
	public static interface StateVisitor {

		void handle(StateQuestionnaire stateQuestionnaire);

		void handle(StateRewards stateRewards);

		void handle(StateEditor stateEditor);

		void handle(StateMenu stateMenu);
		
	}

	private State state;

	private OOPDI<VocabularyTrainer> oopdi;

	public VocabularyTrainer(OOPDI<VocabularyTrainer> oopdi) {
		this.oopdi = oopdi;
		this.vocabularySetSelector = new Selector<>();
		this.listeners = new HashSet<>();
		this.state = new StateMenu(this);
	}

	@PostConstruct
	private void postConstruct() {
		this.vocabularySetSelector.addOnSelectObserver(editor::setVocabularySet);
		this.vocabularySetSelector.addOnDeselectObserver(editor::setVocabularySet);
		this.vocabularySetSelector.addOnSelectionChangedObserver((from, to) -> editor.setVocabularySet(to));
		oopdi.getInstance(Questionnaire.class).addListener(new Questionnaire.Listener() {
			@Override
			public void notifyAllOnAbortQuestionnaire() {
				abortQuestionnaire();
			}
			@Override
			public void notifyAllOnAcceptFinished() {
				abortQuestionnaire();
			}
		});
//		oopdi.execConsumer(Questionnaire.class, q -> q::addListener, new Questionnaire.Listener() {
//			@Override
//			public void notifyAllOnAbortQuestionnaire() {
//				abortQuestionnaire();
//			}
//			@Override
//			public void notifyAllOnAcceptFinished() {
//				abortQuestionnaire();
//			}
//		});
		oopdi.getInstance(RewardManager.class).add(new RewardManager.Listener() {
			@Override
			public void onAbortRewards() {
				abortRewards();
			}
		});
//		oopdi.execConsumer(RewardManager.class, q -> q::add, new RewardManager.Listener() {
//			@Override
//			public void onAbortRewards() {
//				abortRewards();
//			}
//		});
	}

	public void selectVocabularySet(VocabularySet selectedVocabularySet) {
		if (vocabularyLibrary.contains(selectedVocabularySet) || selectedVocabularySet == null) {
			vocabularySetSelector.change(selectedVocabularySet);
		}
	}

	public void addOnSelectionChangedObserver(OnSelectionChangedObserver<VocabularySet> e) {
		vocabularySetSelector.addOnSelectionChangedObserver(e);
	}

	public void addOnDeselectObserver(OnDeselectObserver<VocabularySet> e) {
		vocabularySetSelector.addOnDeselectObserver(e);
	}

	public void addOnSelectObserver(OnSelectObserver<VocabularySet> e) {
		vocabularySetSelector.addOnSelectObserver(e);
	}

	public void addListener(Listener e) {
		listeners.add(e);
		e.onRegisteredAsListener(this.vocabularyLibrary.getCurrentScore(), this.state);
	}

	private void abortQuestionnaire() {
		this.state.abortQuestionnaiere();
	}

	private void abortRewards() {
		this.state.abortRewards();
	}

	public void gotoRewards() {
		this.state.gotoRewards();
	}
	
	public void gotoQuestionnaire() {
		if (vocabularySetSelector.getObject() != null) {
			this.state.gotoQuestionnaire(vocabularySetSelector.getObject(), switchLanguage);
		}
	}
	
	public void setSwitchLanguage(boolean switchLanguage) {
		this.state.switchLanguage(switchLanguage);
	}
	
	public int getCurrentScore() {
		return vocabularyLibrary.getCurrentScore();
	}

}
