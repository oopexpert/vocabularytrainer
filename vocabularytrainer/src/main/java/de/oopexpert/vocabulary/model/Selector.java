package de.oopexpert.vocabulary.model;

import java.util.HashSet;
import java.util.Set;

public class Selector<T> {

	@FunctionalInterface
	public interface OnSelectionChangedObserver<T> {
		void onSelectionChanged(T from, T to);
	}

	@FunctionalInterface
	public interface OnDeselectObserver<T> {
		void onDeselected(T previous);
	}

	@FunctionalInterface
	public interface OnSelectObserver<T> {
		void onSelected(T t);
	}

	private final Set<OnSelectionChangedObserver<T>> onSelectionChangedObservers = new HashSet<>();
	private final Set<OnDeselectObserver<T>> onDeselectedObservers = new HashSet<>();
	private final Set<OnSelectObserver<T>> onSelectedObservers = new HashSet<>();

	private T t;
	
	public void change(T newT) {
		if (t != null && newT == null) {
			T previous = t;
			this.t = newT;
			onDeselectedObservers.forEach(o -> o.onDeselected(previous));
		} else if (t == null && newT != null) {
			this.t = newT;
			onSelectedObservers.forEach(o -> o.onSelected(this.t));
		} else if (t != null && newT != null) {
			if (!t.equals(newT)) {
				T previous = t;
				this.t = newT;
				onSelectionChangedObservers.forEach(o -> o.onSelectionChanged(previous, this.t));
			}
		}
	}

	public void addOnSelectionChangedObserver(OnSelectionChangedObserver<T> e) {
		onSelectionChangedObservers.add(e);
	}

	public void removeOnSelectionChangedObserver(OnSelectionChangedObserver<T> o) {
		onSelectionChangedObservers.remove(o);
	}

	public void addOnDeselectObserver(OnDeselectObserver<T> e) {
		onDeselectedObservers.add(e);
	}

	public void removeOnDeselectObserver(OnDeselectObserver<T> o) {
		onDeselectedObservers.remove(o);
	}

	public void addOnSelectObserver(OnSelectObserver<T> e) {
		onSelectedObservers.add(e);
	}

	public void removeOnSelectObserver(OnSelectObserver<T> o) {
		onSelectedObservers.remove(o);
	}

	public T getObject() {
		return t;
	}
		
	
}
