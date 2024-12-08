package de.oopexpert.vocabulary.model;

import java.util.UUID;

public class Reward {
	
	private static final long ONE_DAY = 1000l * 60l * 60l * 24l;
	private transient String id;
	private String title;
	private String description;
	private long lastClaimedAt;
	private long claimInterval;
	private int score;
	
	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public long getLastClaimedAt() {
		return lastClaimedAt;
	}

	public long getClaimInterval() {
		return claimInterval;
	}

	public void setLastClaimedAt(long lastClaimedAt) {
		this.lastClaimedAt = lastClaimedAt;
	}

	public UUID getId() {
		return UUID.fromString(id);
	}

	public void setId(UUID uuid) {
		this.id = uuid.toString();
	}

	public int getScore() {
		return score;
	}
	
	public long getClaimableAt() {
		return getLastClaimedAt() + getClaimInterval() - ((getLastClaimedAt() + getClaimInterval()) % ONE_DAY);
	}
	
	public boolean isClaimable(int score) {
		return hasEnoughPoints(score) && isClaimTemporarilyAvailable();
	}

	public boolean isClaimTemporarilyAvailable(Reward reward) {
		return reward.getClaimableAt() < System.currentTimeMillis();
	}

	public boolean hasEnoughPoints(int score) {
		return score > this.getScore();
	}
	
	public boolean isClaimTemporarilyAvailable() {
		return getClaimableAt() < System.currentTimeMillis();
	}

}
