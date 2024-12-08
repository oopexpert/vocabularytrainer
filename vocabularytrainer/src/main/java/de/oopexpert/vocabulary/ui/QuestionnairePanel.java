package de.oopexpert.vocabulary.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import de.oopexpert.oopdi.OOPDI;
import de.oopexpert.vocabulary.model.Questionnaire;
import de.oopexpert.vocabulary.model.Questionnaire.CorrectAnswerState;
import de.oopexpert.vocabulary.model.Questionnaire.FinishedState;
import de.oopexpert.vocabulary.model.Questionnaire.InitState;
import de.oopexpert.vocabulary.model.Questionnaire.Listener;
import de.oopexpert.vocabulary.model.Questionnaire.QuestionState;
import de.oopexpert.vocabulary.model.Questionnaire.State;
import de.oopexpert.vocabulary.model.Questionnaire.StateVisitor;
import de.oopexpert.vocabulary.model.Questionnaire.WrongAnswerState;
import de.oopexpert.vocabulary.model.VocabularySet.BadgeTime;
import de.oopexpert.vocabulary.model.VocabularyTrainer;

public class QuestionnairePanel extends JPanel {

	private static final long serialVersionUID = -6922470704644768630L;
	
	private static Color TRANSPARENT_BACKGROUND = new Color(255,255,255,65);

	private Font FONT_12 = new Font("Monospaced", Font.BOLD, 12);
	private Font FONT_16 = new Font("Monospaced", Font.BOLD, 16);
	private Font FONT_24 = new Font("Monospaced", Font.BOLD, 24);

	private Border textFieldBorder = BorderFactory.createEmptyBorder(0, 5, 0, 5);

	
	private OOPDI<VocabularyTrainer> oopdi;

	private JLabel labelWord1;
	private JButton buttonRequestHint;
	private JButton buttonRequestJoker;
	private JButton buttonRequestBrain;
	private JTextField textFieldWord1;
	private JLabel labelDescription1;
	private JTextField textFieldDescription1;
	private JLabel labelWord2;
	private JTextField textFieldCorrectAnswer;
	private JButton buttonContinue;
	private JLabel labelPercentage;
	private JButton buttonAbort;
	private JTextField textFieldTraineeInput;
	private JLabel labelHint;
	private JLabel labelJoker;
	private JLabel labelBrain;
	private JLabel labelGoldBadge;
	private JLabel labelSilverBadge;
	private JLabel labelBronzeBadge;
	private JLabel labelNoneBadge;
	private JProgressBar progressBarTime;

	private JFrame parentFrame;

	public QuestionnairePanel(OOPDI<VocabularyTrainer> oopdi, JFrame parentFrame) {
		
		this.oopdi = oopdi;
		this.parentFrame = parentFrame;
		
		setBackground(Color.BLACK);
		setLayout(null);

		add(getLabelWord1());
		add(getLabelDescription1());
		add(getTextFieldWord1());
		add(getTextFieldDescription1());
		add(getLabelWord2());
		add(getTextFieldTraineeInput());
		add(getTextFieldCorrectAnswer());
		add(getButtonContinue());
		add(getButtonAbort());
		add(getLabelPercentage());
		add(getButtonRequestHint());
		add(getButtonRequestJoker());
		add(getButtonRequestBrain());
		add(getLabelHint());
		add(getLabelJoker());
		add(getLabelBrain());
		add(getProgressBarTime());
		
//		oopdi.execConsumer(Questionnaire.class, q -> q::addListener, createQuestionnaireListener());
		oopdi.getInstance(Questionnaire.class).addListener(createQuestionnaireListener());

	}

