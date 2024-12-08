package de.oopexpert.vocabulary.model;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Timer;

import de.oopexpert.oopdi.Scope;
import de.oopexpert.oopdi.annotation.InjectInstance;
import de.oopexpert.oopdi.annotation.Injectable;
import de.oopexpert.oopdi.annotation.PostConstruct;

@Injectable(scope = Scope.GLOBAL)
public class ImageTransition {

	private Image currentImage;
	private Image nextImage;
	private float transitionProgress; // Ranges from 0.0 to 1.0
	private Timer transitionTimer;
	
	@InjectInstance
	private TransitionParameters transitionParameters;
	
	private Set<Listener> listeners;

	public record TransitionInfo(Image currentImage, Image nextImage, float transitionProgress) {

	}

	public interface Listener {
		void init(Image currentImage);

		void transit(TransitionInfo ti);
	}

	public ImageTransition() {
		transitionProgress = 0.0f;
		listeners = new HashSet<>();
	}

	@PostConstruct
	public void postConstruct() {
		transitionTimer = new Timer(transitionParameters.getDelay(), this::transisitionStep);
	}

	private void transisitionStep(ActionEvent e) {
		transitionProgress += transitionParameters.getProgressIncrement();
		if (transitionProgress >= 1.0f) {
			transitionTimer.stop();
			transitionProgress = 1.0f;
			currentImage = nextImage;
		}
		listeners.forEach(l -> l.transit(new TransitionInfo(currentImage, nextImage, transitionProgress)));
	}
	
	public void startTransition(Image nextImage) {
		if (transitionTimer.isRunning())
			return;
		if (currentImage == null) {
			currentImage = nextImage;
			listeners.forEach(l -> l.transit(new TransitionInfo(currentImage, nextImage, transitionProgress)));
		} else {
			this.nextImage = nextImage;
			transitionProgress = 0.0f;
			transitionTimer.start();
		}
	}

	public void addListener(Listener listener) {
		listener.init(currentImage);
		this.listeners.add(listener);
	}

}
