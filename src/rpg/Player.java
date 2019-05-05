package rpg;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import rpg.cyberpunk._2020.combat.AikidoFightingStyleFactory;
import rpg.cyberpunk._2020.combat.AnimalKungFuFightingStyleFactory;
import rpg.cyberpunk._2020.combat.ArmorManager;
import rpg.cyberpunk._2020.combat.BoxingFightingStyleFactory;
import rpg.cyberpunk._2020.combat.BrawlingFightingStyleFactory;
import rpg.cyberpunk._2020.combat.CapoeriaFightingStyleFactory;
import rpg.cyberpunk._2020.combat.ChoiLiFutFightingStyleFactory;
import rpg.cyberpunk._2020.combat.CyberpunkArmor;
import rpg.cyberpunk._2020.combat.CyberpunkCombatant;
import rpg.cyberpunk._2020.combat.CyberpunkWeapon;
import rpg.cyberpunk._2020.combat.FightingMove;
import rpg.cyberpunk._2020.combat.FightingStyle;
import rpg.cyberpunk._2020.combat.FightingStyleFactory;
import rpg.cyberpunk._2020.combat.JudoFightingStyleFactory;
import rpg.cyberpunk._2020.combat.KarateFightingStyleFactory;
import rpg.cyberpunk._2020.combat.TaeKwonDoFightingStyleFactory;
import rpg.cyberpunk._2020.combat.ThaiKickBoxingFightingStyleFactory;
import rpg.cyberpunk._2020.combat.WrestlingFightingStyleFactory;
import rpg.cyberpunk._2020.commerce.BottomlessInventory;
import rpg.cyberpunk._2020.commerce.Inventory;
import rpg.cyberpunk._2020.commerce.PlayerTrader;
import rpg.cyberpunk._2020.stats.CyberpunkSkill;
import rpg.cyberpunk._2020.stats.Role;
import rpg.cyberpunk._2020.stats.StatisticFactory;
import rpg.general.combat.Ammunition;
import rpg.general.combat.AmmunitionContainer;
import rpg.general.combat.BodyLocation;
import rpg.general.combat.Combatant;
import rpg.general.commerce.Item;
import rpg.general.commerce.Trader;
import rpg.general.stats.Attribute;
import rpg.util.Probability;

public class Player {
  public static final String PROPERTY_NAME_MONEY = "Money";
  public static final String PROPERTY_NAME_INVENTORY_WEIGHT = "Inventory: Weight";
  public static final String PROPERTY_NAME_INVENTORY_WEAPON_MANIPULATED =
      "Inventory: Weapon Manipulated";
  public static final String PROPERTY_NAME_INVENTORY_ARMOR_MANIPULATED =
      "Inventory: Armor Manipulated";
  public static final String PROPERTY_NAME_INVENTORY_AMMUNITION_MANIPULATED =
      "Inventory: Ammunition Manipulated";
  public static final String PROPERTY_NAME_INVENTORY_ITEM_MANIPULATED =
      "Inventory: Item Manipulated";
  public static final String PROPERTY_NAME_EQUIPMENT_WEAPON = "Equipment: Weapon";
  public static final String PROPERTY_NAME_EQUIPMENT_ARMOR = "Equipment: Armor";
  public static final String PROPERTY_NAME_SKILL_LEVEL = "Skill Level";
  public static final String PROPERTY_NAME_ROLE = "Role";

  /**
   * A constant representing the index of the primary weapon slot.
   */
  public static final int PRIMARY_SLOT = 0;

  /**
   * A constant representing the index of the secondary weapon slot.
   */
  public static final int SECONDARY_SLOT = 1;

  private PropertyChangeSupport changeSupport;
  private Inventory pocketInventory = new BottomlessInventory();
  private Role role;
  private Trader trader;
  private Combatant<CyberpunkWeapon> combatant;
  private FightingStyleFactory unarmedWeaponFactory;
  private CyberpunkWeapon[] equippedWeapons;
  private ArmorManager armorManager;
  private Map<String, Attribute> attributesByName;
  private Map<String, Map<String, CyberpunkSkill>> skillsByNameByCategoryName;

  public Player() {
    changeSupport = new PropertyChangeSupport(this);
    trader = new PlayerTrader(0.0);
    combatant = new CyberpunkCombatant(this);
    unarmedWeaponFactory = BrawlingFightingStyleFactory.getInstance();
    equippedWeapons = new CyberpunkWeapon[] {unarmedWeaponFactory.createStrike(),
        unarmedWeaponFactory.createStrike()};
    armorManager = new ArmorManager();
    attributesByName = StatisticFactory.createAttributes();
    skillsByNameByCategoryName = createObservedSkillsByNameByCategoryName(attributesByName);
  }

