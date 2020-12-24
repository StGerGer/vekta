package vekta.player;

import vekta.KeyBinding;
import vekta.item.Item;
import vekta.menu.Menu;
import vekta.mission.Mission;
import vekta.module.Module;
import vekta.object.SpaceObject;
import vekta.object.ship.ModularShip;
import vekta.overlay.singleplayer.Notification;
import vekta.terrain.Terrain;
import vekta.terrain.location.Location;

import java.io.Serializable;

/**
 * Player-related event listener
 */
public interface PlayerListener extends Serializable {
	default void onKeyPress(KeyBinding key) {
	}

	default void onKeyRelease(KeyBinding key) {
	}
	
	default void onControllerPress(KeyBinding key) {
	}
	
	default void onControllerRelease(KeyBinding key) {
	}

	default void onMenu(Menu menu) {
	}

	default void onEvent(PlayerEvent event) {
	}

	default void onChangeShip(ModularShip ship) {
	}

	default void onDamageShip(ModularShip.DamageAttempt attempt) {
	}

	default void onNotification(Notification notification) {
	}

	default void onVisit(Location location) {
	}

	default void onDock(SpaceObject object) {
	}

	default void onMissionStatus(Mission mission) {
	}

	default void onGameOver(ModularShip ship) {
	}

	default void onInstallModule(Module module) {
	}

	default void onUninstallModule(Module module) {
	}

	default void onAddItem(Item item) {
	}

	default void onRemoveItem(Item item) {
	}

	default void onDestroyObject(SpaceObject object) {
	}
}