	private JProgressBar getProgressBarTime() {
		if (progressBarTime == null) {
			progressBarTime = new JProgressBar(JProgressBar.VERTICAL, 0, 100) {
				
				private static final long serialVersionUID = -7007727856647018665L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}
			};
			progressBarTime.setBackground(TRANSPARENT_BACKGROUND);
			progressBarTime.setBorderPainted(false);
			progressBarTime.setOpaque(false);
			progressBarTime.setValue(UNDEFINED_CONDITION);
			progressBarTime.setBounds(755, 55, 25, 255);
			progressBarTime.setForeground(Color.WHITE);
		}
		return progressBarTime;
	}
	
	private JLabel getLabelGoldBadge() {
		if (labelGoldBadge == null) {
			labelGoldBadge = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("badge_gold.png")));
			labelGoldBadge.setBounds(755, 25, 25, 25);
		}
		return labelGoldBadge;
	}

	private JLabel getLabelSilverBadge() {
		if (labelSilverBadge == null) {
			labelSilverBadge = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("badge_silver.png")));
			labelSilverBadge.setBounds(755, 25, 25, 25);
		}
		return labelSilverBadge;
	}

	private JLabel getLabelBronzeBadge() {
		if (labelBronzeBadge == null) {
			labelBronzeBadge = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("badge_bronze.png")));
			labelBronzeBadge.setBounds(755, 25, 25, 25);
		}
		return labelBronzeBadge;
	}

	private JLabel getLabelNoneBadge() {
		if (labelNoneBadge == null) {
			labelNoneBadge = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("badge_none.png")));
			labelNoneBadge.setBounds(755, 25, 25, 25);
		}
		return labelNoneBadge;
	}

	private Listener createQuestionnaireListener() {
		return new Questionnaire.Listener() {
			
			private DecimalFormat decimalFormat = new DecimalFormat("#.##");

			private String getRatingText(double percentage) {
				return "Rating: " + decimalFormat.format(percentage) + "%";
			}
			
			@Override
			public void onSetupQuestionnaire(String word1, String description1, int hintsLeft, int jokerLeft, int brainsLeft) {
				getTextFieldWord1().setText(word1);
				getTextFieldDescription1().setText(description1);
				getButtonContinue().setEnabled(false);
				getTextFieldTraineeInput().setEnabled(true);
				getTextFieldTraineeInput().requestFocus();
				getButtonRequestHint().setText("" + hintsLeft);
				getButtonRequestJoker().setText("" + jokerLeft);
				getButtonRequestBrain().setText("" + brainsLeft);
				if (jokerLeft > 0) {
					getButtonRequestJoker().setEnabled(true);
				}
				if (hintsLeft > 0) {
					getButtonRequestHint().setEnabled(true);
				}
				if (brainsLeft > 0) {
					getButtonRequestBrain().setEnabled(true);
				}
			}
			
			@Override
			public void notifyAllOnWrongAnswer(String correctAnswer, double percentage) {
				getButtonContinue().setEnabled(true);
				getButtonContinue().requestFocus();
				getTextFieldTraineeInput().setEnabled(false);
				getTextFieldCorrectAnswer().setText(correctAnswer);
				getLabelPercentage().setText(getRatingText(percentage));
				getButtonRequestHint().setEnabled(false);
				getButtonRequestJoker().setEnabled(false);
				getButtonRequestBrain().setEnabled(false);
			}

			@Override
			public void notifyAllOnCorrectAnswer(double percentage) {
				getButtonContinue().setEnabled(true);
				getButtonContinue().requestFocus();
				getTextFieldTraineeInput().setEnabled(false);
				getTextFieldTraineeInput().setDisabledTextColor(Color.GREEN);
				getLabelPercentage().setText(getRatingText(percentage));
				getButtonRequestHint().setEnabled(false);
				getButtonRequestJoker().setEnabled(false);
				getButtonRequestBrain().setEnabled(false);
			}
			
			@Override
			public void notifyAllOnContinueQuestionnaire(String word1, String description1, int hintsLeft, int jokerLeft, int brainsLeft) {
				if (jokerLeft > 0) {
					getButtonRequestJoker().setEnabled(true);
				}
				if (hintsLeft > 0) {
					getButtonRequestHint().setEnabled(true);
				}
				if (brainsLeft > 0) {
					getButtonRequestBrain().setEnabled(true);
				}
				getTextFieldWord1().setText(word1);
				getTextFieldDescription1().setText(description1);
				getButtonContinue().setEnabled(false);
				getTextFieldTraineeInput().setEnabled(true);
				getTextFieldTraineeInput().setText("");
				getTextFieldTraineeInput().requestFocus();
				getTextFieldTraineeInput().setDisabledTextColor(Color.WHITE);
				getTextFieldCorrectAnswer().setText("");
			}

			@Override
			public void notifyAllOnAbortQuestionnaire() {
				getTextFieldWord1().setText("");
				getTextFieldDescription1().setText("");
				getButtonContinue().setEnabled(false);
				getTextFieldTraineeInput().setEnabled(false);
				getTextFieldTraineeInput().setText("");
				getTextFieldCorrectAnswer().setText("");
				getLabelPercentage().setText(getRatingText(0.0));
			}

			private JDialog frame;

			
			JDialog getFinishedDialog() {
				if (frame == null) {
					frame = new JDialog(parentFrame, "", true); 
					JPanel panel = new JPanel();
					panel.setOpaque(false);
					panel.setLayout(new BorderLayout());
					JButton jButton = new JButton() {

						private static final long serialVersionUID = -6411593188920813232L;

						@Override
						protected void paintComponent(Graphics g) {
							 g.setColor( getBackground() );
						     g.fillRect(0, 0, getWidth(), getHeight());
						     super.paintComponent(g);
						}
					};
					jButton.setFont(FONT_16);
					jButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "pressed");
					jButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
					jButton.setOpaque(false);
					jButton.setBackground(TRANSPARENT_BACKGROUND);
					jButton.setForeground(Color.WHITE);
					jButton.setText("Perfect!");
					jButton.addActionListener(a -> oopdi.getInstance(Questionnaire.class).acceptFinished());
//					jButton.addActionListener(a -> oopdi.execRunnable(Questionnaire.class, q -> q::acceptFinished));
					panel.add(jButton, BorderLayout.CENTER);
					frame.setUndecorated(true);
					frame.setSize(240, 100);
					frame.setResizable(false);
					frame.setLocation(ALLBITS, ABORT);
					frame.setBackground(Color.BLACK);
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					frame.setContentPane(panel);
					frame.setLocation((int) ((Toolkit.getDefaultToolkit().getScreenSize().width - frame.getSize().width) / 2), (int) ((Toolkit.getDefaultToolkit().getScreenSize().height - frame.getSize().height) / 2));
				}
				return frame;
			}
			
			@Override
			public void notifyAllOnFinished() {
				getFinishedDialog().setVisible(true);
			}
			
			@Override
			public void notifyAllOnAcceptFinished() {
				getFinishedDialog().setVisible(false);
				notifyAllOnAbortQuestionnaire();			
			}
			
			@Override
			public void notifyAllOnRegisteredAsListener(State state) {
				state.accept(createStateVisitor());
			}

			private StateVisitor createStateVisitor() {
				return new StateVisitor() {
					
					@Override
					public void handle(QuestionState questionState) {
						onSetupQuestionnaire(questionState.getWord1(), questionState.getDescription1(), questionState.getHintsLeft(), questionState.getJokerLeft(), questionState.getBrainsLeft());
					}
					
					@Override
					public void handle(WrongAnswerState wrongAnswerState) {
						notifyAllOnWrongAnswer(wrongAnswerState.getCorrectAnswer(), wrongAnswerState.getPercentage());
					}
					
					@Override
					public void handle(CorrectAnswerState correctAnswerState) {
						notifyAllOnCorrectAnswer(correctAnswerState.getPercentage());
					}
					
					@Override
					public void handle(InitState initState) {
						notifyAllOnAbortQuestionnaire();
					}

					@Override
					public void handle(FinishedState finishedState) {
						notifyAllOnFinished();
					}
					
				};
			}

			@Override
			public void notifyAllOnHintGained(int hintsAvailable) {
				if (hintsAvailable == 1) {
					getButtonRequestHint().setEnabled(true);
				}
				getButtonRequestHint().setText("" + hintsAvailable);
			}

			@Override
			public void notifyAllOnHintGranted(String hint, int hintsLeft) {
				if (hintsLeft == 0) {
					getButtonRequestHint().setEnabled(false);
				}
				getButtonRequestHint().setText("" + hintsLeft);
				getButtonRequestHint().setEnabled(false);
				getTextFieldTraineeInput().setText(hint);
				getTextFieldTraineeInput().requestFocus();
			}

			@Override
			public void notifyAllOnJokerGained(int jokersAvailable) {
				if (jokersAvailable == 1) {
					getButtonRequestJoker().setEnabled(true);
				}
				getButtonRequestJoker().setText("" + jokersAvailable);
			}

			@Override
			public void notifyAllOnJokerGranted(int jokersLeft) {
				if (jokersLeft == 0) {
					getButtonRequestJoker().setEnabled(false);
				}
				getButtonRequestJoker().setText("" + jokersLeft);
				getButtonRequestJoker().setEnabled(false);
				getTextFieldTraineeInput().requestFocus();
			}

			@Override
			public void notifyAllOnBrainGranted(int brainsLeft) {
				if (brainsLeft == 0) {
					getButtonRequestBrain().setEnabled(false);
				}
				getButtonRequestBrain().setText("" + brainsLeft);
				getButtonRequestBrain().setEnabled(false);
				getTextFieldTraineeInput().requestFocus();
			}

			private JLabel currentBadge;
			
			@Override
			public void notifyAllOnTimerProgress(BadgeTime badgeTime) {
				SwingUtilities.invokeLater(() -> {
					getProgressBarTime().setValue(badgeTime.value());
					JLabel jLabelBadge = switch (badgeTime.badge()) {
						case GOLD -> getLabelGoldBadge();
						case SILVER -> getLabelSilverBadge();
						case BRONZE -> getLabelBronzeBadge();
						case NONE -> getLabelNoneBadge();
					};
					
					if (currentBadge == null) {
						currentBadge = jLabelBadge;
						QuestionnairePanel.this.add(currentBadge);
						QuestionnairePanel.this.revalidate();
						QuestionnairePanel.this.repaint();
					} else {
						if (!currentBadge.equals(jLabelBadge)) {
							QuestionnairePanel.this.remove(currentBadge);
							currentBadge = jLabelBadge;
							QuestionnairePanel.this.add(currentBadge);
							QuestionnairePanel.this.revalidate();
							QuestionnairePanel.this.repaint();
						}
					}
				});
			}

		};
	}


	private JLabel getLabelPercentage() {
		if (labelPercentage == null) {
			labelPercentage = new JLabel("", JLabel.RIGHT);
			labelPercentage.setBounds(380, 365, 400, 40);
			labelPercentage.setForeground(Color.WHITE);
			labelPercentage.setFont(FONT_24);
		}
		return labelPercentage;
	}


	private JButton getButtonAbort() {
		if (buttonAbort == null) {
			buttonAbort = new JButton("Abort") {

				private static final long serialVersionUID = -8456557896265701288L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}
			};
			buttonAbort.setBounds(180, 365, 140, 50);
			buttonAbort.setFont(FONT_16);
			buttonAbort.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "pressed");
			buttonAbort.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
			buttonAbort.setOpaque(false);
			buttonAbort.setBackground(TRANSPARENT_BACKGROUND);
			buttonAbort.setForeground(Color.WHITE);
