package rpg.cyberpunk._2020.stats;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import rpg.general.stats.Attribute;

/**
 * A derived attribute representing the distance a character can leap. Differs from the other
 * derived attributes by the way it calculates its level/value.
 */
public class LeapAttribute implements Attribute, PropertyChangeListener {
  private String name;
  private String description;
  private Attribute parentAttribute;
  private int value;
  private PropertyChangeSupport changeSupport;

  /**
   * Constructs an attribute dependent on another attribute.
   * 
   * @param name the identifier for this attribute
   * @param description a blurb giving an overview of what this skill does/is
   * @param parentAttribute the attribute used to derive the distance that a person can leap
   */
  public LeapAttribute(String name, String description, Attribute parentAttribute) {
    setName(name);
    setDescription(description);
    setParentAttribute(parentAttribute);
    changeSupport = new PropertyChangeSupport(this);
  }

  private void setName(String name) {
    if (name == null) {
      throw new NullPointerException();
    } else {
      this.name = name;
    }
  }

  private void setDescription(String description) {
    if (description == null) {
      throw new NullPointerException();
    } else {
      this.description = description;
    }
  }

  private void setParentAttribute(Attribute parentAttribute) {
    if (parentAttribute == null) {
      throw new NullPointerException();
    } else {
      parentAttribute.addPropertyChangeListener(Attribute.PROPERTY_NAME_ATTRIBUTE_LEVEL, this);
      this.parentAttribute = parentAttribute;
      value = calculateLevel();
    }
  }

  private int calculateLevel() {
    return (int) ((3.0 * parentAttribute.getModifier()) / 4.0);
  }

  @Override
  public void incrementLevel() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void decrementLevel() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void resetLevel() {
    throw new UnsupportedOperationException();
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
  public int getLevel() {
    return value;
  }

  @Override
  public int getModifier() {
    return value;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (parentAttribute == evt.getSource()) {
      value = calculateLevel();
    }
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }

  @Override
  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  @Override
  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(propertyName, listener);
  }
}