  private Map<String, Map<String, CyberpunkSkill>> createObservedSkillsByNameByCategoryName(
      Map<String, Attribute> attributesByName) {
    Map<String, Map<String, CyberpunkSkill>> observedSkillsByNameByCategoryName =
        new LinkedHashMap<String, Map<String, CyberpunkSkill>>();

    for (Map.Entry<String, Map<String, CyberpunkSkill>> categoryEntry : StatisticFactory
        .createSkillByNameByCategoryName(attributesByName, this).entrySet()) {

      Map<String, CyberpunkSkill> observedSkillsByName = new TreeMap<String, CyberpunkSkill>();

      for (Map.Entry<String, CyberpunkSkill> skillEntry : categoryEntry.getValue().entrySet()) {
        observedSkillsByName.put(skillEntry.getKey(),
            new PlayerSkill(changeSupport, skillEntry.getValue()));
      }

      observedSkillsByNameByCategoryName.put(categoryEntry.getKey(), observedSkillsByName);
    }

    return observedSkillsByNameByCategoryName;
  }

  public void buy(CyberpunkWeapon weapon, double price) {
    double oldMoney = trader.getMoney();

    try {
      trader.buy(price);
      addToInventory(weapon);
    } catch (Exception ex) {
      trader.sell(price);
      throw ex;
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_MONEY, oldMoney, trader.getMoney());
  }

  public void buy(CyberpunkArmor armor, double price) {
    double oldMoney = trader.getMoney();

    try {
      trader.buy(price);
      addToInventory(armor);
    } catch (Exception ex) {
      trader.sell(price);
      throw ex;
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_MONEY, oldMoney, trader.getMoney());
  }

  public void buy(List<Ammunition> ammunition, double price) {
    double oldMoney = trader.getMoney();

    try {
      trader.buy(price);

      Iterator<Ammunition> iterator = ammunition.iterator();
      while (iterator.hasNext()) {
        addToInventory(iterator.next());
      }
    } catch (Exception ex) {
      trader.sell(price);
      throw ex;
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_MONEY, oldMoney, trader.getMoney());
  }

  public void sell(CyberpunkWeapon weapon, double price) {
    double oldMoney = trader.getMoney();

    try {
      trader.sell(price);
      removeFromInventory(weapon, 1);
    } catch (Exception ex) {
      trader.buy(price);
      throw ex;
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_MONEY, oldMoney, trader.getMoney());
  }

  public void sell(CyberpunkArmor armor, double price) {
    double oldMoney = trader.getMoney();

    try {
      trader.sell(price);
      removeFromInventory(armor, 1);
    } catch (Exception ex) {
      trader.buy(price);
      throw ex;
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_MONEY, oldMoney, trader.getMoney());
  }

  public void sell(Ammunition ammunition, double price) {
    double oldMoney = trader.getMoney();

    try {
      trader.sell(price);
      removeFromInventory(ammunition, 1);
    } catch (Exception ex) {
      trader.buy(price);
      throw ex;
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_MONEY, oldMoney, trader.getMoney());
  }

  public void sell(Item item, double price) {
    double oldMoney = trader.getMoney();

    trader.sell(price);
    try {
      removeFromInventory(item, 1);
    } catch (Exception ex) {
      trader.buy(price);
      throw ex;
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_MONEY, oldMoney, trader.getMoney());

  }

  public double getMoney() {
    return trader.getMoney();
  }

  public void equip(int slot, CyberpunkWeapon weapon) {
    CyberpunkWeapon tempWeapon = equippedWeapons[slot];

    unequip(slot);
    try {
      removeFromInventory(weapon, 1);

      if (!CyberpunkWeapon.WEAPON_TYPE_UNARMED.equals(weapon.getWeaponType())) {
        equippedWeapons[slot] = weapon;
      }
    } catch (NullPointerException ex) {
      equip(slot, tempWeapon);
      throw ex;
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_EQUIPMENT_WEAPON, tempWeapon, weapon);
  }

  // TODO Think of throwing an exception to allow propagation of an error to the
  // UI.
  public void equip(CyberpunkArmor armor) {
    if (armorManager.add(armor)) {
      removeFromInventory(armor, 1);

      changeSupport.firePropertyChange(PROPERTY_NAME_EQUIPMENT_ARMOR, null, armor);
    }
  }

