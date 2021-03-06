package vekta.faction;

import vekta.player.Player;

public class PlayerFaction extends Faction {
	private Player player;

	public PlayerFaction(String name, int color) {
		super(name, 1, .1F, color);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		if(this.player != null) {
			throw new RuntimeException("Player already assigned to faction");
		}
		if(player == null) {
			throw new RuntimeException("Player cannot be null");
		}
		this.player = player;
	}

	@Override
	public void updateEconomy() {
		// Override typical faction economy behavior
	}
}
