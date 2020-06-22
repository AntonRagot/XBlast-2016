package ch.epfl.xblast.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;

public class BombTest {
	/*
	 * Tests sur le constructeur principal
	 */
	@Test(expected = NullPointerException.class)
	public void constructorWithNullOwnerIdThrowsException() {
		new Bomb(null, new Cell(0, 0), Sq.constant(0), 0);
	}

	@Test(expected = NullPointerException.class)
	public void constructorWithNullPositionThrowsException() {
		new Bomb(PlayerID.PLAYER_1, null, Sq.constant(0), 0);
	}

  @Test(expected = NullPointerException.class)
  public void constructorWithNullFuseLengthsThrowsException() {
    new Bomb(PlayerID.PLAYER_1, new Cell(0, 0), null, 0);
  }

	@Test(expected = IllegalArgumentException.class)
	public void constructorWithEmptyFuseLengthsThrowsException() {
		new Bomb(PlayerID.PLAYER_1, new Cell(0, 0), Sq.empty(), 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorWithNegativeRangeThrowsException() {
		new Bomb(PlayerID.PLAYER_1, new Cell(0, 0), Sq.constant(0), -1);
	}

	/*
	 * Mêmes tests sur le second constructeur
	 */
	@Test(expected = NullPointerException.class)
	public void constructor2WithNullOwnerIdThrowsException() {
		new Bomb(null, new Cell(0, 0), 1, 0);
	}

	@Test(expected = NullPointerException.class)
	public void constructor2WithNullPositionThrowsException() {
		new Bomb(PlayerID.PLAYER_1, null, 1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor2WithNegativeRangeThrowsException() {
		new Bomb(PlayerID.PLAYER_1, new Cell(0, 0), 1, -1);
	}

	/*
	 * Tests sur les accesseurs
	 */
	private Bomb testBomb = new Bomb(PlayerID.PLAYER_1, new Cell(0, 0), Sq.constant(2), 2);

	@Test
	public void ownerIDGetterWorks() {
		assertEquals(PlayerID.PLAYER_1, testBomb.ownerId());
	}

	@Test
	public void positionGetterWorks() {
		assertEquals(new Cell(0, 0), testBomb.position());
	}

	@Test
	public void fuseLengthsGetterWorks() {
		Sq<Integer> fuseLengths = testBomb.fuseLengths();
		for (int i = 0; i < 50; ++i) {
			assertEquals(2, (int)fuseLengths.head());
			fuseLengths = fuseLengths.tail();
		}
	}
	
	@Test
	public void fuseLengthGetterWorks() {
		assertEquals(2, testBomb.fuseLength());
	}
	
	@Test
	public void rangeGetterWorks() {
		assertEquals(2, testBomb.range());
	}
	
	/*
	 * Tests sur les valeurs d'une bombe crée avec le second constructeur
	 */
	private Bomb testBomb2 = new Bomb(PlayerID.PLAYER_1, new Cell(0, 0), 3, 6);
	
	@Test
	public void constructor2SetsOwnerId() {
		assertEquals(PlayerID.PLAYER_1, testBomb2.ownerId());
	}

	@Test
	public void constructor2SetsPosition() {
		assertEquals(new Cell(0, 0), testBomb2.position());
	}

	@Test
	public void constructor2SetsFuseLengths() {
		// 3, 2, 1
		Sq<Integer> fuseLengthsModel = Sq.constant(3).limit(1).concat(Sq.constant(2).limit(1)).concat(Sq.constant(1).limit(1));
		Sq<Integer> fuseLengthsBomb = testBomb2.fuseLengths();
		for (int i = 0; i < 3; ++i) {
			assertEquals(fuseLengthsModel.head(), fuseLengthsBomb.head());
			fuseLengthsModel = fuseLengthsModel.tail();
			fuseLengthsBomb = fuseLengthsBomb.tail();
		}
		assertTrue(fuseLengthsBomb.isEmpty());
	}
	
	@Test
	public void constructor2SetsRange() {
		assertEquals(6, testBomb2.range());
	}
	
	/*
	 * Tests sur explosion()
	 */
	private Direction armDirection(Sq<Sq<Cell>> arm){
		for(Direction dir : Direction.values()){
			Sq<Cell> particle = arm.head();
			if(particle.head().neighbor(dir).equals(particle.tail().head())){
				return dir;
			}
		}
		return null;
	}
	
	@Test
	public void explosionContainsAllDirections() {
		Bomb bomb = new Bomb(PlayerID.PLAYER_1, new Cell(0, 0), 1, 3);
		List<Sq<Sq<Cell>>> explosion = bomb.explosion();
		
		assertEquals(4, explosion.size()); // 4 bras
		
		Set<Direction> presentDirections = new HashSet<>();
		for(int i = 0; i < 4; ++i)
			presentDirections.add(armDirection(explosion.get(i)));
		
		assertEquals(4, presentDirections.size());
	}
	
	@Test
	public void explosionArmsContainRightNumberOfParticles() {
		Bomb bomb = new Bomb(PlayerID.PLAYER_1, new Cell(0, 0), 1, 3);
		List<Sq<Sq<Cell>>> explosion = bomb.explosion();
		
		for(int i = 0; i < explosion.size(); ++i){
			Sq<Sq<Cell>> arm = explosion.get(i);
			for(int j = 0; j < Ticks.EXPLOSION_TICKS; ++j)
				arm = arm.tail();
			
			assertTrue(arm.isEmpty());
		}
	}

	@Test
  public void explosionArmsHaveCorrectDurationAndRange() {
    int range = 2;
    Bomb bomb = new Bomb(PlayerID.PLAYER_1, new Cell(1, 1), 1, range);
		List<Sq<Sq<Cell>>> explosion = bomb.explosion();
		
		for(int i = 0; i < explosion.size(); ++i){
			Sq<Sq<Cell>> arm = explosion.get(i);
			Direction dir = armDirection(arm);
			for(int j = 0; j < Ticks.EXPLOSION_TICKS; ++j){
				Sq<Cell> particle = arm.head();
        for(int k = 0; k < range; ++k){
					if(k == 0)
						assertEquals(bomb.position(), particle.head());
					else
						assertEquals(bomb.position().neighbor(dir), particle.head());
					
					particle = particle.tail();
				}
				assertTrue(particle.isEmpty());
				
				arm = arm.tail();
			}
		}
	}
}
