package de.oopexpert.vocabulary.model;

import de.oopexpert.oopdi.Scope;
import de.oopexpert.oopdi.annotation.Injectable;

@Injectable(scope = Scope.GLOBAL)
public class TransitionParameters {

	public TransitionParameters() {
	}
	
	public int getDelay() {
		return 50;
	}
	
	public float getProgressIncrement() {
		return 0.05f;
	}
	
}
