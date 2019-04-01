package rpg.cyberpunk._2020.gui;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import rpg.Player;
import rpg.cyberpunk._2020.combat.CyberpunkWeapon;
import rpg.cyberpunk._2020.combat.CyberpunkWeapon.Availability;
import rpg.cyberpunk._2020.combat.CyberpunkWeapon.Concealability;
import rpg.cyberpunk._2020.combat.CyberpunkWeapon.Reliability;
import rpg.util.Probability;

/**
 * A table that displays a set of weapons that is owned by the given player.
 * 
 * @author Coul Greer
 *
 */
public class InventoryWeaponCategoryTable extends JTable implements PropertyChangeListener {
	private Player player;

	/**
	 * Constructs a table used to display a player's collection of weapons held in
	 * their inventory.
	 * 
	 * @param player the owner of the displayed inventory
	 */
	public InventoryWeaponCategoryTable(Player player) {
		super(new InventoryWeaponTableModel(player.getCarriedWeapons()));

		setupRenderers();

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(getModel());
		setRowSorter(sorter);
		sorter.setComparator(InventoryWeaponTableModel.TYPE_COLUMN_INDEX, new WeaponTypeComparator());

		setRowHeight(WeaponTypeRenderer.ICON_HEIGHT);
		getColumnModel().removeColumn(getColumnModel().getColumn(InventoryWeaponTableModel.OBJECT_INDEX));
		getColumnModel().getColumn(InventoryWeaponTableModel.TYPE_COLUMN_INDEX)
				.setPreferredWidth(WeaponTypeRenderer.ICON_HEIGHT);

		this.player = player;
		player.addPropertyChangeListener(Player.PROPERTY_NAME_INVENTORY_WEAPON_ADDED, this);
	}

	private void setupRenderers() {
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(InventoryWeaponTableModel.TYPE_COLUMN_INDEX).setCellRenderer(new WeaponTypeRenderer());
		columnModel.getColumn(InventoryWeaponTableModel.WEIGHT_COLUMN_INDEX).setCellRenderer(new WeightRenderer());
		columnModel.getColumn(InventoryWeaponTableModel.RANGE_COLUMN_INDEX).setCellRenderer(new DistanceRenderer());
		columnModel.getColumn(InventoryWeaponTableModel.COST_COLUMN_INDEX).setCellRenderer(new CurrencyRenderer());
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component component = super.prepareRenderer(renderer, row, column);

		packColumnWidth(component, column);
		alternateRowColors(component, row);

		return component;
	}

