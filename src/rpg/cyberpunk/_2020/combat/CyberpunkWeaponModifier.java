package rpg.cyberpunk._2020.combat;

import java.util.Iterator;
import java.util.List;
import rpg.general.combat.WeaponAttachment;
import rpg.general.commerce.Item;
import rpg.util.Measurement;

public class CyberpunkWeaponModifier implements Item, WeaponAttachment {
  public static final String ATTACHMENT_POINT_UNDERBARREL = "Underbarrel";
  public static final String ATTACHMENT_POINT_BARREL = "Barrel";
  public static final String ATTACHMENT_POINT_OPTIC = "Optic";
  public static final String ATTACHMENT_POINT_STOCK = "Stock";
  public static final String ATTACHMENT_POINT_GRIP = "Grip";

  public static final Measurement WEIGHT = new Measurement( //
      Measurement.Type.MASS, //
      0.0, //
      Measurement.Unit.KILOGRAM);

  private static final long serialVersionUID = 1L;

  private String name;
  private String description;
  private int hitModifier;
  private int damageModifier;
  private Measurement rangeModifier;
  private String attachmentPoint;
  private List<String> bonuses;
  private double cost;

  public CyberpunkWeaponModifier(String name, String description, int hitModifier,
      int damageModifier, Measurement rangeModifier, String attachmentPoint, List<String> bonuses,
      double cost) {
    setName(name);
    setDescription(description);
    this.hitModifier = hitModifier;
    this.damageModifier = damageModifier;
    this.rangeModifier = rangeModifier;
    setBonuses(bonuses);
    setCost(cost);
  }

  private void setName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("The name of an item cannot be set to a null value.");
    } else {
      this.name = name;
    }
  }

  private void setDescription(String description) {
    if (description == null) {
      throw new IllegalArgumentException(
          "The description of an item cannot be set to a null value.");
    } else {
      this.description = description;
    }
  }

  private void setBonuses(List<String> bonuses) {
    if (bonuses == null) {
      throw new IllegalArgumentException("The list of bonuses cannot be null.");
    } else {
      this.bonuses = bonuses;
    }
  }

  private void setCost(double cost) {
    if (cost < 0.0) {
      throw new IllegalArgumentException("The cost of an item cannot be negative.");
    } else {
      this.cost = cost;
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Measurement getWeight() {
    return WEIGHT;
  }

  @Override
  public double getCost() {
    return cost;
  }

  @Override
  public int getAttackModifier() {
    return hitModifier;
  }

  @Override
  public int getDamageModifier() {
    return damageModifier;
  }

  @Override
  public Measurement getRangeModifier() {
    return rangeModifier;
  }

  @Override
  public String getAttachmentPoint() {
    return attachmentPoint;
  }

  @Override
  public Iterator<String> getConditionalBonus() {
    return bonuses.iterator();
  }

}
