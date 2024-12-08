package de.oopexpert.vocabulary.model;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import de.oopexpert.oopdi.Scope;
import de.oopexpert.oopdi.annotation.Injectable;
import de.oopexpert.oopdi.annotation.PostConstruct;

@Injectable(scope=Scope.GLOBAL)
public class VocabularyEditor {
	
	private Selector<VocabularySet> vocabularySetSelector;
	
	private Selector<Word> wordSelector;
	
	private String language1;
	private String description1;
	private String language2;
	private String description2;
	
	private int index;
	

	public VocabularyEditor() {
		this.vocabularySetSelector = new Selector<>();
		clean();
	}
	
	public void setVocabularySet(VocabularySet vocabularyset) {
		this.vocabularySetSelector.change(vocabularyset);
	}
	
	@PostConstruct
	private void postConstruct() {
		this.vocabularySetSelector.addOnDeselectObserver(this::deselect);
		this.vocabularySetSelector.addOnSelectObserver(this::select);
		this.vocabularySetSelector.addOnSelectionChangedObserver(this::changedSelection);
	}
	
	private void deselect(VocabularySet previous) {
		clean();
	}

	private void clean() {
		this.index = -1;
		this.language1 = "";
		this.description1 = "";
		this.language2 = "";
		this.description2 = "";
	}

	private void select(VocabularySet current) {
		init(current);
	}

	private void changedSelection(VocabularySet previous, VocabularySet current) {
		init(current);
	}

	private void init(VocabularySet current) {
		if (current.count() == 0) {
			clean();
		} else {
			this.index = 0;
			Word word = current.get(this.index);
//			this.language1 = word.word1;
//			this.description1 = word.description1;
//			this.language2 = word.word2;
//			this.description2 = word.description2;
		}
	}
	
    public static void saveCSV(VocabularySet vocabularySet) throws IOException {
        try (FileWriter fileWriter = new FileWriter(vocabularySet.getSource());
             CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.builder().setHeader(VocabularyLibrary.header).build())) {

            fileWriter.write("# " + vocabularySet.getName() + "\n"); // Add name as a comment

            for (Word word : vocabularySet.getWords()) {
//                csvPrinter.printRecord(word.word1, word.description1, word.word2, word.description2);
            }

        }
    }


}
