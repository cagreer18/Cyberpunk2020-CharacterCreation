package rpg.cyberpunk._2020.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import rpg.Player;
import rpg.cyberpunk._2020.combat.CyberpunkArmor;
import rpg.cyberpunk._2020.combat.CyberpunkWeapon;
import rpg.cyberpunk._2020.commerce.CyberpunkVendor;
import rpg.cyberpunk._2020.gui.InventoryWeaponCategoryTable.InventoryWeaponTableModel;

/**
 * A panel used to keep all the inventory GUI elements organized for layout
 * mangers.
 * 
 * @author Coul Greer
 */
public class InventoryTab extends JPanel {
	private Player player;
	private CyberpunkVendor vendor;

	/**
	 * Constructs a panel filled with elements needed under the 'Inventory' tab.
	 * 
	 * @param player the object from which data is drawn from and passed to while
	 *               also displaying its contents to the GUI
	 */
	public InventoryTab(Player player, CyberpunkVendor vendor) {
		super(new BorderLayout());

		this.player = player;
		this.vendor = vendor;

		JPanel infoPanel = new InventoryInfoPanel(player);
		add(infoPanel, BorderLayout.NORTH);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.LEFT);
		add(tabbedPane, BorderLayout.CENTER);

		JTable weaponTable = new InventoryWeaponCategoryTable(player);
		weaponTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tabbedPane.addTab("Weapons", new InventoryCategoryPanel(weaponTable, evt -> sellWeapon(weaponTable)));

		JTable armorTable = new InventoryArmorCategoryTable(player);
		tabbedPane.addTab("Armor", new InventoryCategoryPanel(armorTable, evt -> sellArmor(weaponTable)));
	}

	private void sellWeapon(JTable table) {
		int selectedRowIndex = table.getSelectedRow();

		if (selectedRowIndex >= 0) {
			int actualSelectedRowIndex = table.convertRowIndexToModel(selectedRowIndex);

			CyberpunkWeapon weapon = (CyberpunkWeapon) table.getModel().getValueAt(actualSelectedRowIndex,
					InventoryWeaponTableModel.OBJECT_INDEX);

			player.sell(weapon, vendor.getBidPrice(weapon));
		}
	}

	private void sellArmor(JTable table) {
		int selectedRowIndex = table.getSelectedRow();

		if (selectedRowIndex >= 0) {
			int actualSelectedRowIndex = table.convertRowIndexToModel(selectedRowIndex);

			CyberpunkArmor armor = (CyberpunkArmor) table.getModel().getValueAt(actualSelectedRowIndex,
					InventoryWeaponTableModel.OBJECT_INDEX);

			player.sell(armor, vendor.getBidPrice(armor));
		}
	}

}
