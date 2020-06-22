package ch.epfl.xblast.server;

import java.util.NoSuchElementException;

import ch.epfl.xblast.server.Player.LifeState.State;

public final class PlayerPainter {
	private static final int NUMER_OF_IMAGES_FOR_STEP = 3;
	private static final int INDEX_OF_THIRD_IMAGE = 2;
	private static final int NUMBER_OF_DIRECTIONS = 4;
	private static final int WHITE_IMAGES_ORDINAL_START = 80;
	private static final int NUMBER_IMAGE_PER_MOUVEMENT = 3;
	private static final int DYING_PLAYER_IMAGE_ORDINAL = 13;
	private static final int LOSING_LIFE_IMAGE_ORDINAL = 12;
	private static final int DEAD_PLAYER_IMAGE_ORDINAL = 14;
	private static final int NUMBER_OF_IMAGE_FOR_PLAYER = 20;

	/**
	 * Private constructor for the class PlayerPainter.
	 */
	private PlayerPainter() {
	}

	/**
	 * Returns the byte of the image given a tick and Player, depending on the
	 * State of the Player and on the Direction they are facing.
	 * 
	 * @param tick
	 * @param player
	 * @return byte for the Player image
	 * @throws NoSuchElementException
	 */
	public static byte byteForPlayer(int tick, Player player) throws NoSuchElementException {
		int directionOrdinal = player.direction().ordinal();
		int idOrdinal = player.id().ordinal();
		if (!player.isAlive()) {
			return (byte) (idOrdinal * NUMBER_OF_IMAGE_FOR_PLAYER + DEAD_PLAYER_IMAGE_ORDINAL);
		} else if (player.lifeState().state() == State.DYING) {
			return (player.lives() > 1) ? (byte) (idOrdinal * NUMBER_OF_IMAGE_FOR_PLAYER + LOSING_LIFE_IMAGE_ORDINAL)
					: (byte) (idOrdinal * NUMBER_OF_IMAGE_FOR_PLAYER + DYING_PLAYER_IMAGE_ORDINAL);
		} else {
			return (player.direction().isHorizontal())
					? (byte) (directionOrdinal * NUMBER_IMAGE_PER_MOUVEMENT
							+ movementImage(player, player.position().x(), tick))
					: (byte) (directionOrdinal * NUMBER_IMAGE_PER_MOUVEMENT
							+ movementImage(player, player.position().y(), tick));
		}
	}

	/**
	 * Returns the byte of the image given the Player, their coordinate and the
	 * current tick of the game.
	 * 
	 * @param player
	 * @param coordinate
	 * @param tick
	 * @return int
	 */
	private static int movementImage(Player player, int coordinate, int tick) {
		int inter = (coordinate % NUMBER_OF_DIRECTIONS == NUMER_OF_IMAGES_FOR_STEP) ? INDEX_OF_THIRD_IMAGE
				: coordinate % 2;
		if (tick % 2 != 0 && player.lifeState().state().equals(State.INVULNERABLE)) {
			return WHITE_IMAGES_ORDINAL_START + inter;
		}
		return player.id().ordinal() * NUMBER_OF_IMAGE_FOR_PLAYER + inter;
	}
}