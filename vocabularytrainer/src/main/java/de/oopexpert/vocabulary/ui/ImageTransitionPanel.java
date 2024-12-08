package de.oopexpert.vocabulary.ui;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

import de.oopexpert.oopdi.OOPDI;
import de.oopexpert.vocabulary.model.ImageTransition;
import de.oopexpert.vocabulary.model.ImageTransition.TransitionInfo;
import de.oopexpert.vocabulary.model.VocabularyTrainer;

public class ImageTransitionPanel extends JPanel {

	private static final long serialVersionUID = 8121667548376040520L;

	private TransitionInfo transitionInfo;

    public ImageTransitionPanel(OOPDI<VocabularyTrainer> oopdi) {
    	transitionInfo = new TransitionInfo(null, null, 0.0f);
    	oopdi.getInstance(ImageTransition.class).addListener(new ImageTransition.Listener() {

			@Override
			public void init(Image currentImage) {
				transitionInfo = new TransitionInfo(currentImage, transitionInfo.nextImage(), transitionInfo.transitionProgress());
				repaint();
			}

			@Override
			public void transit(TransitionInfo transitionInfo) {
				ImageTransitionPanel.this.transitionInfo = transitionInfo;
				repaint();
			}
			
		});
//		oopdi.execConsumer(ImageTransition.class, q -> q::addListener, new ImageTransition.Listener() {
//
//			@Override
//			public void init(Image currentImage) {
//				transitionInfo = new TransitionInfo(currentImage, transitionInfo.nextImage(), transitionInfo.transitionProgress());
//				repaint();
//			}
//
//			@Override
//			public void transit(TransitionInfo transitionInfo) {
//				ImageTransitionPanel.this.transitionInfo = transitionInfo;
//				repaint();
//			}
//			
//		});
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (transitionInfo.currentImage() != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            // Create an off-screen image buffer
            Image buffer = createImage(panelWidth, panelHeight);
            Graphics2D bufferGraphics = (Graphics2D) buffer.getGraphics();
            
            // Draw the current image
            bufferGraphics.drawImage(transitionInfo.currentImage(), 0, 0, null);

            if (transitionInfo.nextImage() != null && transitionInfo.transitionProgress() > 0.0f) {
                // Draw the next image with the transition effect
                bufferGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transitionInfo.transitionProgress()));
                bufferGraphics.drawImage(transitionInfo.nextImage(), 0, 0, null);
            }

            // Draw the buffered image onto the panel
            g.drawImage(buffer, 0, 0, null);
        }
    }

}
