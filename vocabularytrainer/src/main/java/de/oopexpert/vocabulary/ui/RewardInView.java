package de.oopexpert.vocabulary.ui;

import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

class RewardInView extends JPanel {
	
	private static final long serialVersionUID = 1639692973549802492L;
	
	public final JLabel labelTitle;
	public final JLabel labelId;
	public final JLabel labelDescription;
	public final JLabel labelClaimScore;
	public final JLabel labelClaimableOn;
	
	public RewardInView() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10,10,10,10));
		
        Font nameFont = new Font(Font.SANS_SERIF, Font.BOLD, 16); // Use a bold font for the name
		this.labelTitle = new JLabel();
		this.labelTitle.setFont(nameFont);
		labelTitle.setOpaque(false);
		
        Font sourceFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12); // Use a regular font for the source
		this.labelId = new JLabel();
		this.labelId.setFont(sourceFont);
		labelId.setOpaque(false);

		this.labelDescription = new JLabel();
		this.labelDescription.setFont(sourceFont);
		labelDescription.setOpaque(false);

		this.labelClaimScore = new JLabel();
		this.labelClaimScore.setFont(sourceFont);
		labelClaimScore.setOpaque(false);

		this.labelClaimableOn = new JLabel();
		this.labelClaimableOn.setFont(sourceFont);
		labelClaimableOn.setOpaque(false);

		setOpaque(true);

		add(labelTitle);
		add(labelId);
		add(labelDescription);
		add(labelClaimScore);
		add(labelClaimableOn);
		
	}
	
}