  public void unequip(int slot) {
    CyberpunkWeapon weapon = equippedWeapons[slot];

    addToInventory(weapon);

    if (!CyberpunkWeapon.WEAPON_TYPE_UNARMED.equals(weapon.getWeaponType())) {
      equippedWeapons[slot] = createFightingMove(FightingMove.STRIKE);
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_EQUIPMENT_WEAPON, weapon, null);
  }

  // TODO Think of throwing an exception to allow propagation of an error to the
  // UI.
  public void unequip(CyberpunkArmor armor) {
    if (armorManager.remove(armor)) {
      addToInventory(armor);

      changeSupport.firePropertyChange(PROPERTY_NAME_EQUIPMENT_ARMOR, armor, null);
    }
  }

  public int getAttributeValue(String name) {
    return attributesByName.get(name) //
        .getModifier();
  }

  public Iterator<Attribute> createAttributeIterator() {
    return attributesByName.values() //
        .iterator();
  }

  public int getSkillValue(String name) {
    CyberpunkSkill skill = getSkill(name);

    return skill.getTotalValue();
  }

  private CyberpunkSkill getSkill(String name) {
    Iterator<Map<String, CyberpunkSkill>> iterator = skillsByNameByCategoryName.values() //
        .iterator();

    while (iterator.hasNext()) {
      Map<String, CyberpunkSkill> skillsByName = iterator.next();

      if (skillsByName.containsKey(name)) {
        return skillsByName.get(name);
      }
    }

    throw new NoSuchElementException("A skill with the name " + name + "does not exist.");
  }

  public Iterator<Map.Entry<String, Map<String, CyberpunkSkill>>> createSkillCategoryIterator() {
    return skillsByNameByCategoryName.entrySet().iterator();
  }

  public Probability getTotalAttackChance(int slot) {
    return combatant.getTotalAttackChance(equippedWeapons[slot]);
  }

  public int getPlayerAttackModifier(int slot) {
    return combatant.getAttackModifier(equippedWeapons[slot]);
  }

  public int getWeaponAttackModifier(int slot) {
    return equippedWeapons[slot].getAttackModifier();
  }

  public Probability getTotalDamageChance(int slot) {
    return combatant.getTotalDamageChance(equippedWeapons[slot]);
  }

  public int getPlayerDamageModifier(int slot) {
    return combatant.getDamageModifier(equippedWeapons[slot]);
  }

  public int getWeaponDamageModifier(int slot) {
    return equippedWeapons[slot].getDamageModifier();
  }

  public int getRangeScore(int slot) {
    return combatant.getRangeScore(equippedWeapons[slot]);
  }

  public int getPlayerRangeModifier(int slot) {
    return combatant.getRangeModifier(equippedWeapons[slot]);
  }

  public int getWeaponRangeModifier(int slot) {
    return equippedWeapons[slot].getRangeModifier();
  }

  public void attack(int slot, int shotsFired) {
    equippedWeapons[slot].fire(shotsFired);
  }

  public List<Ammunition> reload(int slot, AmmunitionContainer ammunitionStorage) {
    List<Ammunition> spareAmmunition = equippedWeapons[slot].reload(ammunitionStorage);
    spareAmmunition.stream().forEach(a -> addToInventory(a));
    return spareAmmunition;
  }

  public void setFightingStance(FightingStyle style, FightingMove move) {
    unarmedWeaponFactory = getFightingStyleFactory(style);
    CyberpunkWeapon weapon = createFightingMove(move);

    equip(PRIMARY_SLOT, weapon);
    equip(SECONDARY_SLOT, weapon);
  }

  private FightingStyleFactory getFightingStyleFactory(FightingStyle style) {
    switch (style) {
      case BRAWLING:
        return BrawlingFightingStyleFactory.getInstance();
      case KARATE:
        return KarateFightingStyleFactory.getInstance();
      case JUDO:
        return JudoFightingStyleFactory.getInstance();
      case BOXING:
        return BoxingFightingStyleFactory.getInstance();
      case THAI_BOXING:
        return ThaiKickBoxingFightingStyleFactory.getInstance();
      case CHOI_LI_FUT:
        return ChoiLiFutFightingStyleFactory.getInstance();
      case AIKIDO:
        return AikidoFightingStyleFactory.getInstance();
      case ANIMAL_KUNG_FU:
        return AnimalKungFuFightingStyleFactory.getInstance();
      case TAE_KWON_DO:
        return TaeKwonDoFightingStyleFactory.getInstance();
      case WRESTLING:
        return WrestlingFightingStyleFactory.getInstance();
      case CAPEOIRA:
        return CapoeriaFightingStyleFactory.getInstance();
      default:
        return unarmedWeaponFactory;
    }
  }

