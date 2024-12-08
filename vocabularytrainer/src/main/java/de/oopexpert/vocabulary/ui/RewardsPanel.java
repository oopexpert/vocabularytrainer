package de.oopexpert.vocabulary.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.oopexpert.oopdi.OOPDI;
import de.oopexpert.vocabulary.model.Reward;
import de.oopexpert.vocabulary.model.RewardManager;
import de.oopexpert.vocabulary.model.VocabularyTrainer;

public class RewardsPanel extends JPanel {

	private Font FONT_16 = new Font("Monospaced", Font.BOLD, 16);

	private static final long serialVersionUID = -6922470704644768630L;
	
	private JList<Reward> listRewards;
	private ListCellRenderer<Reward> cellRendererRewards;
	private JScrollPane scrollPaneRewards;
	private JButton buttonClaimReward;

	private Color TRANSPARENT_BACKGROUND = new Color(255,255,255,65);

	private OOPDI<VocabularyTrainer> oopdi;

	private JButton buttonAbort;

	public RewardsPanel(OOPDI<VocabularyTrainer> oopdi) {
		
		setLayout(null);
		
		this.oopdi = oopdi;
		
		add(getScrollPaneRewards());
		add(getButtonAbort());
		add(getButtonClaimReward());

		attachListeners();

	}

	private void attachListeners() {
		oopdi.getInstance(RewardManager.class).add(new RewardManager.Listener() {

			@Override
			public void onRegisteredAsListener(List<Reward> rewards) {
		        DefaultListModel<Reward> model = new DefaultListModel<>();

		        for (Reward reward : rewards) {
		            model.addElement(reward); // Add name of VocabularySet to list model
		        }

		        getListVocabularySets().setModel(model); 
			}

			@Override
			public void onRewardClaimed(Reward reward) {
				SwingUtilities.invokeLater(() -> {
					getListVocabularySets().updateUI();
				});
				
			}

			@Override
			public void onAbortRewards() {
			}
			
		});
//		oopdi.execConsumer(RewardManager.class, rm -> rm::add, new RewardManager.Listener() {
//
//			@Override
//			public void onRegisteredAsListener(List<Reward> rewards) {
//		        DefaultListModel<Reward> model = new DefaultListModel<>();
//
//		        for (Reward reward : rewards) {
//		            model.addElement(reward); // Add name of VocabularySet to list model
//		        }
//
//		        getListVocabularySets().setModel(model); 
//			}
//
//			@Override
//			public void onRewardClaimed(Reward reward) {
//				SwingUtilities.invokeLater(() -> {
//					getListVocabularySets().updateUI();
//				});
//				
//			}
//
//			@Override
//			public void onAbortRewards() {
//			}
//			
//		});

	}
	

	private JList<Reward> getListVocabularySets() {
		if (listRewards == null) {
			listRewards = new JList<>();
			listRewards.setBorder(new EmptyBorder(0, 0, 0, 0));
			listRewards.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listRewards.setSelectionBackground(Color.LIGHT_GRAY);
			listRewards.setCellRenderer(getCellRendererReward());
			listRewards.addListSelectionListener(new ListSelectionListener() {
	            @Override
	            public void valueChanged(ListSelectionEvent e) {
	                // Handle the selection change here
	                if (!e.getValueIsAdjusting()) {
	                    // Get the selected VocabularySet object
	                    Reward reward = listRewards.getSelectedValue();
	                    oopdi.getInstance(RewardManager.class).change(reward);
//	                    oopdi.execConsumer(RewardManager.class, vt -> vt::change, reward);
	                }
	            }
	        });

		}
		return listRewards;
	}
	
