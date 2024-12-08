package de.oopexpert.vocabulary.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;

import de.oopexpert.oopdi.Scope;
import de.oopexpert.oopdi.annotation.InjectInstance;
import de.oopexpert.oopdi.annotation.Injectable;
import de.oopexpert.oopdi.annotation.PostConstruct;
import de.oopexpert.vocabulary.model.Selector.OnDeselectObserver;
import de.oopexpert.vocabulary.model.Selector.OnSelectObserver;
import de.oopexpert.vocabulary.model.Selector.OnSelectionChangedObserver;

@Injectable(scope=Scope.GLOBAL)
public class RewardManager {

	public static interface Listener {

		default void onRegisteredAsListener(List<Reward> rewards) {};

		default void onRewardClaimed(Reward reward) {};
		
		default void onAbortRewards() {};
		
	}
	
	private Set<Listener> listeners;
	
	private Selector<Reward> selectorReward;
	
	
	@InjectInstance
	private VocabularyTrainer vocabularyTrainer;
	
	private List<Reward> rewards;
	
	public RewardManager() {
		this.rewards = new ArrayList<>();
		this.listeners = new HashSet<>();
		this.selectorReward = new Selector<>();
	}

	@PostConstruct
	public void postConstruct() {
		this.rewards.addAll(loadRewards());
	}
	
	
    private List<Reward> loadRewards() {
        File directory = new File("rewards");
        File[] rewardFiles = directory.listFiles();

        List<Reward> rewards = new ArrayList<>();

        Gson gson = new Gson();

        for (File rewardFile : rewardFiles) {
            try (FileReader reader = new FileReader(rewardFile, Charset.forName("UTF8"))) {
                Reward reward = gson.fromJson(reader, Reward.class);
                reward.setId(UUID.fromString(rewardFile.getName()));
                rewards.add(reward);
            } catch (FileNotFoundException e) {
            	throw new RuntimeException(e);
			} catch (IOException e) {
            	throw new RuntimeException(e);
			}
        }

        return rewards;
    }
    
    public void saveReward(Reward reward) {
        UUID id = reward.getId();
        String filename = "rewards/" + id.toString();
        Gson gson = new Gson();
        String json = gson.toJson(reward);
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(json);
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
    }
	
    public void claim() {
		if (this.selectorReward.getObject() != null) {
			if (this.selectorReward.getObject().isClaimable(this.vocabularyTrainer.getCurrentScore())) {
				this.selectorReward.getObject().setLastClaimedAt(System.currentTimeMillis());
				saveReward(this.selectorReward.getObject());
				this.listeners.forEach(l -> l.onRewardClaimed(this.selectorReward.getObject()));
			}
		}
    }

	public void add(Listener e) {
		listeners.add(e);
		e.onRegisteredAsListener(new ArrayList<>(this.rewards));
	}

	public void change(Reward newT) {
		if (newT == null || this.rewards.contains(newT) ) {
			selectorReward.change(newT);
		}
	}

	public void addOnSelectionChangedObserver(OnSelectionChangedObserver<Reward> e) {
		selectorReward.addOnSelectionChangedObserver(e);
	}

	public void addOnDeselectObserver(OnDeselectObserver<Reward> e) {
		selectorReward.addOnDeselectObserver(e);
	}

	public void addOnSelectObserver(OnSelectObserver<Reward> e) {
		selectorReward.addOnSelectObserver(e);
	}

	public boolean isClaimable(Reward reward) {
		return hasEnoughPoints(reward) && isClaimTemporarilyAvailable(reward);
	}

	public boolean isClaimTemporarilyAvailable(Reward reward) {
		return reward.getClaimableAt() < System.currentTimeMillis();
	}

	public boolean hasEnoughPoints(Reward reward) {
		return this.vocabularyTrainer.getCurrentScore() > reward.getScore();
	}
	
	public void abortRewards() {
		listeners.forEach(l -> l.onAbortRewards());
	}
	
}
