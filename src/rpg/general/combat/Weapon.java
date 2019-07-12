package rpg.general.combat;

import java.util.Iterator;
import rpg.general.combat.Ammunition.Type;
import rpg.general.commerce.Item;
import rpg.util.Die;

/**
 * An object that can be held by a combatant resulting in modification to the
 * internal data. A skill is associated to help derive the modified values
 * provided by the combatant.
 */
public interface Weapon extends Item, Shootable {
	public static final String NO_WEAPON_TYPE = "None";
	public static final String NO_SKILL_NAME = "None";
	public static final Type NO_AMMUNITION_TYPE = new Type("None");

	/**
	 * Gets the category that this <code>Weapon</code> belongs to.
	 * 
	 * @return a string representation of this <code>Weapon</code>'s category
	 */
	public String getWeaponType();

	/**
	 * Returns the skill name that this <code>Weapon</code> uses for range, damage,
	 * and hit modifications.
	 * 
	 * @return the skill name used to modify this <code>Weapon</code>'s parameters
	 */
	public String getSkillName();

	/**
	 * Return a <code>Die</code> to represent the probability of damage.
	 * 
	 * @return the probability of damage
	 */
	public Die getDamageDice();

	/**
	 * Return a <code>Die</code> to represent the probability of hitting a target.
	 * 
	 * @return the probability of hitting a target
	 */
	public Die getHitDice();

	/**
	 * Returns the conditional bonuses. This excludes any flat bonuses that are
	 * applied in general to the scores of range, damage, and hit. For example,
	 * modifying values in a specific situation like "when crouched" or "in certain
	 * lighting".
	 * 
	 * @return any bonuses that are not modifiers to range, damage, and hit in a
	 *         general case. Such as, modifying values in a specific situation like
	 *         "when crouched" or "in certain lighting".
	 */
	public Iterator<WeaponAttachment> getAttachments();

	// TODO (Coul Greer): Implement attachment module.
	public WeaponAttachment addAttachment(WeaponAttachment attachment);
}