//			buttonAbort.addActionListener(a -> oopdi.execRunnable(Questionnaire.class, q -> q::abortQuestionnaire));
			buttonAbort.addActionListener(a -> oopdi.getInstance(Questionnaire.class).abortQuestionnaire());
		}

		return buttonAbort;
	}

	
	private JButton getButtonContinue() {
		if (buttonContinue == null) {
			buttonContinue = new JButton("Continue") {

				private static final long serialVersionUID = -6411593188920813232L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}
			};
			buttonContinue.setBounds(20, 365, 140, 50);
			buttonContinue.setFont(FONT_16);
			buttonContinue.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "pressed");
			buttonContinue.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
			buttonContinue.setOpaque(false);
			buttonContinue.setBackground(TRANSPARENT_BACKGROUND);
			buttonContinue.setForeground(Color.WHITE);
			buttonContinue.addActionListener(a -> {
				/*oopdi.execRunnable(Questionnaire.class, q -> q::continueQuestionnaire);*/
				oopdi.getInstance(Questionnaire.class).continueQuestionnaire();
			});
		}
		return buttonContinue;
	}


	private JTextField getTextFieldCorrectAnswer() {
		if (textFieldCorrectAnswer == null) {
			textFieldCorrectAnswer = new JTextField();
			textFieldCorrectAnswer.setBounds(20, 275, 550, 50);
			textFieldCorrectAnswer.setBorder(textFieldBorder);
			textFieldCorrectAnswer.setOpaque(false);
			textFieldCorrectAnswer.setForeground(Color.RED);
			textFieldCorrectAnswer.setEnabled(false);
			textFieldCorrectAnswer.setDisabledTextColor(Color.RED);
		}
		return textFieldCorrectAnswer;
	}


	private JTextField getTextFieldTraineeInput() {
		if (textFieldTraineeInput == null) {
			textFieldTraineeInput = new JTextField() {

				private static final long serialVersionUID = -5311961230116363510L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}
			};
			textFieldTraineeInput.setDisabledTextColor(Color.WHITE);
			textFieldTraineeInput.setForeground(Color.WHITE);
			textFieldTraineeInput.setBounds(20, 225, 720, 50);
			textFieldTraineeInput.setBorder(textFieldBorder);
			textFieldTraineeInput.setBackground(TRANSPARENT_BACKGROUND);
			textFieldTraineeInput.setOpaque(false);
			textFieldTraineeInput.setCaretColor(Color.WHITE);
			textFieldTraineeInput.addActionListener(a -> {
				/*oopdi.execConsumer(Questionnaire.class, q -> q::provideAnswer, getTextFieldTraineeInput().getText());*/
				oopdi.getInstance(Questionnaire.class).provideAnswer(getTextFieldTraineeInput().getText());
			});
		}
		return textFieldTraineeInput;
	}


	private JLabel getLabelWord2() {
		if (labelWord2 == null) {
			labelWord2 = new JLabel();
			labelWord2.setText("Your translation:");
			labelWord2.setFont(FONT_12);
			labelWord2.setBounds(20, 200, 720, 20);
			labelWord2.setForeground(Color.WHITE);
		}
		return labelWord2;
	}


	private JTextField getTextFieldDescription1() {
		if (textFieldDescription1 == null) {
			textFieldDescription1 = new JTextField() {

				private static final long serialVersionUID = -7007727856647018665L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}
			};
			textFieldDescription1.setBackground(TRANSPARENT_BACKGROUND);
			textFieldDescription1.setDisabledTextColor(Color.WHITE);
			textFieldDescription1.setBounds(20, 110, 720, 50);
			textFieldDescription1.setBorder(textFieldBorder);
			textFieldDescription1.setEnabled(false);
			textFieldDescription1.setText("Description 1");
			textFieldDescription1.setOpaque(false);
		}
		return textFieldDescription1;
	}


	private JLabel getLabelDescription1() {
		if (labelDescription1 == null) {
			labelDescription1 = new JLabel();
			labelDescription1.setText("This is a hint!");
			labelDescription1.setFont(FONT_12);
			labelDescription1.setBounds(20, 85, 720, 20);
			labelDescription1.setForeground(Color.WHITE);
		}
		return labelDescription1;
	}


	private JTextField getTextFieldWord1() {
		if (textFieldWord1 == null) {
			textFieldWord1 = new JTextField() {

				private static final long serialVersionUID = -7093619312704386950L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}
			};

			textFieldWord1.setBackground(TRANSPARENT_BACKGROUND);
			textFieldWord1.setBounds(20, 25, 720, 50);
			textFieldWord1.setBorder(textFieldBorder);
			textFieldWord1.setEnabled(false);
			textFieldWord1.setText("dfjdshkj");
			textFieldWord1.setOpaque(false);
			textFieldWord1.setDisabledTextColor(Color.WHITE);
		}
		return textFieldWord1;
	}

	private JLabel getLabelWord1() {
		if (labelWord1 == null) {
			labelWord1 = new JLabel();
			labelWord1.setText("Translate following term:");
			labelWord1.setFont(FONT_12);
			labelWord1.setBounds(20, 0, 720, 20);
			labelWord1.setForeground(Color.WHITE);
		}
		return labelWord1;
	}

	private JButton getButtonRequestHint() {
		if (buttonRequestHint == null) {
			buttonRequestHint = new JButton() {

				private static final long serialVersionUID = -6411593188920813232L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}
			};
			buttonRequestHint.setText("0");
			buttonRequestHint.setFont(FONT_12);
			buttonRequestHint.setBounds(645, 285, 25, 25);
			buttonRequestHint.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "pressed");
			buttonRequestHint.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
			buttonRequestHint.setOpaque(false);
			buttonRequestHint.setBackground(TRANSPARENT_BACKGROUND);
			buttonRequestHint.setForeground(Color.WHITE);
			buttonRequestHint.setEnabled(false);
			buttonRequestHint.setMargin(new Insets(0, 0, 0, 0));
			buttonRequestHint.addActionListener(a -> {
				/*oopdi.execRunnable(Questionnaire.class, q -> q::requestHint);*/
				oopdi.getInstance(Questionnaire.class).requestHint();
			});
		}
		return buttonRequestHint;
	}

	private JButton getButtonRequestJoker() {
		if (buttonRequestJoker == null) {
			buttonRequestJoker = new JButton() {

				private static final long serialVersionUID = -6411593188920813232L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}
			};
			buttonRequestJoker.setText("0");
			buttonRequestJoker.setFont(FONT_12);
			buttonRequestJoker.setBounds(715, 285, 25, 25);
			buttonRequestJoker.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "pressed");
			buttonRequestJoker.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
			buttonRequestJoker.setOpaque(false);
			buttonRequestJoker.setBackground(TRANSPARENT_BACKGROUND);
			buttonRequestJoker.setForeground(Color.WHITE);
			buttonRequestJoker.setEnabled(false);
			buttonRequestJoker.setMargin(new Insets(0, 0, 0, 0));
			buttonRequestJoker.addActionListener(a -> {
				/*oopdi.execRunnable(Questionnaire.class, q -> q::requestJoker);*/
				oopdi.getInstance(Questionnaire.class).requestJoker();
			});
		}
		return buttonRequestJoker;
	}

	private JButton getButtonRequestBrain() {
		if (buttonRequestBrain == null) {
			buttonRequestBrain = new JButton() {

				private static final long serialVersionUID = -6411593188920813232L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}
			};
			buttonRequestBrain.setText("0");
			buttonRequestBrain.setFont(FONT_12);
			buttonRequestBrain.setBounds(575, 285, 25, 25);
			buttonRequestBrain.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "pressed");
			buttonRequestBrain.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
			buttonRequestBrain.setOpaque(false);
			buttonRequestBrain.setBackground(TRANSPARENT_BACKGROUND);
			buttonRequestBrain.setForeground(Color.WHITE);
			buttonRequestBrain.setMargin(new Insets(0, 0, 0, 0));
			buttonRequestBrain.addActionListener(a -> {
				/*oopdi.execRunnable(Questionnaire.class, q -> q::requestBrain);*/
				oopdi.getInstance(Questionnaire.class).requestBrain();
			});
		}
		return buttonRequestBrain;
	}

	private JLabel getLabelHint() {
		if (labelHint == null) {
			labelHint = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("hint.png")));
			labelHint.setBounds(615, 285, 25, 25);
			labelHint.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}
		
		return labelHint;
	}


	private JLabel getLabelJoker() {
		if (labelJoker == null) {
			labelJoker = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("joker.png")));
			labelJoker.setBounds(685, 285, 25, 25);
			labelJoker.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}
		return labelJoker;
	}
	    
	private JLabel getLabelBrain() {
		if (labelBrain == null) {
			labelBrain = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("brain.png")));
			labelBrain.setBounds(545, 285, 25, 25);
			labelBrain.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}
		return labelBrain;
	}

}
