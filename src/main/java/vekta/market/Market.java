package vekta.market;

import vekta.item.Inventory;
import vekta.item.Item;
import vekta.menu.Menu;
import vekta.menu.option.MarketButton;
import vekta.sync.Syncable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static vekta.Vekta.getWorld;

public class Market extends Syncable<Market> {
	private final String name;
	private final Stock stock;
	private final Inventory inventory;

	private final Map<Item, Integer> merchantOffers = new HashMap<>();
	private final Map<Item, Integer> playerOffers = new HashMap<>();

	private float nextRestockTime;

	public Market(String name, Stock stock) {
		this(name, stock, new Inventory());
	}

	public Market(String name, Inventory inventory) {
		this(name, null, inventory);
	}

	public Market(String name, Stock stock, Inventory inventory) {
		this.name = name;
		this.stock = stock == null ? new DefaultStock() : stock;
		this.inventory = inventory;
	}

	public String getName() {
		return name;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public boolean isBuyable(Item item) {
		return stock.isBuyable(this, item);
	}

	public boolean isSellable(Item item) {
		return stock.isSellable(this, item);
	}

	public void restock() {
		if(!stock.shouldKeepOffersOnRestock()) {
			merchantOffers.clear();
			playerOffers.clear();
		}

		if(!stock.shouldKeepItemsOnRestock()) {
			inventory.clear();
		}

		nextRestockTime = getWorld().getTime() + stock.onRestock(this);
	}

	public void setupMenu(Menu menu, boolean buy, boolean sell) {
		if(getWorld().getTime() >= nextRestockTime) {
			restock();
		}

		Inventory playerInventory = menu.getPlayer().getInventory();

		computeOffers(playerInventory, playerOffers, merchantOffers, true);
		computeOffers(getInventory(), merchantOffers, playerOffers, false);

		if(buy) {
			menu.add(new MarketButton(this, true, playerInventory, merchantOffers));
		}
		if(sell) {
			menu.add(new MarketButton(this, false, playerInventory, playerOffers));
		}
	}

	private void computeOffers(Inventory inv, Map<Item, Integer> thisSide, Map<Item, Integer> otherSide, boolean buying) {
		// TODO: adjust based on economy
		for(Item item : inv) {
			if(otherSide.containsKey(item)) {
				float markup = item.getType().getMarkupFactor();
				if(buying) {
					markup = 1 / markup;
				}
				int price = (int)(otherSide.get(item) * markup);
				thisSide.put(item, price);
			}
			else if(!thisSide.containsKey(item)) {
				int price = item.randomPrice();
				thisSide.put(item, price);
			}
		}
	}

	public interface Stock extends Serializable {
		boolean isBuyable(Market market, Item item);

		default boolean isSellable(Market market, Item item) {
			return isBuyable(market, item);
		}

		float onRestock(Market market);

		default boolean shouldKeepOffersOnRestock() {
			return false;
		}

		default boolean shouldKeepItemsOnRestock() {
			return false;
		}
	}

	private static class DefaultStock implements Stock {
		@Override
		public boolean isBuyable(Market market, Item item) {
			return true;
		}

		@Override
		public float onRestock(Market market) {
			return Float.POSITIVE_INFINITY;
		}

		@Override
		public boolean shouldKeepItemsOnRestock() {
			return true;
		}
	}
}
