package rpg.cyberpunk._2020.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import rpg.cyberpunk._2020.Player;
import rpg.cyberpunk._2020.stats.CyberpunkSkill;

/**
 * A panel that displays a collection of skills possessed by a player.
 */
public class SkillTab extends JPanel {
  private static final int COLUMN_COUNT = 4;
  private static final long serialVersionUID = 1L;

  private Player player;

  /**
   * Creates a SkillTab that uses player to get data for display. Creates a JScrollPane to assist
   * with the display of the information acquired.
   * 
   * @param player the owner of the information displayed
   */
  public SkillTab(Player player) {
    super(new BorderLayout());
    setPlayer(player);

    JScrollPane scrollPane = new JScrollPane(createSkillColumns(COLUMN_COUNT));
    scrollPane.getVerticalScrollBar().setUnitIncrement(20);
    add(scrollPane, BorderLayout.CENTER);
  }

  private void setPlayer(Player player) {
    if (player == null) {
      throw new NullPointerException();
    } else {
      this.player = player;
    }
  }

  private Component createSkillColumns(int maxColumns) {
    JPanel panel = new JPanel(new GridLayout(1, maxColumns));

    Iterator<Map.Entry<String, Map<String, CyberpunkSkill>>> skillCategoryIterator =
        player.createSkillCategoryIterator();

    double maxElements = countSkillElements();
    int elementsPerColumn = (int) Math.ceil(maxElements / maxColumns);

    int elementCount = 0;
    Container columnPanel = createColumnPanel();
    panel.add(columnPanel);
    while (skillCategoryIterator.hasNext()) {
      Map.Entry<String, Map<String, CyberpunkSkill>> categoryEntry = skillCategoryIterator.next();

      if (hasReachedCapacity(elementsPerColumn, elementCount)) {
        columnPanel = createColumnPanel();
        panel.add(columnPanel);
        elementCount = 0;
      }

      columnPanel.add(createSkillCategoryPanel(categoryEntry.getKey()));
      elementCount++;

      Iterator<CyberpunkSkill> skillsByNameIterator = categoryEntry.getValue().values().iterator();
      while (skillsByNameIterator.hasNext()) {
        if (hasReachedCapacity(elementsPerColumn, elementCount)) {
          columnPanel = createColumnPanel();
          panel.add(columnPanel);
          elementCount = 0;
        }

        columnPanel.add(new SkillPane(skillsByNameIterator.next()));
        elementCount++;
      }
    }

    return panel;
  }

  private int countSkillElements() {
    int count = 0;
    Iterator<Map.Entry<String, Map<String, CyberpunkSkill>>> iterator =
        player.createSkillCategoryIterator();

    while (iterator.hasNext()) {
      count++;
      count += iterator.next().getValue() //
          .size();
    }

    return count;
  }

  private Container createColumnPanel() {
    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.setBorder(BorderFactory.createCompoundBorder( //
        BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK),
        BorderFactory.createEmptyBorder(0, 5, 0, 0)));

    return panel;
  }

  private boolean hasReachedCapacity(int capacity, int count) {
    return count >= capacity;
  }

  private Component createSkillCategoryPanel(String categoryName) {
    JPanel panel = new JPanel(new BorderLayout());

    JLabel label = new JLabel(categoryName);
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setVerticalAlignment(SwingConstants.CENTER);
    panel.add(label);

    return panel;
  }

}
