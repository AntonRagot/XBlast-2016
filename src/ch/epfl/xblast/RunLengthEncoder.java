package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.List;

public final class RunLengthEncoder {

	/**
	 * Private constructor for the class RunLengthEncoder.
	 */
	private RunLengthEncoder() {
	}

	/**
	 * Given a list of Bytes, this method encodes it using the run-length
	 * encoding.
	 * 
	 * @param l
	 * @return encoded List<Byte>
	 * @throws IllegalArgumentException
	 */
	public static List<Byte> encode(List<Byte> l) throws IllegalArgumentException {
		List<Byte> encoding = new ArrayList<Byte>();
		// counter counts the number of consecutive bytes that are equal
		int counter = 1;
		int size = l.size();
		byte actual;
		for (int it = 0; it < size; it++) {
			actual = l.get(it);
			if (actual < 0) {
				throw new IllegalArgumentException();
			}
			while (it < size - 1 && actual == l.get(it + 1) && counter < 130) {
				counter++;
				it++;
			}
			switch (counter) {
			case 2:
				encoding.add(actual);
			case 1:
				encoding.add(actual);
				break;
			default:
				encoding.add((byte) (-(counter - 2)));
				encoding.add(actual);
			}
			counter = 1;
		}
		return encoding;
	}

	/**
	 * Given a list that has been encoded with the run-length encoding, this
	 * method returns the decoded version of that list.
	 * 
	 * @param l
	 * @return decoded List<Byte>
	 * @throws IllegalArgumentException
	 */
	public static List<Byte> decode(List<Byte> l) throws IllegalArgumentException {
		List<Byte> decoding = new ArrayList<Byte>();
		if (l.get(l.size() - 1) < 0) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < l.size(); i++) {
			byte eachByte = l.get(i);
			if (eachByte < 0) {
				int counter = Math.abs(eachByte) + 2;
				// We stop at 1 (instead of 0) because at the next iteration
				// on l we will add l.get(i + 1) one more time.
				do {
					decoding.add(l.get(i + 1));
					counter--;
				} while (counter > 1);
			} else {
				decoding.add(eachByte);
			}
		}
		return decoding;
	}
}