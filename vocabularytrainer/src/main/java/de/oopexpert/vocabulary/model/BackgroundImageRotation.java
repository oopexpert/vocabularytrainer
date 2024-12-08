package de.oopexpert.vocabulary.model;


import java.awt.Image;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import de.oopexpert.oopdi.Scope;
import de.oopexpert.oopdi.annotation.InjectInstance;
import de.oopexpert.oopdi.annotation.Injectable;
import de.oopexpert.oopdi.annotation.PostConstruct;

@Injectable(scope=Scope.GLOBAL, immediate = true)
public class BackgroundImageRotation {

	private static final long ROTATION_PERIOD = 60l * 1000l;

	@InjectInstance
	private BackgroundImages backgroundImages;
	
	@InjectInstance
	private ImageTransition imageTransition;

	private Random random;
	private Timer timer;
	
	private Image currentImage;
	
	public BackgroundImageRotation() {
	    this.timer = new Timer();
	    this.random = new Random(System.currentTimeMillis());
	    try {
	    	this.currentImage = ImageIO.read(BackgroundImageRotation.class.getClassLoader().getResourceAsStream("no_background.jpg"));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@PostConstruct
	private void init() {
		timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
            	rotate();
            }

        }, 0, ROTATION_PERIOD);
	}

	private void rotate() {
		
		if (backgroundImages.isEmpty()) {
    		// do nothing
    	} else if (backgroundImages.count() == 1) {
    		currentImage = backgroundImages.get(0);
    	} else {
    		currentImage = nextRandomImageDifferentFromCurrentImage();
    		imageTransition.startTransition(currentImage);
    	}
	}

	private Image nextRandomImageDifferentFromCurrentImage() {
		Image image = currentImage;
		while (currentImage == image) {
			image = backgroundImages.get(random.nextInt(backgroundImages.count()));
		}
		return image;
	}

}
