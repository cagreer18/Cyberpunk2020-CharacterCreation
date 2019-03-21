package rpg.cyberpunk._2020.stats;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import rpg.general.stats.Attribute;

public class AttributeManagerTest {

	@Test
	public void testAttributeLevelEqualsThreeIfAttributeLevelIncreasedOnce() {

		AttributeManager attributeManager = new AttributeManager();

		attributeManager.increaseLevel(CyberpunkAttribute.ATTRACTIVENESS);

		assertEquals(3, attributeManager.getBaseLevel(CyberpunkAttribute.ATTRACTIVENESS));
	}

	@Test
	public void testAttributeLevelEqualsThreeIfAttributeLevelIncreasedTwiceThenDecreasedOnce() {
		AttributeManager attributeManager = new AttributeManager();

		attributeManager.increaseLevel(CyberpunkAttribute.ATTRACTIVENESS);
		attributeManager.increaseLevel(CyberpunkAttribute.ATTRACTIVENESS);
		attributeManager.decreaseLevel(CyberpunkAttribute.ATTRACTIVENESS);

		assertEquals(3, attributeManager.getBaseLevel(CyberpunkAttribute.ATTRACTIVENESS));
	}

	@Test
	public void testAttributeLevelEqualsMinimumLevelIfAttributeLevelIncreasedOnceThenResetLevel() {
		AttributeManager attributeManager = new AttributeManager();

		attributeManager.increaseLevel(CyberpunkAttribute.ATTRACTIVENESS);
		attributeManager.resetLevel(CyberpunkAttribute.ATTRACTIVENESS);

		assertEquals(CyberpunkAttribute.MIN_LEVEL, attributeManager.getBaseLevel(CyberpunkAttribute.ATTRACTIVENESS));
	}

	@Test
	public void testNullIsReturnedIfNullAttributeNameIsGiven() {
		AttributeManager manager = new AttributeManager();

		assertEquals(null, manager.getStatistic(null));
	}

	@Test(expected = NullPointerException.class)
	public void testExceptionThrownIfNullNameIsGivenWhenGettingLevel() {
		AttributeManager manager = new AttributeManager();

		manager.getBaseLevel(null);
	}

	@Test
	public void testMockAttributeReturnedIfAddedToAttributeManager() {
		Attribute mockAttribute = mock(Attribute.class);
		when(mockAttribute.getName()).thenReturn("Mock Attribute");

		AttributeManager manager = new AttributeManager();

		manager.add(mockAttribute);

		assertEquals(mockAttribute, manager.getStatistic(mockAttribute.getName()));
	}

	@Test
	public void testNullReturnedIfMockAttributeAddedThenRemoved() {
		Attribute mockAttribute = mock(Attribute.class);
		when(mockAttribute.getName()).thenReturn("Mock Attribute");

		AttributeManager manager = new AttributeManager();

		manager.add(mockAttribute);
		manager.remove(mockAttribute.getName());

		assertEquals(null, manager.getStatistic(mockAttribute.getName()));
	}

}
