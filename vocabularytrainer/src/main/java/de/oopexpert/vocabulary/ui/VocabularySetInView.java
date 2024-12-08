package de.oopexpert.vocabulary.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

class VocabularySetInView extends JPanel {
	
	private static final long serialVersionUID = 1639692973549802492L;
	
	public JLabel labelName;
	public JLabel labelSource;
	public JLabel labelCount;
	public JLabel labelScore;
	public JLabel labelScore1;
	public JLabel descScore1;
	public JLabel labelBadge1;
	public JLabel labelScore2;
	public JLabel descScore2;
	public JLabel labelBadge2;
	private JPanel panelStatistic;
	

	private JLabel labelGoldBadge;
	private JLabel labelSilverBadge;
	private JLabel labelBronzeBadge;
	private JLabel labelNoneBadge;
	
	
	
	public VocabularySetInView() {
		
		
		setLayout(new GridLayout(4, 1));
		setBorder(new EmptyBorder(10,10,10,10));
		
        Font nameFont = new Font("Monospaced", Font.BOLD, 16); // Use a bold font for the name
		this.labelName = new JLabel();
		this.labelName.setFont(nameFont);
		labelName.setOpaque(false);
		
        Font sourceFont = new Font("Monospaced", Font.BOLD, 12); // Use a regular font for the source
		this.labelSource = new JLabel();
		this.labelSource.setFont(sourceFont);
		labelSource.setOpaque(false);

		this.labelCount = new JLabel();
		this.labelCount.setFont(sourceFont);
		labelCount.setOpaque(false);

		this.labelScore = new JLabel("Score: ");
		this.labelScore.setFont(sourceFont);
		labelScore.setOpaque(false);
		labelScore.setBorder(new EmptyBorder(0,0,0,0));

		this.descScore1 = new JLabel("Lang.1:");
		this.descScore1.setFont(sourceFont);
		descScore1.setOpaque(false);
		descScore1.setBorder(new EmptyBorder(0,0,0,0));
		
		labelBadge1 = new JLabel();

		this.labelScore1 = new JLabel();
		this.labelScore1.setFont(sourceFont);
		labelScore1.setOpaque(false);
		labelScore1.setBorder(new EmptyBorder(0,0,0,0));

		this.descScore2 = new JLabel("Lang.2:");
		this.descScore2.setFont(sourceFont);
		descScore2.setOpaque(false);
		descScore2.setBorder(new EmptyBorder(0,0,0,0));

		labelBadge2 = new JLabel();

		this.labelScore2 = new JLabel();
		this.labelScore2.setFont(sourceFont);
		labelScore2.setOpaque(false);
		labelScore2.setBorder(new EmptyBorder(0,0,0,0));

		JPanel panelContainerStatistic = new JPanel();
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 0, 0);
		panelContainerStatistic.setLayout(flowLayout);
		panelContainerStatistic.setBorder(new EmptyBorder(0, 0, 0, 0));
		panelContainerStatistic.setOpaque(false);
		
		this.panelStatistic = new JPanel();
		this.panelStatistic.setLayout(new GridLayout(1, 7));
		this.panelStatistic.setBorder(new EmptyBorder(0,0,0,0));
		this.panelStatistic.add(labelScore2);

		this.panelStatistic.add(descScore1);
		this.panelStatistic.add(labelScore1);
		this.panelStatistic.add(labelBadge1);
		this.panelStatistic.add(descScore2);
		this.panelStatistic.add(labelScore2);
		this.panelStatistic.add(labelBadge2);

		panelContainerStatistic.add(panelStatistic);
		
		setOpaque(true);

		add(labelName);
		add(labelSource);
		add(labelCount);
		add(panelContainerStatistic);
		
	}
	
	private JLabel getLabelGoldBadge() {
		if (labelGoldBadge == null) {
			labelGoldBadge = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("badge_gold_small.png")));
		}
		return labelGoldBadge;
	}

	private JLabel getLabelSilverBadge() {
		if (labelSilverBadge == null) {
			labelSilverBadge = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("badge_silver_small.png")));
		}
		return labelSilverBadge;
	}

	private JLabel getLabelBronzeBadge() {
		if (labelBronzeBadge == null) {
			labelBronzeBadge = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("badge_bronze_small.png")));
		}
		return labelBronzeBadge;
	}

	private JLabel getLabelNoneBadge() {
		if (labelNoneBadge == null) {
			labelNoneBadge = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("badge_none_small.png")));
		}
		return labelNoneBadge;
	}

	
}