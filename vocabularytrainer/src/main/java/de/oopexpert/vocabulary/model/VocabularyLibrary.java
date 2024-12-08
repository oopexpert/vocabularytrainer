package de.oopexpert.vocabulary.model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.oopexpert.oopdi.Scope;
import de.oopexpert.oopdi.annotation.Injectable;
import de.oopexpert.oopdi.annotation.PostConstruct;

@Injectable(scope=Scope.GLOBAL)
public class VocabularyLibrary {

	public interface Listener {
		void onAddedAsListener(List<VocabularySet> sets);
		void vocabularySetAdded(VocabularySet set);
		void vocabularySetRemoved(VocabularySet set);
	}

	private final Set<Listener> listeners = new HashSet<>();
	
	private final List<VocabularySet> vocabularySets = new ArrayList<>();

	
	public void addListener(Listener listener) {
		this.listeners.add(listener);
		listener.onAddedAsListener(vocabularySets);
	}
	
	public void add(VocabularySet vs) {
		vocabularySets.add(vs);
		listeners.forEach(o -> o.vocabularySetAdded(vs));
	}

	public void remove(VocabularySet vs) {
		vocabularySets.remove(vs);
		listeners.forEach(o -> o.vocabularySetRemoved(vs));
	}

	public boolean contains(VocabularySet vs) {
		return vocabularySets.contains(vs);
	}
	
	@PostConstruct
	private void init() throws IOException {
		vocabularySets.addAll(loadCSVFiles());
		vocabularySets.sort(new Comparator<>() {

			@Override
			public int compare(VocabularySet o1, VocabularySet o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
	}
	
	private static final String WORD1_COLUMN = "word1";
    private static final String DESCRIPTION1_COLUMN = "description1";
    private static final String WORD2_COLUMN = "word2";
    private static final String DESCRIPTION2_COLUMN = "description2";
    public static final String[] header = {WORD1_COLUMN, DESCRIPTION1_COLUMN, WORD2_COLUMN, DESCRIPTION2_COLUMN};
	
    private static VocabularySet loadFile(File filePath) throws IOException {
        VocabularySet vocabSet = new VocabularySet();
        try (CSVParser csvParser = new CSVParser(new FileReader(filePath, Charset.forName("UTF8")), CSVFormat.DEFAULT.builder().setHeader(header).setDelimiter(',').setCommentMarker('#').build())) {
             if (csvParser.iterator().hasNext()) {
                 CSVRecord firstRecord = csvParser.iterator().next();
                 if (firstRecord.getComment() != null) {
                	 String[] metaData = firstRecord.getComment().split(";");
                	 vocabSet.setName(metaData[0]);
                	 vocabSet.setMaxScore1(Integer.parseInt(metaData[1]));
                	 vocabSet.setMaxScore2(Integer.parseInt(metaData[2]));
                	 vocabSet.setDecayTime(Integer.parseInt(metaData[3]));
                 }
             }        	;
        	vocabSet.setSource(filePath.getName());
        	int index = 0;
            for (CSVRecord record : csvParser) {
            	try {
            		Word word = new Word(record.get(WORD1_COLUMN), record.get(DESCRIPTION1_COLUMN), record.get(WORD2_COLUMN), record.get(DESCRIPTION2_COLUMN));
                    vocabSet.add(word);
                	index++;
            	} catch (Exception e) {
            		throw new RuntimeException("Cannot extract word with index " + index + " from vocabulary set '" + vocabSet.getId() + "'", e);
            	}
            }
        }
        return vocabSet;
    }
    
    private static Set<VocabularySet> loadCSVFiles() throws IOException {
        Set<VocabularySet> vocabularySets = new HashSet<>();

        File directory = new File(new File("vocabularysets").getAbsolutePath());
        File[] files = directory.listFiles();

        for (File file : files) {
            VocabularySet vocabularySet = loadFile(file);
            saveVocabularySetToJson(vocabularySet, file);
            vocabularySets.add(vocabularySet);
        }

        return vocabularySets;
    }

    public static void saveVocabularySetToJson(VocabularySet vocabularySet, File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(file)) {
            // Write the VocabularySet object to a JSON file
            gson.toJson(vocabularySet, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	private List<VocabularySet> getVocabularySets() {
		return vocabularySets;
	}

	private List<VocabularySet> getSuggestionRanking() {

		List<VocabularySet> vocabularySets = new ArrayList<>(getVocabularySets());

		Collections.sort(vocabularySets, new Comparator<VocabularySet>() {

			@Override
			public int compare(VocabularySet o1, VocabularySet o2) {

				int scoreToIncrease1 = o1.getMaxScore1() + o1.getMaxScore2() - o1.getCurrentScore1()
						- o1.getCurrentScore2();
				int scoreToIncrease2 = o2.getMaxScore1() + o2.getMaxScore2() - o2.getCurrentScore1()
						- o2.getCurrentScore2();

				return scoreToIncrease1 - scoreToIncrease2;
			}

		});

		return vocabularySets;
	}
	
	public int getCurrentScore() {
		
		int score = 0;
		
		List<VocabularySet> vocabularySets = this.getVocabularySets();
		
		for (VocabularySet vocabularySet : vocabularySets) {
			
			score = score + vocabularySet.getCurrentScore1();
			score = score + vocabularySet.getCurrentScore2();
			
		}
		
		return score;
	}

	
}
