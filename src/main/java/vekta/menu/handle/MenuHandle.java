package vekta.menu.handle;

import processing.event.KeyEvent;
import vekta.KeyBinding;
import vekta.Resources;
import vekta.Settings;
import vekta.menu.Menu;
import vekta.menu.option.MenuOption;

import java.io.Serializable;

import static processing.core.PConstants.CENTER;
import static vekta.Vekta.BODY_FONT;
import static vekta.Vekta.v;

/**
 * Default menu renderer implementation; draws buttons and selection text
 */
public class MenuHandle implements Serializable {
	private static final int ITEMS_BEFORE_SCROLL = 5; // Number of items before menu starts scrolling
	private static final float SCROLL_RATE = 10; // Smooth scroll rate

	private Menu menu;

	// Smooth scroll offset
	private float scrollOffset;

	public MenuHandle() {
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		if(menu == null) {
			throw new RuntimeException(getClass().getSimpleName() + " menu cannot be null");
		}
		if(menu == getMenu()) {
			throw new RuntimeException(getClass().getSimpleName() + " was already initialized");
		}
		this.menu = menu;
	}

	public int getItemWidth() {
		return 200;
	}

	public int getItemHeight() {
		return 50;
	}

	public int getSpacing() {
		return getItemHeight() + 50;
	}

	public int getItemX() {
		return v.width / 2;
	}

	public int getItemY(int i) {
		// Scroll items
		int y = v.height / 2 - 64 + i * getSpacing();
		y -= scrollOffset;
		return y;
	}

	public KeyBinding getShortcutKey() {
		return null;
	}

	public void init() {
	}

	public void focus() {
	}

	public void unfocus() {
	}

	public void beforeDraw() {
		v.clear();
	}

	public void render() {
		Menu menu = getMenu();
		int extraOptions = menu.size() - ITEMS_BEFORE_SCROLL;
		float targetOffset = extraOptions > 0 ? menu.getIndex() * getSpacing() * extraOptions / (float)menu.size() : 0;
		scrollOffset += (targetOffset - scrollOffset) * SCROLL_RATE / 60;

		beforeDraw();

		v.noStroke();
		v.fill(255);
		v.textFont(BODY_FONT);
		v.textSize(24);
		v.textAlign(CENTER, CENTER);
		v.rectMode(CENTER);
		for(int i = 0; i < menu.size(); i++) {
			MenuOption opt = menu.get(i);
			opt.onUpdate(menu);
			drawButton(opt, i);
		}

		v.noStroke();
		v.fill(255);
		v.textAlign(CENTER);

		String selectVerb = menu.getCursor().getSelectVerb();
		if(menu.size() > 0 && menu.getCursor().isEnabled() && selectVerb != null) {
			if(menu.getCursor().isEnabled() && selectVerb != null) {
				v.text(Settings.getKeyText(KeyBinding.MENU_SELECT) + " to " + selectVerb, getItemX(), getItemY(menu.size()) + 100);
			}
		}
	}

	protected void drawButton(MenuOption opt, int index) {
		opt.render(getMenu(), index);
	}

	public void keyPressed(KeyEvent event) {
		Menu menu = getMenu();
		if(menu.size() > 0) {
			menu.getCursor().keyPressed(menu, event);
		}
		menu.getCursor().keyPressed(menu, event);
	}

	public void keyPressed(KeyBinding key) {
		Menu menu = getMenu();
		if(menu.size() > 0) {
			if(menu.getCursor().interceptKeyPressed(menu, key)) {
				return;
			}
			else if(key == KeyBinding.MENU_CLOSE || key == getShortcutKey()) {
				menu.close();
				return;
			}

			if(key == KeyBinding.MENU_UP) {
				menu.scroll(-1);
				Resources.playSound("change");
			}
			else if(key == KeyBinding.MENU_DOWN) {
				menu.scroll(1);
				Resources.playSound("change");
			}
			else if(key == KeyBinding.MENU_SELECT) {
				if(menu.getCursor().isEnabled()) {
					menu.selectCursor();
					Resources.playSound("select");
				}
			}
		}
	}

	public void onChange() {
	}
}