	private ListCellRenderer<Reward> getCellRendererReward() {
		if (cellRendererRewards == null) {
			
			final RewardInView view = new RewardInView();
			final String PATTERN_FORMAT = "dd.MM.yyyy";
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
		            .withZone(ZoneId.systemDefault());

			
			cellRendererRewards = new ListCellRenderer<Reward>() {

				@Override
				public Component getListCellRendererComponent(JList<? extends Reward> list, Reward value, int index, boolean isSelected, boolean cellHasFocus) {
					
					view.labelTitle.setText(value.getTitle());
					view.labelId.setText(value.getId().toString());
					if (value.isClaimTemporarilyAvailable()) {
						view.labelClaimableOn.setText("Retention time exceeded!");
					} else {
						view.labelClaimableOn.setText("Claimable at: " + formatter.format(Instant.ofEpochMilli(value.getClaimableAt())));
					}
					if (value.hasEnoughPoints(
							/*oopdi.execSupplier(VocabularyTrainer.class, vt -> vt::getCurrentScore)*/
							oopdi.getInstance(VocabularyTrainer.class).getCurrentScore())) {
						view.labelClaimScore.setText("You have enough points!");
					} else {
						view.labelClaimScore.setText("Claim Score: " + value.getScore());
					}
					if (value.isClaimable(
							/*oopdi.execSupplier(VocabularyTrainer.class, vt -> vt::getCurrentScore)*/
							oopdi.getInstance(VocabularyTrainer.class).getCurrentScore()
							)) {
						view.labelDescription.setText("Claim this reward NOW!");
						view.labelDescription.setForeground(Color.GREEN.darker());
					} else {
						view.labelDescription.setText("You cannot claim this reward!");
						view.labelDescription.setForeground(Color.RED);
					}
			        if (isSelected) {
			        	view.setBackground(list.getSelectionBackground());
			            view.labelTitle.setBackground(list.getSelectionBackground());
			            view.labelId.setForeground(list.getSelectionForeground());
			        } else {
			        	view.setBackground(list.getBackground());
			            view.labelTitle.setBackground(list.getBackground());
			            view.labelId.setForeground(list.getForeground());
			        }
			        return view;
				}
			};
		}
		return cellRendererRewards;
	}
	
	private JScrollPane getScrollPaneRewards() {
		if (scrollPaneRewards == null) {
			scrollPaneRewards = new JScrollPane(getListVocabularySets());
			scrollPaneRewards.setBorder(new EmptyBorder(0, 0, 0, 0));
			scrollPaneRewards.setBounds(100, 50, 350, 280);
		}
		return scrollPaneRewards;
	}

	private JButton getButtonClaimReward() {
		if (buttonClaimReward == null) {
			buttonClaimReward = new JButton("Claim Reward") {

				private static final long serialVersionUID = 2090209840351236860L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}

			};
			buttonClaimReward.setBounds(500, 50, 200, 80);
			buttonClaimReward.setFont(FONT_16);
			buttonClaimReward.setBackground(Color.WHITE);
//			buttonClaimReward.addActionListener(a -> oopdi.execRunnable(RewardManager.class, rm -> rm::claim));
			buttonClaimReward.addActionListener(a -> oopdi.getInstance(RewardManager.class).claim());
			buttonClaimReward.setOpaque(false);
			buttonClaimReward.setBackground(TRANSPARENT_BACKGROUND);
			buttonClaimReward.setForeground(Color.WHITE);
		}
		return buttonClaimReward;
	}


	private JButton getButtonAbort() {
		if (buttonAbort == null) {
			buttonAbort = new JButton("Return to Menu") {

				private static final long serialVersionUID = -8456557896265701288L;

				@Override
				protected void paintComponent(Graphics g) {
					 g.setColor( getBackground() );
				     g.fillRect(0, 0, getWidth(), getHeight());
				     super.paintComponent(g);
				}
			};
			buttonAbort.setBounds(500, 150, 200, 80);
			buttonAbort.setFont(FONT_16);
			buttonAbort.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "pressed");
			buttonAbort.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
			buttonAbort.setOpaque(false);
			buttonAbort.setBackground(TRANSPARENT_BACKGROUND);
			buttonAbort.setForeground(Color.WHITE);
//			buttonAbort.addActionListener(a -> oopdi.execRunnable(RewardManager.class, q -> q::abortRewards));
			buttonAbort.addActionListener(a -> oopdi.getInstance(RewardManager.class).abortRewards());
		}

		return buttonAbort;
	}
	
}
