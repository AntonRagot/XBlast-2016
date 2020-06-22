package ch.epfl.xblast.server;

//Enumeration containing all possible bonuses and their respective method to
//apply itself to the player.
public enum Bonus {
    INC_BOMB {
        private final int MAX_BOMB = 9;
        
        @Override
        public Player applyTo(Player player) {
            return player.withMaxBombs(Math.min(player.maxBombs() + 1, MAX_BOMB));
        }
    },
    INC_RANGE {
        private final int MAX_RANGE = 9;
        
        @Override
        public Player applyTo(Player player) {
            return player.withBombRange(Math.min(player.bombRange() + 1, MAX_RANGE));
        }
    };
    abstract public Player applyTo(Player player);
}