  private CyberpunkWeapon createFightingMove(FightingMove move) {
    switch (move) {
      case STRIKE:
        return unarmedWeaponFactory.createStrike();
      case KICK:
        return unarmedWeaponFactory.createKick();
      case BLOCK:
        return unarmedWeaponFactory.createBlock();
      case DODGE:
        return unarmedWeaponFactory.createDodge();
      case DISARM:
        return unarmedWeaponFactory.createDisarm();
      case THROW:
        return unarmedWeaponFactory.createThrow();
      case HOLD:
        return unarmedWeaponFactory.createHold();
      case ESCAPE:
        return unarmedWeaponFactory.createEscape();
      case CHOKE:
        return unarmedWeaponFactory.createChoke();
      case SWEEP:
        return unarmedWeaponFactory.createSweep();
      case GRAPPLE:
        return unarmedWeaponFactory.createGrapple();
      default:
        return unarmedWeaponFactory.createStrike();
    }
  }

  // TODO (Coul Greer): Consider hiding this from any other classes. Perhaps use
  // the player as a middle man to access a weapons data and delete this function.
  public CyberpunkWeapon getWeapon(int slot) {
    return equippedWeapons[slot];
  }

  public boolean hasItem(Object o) {
    return pocketInventory.contains(o);
  }

  public int getQuantity(Item item) {
    return pocketInventory.getQuantity(item);
  }

