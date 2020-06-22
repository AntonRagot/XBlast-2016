package ch.epfl.xblast.client;

import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.imageio.ImageIO;

public final class ImageCollection {
	private final Map<Integer, Image> imageInFolder = new HashMap<>();

	/**
	 * Constructor for the class ImageCollection.
	 * 
	 * @param s
	 */
	public ImageCollection(String s) {
		try {
			File files = new File(
					ImageCollection.class.getClassLoader().getResource(Objects.requireNonNull(s)).toURI());
			for (File i : files.listFiles()) {
				imageInFolder.put(Integer.parseInt(i.getName().substring(0, 3)), ImageIO.read(i));
			}
		} catch (Exception e) {
			// ignore this file
		}
	}

	/**
	 * Given an int, this method returns the associated Image in the folder of
	 * the ImageCollection.
	 * 
	 * @param number
	 * @return Image
	 * @throws NoSuchElementException
	 */
	public Image image(int number) throws NoSuchElementException {
		if (imageInFolder.containsKey(number)) {
			return imageInFolder.get(number);
		} else {
			throw new NoSuchElementException();
		}
	}

	/**
	 * Returns the Image associated to given int in the ImageCollection folder
	 * or returns null if no image associated to the number exists.
	 * 
	 * @param number
	 * @return Image
	 */
	public Image imageOrNull(int number) {
		return imageInFolder.get(number);
	}
}