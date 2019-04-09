package rpg.cyberpunk._2020.commerce;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;

import rpg.general.combat.Ammunition;

/**
 * An instance of <code>Box</code> that allows the storage of only one type of
 * ammunition. The type is maintained by calling cloning a given item.
 * 
 * @return
 */
public class AmmunitionBox implements Box<Ammunition> {
	public static final double WEIGHT = 0.5;

	private Ammunition ammunition;
	private List<Ammunition> list;

	/**
	 * Constructs an AmmunitionBox that takes ammunition and clones it quantity
	 * times.
	 * 
	 * @param ammunition the item to store
	 * @param quantity   the amount of an item stored
	 */
	public AmmunitionBox(Ammunition ammunition, int quantity) {
		this.ammunition = ammunition;
		list = new ArrayList<Ammunition>();

		for (int i = 0; i < quantity; i++) {

			list.add(SerializationUtils.clone(ammunition));
		}
	}

	@Override
	public String getName() {
		return ammunition.getName();
	}

	@Override
	public String getDescription() {
		return ammunition.getDescription();
	}

	@Override
	public double getWeight() {
		return ammunition.getWeight() * getQuantity();
	}

	@Override
	public double getCost() {
		return ammunition.getCost() * getQuantity();
	}

	@Override
	public int getQuantity() {
		return list.size();
	}

	@Override
	public List<Ammunition> getItems() {
		return list;
	}

}
