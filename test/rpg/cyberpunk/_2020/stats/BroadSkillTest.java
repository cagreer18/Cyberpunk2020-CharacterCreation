package rpg.cyberpunk._2020.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BroadSkillTest {
	private static CyberpunkAttribute mockAttribute;

	private BroadSkill broadSkill;

	@BeforeClass
	public static void setUpBeforeClass() {
		mockAttribute = mock(CyberpunkAttribute.class);
		when(mockAttribute.getModifier()).thenReturn(2);
	}

	@Before
	public void setUp() {
		broadSkill = new BroadSkill(CyberpunkSkill.MARTIAL_ART, "A parent skill.");
		broadSkill.add(new SpecializedSkill(mockAttribute, CyberpunkSkill.AIKIDO, "", 1));
		broadSkill.add(new SpecializedSkill(mockAttribute, CyberpunkSkill.ANIMAL_KUNG_FU, "", 1));
		broadSkill.add(new SpecializedSkill(mockAttribute, CyberpunkSkill.CAPOERIA, "", 1));
		broadSkill.add(new SpecializedSkill(mockAttribute, CyberpunkSkill.JUDO, "", 1));
		broadSkill.add(new SpecializedSkill(mockAttribute, CyberpunkSkill.KARATE, "", 1));
	}

	@After
	public void tearDown() {
		broadSkill = null;
	}

	@Test
	public void testGettingNonexistantChildReturnsNullCyberpunkSkill() {
		assertEquals(NullSkill.getInstance(), broadSkill.getChild(null));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testExceptionThrownWhenGetLevel() {
		broadSkill.getLevel();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testExceptionThrownWhenGetCurrentImprovementPoints() {
		broadSkill.getCurrentImprovementPoints();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testExceptionThrownIfGetNeededImprovementPoints() {
		broadSkill.getNeededImprovementPoints();
	}

	@Test
	public void testIsNotEnabledAlways() {
		BroadSkill skill = new BroadSkill("Test Skill", "A skill that is always enabled.");

		assertFalse(skill.isEnabled());
	}
}
