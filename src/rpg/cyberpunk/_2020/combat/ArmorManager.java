package rpg.cyberpunk._2020.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rpg.Player;
import rpg.cyberpunk._2020.combat.CyberpunkArmor.ArmorType;
import rpg.general.combat.BodyLocation;
import rpg.general.commerce.QuantifiableProduct;

public class ArmorManager {
	public static final int MAX_HARD_ARMOR_LAYERS = 1;
	public static final int MAX_TOTAL_ARMOR_LAYERS = 3;

	public static final int NO_STOPPING_POWER_MODIFIER = 0;
	public static final int VERY_LOW_STOPPING_POWER_MODIFIER = 1;
	public static final int LOW_STOPPING_POWER_MODIFIER = 2;
	public static final int AVERAGE_STOPPING_POWER_MODIFIER = 3;
	public static final int HIGH_STOPPING_POWER_MODIFIER = 4;
	public static final int VERY_HIGH_STOPPING_POWER_MODIFIER = 5;

	private Player player;
	private int totalEncumbranceValue;
	private List<CyberpunkArmor> armors;
	private Map<BodyLocation, Integer> layerTracker;
	private Map<BodyLocation, Integer> localizedDurabilities;

	public ArmorManager(Player player) {
		this.player = player;
		totalEncumbranceValue = 0;
		armors = new ArrayList<CyberpunkArmor>();
		initializeLayerTracker();
		initializeLocalizedDurabilities();
	}

	private void initializeLayerTracker() {
		layerTracker = new HashMap<BodyLocation, Integer>();
		Iterator<BodyLocation> setIterator = BodyLocation.createIterator();
		while (setIterator.hasNext()) {
			layerTracker.put(setIterator.next(), 0);
		}
	}

	private void initializeLocalizedDurabilities() {
		localizedDurabilities = new HashMap<BodyLocation, Integer>();
		Iterator<BodyLocation> setIterator = BodyLocation.createIterator();
		while (setIterator.hasNext()) {
			localizedDurabilities.put(setIterator.next(), CyberpunkArmor.DEFAULT_STOPPING_POWER);
		}
	}

	public boolean add(CyberpunkArmor armor) {
		boolean hasAdded;
		if (hasAdded = isAddable(armor)) {
			player.removeFromInventory(armor, 1);
			updateLocalizedDurabilities(armor);
			incrementLayers(armor);
			armors.add(armor);
			calculateEncumbranceValue();
		}
		return hasAdded;
	}

	private boolean isAddable(CyberpunkArmor armor) {
		return !hasHardArmor(armor) && !hasMaxLayers(armor);

	}

	private boolean hasHardArmor(CyberpunkArmor armor) {
		if (armor.getArmorType() == ArmorType.HARD) {
			Iterator<CyberpunkArmor> iterator = armors.iterator();
			while (iterator.hasNext()) {
				CyberpunkArmor tempArmor = iterator.next();
				if (tempArmor.getArmorType() == ArmorType.HARD) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasMaxLayers(CyberpunkArmor armor) {
		Iterator<BodyLocation> iterator = BodyLocation.createIterator();
		while (iterator.hasNext()) {
			BodyLocation location = iterator.next();
			if (armor.isCovering(location) && layerTracker.get(location) >= MAX_TOTAL_ARMOR_LAYERS) {
				return true;
			}
		}
		return false;
	}

	private void updateLocalizedDurabilities(CyberpunkArmor armor) {
		Iterator<BodyLocation> iterator = BodyLocation.createIterator();
		while (iterator.hasNext()) {
			BodyLocation location = iterator.next();
			if (armor.isCovering(location)) {
				localizedDurabilities.replace(location, calculateStoppingPower(armor, location));
			}
		}
	}

	private int calculateStoppingPower(CyberpunkArmor armor, BodyLocation location) {
		int newArmorStoppingPower = armor.getDurability(location);
		if (armors.size() < 1) {
			return newArmorStoppingPower;
		} else {
			Iterator<CyberpunkArmor> iterator = armors.iterator();
			int stoppingPower1 = iterator.next().getDurability(location);
			while (iterator.hasNext()) {
				int stoppingPower2 = iterator.next().getDurability(location);
				stoppingPower1 = increaseStoppingPower(stoppingPower1, stoppingPower2);
			}
			return increaseStoppingPower(stoppingPower1, newArmorStoppingPower);
		}
	}

	private int increaseStoppingPower(int stoppingPower1, int stoppingPower2) {
		if (stoppingPower1 > stoppingPower2) {
			return stoppingPower1 + getArmorModifier(stoppingPower1, stoppingPower2);
		} else {
			return stoppingPower2 + getArmorModifier(stoppingPower1, stoppingPower2);
		}
	}

	private int getArmorModifier(int stoppingPower1, int stoppingPower2) {
		int result = Math.abs(stoppingPower1 - stoppingPower2);
		if (result <= 4) {
			return VERY_HIGH_STOPPING_POWER_MODIFIER;
		} else if (5 <= result && result <= 8) {
			return HIGH_STOPPING_POWER_MODIFIER;
		} else if (9 <= result && result <= 14) {
			return AVERAGE_STOPPING_POWER_MODIFIER;
		} else if (15 <= result && result <= 20) {
			return LOW_STOPPING_POWER_MODIFIER;
		} else if (21 <= result && result <= 26) {
			return VERY_LOW_STOPPING_POWER_MODIFIER;
		} else {
			return NO_STOPPING_POWER_MODIFIER;
		}
	}

	private void incrementLayers(CyberpunkArmor armor) {
		Iterator<BodyLocation> iterator = BodyLocation.createIterator();
		while (iterator.hasNext()) {
			BodyLocation location = iterator.next();
			if (armor.isCovering(location)) {
				int layers = layerTracker.get(location) + 1;
				layerTracker.replace(location, layers);
			}
		}
	}

	public boolean remove(CyberpunkArmor armor) {
		boolean hasRemoved = false;
		if (armors.contains(armor)) {
			player.addToInventory(new QuantifiableProduct(armor, 1));
			armors.remove(armor);
			hasRemoved = true;
		}
		return hasRemoved;
	}

	public int getLocationDurability(BodyLocation location) {
		return localizedDurabilities.get(location);
	}

	public void calculateEncumbranceValue() {
		totalEncumbranceValue = totalEncumbranceValue + getEncumbranceBonus(layerTracker.get(BodyLocation.HEAD))
				+ getEncumbranceBonus(layerTracker.get(BodyLocation.TORSO))
				+ (getEncumbranceBonus(layerTracker.get(BodyLocation.RIGHT_ARM))
						+ getEncumbranceBonus(layerTracker.get(BodyLocation.LEFT_ARM))) / 2
				+ (getEncumbranceBonus(layerTracker.get(BodyLocation.RIGHT_LEG))
						+ getEncumbranceBonus(layerTracker.get(BodyLocation.LEFT_LEG))) / 2;

		Iterator<CyberpunkArmor> iterator = armors.iterator();
		while (iterator.hasNext()) {
			CyberpunkArmor armor = iterator.next();
			totalEncumbranceValue += armor.getEncumbranceValue();
		}
	}

	private int getEncumbranceBonus(int layers) {
		if (layers <= 1) {
			return 0;
		} else if (2 == layers) {
			return 1;
		} else if (3 == layers) {
			return 3;
		} else {
			return -1;
		}
	}

	public int getEncumbranceValue() {
		return totalEncumbranceValue;
	}
}
