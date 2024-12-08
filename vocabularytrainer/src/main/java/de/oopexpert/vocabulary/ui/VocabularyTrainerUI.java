package de.oopexpert.vocabulary.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import de.oopexpert.oopdi.OOPDI;
import de.oopexpert.vocabulary.model.Questionnaire;
import de.oopexpert.vocabulary.model.Questionnaire.QuestionaireSetup;
import de.oopexpert.vocabulary.model.VocabularySet;
import de.oopexpert.vocabulary.model.VocabularyTrainer;

public class VocabularyTrainerUI extends JFrame {

	private static final int IMAGE_COUNT = 8;

	private static final long serialVersionUID = -7516924811751556693L;

	private ImageTransitionPanel content;
	
	private JLabel labelTitle;

	private MainPanel mainScreen;
	private RewardsPanel rewardsScreen;
	private QuestionnairePanel questionaireScreen;

	private JPanel editScreen;

	private OOPDI<VocabularyTrainer> oopdi;

	Font segoeUIFont = new Font("Monospaced", Font.BOLD, 12);


	public VocabularyTrainerUI(OOPDI<VocabularyTrainer> oopdi) throws IOException {
		this.oopdi = oopdi;
		this.setUndecorated(true);
		
		oopdi.getInstance(VocabularyTrainer.class).addListener(new VocabularyTrainer.Listener() {
			
			@Override
			public void onAbortQuestionnaire(int currentScore) {
				VocabularyTrainerUI.this.onAbortQuestionnaire();
			}

			@Override
			public void onQuestionnaire(VocabularySet vocabularySet, boolean switchLanguage) {
				QuestionaireSetup qs = new QuestionaireSetup(vocabularySet, switchLanguage);
				VocabularyTrainerUI.this.onQuestionnaire(qs);
			}

			@Override
			public void onRewards() {
				VocabularyTrainerUI.this.onRewards();
			}
			
			public void onAbortRewards() {
				VocabularyTrainerUI.this.onAbortRewards();
			};
			
		});
		
//		oopdi.execConsumer(VocabularyTrainer.class, vt -> vt::addListener, new VocabularyTrainer.Listener() {
//			
//			@Override
//			public void onAbortQuestionnaire(int currentScore) {
//				VocabularyTrainerUI.this.onAbortQuestionnaire();
//			}
//
//			@Override
//			public void onQuestionnaire(VocabularySet vocabularySet, boolean switchLanguage) {
//				QuestionaireSetup qs = new QuestionaireSetup(vocabularySet, switchLanguage);
//				VocabularyTrainerUI.this.onQuestionnaire(qs);
//			}
//
//			@Override
//			public void onRewards() {
//				VocabularyTrainerUI.this.onRewards();
//			}
//			
//			public void onAbortRewards() {
//				VocabularyTrainerUI.this.onAbortRewards();
//			};
//			
//		});
	}

	protected void onRewards() {
		SwingUtilities.invokeLater(() -> {
			getContent().remove(getMainScreen());
			getContent().add(getRewardsScreen(), BorderLayout.CENTER);
			getContent().revalidate();
			getContent().repaint();
		});
	}

	private JLabel getLabelTitle() {
		
		if (labelTitle == null) {
			labelTitle = new JLabel();
			labelTitle.setText("Vocabulary Trainer");
			Font segoeUIFont26 = new Font("Monospaced", Font.BOLD, 26);
			labelTitle.setFont(segoeUIFont26);
			labelTitle.setForeground(Color.WHITE);
			labelTitle.setBorder(new EmptyBorder(10,20,10,20));

		}
		return labelTitle;
	}

	private ImageTransitionPanel getContent() {
		
		if (content == null) {
			
			content = new ImageTransitionPanel(oopdi);

			content.setLayout(new BorderLayout());
			content.setOpaque(false);
			
			JPanel jPanel = new JPanel();
			jPanel.setOpaque(false);
			jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));
			jPanel.add(getLabelTitle(), BorderLayout.WEST);
			content.add(jPanel, BorderLayout.NORTH);
			content.add(getMainScreen(), BorderLayout.CENTER);
		}
		
		return content;
	}

	private JPanel getEditScreen() {
		if (this.editScreen == null) {
			this.editScreen = new EditPanel();
			this.editScreen.setOpaque(false);

		}
		return editScreen;
	}
	
	private MainPanel getMainScreen() {
		if (this.mainScreen == null) {
			this.mainScreen = new MainPanel(oopdi);
			this.mainScreen.setOpaque(false);

		}
		return mainScreen;
	}

	private RewardsPanel getRewardsScreen() {
		if (this.rewardsScreen == null) {
			this.rewardsScreen = new RewardsPanel(oopdi);
			this.rewardsScreen.setOpaque(false);

		}
		return rewardsScreen;
	}

	private void onEditor() {
//		SwingUtilities.invokeLater(() -> {
//			getContent().remove(getMainScreen());
//			getContent().add(getEditScreen(), BorderLayout.CENTER);
//			getContent().revalidate();
//			getContent().repaint();
//		});
	}
	
	private JPanel getQuestionaireScreen() {
		if (this.questionaireScreen == null) {
			this.questionaireScreen = new QuestionnairePanel(oopdi, this);
			this.questionaireScreen.setOpaque(false);
		}
		return questionaireScreen;
	}

	private int bck = new Random(System.currentTimeMillis()).nextInt(IMAGE_COUNT);

	private void onAbortQuestionnaire() {
		SwingUtilities.invokeLater(() -> {
			getContent().remove(getQuestionaireScreen());
			getContent().add(getMainScreen(), BorderLayout.CENTER);
			getContent().revalidate();
			getContent().repaint();
		});
	}

	private void onAbortRewards() {
		SwingUtilities.invokeLater(() -> {
			getContent().remove(getRewardsScreen());
			getContent().add(getMainScreen(), BorderLayout.CENTER);
			getContent().revalidate();
			getContent().repaint();
		});
	}

	private void onQuestionnaire(QuestionaireSetup setup) {
		SwingUtilities.invokeLater(() -> {
			getContent().remove(getMainScreen());
			getContent().add(getQuestionaireScreen(), BorderLayout.CENTER);
			getContent().revalidate();
			getContent().repaint();
			oopdi.getInstance(Questionnaire.class).setupQuestionnaire(setup);
//			oopdi.execConsumer(Questionnaire.class, q -> q::setupQuestionnaire, setup);
		});
	}
	
	public void initialize() {
		Font segoeUIFont = new Font("Monospaced", Font.BOLD, 24);
		UIManager.put("TextField.font", segoeUIFont);
		this.setContentPane(getContent());

		// set size and visibility
		setSize(800, 500);
		setResizable(false);
		setVisible(true);
		setLocation((int) ((Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2), (int) ((Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height) / 2));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) throws IOException {
		OOPDI<VocabularyTrainer> oopdi = new OOPDI<>(VocabularyTrainer.class);
		VocabularyTrainerUI ui = new VocabularyTrainerUI(oopdi);
		ui.initialize();
	}
	
}