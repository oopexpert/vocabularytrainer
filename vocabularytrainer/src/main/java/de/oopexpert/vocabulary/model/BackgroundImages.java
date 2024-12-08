package de.oopexpert.vocabulary.model;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import de.oopexpert.oopdi.Scope;
import de.oopexpert.oopdi.annotation.Injectable;
import de.oopexpert.oopdi.annotation.PostConstruct;

@Injectable(scope = Scope.GLOBAL)
public class BackgroundImages {

	private List<Image> images;
	
	public BackgroundImages() {
		this.images = new ArrayList<Image>();
	}
	
	@PostConstruct
	private void init() {
		this.images.addAll(loadImages("backgrounds"));
	}
	
	private static List<Image> loadImages(String directoryPath) {
		
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        File[] files = directory.listFiles();

        List<Image> imageList = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try {
						imageList.add(ImageIO.read(file));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
        }

        return imageList;
    }
		
	public int count() {
		return this.images.size();
	}

	public Image get(int indx) {
		return this.images.get(indx);
	}

	public boolean isEmpty() {
		return this.images.isEmpty();
	}

}
