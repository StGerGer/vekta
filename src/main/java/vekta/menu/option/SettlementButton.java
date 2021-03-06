package vekta.menu.option;

import vekta.faction.Faction;
import vekta.menu.Menu;
import vekta.menu.handle.SecurityMenuHandle;
import vekta.menu.handle.SettlementMenuHandle;
import vekta.spawner.dialog.SecurityDialogSpawner;
import vekta.terrain.settlement.Settlement;

import static vekta.Vekta.setContext;

public class SettlementButton extends ButtonOption {
	private final Settlement settlement;

	public SettlementButton(Settlement settlement) {
		this.settlement = settlement;
	}

	@Override
	public String getName() {
		return "Visit " + settlement.getGenericName();
	}

	//	@Override
	//	public int getColor() {
	//		return settlement.getFaction().getColor();
	//	}

	public Settlement getSettlement() {
		return settlement;
	}

	@Override
	public void onSelect(Menu menu) {
		Menu sub = new Menu(menu, new SettlementMenuHandle(getSettlement()));
		getSettlement().setupMenu(sub);
		sub.addDefault();

		if(getSettlement().hasSecurity(menu.getPlayer())) {
			Faction faction = getSettlement().getFaction();

			ButtonOption confront = new DialogButton("Confront Security", SecurityDialogSpawner.randomSecurityDialog(faction, menu.getDefault()));

			Menu security = new Menu(menu.getPlayer(), confront, new SecurityMenuHandle(sub, faction));
			getSettlement().onSecurityMenu(security);
			security.add(new QuicktimeButton(5, "Walk Away", sub.getDefault()::onSelect));
			setContext(security);
		}
		else {
			setContext(sub);
		}
	}
}
