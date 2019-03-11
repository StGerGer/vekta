package vekta.menu.option;

import vekta.Player;
import vekta.menu.Menu;
import vekta.terrain.building.upgrade.SettlementUpgrade;
import vekta.terrain.settlement.Settlement;

import static vekta.Vekta.moneyString;

public class UpgradeOption implements MenuOption {
	private final Player player;
	private final Settlement settlement;
	private final SettlementUpgrade upgrade;

	public UpgradeOption(Player player, Settlement settlement, SettlementUpgrade upgrade) {
		this.player = player;
		this.settlement = settlement;
		this.upgrade = upgrade;
	}

	@Override
	public String getName() {
		return moneyString(upgrade.getName(), upgrade.getCost(player, settlement));
	}

	@Override
	public boolean isEnabled() {
		return player.getInventory().has(upgrade.getCost(player, settlement));
	}

	@Override
	public void select(Menu menu) {
		upgrade.upgrade(player, settlement);
		menu.close();
	}
}