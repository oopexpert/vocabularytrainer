package de.oopexpert.vocabulary.model;

public enum Badge {

	GOLD {
		@Override
		double limit() {
			return 60.0;
		}
	},
	SILVER {
		@Override
		double limit() {
			return 40.0;
		}
	},
	BRONZE {
		@Override
		double limit() {
			return 20.0;
		}
	},
	NONE {
		@Override
		double limit() {
			return 0.0;
		}
	};

	abstract double limit();
	
}