  public void addToInventory(CyberpunkWeapon weapon) {
    double oldWeight = getTotalWeight();

    if (!CyberpunkWeapon.WEAPON_TYPE_UNARMED.equals(weapon.getWeaponType())) {
      pocketInventory.add(weapon);
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_WEAPON_MANIPULATED, null, weapon);
    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_ITEM_MANIPULATED, null, weapon);
    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_WEIGHT, oldWeight, getTotalWeight());
  }

  public void addToInventory(CyberpunkArmor armor) {
    double oldWeight = getTotalWeight();

    pocketInventory.add(armor);

    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_ARMOR_MANIPULATED, null, armor);
    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_ITEM_MANIPULATED, null, armor);
    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_WEIGHT, oldWeight, getTotalWeight());
  }

  public void addToInventory(Ammunition ammunition) {
    double oldWeight = getTotalWeight();

    pocketInventory.add(ammunition);

    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_AMMUNITION_MANIPULATED, null,
        ammunition);
    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_ITEM_MANIPULATED, null, ammunition);
    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_WEIGHT, oldWeight, getTotalWeight());
  }

  public void removeFromInventory(CyberpunkWeapon weapon, int quantity) {
    double oldWeight = getTotalWeight();

    if (!CyberpunkWeapon.WEAPON_TYPE_UNARMED.equals(weapon.getWeaponType())) {
      for (int i = 0; i < quantity; i++) {
        pocketInventory.remove(weapon);
      }
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_WEAPON_MANIPULATED, weapon, null);
    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_ITEM_MANIPULATED, weapon, null);
    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_WEIGHT, oldWeight, getTotalWeight());
  }

  public void removeFromInventory(CyberpunkArmor armor, int quantity) {
    double oldWeight = getTotalWeight();

    for (int i = 0; i < quantity; i++) {
      pocketInventory.remove(armor);
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_ARMOR_MANIPULATED, armor, null);
    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_ITEM_MANIPULATED, armor, null);
    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_WEIGHT, oldWeight, getTotalWeight());
  }

  public void removeFromInventory(Ammunition ammunition, int quantity) {
    double oldWeight = getTotalWeight();

    for (int i = 0; i < quantity; i++) {
      pocketInventory.remove(ammunition);
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_AMMUNITION_MANIPULATED, ammunition,
        null);
    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_ITEM_MANIPULATED, ammunition, null);
    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_WEIGHT, oldWeight, getTotalWeight());
  }

  public void removeFromInventory(Item item, int quantity) {
    double oldWeight = getTotalWeight();

    for (int i = 0; i < quantity; i++) {
      pocketInventory.removeItem(item);
    }

    if (item instanceof CyberpunkWeapon) {
      changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_WEAPON_MANIPULATED, item, null);
    }

    if (item instanceof CyberpunkArmor) {
      changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_ARMOR_MANIPULATED, item, null);
    }

    if (item instanceof Ammunition) {
      changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_AMMUNITION_MANIPULATED, item, null);
    }

    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_ITEM_MANIPULATED, item, null);
    changeSupport.firePropertyChange(PROPERTY_NAME_INVENTORY_WEIGHT, oldWeight, getTotalWeight());
  }

  public Collection<CyberpunkWeapon> createCarriedWeaponCollection() {
    return pocketInventory.createWeaponCollection();
  }

  public Collection<CyberpunkArmor> createCarriedArmorCollection() {
    return pocketInventory.createArmorCollection();
  }

  public Collection<Ammunition> createCarriedAmmunitionCollection() {
    return pocketInventory.createAmmunitionCollection();
  }

  public Collection<Item> createCarriedItemCollection() {
    return pocketInventory.createItemCollection();
  }

  public Collection<CyberpunkArmor> createEquippedArmorCollection() {
    return armorManager.createArmorCollection();
  }

  public double getTotalWeight() {
    return pocketInventory.getTotalWeight() //
        + equippedWeapons[PRIMARY_SLOT].getWeight() + equippedWeapons[SECONDARY_SLOT].getWeight() //
        + armorManager.getTotalWeight();
  }

  public int getLocationDurability(BodyLocation location) {
    return armorManager.getLocationDurability(location);
  }

  public int getEncumbranceValue() {
    return armorManager.getEncumbranceValue();
  }

  public Role getRole() {
    return role;
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  private static class PlayerSkill implements CyberpunkSkill {
    private PropertyChangeSupport playerChangeSupport;
    private PropertyChangeSupport skillChangeSupport;
    private CyberpunkSkill skill;

    /**
     * Wraps a CyberpunkSkill. The Player can notify listeners when the wrapped skill's level or
     * improvement points are increased or decreased.
     * 
     * @param changeSupport the PropertyChangeSupport object used by a Player
     * @param skill the statistic to be observed
     */
    public PlayerSkill(PropertyChangeSupport changeSupport, CyberpunkSkill skill) {
      setChangeSupport(changeSupport);
      setSkill(skill);

      skillChangeSupport = new PropertyChangeSupport(this);
    }

    private void setChangeSupport(PropertyChangeSupport changeSupport) {
      if (changeSupport == null) {
        throw new NullPointerException();
      } else {
        this.playerChangeSupport = changeSupport;
      }
    }

    private void setSkill(CyberpunkSkill skill) {
      if (skill == null) {
        throw new NullPointerException();
      } else {
        this.skill = skill;
      }
    }

    @Override
    public int getTotalValue() {
      return skill.getTotalValue();
    }

    @Override
    public String getName() {
      return skill.getName();
    }

    @Override
    public String getDescription() {
      return skill.getDescription();
    }

    @Override
    public int getLevel() {
      return skill.getLevel();
    }

    @Override
    public void increaseLevel() {
      int oldValue = skill.getLevel();

      skill.increaseLevel();
      playerChangeSupport.firePropertyChange(PROPERTY_NAME_SKILL_LEVEL, oldValue, skill.getLevel());
      skillChangeSupport.firePropertyChange(PROPERTY_NAME_SKILL_LEVEL, oldValue, skill.getLevel());
    }

    @Override
    public void decreaseLevel() {
      int oldValue = skill.getLevel();

      skill.decreaseLevel();
      playerChangeSupport.firePropertyChange(PROPERTY_NAME_SKILL_LEVEL, oldValue, skill.getLevel());
      skillChangeSupport.firePropertyChange(PROPERTY_NAME_SKILL_LEVEL, oldValue, skill.getLevel());
    }

    @Override
    public void resetLevel() {
      int oldValue = skill.getLevel();

      skill.resetLevel();
      playerChangeSupport.firePropertyChange(PROPERTY_NAME_SKILL_LEVEL, oldValue, skill.getLevel());
      skillChangeSupport.firePropertyChange(PROPERTY_NAME_SKILL_LEVEL, oldValue, skill.getLevel());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      skill.propertyChange(evt);
    }

    @Override
    public boolean isEnabled() {
      return skill.isEnabled();
    }

    @Override
    public void increaseCurrentImprovementPoints(int improvementPoints) {
      int oldValue = skill.getLevel();

      skill.increaseCurrentImprovementPoints(improvementPoints);
      playerChangeSupport.firePropertyChange(PROPERTY_NAME_SKILL_LEVEL, oldValue, skill.getLevel());
      skillChangeSupport.firePropertyChange(PROPERTY_NAME_SKILL_LEVEL, oldValue, skill.getLevel());
    }

    @Override
    public int getCurrentImprovementPoints() {
      return skill.getCurrentImprovementPoints();
    }

    @Override
    public int getNeededImprovementPoints() {
      return skill.getNeededImprovementPoints();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
      skillChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
      skillChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
      skillChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
      skillChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

  }

}