	private void packColumnWidth(Component component, int column) {
		int rendererWidth = component.getPreferredSize().width;
		TableColumn tableColumn = getColumnModel().getColumn(column);
		tableColumn.setPreferredWidth(
				Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
	}

	private void alternateRowColors(Component component, int row) {
		Color primeColor = Color.WHITE;
		Color secondaryColor = new Color(220, 220, 220); // gainsboro

		if (!component.getBackground().equals(getSelectionBackground())) {
			Color bg = (row % 2 == 0 ? secondaryColor : primeColor);
			component.setBackground(bg);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();
		String propertyName = evt.getPropertyName();
		if (source == player) {
			if (propertyName.equals(Player.PROPERTY_NAME_INVENTORY_WEAPON_ADDED)) {
				DefaultTableModel tableModel = (DefaultTableModel) getModel();
				CyberpunkWeapon weapon = (CyberpunkWeapon) evt.getNewValue();
				tableModel.addRow(createRow(weapon));
			}
		}
	}

	private Object[] createRow(CyberpunkWeapon weapon) {
		Object[] row = new Object[InventoryWeaponTableModel.COLUMN_NAMES.length];

		row[InventoryWeaponTableModel.NAME_COLUMN_INDEX] = weapon.getName();
		row[InventoryWeaponTableModel.TYPE_COLUMN_INDEX] = weapon.getWeaponType();
		row[InventoryWeaponTableModel.WEAPON_ACCURACY_COLUMN_INDEX] = weapon.getHitModifier();
		row[InventoryWeaponTableModel.CONCEALABILITY_COLUMN_INDEX] = weapon.getConcealability();
		row[InventoryWeaponTableModel.AVAILABILITY_COLUMN_INDEX] = weapon.getAvailability();
		row[InventoryWeaponTableModel.DAMAGE_COLUMN_INDEX] = weapon.getHitDice() + "+" + weapon.getDamageScore();
		row[InventoryWeaponTableModel.AMMO_COLUMN_INDEX] = weapon.getAmmunitionType();
		row[InventoryWeaponTableModel.CURRENT_SHOTS_COLUMN_INDEX] = weapon.getAmmunitionCount();
		row[InventoryWeaponTableModel.MAX_SHOTS_COLUMN_INDEX] = weapon.getAmmunitionCapacity();
		row[InventoryWeaponTableModel.RATE_OF_FIRE_COLUMN_INDEX] = weapon.getRateOfFire();
		row[InventoryWeaponTableModel.RELIABILITY_COLUMN_INDEX] = weapon.getReliability();
		row[InventoryWeaponTableModel.RANGE_COLUMN_INDEX] = weapon.getRangeModifier();
		row[InventoryWeaponTableModel.COST_COLUMN_INDEX] = weapon.getCost();
		row[InventoryWeaponTableModel.WEIGHT_COLUMN_INDEX] = weapon.getWeight();
		row[InventoryWeaponTableModel.OBJECT_INDEX] = weapon;

		return row;
	}

	/**
	 * The underlying model used by a table that displays a player's weapon
	 * inventory.
	 * 
	 * @author Coul Greer
	 */
	public static class InventoryWeaponTableModel extends DefaultTableModel {
		/**
		 * The index of the column used to hold the type of a weapon.
		 */
		public static final int TYPE_COLUMN_INDEX = 0;

		/**
		 * The index of the column used to hold the name of a weapon.
		 */
		public static final int NAME_COLUMN_INDEX = 1;

		/**
		 * The index of the column used to hold the flat bonus to accuracy of a weapon.
		 */
		public static final int WEAPON_ACCURACY_COLUMN_INDEX = 2;

		/**
		 * The index of the column used to hold the concealability rating of a weapon.
		 */
		public static final int CONCEALABILITY_COLUMN_INDEX = 3;

		/**
		 * The index of the column used to hold the availability rating of a weapon.
		 */
		public static final int AVAILABILITY_COLUMN_INDEX = 4;

		/**
		 * The index of the column used to hold the damage of a weapon.
		 */
		public static final int DAMAGE_COLUMN_INDEX = 5;

		/**
		 * The index of the column used to hold the type of ammunition that a weapon
		 * uses.
		 */
		public static final int AMMO_COLUMN_INDEX = 6;

		/**
		 * The index of the column used to hold the current amount of ammunition a
		 * weapon has stored inside itself.
		 */
		public static final int CURRENT_SHOTS_COLUMN_INDEX = 7;

		/**
		 * The index of the column used to hold the maximum amount of ammunition a
		 * weapon can store inside itself.
		 */
		public static final int MAX_SHOTS_COLUMN_INDEX = 8;

		/**
		 * The index of the column used to hold the amount of shots a weapon can make
		 * per turn.
		 */
		public static final int RATE_OF_FIRE_COLUMN_INDEX = 9;

		/**
		 * The index of the column used to hold the reliability rating of a weapon.
		 */
		public static final int RELIABILITY_COLUMN_INDEX = 10;

		/**
		 * The index of the column used to hold the range of attack of a weapon.
		 */
		public static final int RANGE_COLUMN_INDEX = 11;

		/**
		 * The index of the column used to hold the cost of a weapon.
		 */
		public static final int COST_COLUMN_INDEX = 12;

		/**
		 * The index of the column used to hold the weight of a weapon.
		 */
		public static final int WEIGHT_COLUMN_INDEX = 13;

		/**
		 * The index of the column used to hold the object representing a weapon.
		 */
		public static final int OBJECT_INDEX = 14;

		/**
		 * A list of names for each column with respect to the indices.
		 */
		public static final String[] COLUMN_NAMES = { //
				"", //
				"Name", //
				"W.A.", //
				"Con.", //
				"Avail.", //
				"Damage", //
				"Ammo", //
				"Cur. Shots", //
				"Max Shots", //
				"RoF", //
				"Rel.", //
				"Range", //
				"Cost", //
				"Wt.", //
				"Object" };

		private Set<CyberpunkWeapon> weaponSet;

		public InventoryWeaponTableModel(Set<CyberpunkWeapon> weaponSet) {
			this.weaponSet = weaponSet;

			populateModel();
		}

		private void populateModel() {
			Iterator<CyberpunkWeapon> iterator = weaponSet.iterator();

			while (iterator.hasNext()) {
				addRow(buildRow(iterator.next()));
			}
		}

		private Object[] buildRow(CyberpunkWeapon weapon) {
			Object[] row = new Object[COLUMN_NAMES.length];

			row[NAME_COLUMN_INDEX] = weapon.getName();
			row[TYPE_COLUMN_INDEX] = weapon.getWeaponType();
			row[WEAPON_ACCURACY_COLUMN_INDEX] = weapon.getHitModifier();
			row[CONCEALABILITY_COLUMN_INDEX] = weapon.getConcealability();
			row[AVAILABILITY_COLUMN_INDEX] = weapon.getAvailability();
			row[DAMAGE_COLUMN_INDEX] = weapon.getHitDice() + "+" + weapon.getDamageScore();
			row[AMMO_COLUMN_INDEX] = weapon.getAmmunitionType();
			row[CURRENT_SHOTS_COLUMN_INDEX] = weapon.getAmmunitionCount();
			row[MAX_SHOTS_COLUMN_INDEX] = weapon.getAmmunitionCapacity();
			row[RATE_OF_FIRE_COLUMN_INDEX] = weapon.getRateOfFire();
			row[RELIABILITY_COLUMN_INDEX] = weapon.getReliability();
			row[RANGE_COLUMN_INDEX] = weapon.getRangeModifier();
			row[COST_COLUMN_INDEX] = weapon.getCost();
			row[WEIGHT_COLUMN_INDEX] = weapon.getWeight();
			row[OBJECT_INDEX] = weapon;

			return row;
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public String getColumnName(int col) {
			return COLUMN_NAMES[col];
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@Override
		public Class<?> getColumnClass(int col) {
			switch (col) {
			case TYPE_COLUMN_INDEX:
				return String.class;
			case NAME_COLUMN_INDEX:
				return String.class;
			case WEAPON_ACCURACY_COLUMN_INDEX:
				return Integer.class;
			case CONCEALABILITY_COLUMN_INDEX:
				return Concealability.class;
			case AVAILABILITY_COLUMN_INDEX:
				return Availability.class;
			case DAMAGE_COLUMN_INDEX:
				return Probability.class;
			case AMMO_COLUMN_INDEX:
				return String.class;
			case CURRENT_SHOTS_COLUMN_INDEX:
				return Integer.class;
			case MAX_SHOTS_COLUMN_INDEX:
				return Integer.class;
			case RATE_OF_FIRE_COLUMN_INDEX:
				return Integer.class;
			case RELIABILITY_COLUMN_INDEX:
				return Reliability.class;
			case RANGE_COLUMN_INDEX:
				return Integer.class;
			case COST_COLUMN_INDEX:
				return Double.class;
			case WEIGHT_COLUMN_INDEX:
				return Double.class;
			default:
				return Object.class;
			}
		}

	}

}
