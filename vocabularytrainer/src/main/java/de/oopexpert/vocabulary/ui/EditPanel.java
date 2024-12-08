package de.oopexpert.vocabulary.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class EditPanel extends JPanel {

	private static final long serialVersionUID = -6922470704644768630L;

	private Font segoeUIFont = new Font("Monospaced", Font.BOLD, 12);
	private Font segoeUIFont16 = new Font("Monospaced", Font.BOLD, 16);

	private Border textFieldBorder = BorderFactory.createEmptyBorder(0, 5, 0, 5);

	private JLabel labelWord1;

	private JTextField textFieldWord1;

	private JLabel labelDescription1;

	private JTextField textFieldDescription1;

	private JLabel labelWord2;

	private JComponent textFieldWord2;

	private JLabel labelDescription2;

	private JComponent textFieldDescription2;

	private JLabel labelCountFromTo;

	private JComponent buttonNext;

	private JComponent buttonNew;

	private Component buttonDelete;

	private JButton buttonPrevious;


	public EditPanel() {
		
		setBackground(Color.BLACK);
		setLayout(null);

		add(getLabelWord1());
		add(getLabelDescription1());
		add(getTextFieldWord1());
		add(getTextFieldDescription1());
		add(getLabelWord2());
		add(getTextFieldWord2());
		add(getLabelDescription2());
		add(getTextFieldDescription2());
		add(getButtonNext());
		add(getLabelCountFromTo());
		add(getButtonPrevious());
		add(getButtonNew());
		add(getButtonDelete());
		
	}


	private Component getButtonDelete() {
		if (buttonDelete == null) {
			buttonDelete = new JButton("-");
			buttonDelete.setBounds(340, 365, 50, 50);
			buttonDelete.setFont(segoeUIFont16);
			buttonDelete.setBackground(Color.WHITE);
		}
		return buttonDelete;
	}


	private Component getButtonNew() {
		if (buttonNew == null) {
			buttonNew = new JButton("+");
			buttonNew.setBounds(260, 365, 50, 50);
			buttonNew.setFont(segoeUIFont16);
			buttonNew.setBackground(Color.WHITE);
		}
		return buttonNew;
	}


	private Component getButtonPrevious() {
		if (buttonPrevious == null) {
			buttonPrevious = new JButton("<");
			buttonPrevious.setBounds(20, 365, 50, 50);
			buttonPrevious.setFont(segoeUIFont16);
			buttonPrevious.setBackground(Color.WHITE);
		}
		return buttonPrevious;
	}


	private Component getLabelCountFromTo() {
		if (labelCountFromTo == null) {
			labelCountFromTo = new JLabel();
			labelCountFromTo.setText("00/00");
			labelCountFromTo.setFont(segoeUIFont16);
			labelCountFromTo.setBounds(100, 365, 50, 50);
			labelCountFromTo.setForeground(Color.WHITE);
		}
		return labelCountFromTo;
	}


	private Component getButtonNext() {
		if (buttonNext == null) {
			buttonNext = new JButton(">");
			buttonNext.setBounds(180, 365, 50, 50);
			buttonNext.setFont(segoeUIFont16);
			buttonNext.setBackground(Color.WHITE);
		}
		return buttonNext;
	}


	private Component getTextFieldDescription2() {
		if (textFieldDescription2 == null) {
			textFieldDescription2 = new JTextField();
			textFieldDescription2.setBackground(Color.WHITE);
			textFieldDescription2.setBounds(20, 285, 760, 50);
			textFieldDescription2.setBorder(textFieldBorder);
		}
		return textFieldDescription2;
	}


	private Component getLabelDescription2() {
		if (labelDescription2 == null) {
			labelDescription2 = new JLabel();
			labelDescription2.setText("Description 2");
			labelDescription2.setFont(segoeUIFont);
			labelDescription2.setBounds(20, 260, 100, 20);
			labelDescription2.setForeground(Color.WHITE);
		}
		return labelDescription2;
	}


	private Component getTextFieldWord2() {
		if (textFieldWord2 == null) {
			textFieldWord2 = new JTextField();
			textFieldWord2.setBackground(Color.WHITE);
			textFieldWord2.setBounds(20, 205, 760, 50);
			textFieldWord2.setBorder(textFieldBorder);
		}
		return textFieldWord2;
	}


	private Component getLabelWord2() {
		if (labelWord2 == null) {
			labelWord2 = new JLabel();
			labelWord2.setText("Language 2");
			labelWord2.setFont(segoeUIFont);
			labelWord2.setBounds(20, 180, 100, 20);
			labelWord2.setForeground(Color.WHITE);
		}
		return labelWord2;
	}


	private Component getTextFieldDescription1() {
		if (textFieldDescription1 == null) {
			textFieldDescription1 = new JTextField();
			textFieldDescription1.setBackground(Color.WHITE);
			textFieldDescription1.setBounds(20, 110, 760, 50);
			textFieldDescription1.setBorder(textFieldBorder);
		}
		return textFieldDescription1;
	}


	private Component getTextFieldWord1() {
		if (textFieldWord1 == null) {
			textFieldWord1 = new JTextField();
			textFieldWord1.setBackground(Color.WHITE);
			textFieldWord1.setBounds(20, 25, 760, 50);
			textFieldWord1.setBorder(textFieldBorder);
		}
		return textFieldWord1;
	}


	private Component getLabelDescription1() {
		if (labelDescription1 == null) {
			labelDescription1 = new JLabel();
			labelDescription1.setText("Description 1");
			labelDescription1.setFont(segoeUIFont);
			labelDescription1.setBounds(20, 85, 100, 20);
			labelDescription1.setForeground(Color.WHITE);
		}
		return labelDescription1;
	}


	private Component getLabelWord1() {
		if (labelWord1 == null) {
			labelWord1 = new JLabel();
			labelWord1.setText("Language 1");
			labelWord1.setFont(segoeUIFont);
			labelWord1.setBounds(20, 0, 100, 20);
			labelWord1.setForeground(Color.WHITE);
		}
		return labelWord1;
	}
	
}
