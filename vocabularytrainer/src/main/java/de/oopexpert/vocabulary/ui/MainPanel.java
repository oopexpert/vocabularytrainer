package de.oopexpert.vocabulary.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.oopexpert.oopdi.OOPDI;
import de.oopexpert.vocabulary.model.VocabularyLibrary;
import de.oopexpert.vocabulary.model.VocabularySet;
import de.oopexpert.vocabulary.model.VocabularyTrainer;
import de.oopexpert.vocabulary.model.VocabularyTrainer.State;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = -6922470704644768630L;
	
	private Font FONT_16 = new Font("Monospaced", Font.BOLD, 16);
	private Font FONT_14 = new Font("Monospaced", Font.BOLD, 14);

	private JButton buttonExit;
	private JButton buttonQuestionnaire;
	private JButton buttonRewards;
	private JCheckBox checkBoxLanguageSwitch;
	private JList<VocabularySet> listVocabularrySets;
	private ListCellRenderer<VocabularySet> cellRendererVocabularySet;
	private JScrollPane scrollPaneVocabularySets;

	private Color TRANSPARENT_BACKGROUND = new Color(255,255,255,65);

	private OOPDI<VocabularyTrainer> oopdi;

	private JLabel labelScore;

	public MainPanel(OOPDI<VocabularyTrainer> oopdi) {
		
		setLayout(null);
		
		this.oopdi = oopdi;
		
		add(getButtonRewards());
		add(getButtonQuestionnaire());
		add(getButtonExit());
		add(getScrollPaneVocabularySets());
		add(getCheckBoxLanguageSwitch());
		add(getLabelScore());
		
		attachListeners();
		
	}

	public JLabel getLabelScore() {
		if (labelScore == null) {
			labelScore = new JLabel("", JLabel.RIGHT);
			labelScore.setFont(FONT_14);
			labelScore.setBounds(20, 360, 675, 20);
			labelScore.setForeground(Color.WHITE);
			labelScore.setBackground(Color.BLUE);
		}
		return labelScore;
	}

	private String getScoreStr(int currentScore) {
		return "Current score: " + currentScore;
	}

	private void attachListeners() {
		oopdi.getInstance(VocabularyLibrary.class).addListener(new VocabularyLibrary.Listener() {
			
			@Override
			public void vocabularySetRemoved(VocabularySet set) {}
			
			@Override
			public void vocabularySetAdded(VocabularySet set) {}
			
			@Override
			public void onAddedAsListener(List<VocabularySet> sets) {
		        DefaultListModel<VocabularySet> model = new DefaultListModel<>();

		        for (VocabularySet vocabularySet : sets) {
		            model.addElement(vocabularySet); // Add name of VocabularySet to list model
		        }

		        getListVocabularySets().setModel(model); 
			}
			
		});
//		oopdi.execConsumer(VocabularyLibrary.class, vl -> vl::addListener, new VocabularyLibrary.Listener() {
//			
//			@Override
//			public void vocabularySetRemoved(VocabularySet set) {}
//			
//			@Override
//			public void vocabularySetAdded(VocabularySet set) {}
//			
//			@Override
//			public void onAddedAsListener(List<VocabularySet> sets) {
//		        DefaultListModel<VocabularySet> model = new DefaultListModel<>();
//
//		        for (VocabularySet vocabularySet : sets) {
//		            model.addElement(vocabularySet); // Add name of VocabularySet to list model
//		        }
//
//		        getListVocabularySets().setModel(model); 
//			}
//			
//		});

//		oopdi.execConsumer(VocabularyTrainer.class, vt -> vt::addOnSelectionChangedObserver, (from, to) -> this.onVocabularySetChanged(to));
		oopdi.getInstance(VocabularyTrainer.class).addOnSelectionChangedObserver((from, to) -> this.onVocabularySetChanged(to));
//		oopdi.execConsumer(VocabularyTrainer.class, vt -> vt::addOnSelectObserver, (current) -> this.onSelectVocabularySet(current));
		oopdi.getInstance(VocabularyTrainer.class).addOnSelectObserver((current) -> this.onSelectVocabularySet(current));
//		oopdi.execConsumer(VocabularyTrainer.class, vt -> vt::addOnDeselectObserver, (previous) -> this.onDeselectVocabularySet(previous));
		oopdi.getInstance(VocabularyTrainer.class).addOnDeselectObserver((previous) -> this.onDeselectVocabularySet(previous));
//		oopdi.execConsumer(VocabularyTrainer.class, vt -> vt::addListener, new VocabularyTrainer.Listener() {
//
//			@Override
//			public void onRegisteredAsListener(int currentScore, State state) {
//				getLabelScore().setText(getScoreStr(currentScore));
//			}
//
//			@Override
//			public void onAbortQuestionnaire(int currentScore) {
//				getLabelScore().setText(getScoreStr(currentScore));
//			}
//
//		});
		oopdi.getInstance(VocabularyTrainer.class).addListener(new VocabularyTrainer.Listener() {

			@Override
			public void onRegisteredAsListener(int currentScore, State state) {
				getLabelScore().setText(getScoreStr(currentScore));
			}

			@Override
			public void onAbortQuestionnaire(int currentScore) {
				getLabelScore().setText(getScoreStr(currentScore));
			}

		});
	}
	
	private void onVocabularySetChanged(VocabularySet to) {
	}

	private void onDeselectVocabularySet(VocabularySet previous) {
		this.getButtonQuestionnaire().setEnabled(false);
	}

	private void onSelectVocabularySet(VocabularySet selected) {
		this.getButtonQuestionnaire().setEnabled(true);
	}
	
	private JButton getButtonRewards() {
		if (buttonRewards == null) {
			buttonRewards = new JButton("Rewards") {
				private static final long serialVersionUID = 187142657918954169L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}
			};
			buttonRewards.setFont(FONT_16);
			buttonRewards.setBounds(500, 50, 200, 80);
			buttonRewards.setBackground(Color.WHITE);
			buttonRewards.setOpaque(false);
			buttonRewards.setBackground(TRANSPARENT_BACKGROUND);
			buttonRewards.setForeground(Color.WHITE);
//			buttonRewards.addActionListener(a -> oopdi.execRunnable(VocabularyTrainer.class, vt -> vt::gotoRewards));
			buttonRewards.addActionListener(a -> oopdi.getInstance(VocabularyTrainer.class).gotoRewards());
			
		}
		return buttonRewards;
	}
	
	private JButton getButtonQuestionnaire() {
		if (buttonQuestionnaire == null) {
			buttonQuestionnaire = new JButton("Questionnaire") {

				private static final long serialVersionUID = -9186485321936059250L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}

			};
			buttonQuestionnaire.setFont(FONT_16);
			buttonQuestionnaire.setBounds(500, 150, 200, 80);
			buttonQuestionnaire.setBackground(Color.WHITE);
			buttonQuestionnaire.setEnabled(false);
			buttonQuestionnaire.setOpaque(false);
			buttonQuestionnaire.setBackground(TRANSPARENT_BACKGROUND);
			buttonQuestionnaire.setForeground(Color.WHITE);
//			buttonQuestionnaire.addActionListener(a -> oopdi.execRunnable(VocabularyTrainer.class, vt -> vt::gotoQuestionnaire));
			buttonQuestionnaire.addActionListener(a -> oopdi.getInstance(VocabularyTrainer.class).gotoQuestionnaire());

		}
		return buttonQuestionnaire;
	}
	
	private JButton getButtonExit() {
		if (buttonExit == null) {
			buttonExit = new JButton("Exit Program") {

				private static final long serialVersionUID = 2090209840351236860L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}

			};
			buttonExit.setFont(FONT_16);
			buttonExit.setBounds(500, 250, 200, 80);
			buttonExit.setBackground(Color.WHITE);
			buttonExit.addActionListener(a -> System.exit(0));
			buttonExit.setOpaque(false);
			buttonExit.setBackground(TRANSPARENT_BACKGROUND);
			buttonExit.setForeground(Color.WHITE);
		}
		return buttonExit;
	}
	
	
	private JCheckBox getCheckBoxLanguageSwitch() {
		if (checkBoxLanguageSwitch == null) {
			checkBoxLanguageSwitch = new JCheckBox("Switch language?") {

				private static final long serialVersionUID = -7445160627755670937L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}
	
			};
			checkBoxLanguageSwitch.setFont(FONT_14);
			checkBoxLanguageSwitch.setBounds(100, 340, 200, 20);
			checkBoxLanguageSwitch.setSelected(false);
			checkBoxLanguageSwitch.setForeground(Color.WHITE);
			checkBoxLanguageSwitch.setBackground(new Color(255,255,255,0));
			checkBoxLanguageSwitch.setOpaque(false);
//			checkBoxLanguageSwitch.addActionListener(a -> oopdi.execConsumer(VocabularyTrainer.class, vt -> vt::setSwitchLanguage, checkBoxLanguageSwitch.isSelected()));
			checkBoxLanguageSwitch.addActionListener(a -> oopdi.getInstance(VocabularyTrainer.class).setSwitchLanguage(getFocusTraversalKeysEnabled()));
			
		}		
		return checkBoxLanguageSwitch;
	}
	
	private JList<VocabularySet> getListVocabularySets() {
		if (listVocabularrySets == null) {
			listVocabularrySets = new JList<>();
			listVocabularrySets.setBorder(new EmptyBorder(0, 0, 0, 0));
			listVocabularrySets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listVocabularrySets.setSelectionBackground(Color.LIGHT_GRAY);
			listVocabularrySets.setCellRenderer(getCellRendererVocabularySet());
			listVocabularrySets.addListSelectionListener(new ListSelectionListener() {
	            @Override
	            public void valueChanged(ListSelectionEvent e) {
	                // Handle the selection change here
	                if (!e.getValueIsAdjusting()) {
	                    // Get the selected VocabularySet object
	                    VocabularySet selectedVocabularySet = listVocabularrySets.getSelectedValue();
//	                    oopdi.execConsumer(VocabularyTrainer.class, vt -> vt::selectVocabularySet, selectedVocabularySet);
	                    oopdi.getInstance(VocabularyTrainer.class).selectVocabularySet(selectedVocabularySet);
	                }
	            }
	        });

		}
		return listVocabularrySets;
	}
	
	private ListCellRenderer<VocabularySet> getCellRendererVocabularySet() {
		if (cellRendererVocabularySet == null) {
			
			VocabularySetInView view = new VocabularySetInView();

			cellRendererVocabularySet = new ListCellRenderer<VocabularySet>() {

				@Override
				public Component getListCellRendererComponent(JList<? extends VocabularySet> list, VocabularySet value, int index, boolean isSelected, boolean cellHasFocus) {
					view.labelName.setText(value.getName());
					view.labelSource.setText(value.getSource());
					view.labelCount.setText("Count: " + value.count());
					view.labelScore.setText("Score:");
					view.labelBadge1.setIcon(new ImageIcon(getClass().getClassLoader().getResource("badge_" + value.getBadge1().name().toLowerCase() + "_small.png")));
					view.labelScore1.setText("(" + value.getCurrentScore1() + "/" + value.getMaxScore1() + ")");
					view.labelBadge2.setIcon(new ImageIcon(getClass().getClassLoader().getResource("badge_" + value.getBadge2().name().toLowerCase() + "_small.png")));
					view.labelScore2.setText("(" + value.getCurrentScore2() + "/" + value.getMaxScore2() + ")");
			        if (isSelected) {
			        	view.setBackground(list.getSelectionBackground());
			            view.labelName.setBackground(list.getSelectionBackground());
			            view.labelSource.setForeground(list.getSelectionForeground());
			        } else {
			        	view.setBackground(list.getBackground());
			            view.labelName.setBackground(list.getBackground());
			            view.labelSource.setForeground(list.getForeground());
			        }
			        return view;
				}
			};
		}
		return cellRendererVocabularySet;
	}
	
	private JScrollPane getScrollPaneVocabularySets() {
		if (scrollPaneVocabularySets == null) {
			scrollPaneVocabularySets = new JScrollPane(getListVocabularySets());
			scrollPaneVocabularySets.setBorder(new EmptyBorder(0, 0, 0, 0));
			scrollPaneVocabularySets.setBounds(100, 50, 350, 280);
		}
		return scrollPaneVocabularySets;
	}
	
}
