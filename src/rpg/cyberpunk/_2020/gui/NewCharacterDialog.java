package rpg.cyberpunk._2020.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import rpg.Gender;
import rpg.cyberpunk._2020.Age;
import rpg.cyberpunk._2020.Player;
import rpg.cyberpunk._2020.Sibling;
import rpg.cyberpunk._2020.Sibling.RelativeAge;
import rpg.cyberpunk._2020.gui.stats.FastCharacterPointsPane;
import rpg.cyberpunk._2020.gui.stats.RandomCharacterPointsPane;
import rpg.cyberpunk._2020.stats.CyberpunkAttribute;
import rpg.cyberpunk._2020.stats.Role;
import rpg.cyberpunk._2020.stats.RoleFactory;
import rpg.cyberpunk._2020.stats.StatisticFactory;
import rpg.cyberpunk._2020.systems.CharacterPointsManager;
import rpg.cyberpunk._2020.systems.FastCharacterPointsManager;
import rpg.cyberpunk._2020.systems.RandomCharacterPointsManager;
import rpg.general.stats.Attribute;
import rpg.util.Die;
import rpg.util.Name;
import rpg.util.RandomNumberGenerator;

/**
 * An instance of JDialog that takes inputs from various cards that allows a <code>Player</code> to
 * set its background info and sets the Player's initial <code>Attribute</code>s.
 */
public class NewCharacterDialog extends JDialog {
  private static final long serialVersionUID = 1L;

  private static final int MAX_SIBLING_COUNT = 7;

  // Fonts
  private static final Font TITLE1_FONT = new Font("Serif", Font.PLAIN, 30);
  private static final Font SUBTITLE1_FONT = new Font("Serif", Font.PLAIN, 16);
  private static final Font TITLE2_FONT = new Font("Serif", Font.PLAIN, 18);
  private static final Font SUBTITLE2_FONT = new Font("Serif", Font.PLAIN, 12);

  // Card Names
  private static final String ESSENTIAL_INFO_PANE = "Essential Info Pane";
  private static final String CHARACTER_POINT_PANE = "Character Point Pane";
  private static final String RANDOM_POINT_PANE = "Random Points";
  private static final String FAST_POINT_PANE = "Fast Points";
  private static final String FAMILY_RANKING_PANE = "Family Ranking Pane";
  private static final String PARENT_PANE = "Parent Pane";
  private static final String PARENT_TRAGEDY_PANE = "Parent Tragedy Pane";
  private static final String FAMILY_STATUS_PANE = "Family Status Pane";
  private static final String FAMILY_TRAGEDY_PANE = "Family Tragedy Pane";
  private static final String CHILDHOOD_ENVIRONMENT_PANE = "Childhood Environment Pane";
  private static final String SIBLINGS_PANE = "Siblings Pane";
  private static final String MOTIVATION_PANE = "Motivation Pane";
  private static final String ROLE_PANE = "Role Pane";

  private Player player;
  private JPanel contentPane;

  // Statistics
  private Map<String, CharacterPointsManager> managersByCardName;
  private String activePointManagerName;

  // Essential
  private JLabel portraitLabel;
  private JTextField aliasTextField;
  private JTextField ageTextField;
  private JComboBox<Gender> genderComboBox;

  // Family Background
  private JComboBox<String> familyRankingComboBox;
  private JRadioButton[] parentStatusRadioButtons;
  private JComboBox<String> parentTragedyComboBox;
  private JRadioButton[] familyStatusRadioButtons;
  private JComboBox<String> familyTragedyComboBox;
  private JComboBox<String> childhoodEnvironmentComboBox;

  // Siblings
  private int siblingCount;
  private JRadioButton[] siblingCountRadioButtons;
  private Map<Integer, JPanel> siblingPanelsByInteger;
  private Map<Integer, JTextField> siblingNameTextFieldsByInteger;
  private Map<Integer, JComboBox<Gender>> siblingGenderComboBoxesByInteger;
  private Map<Integer, JComboBox<RelativeAge>> siblingAgeComboBoxesByInteger;
  private Map<Integer, JComboBox<String>> siblingRelationshipComboBoxesByInteger;

  // Motivations
  private JComboBox<String> personalityComboBox;
  private JComboBox<String> valuedPersonComboBox;
  private JComboBox<String> valuedConceptComboBox;
  private JComboBox<String> feelingsTowardOthersComboBox;
  private JComboBox<String> valuedPosessionComboBox;

  // Roles
  private JComboBox<Role> roleComboBox;
  private Map<Role, List<JComboBox<String>>> comboBoxesByRole;

  /**
   * Constructs a JDialog that allows the manipulation of a <code>Player</code>'s stats and
   * background.
   * 
   * @param owner the Frame from which the dialog is displayed
   * @param title the String to display in the dialog's title bar
   * @param modal specifies whether dialog blocks user input to other top-level windows when shown.
   *        If true, the modality type property is set to DEFAULT_MODALITY_TYPE otherwise the dialog
   *        is modaless
   * @param player the provider of the <code>Statistic</code>s and owner of the user given
   *        background
   */
  public NewCharacterDialog(Frame owner, String title, boolean modal, Player player) {
    super(owner, title, modal);
    setPlayer(player);

    initializeAlias();
    initializeAge();
    initializeGender();
    initializePortrait();
    initializeFamilyRanking();
    initializeParentStatus();
    initializeParentTragedy();
    initializeFamilyStatus();
    initializeFamilyTragedy();
    initializeChildhoodEnvironment();
    initializeSiblings();
    initializeMotivations();
    initializeRoles();

    initializeContentPane();
  }

  private void setPlayer(Player player) {
    if (player == null) {
      this.player = new Player();
    } else {
      this.player = player;
    }
  }

  private void initializeAlias() {
    aliasTextField = new JTextField("UNKNOWN");
  }

  private void initializeAge() {
    ageTextField = new JTextField(Integer.toString(Player.MIN_AGE));
  }

  private void initializeGender() {
    genderComboBox = new JComboBox<Gender>(Gender.values());
    genderComboBox.addItemListener(evt -> {
      if (evt.getStateChange() == ItemEvent.SELECTED) {
        Gender item = (Gender) evt.getItem();
        player.setGender(item);
      }
    });

    genderComboBox.setSelectedIndex(0);
  }

  private void initializePortrait() {
    portraitLabel = new JLabel("PLACEHOLDER PORTRAIT");
  }

  private void initializeFamilyRanking() {
    familyRankingComboBox = new JComboBox<String>(new String[] { //
        "Corporate Executive", //
        "Corporate Manager", //
        "Corporate Technician", //
        "Nomad Pack", //
        "Pirate Fleet", //
        "Gang Family", //
        "Crime Lord", //
        "Combat Zone Poor", //
        "Urban Homeless", //
        "Arcology Family"});
    familyRankingComboBox.addItemListener(evt -> {
      if (evt.getStateChange() == ItemEvent.SELECTED) {
        String item = (String) evt.getItem();
        player.setFamilyRanking(item);
      }
    });

    familyRankingComboBox.setSelectedIndex(0);
  }

  private void initializeParentStatus() {
    parentStatusRadioButtons = new JRadioButton[2];

    parentStatusRadioButtons[0] = new JRadioButton("Both parents are living.");
    parentStatusRadioButtons[0].setActionCommand(FAMILY_STATUS_PANE);
    parentStatusRadioButtons[0].addActionListener(evt -> {
      player.setParentStatus("Both parents are living.");
      player.setParentTragedy(false);
    });

    parentStatusRadioButtons[1] =
        new JRadioButton("Something has happened to one or both parents.");
    parentStatusRadioButtons[1].setActionCommand(PARENT_TRAGEDY_PANE);
    parentStatusRadioButtons[1].addActionListener(evt -> {
      player.setParentStatus("Something has happened to one or both parents.");
      player.setParentTragedy(true);
    });

    parentStatusRadioButtons[0].setSelected(true);
  }

  private void initializeParentTragedy() {
    parentTragedyComboBox = new JComboBox<String>(new String[] { //
        "Your parent(s) died in warfare.", //
        "Your parent(s) died in an accident.", //
        "Your parent(s) were murdered.", //
        "Your parent(s) have amnesia and don't remember you.", //
        "You never knew your parent(s).", //
        "Your parent(s) are in hiding to protect you.", //
        "You were left with relatives for safekeeping.", //
        "You grew up on the Street and never had parents.", //
        "Your parent(s) gave you up for adoption.", //
        "Your parent(s) sold you for money."});
    parentTragedyComboBox.addItemListener(evt -> {
      if (evt.getStateChange() == ItemEvent.SELECTED) {
        String item = (String) evt.getItem();
        player.setParentTragedy(item);
      }
    });

    parentTragedyComboBox.setSelectedIndex(0);
  }

  private void initializeFamilyStatus() {
    familyStatusRadioButtons = new JRadioButton[2];

    familyStatusRadioButtons[0] = new JRadioButton(
        "Family status in danger, and you risk losing everything (if you haven't already).");
    familyStatusRadioButtons[0].setActionCommand(FAMILY_TRAGEDY_PANE);
    familyStatusRadioButtons[0].addActionListener(evt -> {
      player.setFamilyStatus(
          "Family status in danger, and you risk losing everything (if you haven't already).");
      player.setFamilyTragedy(true);
    });

    familyStatusRadioButtons[1] =
        new JRadioButton("Family status is OK, even if parents are missing or dead.");
    familyStatusRadioButtons[1].setActionCommand(CHILDHOOD_ENVIRONMENT_PANE);
    familyStatusRadioButtons[1].addActionListener(evt -> {
      player.setFamilyStatus("Family status is OK, even if parents are missing or dead.");
      player.setFamilyTragedy(false);
    });

    familyStatusRadioButtons[0].setSelected(true);
  }

  private void initializeFamilyTragedy() {
    familyTragedyComboBox = new JComboBox<String>(new String[] { //
        "Family lost everything through betrayal.", //
        "Family lost everything through bad management.", //
        "Family exiled or otherwise driven from their original home/nation/corporation.", //
        "Family is imprisoned and you alone escaped.", //
        "Family vanished. You are the only remaining member.", //
        "Family was murdered/killed and you were the only survivor.", //
        "Family is involved in a longterm conspiracy, organization or association, such as a crime"
            + " family or revolutionary group.", //
        "Your family was scattered to the winds due to misfortune.", //
        "Your family is cursed with a hereditary feud that has lasted for generations.", //
        "You are the inheritor of a family debt; you must honor this debt before moving on with"
            + " your life."});
    familyTragedyComboBox.addItemListener(evt -> {
      if (evt.getStateChange() == ItemEvent.SELECTED) {
        String item = (String) evt.getItem();
        player.setFamilyTragedy(item);
      }
    });

    familyTragedyComboBox.setSelectedIndex(0);
  }

  private void initializeChildhoodEnvironment() {
    childhoodEnvironmentComboBox = new JComboBox<String>(new String[] { //
        "Spent on the Street, with no adult supervision.", //
        "Spent in a safe Corporate Sububia.", //
        "In a Nomad Pack moving from town to town.", //
        "In a decaying, once upscale neighborhood.", //
        "In a defended Corporate Zone in the central City.", //
        "In the heart of the Combat Zone.", //
        "In a small village or town far from the City.", //
        "In a large arcoloty city.", //
        "In an aquatic Pirate Pack.", //
        "In a Corporate controlled Farm or Research Facility."});
    childhoodEnvironmentComboBox.addItemListener(evt -> {
      if (evt.getStateChange() == ItemEvent.SELECTED) {
        String item = (String) evt.getItem();
        player.setChildhoodEnvironment(item);
      }
    });

    childhoodEnvironmentComboBox.setSelectedIndex(0);
  }

  private void initializeSiblings() {
    siblingCount = 0;
    initializeSiblingsPanels();
    initializeSiblingsRadioButtons();
  }

  private void initializeSiblingsPanels() {
    int paddingWidth = 6;
    siblingPanelsByInteger = new HashMap<Integer, JPanel>();
    siblingNameTextFieldsByInteger = new HashMap<Integer, JTextField>();
    siblingGenderComboBoxesByInteger = new HashMap<Integer, JComboBox<Gender>>();
    siblingAgeComboBoxesByInteger = new HashMap<Integer, JComboBox<RelativeAge>>();
    siblingRelationshipComboBoxesByInteger = new HashMap<Integer, JComboBox<String>>();

    for (int i = 0; i < MAX_SIBLING_COUNT; i++) {
      final Integer key = new Integer(i);
      JPanel p = new JPanel(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.weightx = 1.0;
      gbc.gridy = 0;

      gbc.gridx = 0;
      JTextField nameTextField = new JTextField("UNKNOWN");
      JComponent c = createNameComponent(nameTextField);
      c.setBorder(BorderFactory.createEmptyBorder( //
          paddingWidth, //
          paddingWidth, //
          paddingWidth, //
          paddingWidth));
      p.add(c, gbc);
      siblingNameTextFieldsByInteger.put(key, nameTextField);

      gbc.gridx = 1;
      JComboBox<Gender> genderComboBox = new JComboBox<Gender>(Gender.values());
      c = createGenderComponent(genderComboBox);
      c.setBorder(BorderFactory.createEmptyBorder( //
          paddingWidth, //
          paddingWidth, //
          paddingWidth, //
          paddingWidth));
      p.add(c, gbc);
      siblingGenderComboBoxesByInteger.put(key, genderComboBox);

      gbc.gridx = 2;
      RelativeAge[] ageOptions = new RelativeAge[] { //
          RelativeAge.OLDER, //
          RelativeAge.YOUNGER, //
          RelativeAge.TWIN};
      Map<Integer, Integer> weightsByAgeIndex = new HashMap<Integer, Integer>(ageOptions.length);
      weightsByAgeIndex.put(0, 5);
      weightsByAgeIndex.put(1, 4);
      weightsByAgeIndex.put(2, 1);
      JComboBox<RelativeAge> ageComboBox = new JComboBox<RelativeAge>(ageOptions);
      c = createAgeComponent( //
          ageComboBox, //
          weightsByAgeIndex);
      c.setBorder(BorderFactory.createEmptyBorder( //
          paddingWidth, //
          paddingWidth, //
          paddingWidth, //
          paddingWidth));
      p.add(c, gbc);
      siblingAgeComboBoxesByInteger.put(key, ageComboBox);

      gbc.gridx = 3;
      JComboBox<String> relationshipComboBox = new JComboBox<String>(new String[] { //
          "Sibling dislikes you.", //
          "Sibling likes you.", //
          "Sibling neutral.", //
          "They worship you.", //
          "They hate you."});
      c = createRelationshipComponent(relationshipComboBox);
      c.setBorder(BorderFactory.createEmptyBorder( //
          paddingWidth, //
          paddingWidth, //
          paddingWidth, //
          paddingWidth));
      p.add(c, gbc);
      siblingRelationshipComboBoxesByInteger.put(key, relationshipComboBox);

      siblingPanelsByInteger.put(key, p);
    }
  }

  private JComponent createNameComponent(JTextField textField) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

    JLabel label = new JLabel("Name");
    label.setFont(TITLE2_FONT);
    label.setAlignmentX(CENTER_ALIGNMENT);
    panel.add(label);

    textField.setFont(TITLE2_FONT);
    panel.add(textField);

    return panel;
  }

  private JComponent createGenderComponent(JComboBox<Gender> comboBox) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

    panel.add( //
        createTitleComponent( //
            "Sex", TITLE2_FONT, //
            "Roll or choose one.", SUBTITLE2_FONT));

    panel.add(comboBox);

    JButton button = new JButton("Roll");
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    button.addActionListener(evt -> {
      int randomNum = ThreadLocalRandom.current().nextInt(0, comboBox.getItemCount());
      comboBox.setSelectedIndex(randomNum);
    });
    panel.add(button);

    return panel;
  }

  private JComponent createAgeComponent( //
      JComboBox<RelativeAge> comboBox, //
      Map<Integer, Integer> weightsByIndex) {

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

    panel.add( //
        createTitleComponent( //
            "Relative Age", TITLE2_FONT, //
            "Roll or choose one.", SUBTITLE2_FONT));

    panel.add(comboBox);

    JButton button = new JButton("Roll");
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    button.addActionListener(evt -> {
      int index = RandomNumberGenerator.getRandomWeightedIntegerFrom(weightsByIndex);
      comboBox.setSelectedIndex(index);
    });
    panel.add(button);

    return panel;
  }

  private JComponent createRelationshipComponent(JComboBox<String> comboBox) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

    panel.add( //
        createTitleComponent( //
            "Relationship", TITLE2_FONT, //
            "Roll or choose one.", SUBTITLE2_FONT));

    panel.add(comboBox);

    JButton button = new JButton("Roll");
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    button.addActionListener(evt -> {
      int randomNum = ThreadLocalRandom.current().nextInt(0, comboBox.getItemCount());
      comboBox.setSelectedIndex(randomNum);
    });
    panel.add(button);

    return panel;
  }

  private void initializeSiblingsRadioButtons() {
    siblingCountRadioButtons = new JRadioButton[MAX_SIBLING_COUNT + 1];

    siblingCountRadioButtons[0] = new JRadioButton("1 Sibling");
    siblingCountRadioButtons[0].addItemListener(evt -> setSiblingCount(1));

    siblingCountRadioButtons[1] = new JRadioButton("2 Siblings");
    siblingCountRadioButtons[1].addItemListener(evt -> setSiblingCount(2));

    siblingCountRadioButtons[2] = new JRadioButton("3 Siblings");
    siblingCountRadioButtons[2].addItemListener(evt -> setSiblingCount(3));

    siblingCountRadioButtons[3] = new JRadioButton("4 Siblings");
    siblingCountRadioButtons[3].addItemListener(evt -> setSiblingCount(4));

    siblingCountRadioButtons[4] = new JRadioButton("5 Siblings");
    siblingCountRadioButtons[4].addItemListener(evt -> setSiblingCount(5));

    siblingCountRadioButtons[5] = new JRadioButton("6 Siblings");
    siblingCountRadioButtons[5].addItemListener(evt -> setSiblingCount(6));

    siblingCountRadioButtons[6] = new JRadioButton("7 Siblings");
    siblingCountRadioButtons[6].addItemListener(evt -> setSiblingCount(7));

    siblingCountRadioButtons[7] = new JRadioButton("Only Child");
    siblingCountRadioButtons[7].addItemListener(evt -> setSiblingCount(0));

    siblingCountRadioButtons[0].setSelected(true);
  }

  private void setSiblingCount(int count) {
    siblingCount = count;

    for (int i = 0; i < siblingPanelsByInteger.size(); i++) {
      JPanel panel = siblingPanelsByInteger.get(new Integer(i));

      if (i < count) {
        panel.setVisible(true);
      } else {
        panel.setVisible(false);
      }
    }
  }

  // TODO(Coul Greer): Further initialize the motivation comboBoxes. Put each comboBox in its own
  // method where it has an item listener and sets the initial set value.
  private void initializeMotivations() {
    personalityComboBox = new JComboBox<String>(new String[] { //
        "Shy and secretive", //
        "Rebellious, antisocial, violent", //
        "Arrogant, proud and aloof", //
        "Moody, rash and headstrong", //
        "Picky, fussy and nervous", //
        "Stable and serious", //
        "Silly and fluffheaded", //
        "Sneaky and deceptive", //
        "Intellectual and detached", //
        "Friendly and outgoing"});

    valuedPersonComboBox = new JComboBox<String>(new String[] { //
        "A parent", //
        "Brother or sister", //
        "Lover", //
        "Friend", //
        "Yourself", //
        "A pet", //
        "Teacher or mentor", //
        "Public figure", //
        "A personal hero", //
        "No one"});

    valuedConceptComboBox = new JComboBox<String>(new String[] { //
        "Money", //
        "Honor", //
        "Your word", //
        "Honestly", //
        "Knowledge", //
        "Vengeance", //
        "Love", //
        "Power", //
        "Having a good time", //
        "Friendship"});

    feelingsTowardOthersComboBox = new JComboBox<String>(new String[] { //
        "Neutral", //
        "I like almost everyone", //
        "I hate almost everyone", //
        "People are tools", //
        "Everyone is a valuable individual", //
        "People are obstacles to be destroyed", //
        "People are untrustworthy", //
        "Wipe 'em all out", //
        "People are wonderful"});

    valuedPosessionComboBox = new JComboBox<String>(new String[] { //
        "A weapon", //
        "A tool", //
        "A piece of clothing", //
        "A photograph", //
        "A book or diary", //
        "A recording", //
        "A musical instrument", //
        "A piece of jewelry", //
        "A toy", //
        "A letter"});
  }

  private void initializeRoles() {
    Iterator<String> nameIterator = RoleFactory.createNameIterator();

    ArrayList<Role> roles = new ArrayList<Role>();
    comboBoxesByRole = new HashMap<Role, List<JComboBox<String>>>();
    while (nameIterator.hasNext()) {
      Role r = RoleFactory.createRole(nameIterator.next());
      roles.add(r);
      initializeRoleSkills(r);
    }

    roleComboBox = new JComboBox<Role>(roles.toArray(new Role[0]));
    roleComboBox.setFont(TITLE2_FONT);
    roleComboBox.addActionListener(evt -> player.setRole((Role) roleComboBox.getSelectedItem()));
    roleComboBox.setRenderer(new ListCellRenderer<Role>() {

      @Override
      public Component getListCellRendererComponent( //
          JList<? extends Role> list, //
          Role value, //
          int index, //
          boolean isSelected, //
          boolean cellHasFocus) {

        JLabel label = new JLabel(value.getName());
        label.setOpaque(true);
        if (isSelected) {
          label.setBackground(list.getSelectionBackground());
          label.setForeground(list.getSelectionForeground());
        } else {
          label.setBackground(list.getBackground());
          label.setForeground(list.getForeground());
        }

        return label;
      }

    });

    roleComboBox.setSelectedIndex(0);
  }

  private void initializeRoleSkills(Role role) {
    ArrayList<JComboBox<String>> comboBoxes = new ArrayList<JComboBox<String>>(Role.OPTION_COUNT);

    for (List<String> options : role.getSkillNameOptions()) {
      JComboBox<String> cb = new JComboBox<String>(options.toArray(new String[0]));
      cb.setEnabled(options.size() > 1);
      comboBoxes.add(cb);
    }

    comboBoxesByRole.put(role, comboBoxes);
  }

  private void initializeContentPane() {
    contentPane = new JPanel(new CardLayout());

    contentPane.add(createEssentialInfoContainer(), ESSENTIAL_INFO_PANE);
    contentPane.add(createCharacterPointContainer(), CHARACTER_POINT_PANE);
    contentPane.add(createFamilyRankingContainer(), FAMILY_RANKING_PANE);
    contentPane.add(createParentStatusContainer(), PARENT_PANE);
    contentPane.add(createParentTragedyContainer(), PARENT_TRAGEDY_PANE);
    contentPane.add(createFamilyStatusContainer(), FAMILY_STATUS_PANE);
    contentPane.add(createFamilyTragedyContainer(), FAMILY_TRAGEDY_PANE);
    contentPane.add(createChildhoodEnvironmentContainer(), CHILDHOOD_ENVIRONMENT_PANE);
    contentPane.add(createSiblingsContainer(), SIBLINGS_PANE);
    contentPane.add(createMotivationContainer(), MOTIVATION_PANE);
    contentPane.add(createRoleContainer(), ROLE_PANE);

    setContentPane(contentPane);
  }

  private Container createEssentialInfoContainer() {
    JButton nextButton = new JButton("Next =>>");
    nextButton.addActionListener(evt -> {
      if (!hasValidAlias()) {
        JOptionPane.showMessageDialog( //
            this, //
            "Make sure the alias is at least one character long.");
      } else if (!hasValidAge()) {
        JOptionPane.showMessageDialog( //
            this, //
            "Make sure the age is a number over " + Player.MIN_AGE);
      } else {
        switchCardTo(CHARACTER_POINT_PANE);
      }
    });

    return createNavigatableContainer( //
        "ESSENTIAL INFO", TITLE1_FONT, //
        "Provide the basic info.", SUBTITLE1_FONT, //
        createEssentialInfoContent(), //
        null, nextButton);
  }


  private Container createNavigatableContainer( //
      String title, Font titleFont, //
      String subtitle, Font subtitleFont, //
      JComponent component, //
      JButton leftButton, JButton rightButton) {

    int paddingWidth = 3;
    int dividerHeight = 1;
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder( //
        paddingWidth, //
        paddingWidth, //
        paddingWidth, //
        paddingWidth));

    JComponent titleComponent = createTitleComponent( //
        title, titleFont, //
        subtitle, subtitleFont);
    titleComponent.setBorder(BorderFactory.createCompoundBorder( //
        BorderFactory.createEmptyBorder( //
            paddingWidth, //
            paddingWidth, //
            0, //
            paddingWidth), //
        BorderFactory.createMatteBorder( //
            0, //
            0, //
            dividerHeight, //
            0, //
            Color.BLACK)));
    panel.add(titleComponent, BorderLayout.NORTH);

    component.setBorder(BorderFactory.createEmptyBorder( //
        paddingWidth, //
        paddingWidth, //
        paddingWidth, //
        paddingWidth));
    panel.add(component, BorderLayout.CENTER);

    JComponent naviComponent = createNavigationComponent( //
        leftButton, //
        rightButton);
    naviComponent.setBorder(BorderFactory.createCompoundBorder( //
        BorderFactory.createEmptyBorder( //
            0, //
            paddingWidth, //
            paddingWidth, //
            paddingWidth), //
        BorderFactory.createMatteBorder( //
            dividerHeight, //
            0, //
            0, //
            0, //
            Color.BLACK)));
    panel.add(naviComponent, BorderLayout.SOUTH);

    return panel;
  }

  private JComponent createTitleComponent( //
      String title, Font titleFont, //
      String subtitle, Font subtitleFont) {

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(titleFont);
    titleLabel.setAlignmentX(CENTER_ALIGNMENT);
    panel.add(titleLabel);

    JLabel subtitleLabel = new JLabel(subtitle);
    subtitleLabel.setFont(subtitleFont);
    subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
    panel.add(subtitleLabel);

    return panel;
  }

  private JComponent createEssentialInfoContent() {
    JPanel panel = new JPanel(new GridLayout(0, 2));

    panel.add(createPortraitComponent());
    panel.add(createEssentialTextInfoComponent());

    return panel;
  }

  private JComponent createPortraitComponent() {
    JPanel panel = new JPanel(new GridBagLayout());

    panel.add(portraitLabel);

    return panel;
  }

  private JComponent createEssentialTextInfoComponent() {
    int paddingWidth = 6;
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createEmptyBorder( //
        paddingWidth, //
        paddingWidth, //
        paddingWidth, //
        paddingWidth));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;

    gbc.gridx = 0;
    gbc.gridy = 0;
    JComponent c = createLabeledComponent("Alias", aliasTextField);
    c.setBorder(BorderFactory.createEmptyBorder( //
        paddingWidth, //
        paddingWidth, //
        paddingWidth, //
        paddingWidth));
    panel.add(c, gbc);

    gbc.gridy = 1;
    c = createAgeComponent();
    c.setBorder(BorderFactory.createEmptyBorder( //
        paddingWidth, //
        paddingWidth, //
        paddingWidth, //
        paddingWidth));
    panel.add(c, gbc);

    gbc.gridy = 2;
    c = createLabeledComponent("Gender", genderComboBox);
    c.setBorder(BorderFactory.createEmptyBorder( //
        paddingWidth, //
        paddingWidth, //
        paddingWidth, //
        paddingWidth));
    panel.add(c, gbc);

    return panel;
  }

  private JComponent createLabeledComponent(String title, JComponent component) {
    JPanel panel = new JPanel(new BorderLayout());

    JLabel label = new JLabel(title);
    label.setFont(TITLE2_FONT);
    panel.add(label, BorderLayout.NORTH);

    component.setFont(TITLE2_FONT);
    panel.add(component, BorderLayout.CENTER);

    return panel;
  }

  private JComponent createAgeComponent() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 0;

    gbc.gridx = 0;
    gbc.weightx = 1.0;
    panel.add(createLabeledComponent("Age", ageTextField), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.0;
    gbc.anchor = GridBagConstraints.SOUTH;
    JButton button = new JButton("Roll");
    button.addActionListener(evt -> {
      int result = (int) RandomNumberGenerator.getRandomDoubleFrom(new Die(2, 6));
      ageTextField.setText(Integer.toString(Player.MIN_AGE + result));
    });
    panel.add(button, gbc);

    return panel;
  }

  private boolean hasValidAlias() {
    String txt = aliasTextField.getText();
    return txt.length() >= Name.MIN_NAME_LENGTH;
  }

  private boolean hasValidAge() {
    String txt = ageTextField.getText();

    if (!txt.matches("\\d+")) {
      return false;
    } else if (Player.MIN_AGE > Integer.parseInt(txt)) {
      return false;
    } else {
      return true;
    }
  }

  private JComponent createNavigationComponent(JButton rightButton, JButton leftButton) {
    JPanel panel = new JPanel();

    if (!(rightButton == null)) {
      panel.add(rightButton);
    }

    if (!(leftButton == null)) {
      panel.add(leftButton);
    }

    return panel;
  }

  private Container createCharacterPointContainer() {
    JButton prevButton = new JButton("<<= Previous");
    prevButton.addActionListener(evt -> switchCardTo(ESSENTIAL_INFO_PANE));

    JButton nextButton = new JButton("Next =>>");
    nextButton.addActionListener(evt -> {
      CharacterPointsManager manager = managersByCardName.get(activePointManagerName);
      if (!manager.isValid()) {
        JOptionPane.showMessageDialog( //
            contentPane, //
            "Make sure all points are appropriately spent.");
      } else {
        switchCardTo(FAMILY_RANKING_PANE);
      }
    });

    return createNavigatableContainer( //
        "CHARACTER POINTS (CP)", TITLE1_FONT, //
        "Select a method of acquiring CP.", SUBTITLE1_FONT, //
        createCharacterPointContent(), //
        prevButton, nextButton);
  }

  private JComponent createCharacterPointContent() {
    JPanel panel = new JPanel(new BorderLayout());

    Container cards = createMethodCards();
    panel.add(createMethodSelectionComponent(cards), BorderLayout.NORTH);
    panel.add(cards, BorderLayout.CENTER);

    return panel;
  }

  private Container createMethodCards() {
    JPanel cards = new JPanel(new CardLayout());

    managersByCardName = new HashMap<String, CharacterPointsManager>();
    Iterable<Attribute> iterable =
        () -> player.createAttributeByTypeIterator(StatisticFactory.INDEPENDENT_ATTRIBUTE);
    List<Attribute> attributes = StreamSupport.stream(iterable.spliterator(), false) //
        .collect(Collectors.toList());
    cards.add(createRandomPointManager(attributes), RANDOM_POINT_PANE);
    cards.add(createFastPointManager(attributes), FAST_POINT_PANE);
    // TODO (Coul Greer): Create a component that allows the user to assign character points using
    // the cinematic method. This will allow the user to manual tell the manager how many points
    // they can spend.

    return cards;
  }

  private Component createRandomPointManager(List<Attribute> attributes) {
    RandomCharacterPointsManager manager = new RandomCharacterPointsManager(attributes);
    JPanel panel = new RandomCharacterPointsPane(manager);

    managersByCardName.put(RANDOM_POINT_PANE, manager);

    return panel;
  }

  private Component createFastPointManager(List<Attribute> attributes) {
    FastCharacterPointsManager manager = new FastCharacterPointsManager(attributes);
    JPanel panel = new FastCharacterPointsPane(manager);

    managersByCardName.put(FAST_POINT_PANE, manager);

    return panel;
  }

  private JComponent createMethodSelectionComponent(Container container) {
    JPanel panel = new JPanel();

    JComboBox<String> comboBox = new JComboBox<String>(new String[] { //
        RANDOM_POINT_PANE, //
        FAST_POINT_PANE});
    comboBox.addItemListener(evt -> {
      if (evt.getStateChange() == ItemEvent.SELECTED) {
        activePointManagerName = (String) evt.getItem();
        CardLayout layout = (CardLayout) container.getLayout();
        layout.show(container, activePointManagerName);

        if (FAST_POINT_PANE.equals(activePointManagerName)) {
          managersByCardName.get(activePointManagerName) //
              .rollPoints(new Die( //
                  StatisticFactory.INDEPENDENT_ATTRIBUTE_COUNT, //
                  10));
        } else if (RANDOM_POINT_PANE.equals(activePointManagerName)) {
          managersByCardName.get(activePointManagerName) //
              .rollPoints( //
                  CyberpunkAttribute.MIN_LEVEL * StatisticFactory.INDEPENDENT_ATTRIBUTE_COUNT, //
                  CyberpunkAttribute.MAX_LEVEL * StatisticFactory.INDEPENDENT_ATTRIBUTE_COUNT);
        }

      }
    });

    // Forces the item listener to be fired.
    comboBox.setSelectedItem(FAST_POINT_PANE);
    comboBox.setSelectedItem(RANDOM_POINT_PANE);
    panel.add(comboBox);

    return panel;
  }

  private Container createFamilyRankingContainer() {
    JButton prevButton = new JButton( //
        new SwitchToCardAction( //
            "<<= Previous", null, //
            null, null, //
            contentPane, CHARACTER_POINT_PANE));
    JButton nextButton = new JButton( //
        new SwitchToCardAction( //
            "Next =>>", null, //
            null, null, //
            contentPane, PARENT_PANE));

    return createNavigatableContainer( //
        "FAMILY RANKING", TITLE1_FONT, //
        "Choose one or roll.", SUBTITLE1_FONT, //
        createFamilyRankingContent(), //
        prevButton, nextButton);
  }

  private JComponent createFamilyRankingContent() {
    JPanel panel = new JPanel();

    panel.add(familyRankingComboBox);

    JButton randomButton = new JButton("Roll");
    randomButton.addActionListener(evt -> selectRandomItemFrom(familyRankingComboBox));
    panel.add(randomButton);

    return panel;
  }

  private void selectRandomItemFrom(JComboBox<String> comboBox) {
    int randomNum = ThreadLocalRandom.current().nextInt(0, comboBox.getItemCount());
    comboBox.setSelectedIndex(randomNum);
  }

  private Container createParentStatusContainer() {
    ButtonGroup group = new ButtonGroup();
    for (JRadioButton rb : parentStatusRadioButtons) {
      group.add(rb);
    }

    JButton prevButton = new JButton("<<= Previous");
    prevButton.addActionListener(evt -> switchCardTo(FAMILY_RANKING_PANE));

    JButton nextButton = new JButton("Next =>>");
    nextButton.addActionListener(evt -> switchCardTo(group));

    return createNavigatableContainer( //
        "PARENTS", TITLE1_FONT, //
        "Choose one or roll.", SUBTITLE1_FONT, //
        createParentStatusContent(), //
        prevButton, nextButton);
  }

  private JComponent createParentStatusContent() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

    panel.add(createRadioButtonContainer(parentStatusRadioButtons));

    JButton button = new JButton("Roll");
    Map<Integer, Integer> weightsByIndex =
        new HashMap<Integer, Integer>(parentStatusRadioButtons.length);
    weightsByIndex.put(0, 6);
    weightsByIndex.put(1, 4);
    button.addActionListener(evt -> {
      int index = RandomNumberGenerator.getRandomWeightedIntegerFrom(weightsByIndex);

      parentStatusRadioButtons[index].setSelected(true);
    });
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(button);

    return panel;
  }

  private Container createRadioButtonContainer(JRadioButton[] radioButtons) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

    for (JRadioButton rb : radioButtons) {
      panel.add(rb);
      rb.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    }

    return panel;
  }

  private void switchCardTo(String cardName) {
    CardLayout layout = (CardLayout) contentPane.getLayout();
    layout.show(contentPane, cardName);
  }

  private void switchCardTo(ButtonGroup group) {
    CardLayout layout = (CardLayout) contentPane.getLayout();
    String actionCommand = group.getSelection().getActionCommand();
    layout.show(contentPane, actionCommand);
  }

  private Container createParentTragedyContainer() {
    JButton prevButton = new JButton( //
        new SwitchToCardAction( //
            "<<= Previous", null, //
            null, null, //
            contentPane, PARENT_PANE));

    JButton nextButton = new JButton(//
        new SwitchToCardAction( //
            "Next =>>", null, //
            null, null, //
            contentPane, FAMILY_STATUS_PANE));

    return createNavigatableContainer( //
        "PARENT TRAGEDY", TITLE1_FONT, //
        "Choose one or roll.", SUBTITLE1_FONT, //
        createParentTragedyContent(), //
        prevButton, nextButton);
  }

  private JComponent createParentTragedyContent() {
    JPanel panel = new JPanel();

    panel.add(parentTragedyComboBox);

    JButton button = new JButton("Roll");
    button.addActionListener(evt -> selectRandomItemFrom(parentTragedyComboBox));
    panel.add(button);

    return panel;
  }

  private Container createFamilyStatusContainer() {
    ButtonGroup group = new ButtonGroup();
    for (JRadioButton rb : familyStatusRadioButtons) {
      group.add(rb);
    }

    JButton prevButton = new JButton("<<= Previous");
    prevButton.addActionListener(evt -> switchCardTo(PARENT_PANE));

    JButton nextButton = new JButton("Next =>>");
    nextButton.addActionListener(evt -> switchCardTo(group));

    return createNavigatableContainer( //
        "FAMILY STATUS", TITLE1_FONT, //
        "Choose one or roll.", SUBTITLE1_FONT, //
        createFamilyStatusContent(), //
        prevButton, nextButton);
  }

  private JComponent createFamilyStatusContent() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

    panel.add(createRadioButtonContainer(familyStatusRadioButtons));

    JButton button = new JButton("Roll");

    Map<Integer, Integer> weightsByIndex =
        new HashMap<Integer, Integer>(familyStatusRadioButtons.length);
    weightsByIndex.put(0, 6);
    weightsByIndex.put(1, 4);
    button.addActionListener(evt -> {
      int index = RandomNumberGenerator.getRandomWeightedIntegerFrom(weightsByIndex);

      familyStatusRadioButtons[index].setSelected(true);
    });
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(button);

    return panel;
  }

  private Container createFamilyTragedyContainer() {
    JButton prevButton = new JButton( //
        new SwitchToCardAction( //
            "<<= Previous", null, //
            null, null, //
            contentPane, FAMILY_STATUS_PANE));

    JButton nextButton = new JButton( //
        new SwitchToCardAction( //
            "Next =>>", null, //
            null, null, //
            contentPane, CHILDHOOD_ENVIRONMENT_PANE));

    return createNavigatableContainer( //
        "FAMILY TRAGEDY", TITLE1_FONT, //
        "Choose one or roll.", SUBTITLE1_FONT, //
        createFamilyTragedyContent(), //
        prevButton, nextButton);
  }

  private JComponent createFamilyTragedyContent() {
    JPanel panel = new JPanel();

    panel.add(familyTragedyComboBox);

    JButton randomButton = new JButton("Roll");
    randomButton.addActionListener(evt -> selectRandomItemFrom(familyTragedyComboBox));
    panel.add(randomButton);

    return panel;
  }

  private Container createChildhoodEnvironmentContainer() {
    JButton prevButton = new JButton( //
        new SwitchToCardAction( //
            "<<= Previous", null, //
            null, null, //
            contentPane, FAMILY_STATUS_PANE));

    JButton nextButton = new JButton( //
        new SwitchToCardAction( //
            "Next =>>", null, //
            null, null, //
            contentPane, SIBLINGS_PANE));

    return createNavigatableContainer( //
        "CHILDHOOD ENVIRONMENT", TITLE1_FONT, //
        "Your Childhood was (choose or roll one):", SUBTITLE1_FONT, //
        createChildhoodEnvironmentContent(), //
        prevButton, nextButton);
  }

  private JComponent createChildhoodEnvironmentContent() {
    JPanel panel = new JPanel();

    panel.add(childhoodEnvironmentComboBox);

    JButton button = new JButton("Roll");
    button.addActionListener(evt -> selectRandomItemFrom(childhoodEnvironmentComboBox));
    panel.add(button);

    return panel;
  }

  private Container createSiblingsContainer() {
    ButtonGroup group = new ButtonGroup();
    for (JRadioButton rb : siblingCountRadioButtons) {
      group.add(rb);
    }

    JButton prevButton = new JButton("<<= Previous");
    prevButton.addActionListener(evt -> switchCardTo(CHILDHOOD_ENVIRONMENT_PANE));

    JButton nextButton = new JButton("Next =>>");
    nextButton.addActionListener(evt -> {
      if (!hasValidSiblingNames()) {
        JOptionPane.showMessageDialog( //
            this, //
            "Make sure that all siblings have a name.", //
            "Invalid Sibling Present!", //
            JOptionPane.ERROR_MESSAGE);
      } else {
        switchCardTo(MOTIVATION_PANE);
      }
    });

    return createNavigatableContainer( //
        "SIBLINGS", TITLE1_FONT, //
        "You may have up to 7 brothers/sisters.", SUBTITLE1_FONT, //
        createSiblingsContent(), //
        prevButton, nextButton);
  }

  private JComponent createSiblingsContent() {
    JPanel panel = new JPanel(new BorderLayout());

    panel.add(createSiblingRadioButtonContainer(), BorderLayout.NORTH);
    panel.add(createSiblingForumContainer(), BorderLayout.CENTER);

    return panel;
  }

  private Container createSiblingRadioButtonContainer() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

    panel.add(createRadioButtonContainer(siblingCountRadioButtons));

    JButton button = new JButton("Roll");
    Map<Integer, Integer> weightsByIndex =
        new HashMap<Integer, Integer>(siblingCountRadioButtons.length);
    weightsByIndex.put(0, 1);
    weightsByIndex.put(1, 1);
    weightsByIndex.put(2, 1);
    weightsByIndex.put(3, 1);
    weightsByIndex.put(4, 1);
    weightsByIndex.put(5, 1);
    weightsByIndex.put(6, 1);
    weightsByIndex.put(7, 3);
    button.addActionListener(evt -> {
      int index = RandomNumberGenerator.getRandomWeightedIntegerFrom(weightsByIndex);

      siblingCountRadioButtons[index].setSelected(true);
    });
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(button);

    return panel;
  }

  private Container createSiblingForumContainer() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

    for (JPanel p : siblingPanelsByInteger.values()) {
      panel.add(p);
    }

    return new JScrollPane(panel);
  }

  private boolean hasValidSiblingNames() {
    for (int i = 0; i < siblingCount; i++) {
      String txt = siblingNameTextFieldsByInteger //
          .get(new Integer(i)) //
          .getText();

      if (txt.length() < Name.MIN_NAME_LENGTH) {
        return false;
      }
    }

    return true;
  }

  private Container createMotivationContainer() {
    JButton prevButton = new JButton("<<= Previous");
    prevButton.addActionListener(evt -> switchCardTo(SIBLINGS_PANE));

    JButton nextButton = new JButton("Next =>>");
    nextButton.addActionListener(evt -> switchCardTo(ROLE_PANE));

    return createNavigatableContainer( //
        "MOTIVATIONS", TITLE1_FONT, //
        "What makes you tick? Will you back up your friends or go for the main chance? What's"
            + " important to you?",
        SUBTITLE1_FONT, //
        createMotivationsContent(), //
        prevButton, nextButton);
  }

  private JComponent createMotivationsContent() {
    JPanel panel = new JPanel(new GridLayout(0, 1));
    int paddingWidth = 6;

    // TODO (Coul Greer): Beautify this component. Also, add an empty border with a matte border
    // inside to seperate title from content.
    JComponent c = createPersonalityTraitComponent();
    c.setBorder(BorderFactory.createEmptyBorder( //
        paddingWidth, //
        paddingWidth, //
        paddingWidth, //
        paddingWidth));
    panel.add(c);

    c = createValuedPersonComponent();
    c.setBorder(BorderFactory.createEmptyBorder( //
        paddingWidth, //
        paddingWidth, //
        paddingWidth, //
        paddingWidth));
    panel.add(c);

    c = createValuedConceptComponent();
    c.setBorder(BorderFactory.createEmptyBorder( //
        paddingWidth, //
        paddingWidth, //
        paddingWidth, //
        paddingWidth));
    panel.add(c);

    c = createSocietyFeelingsComponent();
    c.setBorder(BorderFactory.createEmptyBorder( //
        paddingWidth, //
        paddingWidth, //
        paddingWidth, //
        paddingWidth));
    panel.add(c);

    c = createValuedPosessionComponent();
    c.setBorder(BorderFactory.createEmptyBorder( //
        paddingWidth, //
        paddingWidth, //
        paddingWidth * 2, //
        paddingWidth));
    panel.add(c);

    return new JScrollPane(panel);
  }

  private JComponent createPersonalityTraitComponent() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 0;

    gbc.gridx = 0;
    gbc.weightx = 1.0;
    panel.add(createLabeledComponent("Personality Traits", personalityComboBox), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.0;
    gbc.anchor = GridBagConstraints.SOUTH;
    JButton button = new JButton("Roll");
    button.addActionListener(evt -> selectRandomItemFrom(personalityComboBox));
    panel.add(button, gbc);

    return panel;
  }

  private JComponent createValuedPersonComponent() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 0;

    gbc.gridx = 0;
    gbc.weightx = 1.0;
    panel.add(createLabeledComponent("Person You Value Most", valuedPersonComboBox), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.0;
    gbc.anchor = GridBagConstraints.SOUTH;
    JButton button = new JButton("Roll");
    button.addActionListener(evt -> selectRandomItemFrom(valuedPersonComboBox));
    panel.add(button, gbc);

    return panel;
  }

  private JComponent createValuedConceptComponent() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 0;

    gbc.gridx = 0;
    gbc.weightx = 1.0;
    panel.add(createLabeledComponent("What Do You Value Most?", valuedConceptComboBox), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.0;
    gbc.anchor = GridBagConstraints.SOUTH;
    JButton button = new JButton("Roll");
    button.addActionListener(evt -> selectRandomItemFrom(valuedConceptComboBox));
    panel.add(button, gbc);

    return panel;
  }

  private JComponent createSocietyFeelingsComponent() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 0;

    gbc.gridx = 0;
    gbc.weightx = 1.0;
    panel.add(createLabeledComponent("How Do You Feel About People?", feelingsTowardOthersComboBox),
        gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.0;
    gbc.anchor = GridBagConstraints.SOUTH;
    JButton button = new JButton("Roll");
    button.addActionListener(evt -> selectRandomItemFrom(feelingsTowardOthersComboBox));
    panel.add(button, gbc);

    return panel;
  }

  private JComponent createValuedPosessionComponent() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 0;

    gbc.gridx = 0;
    gbc.weightx = 1.0;
    panel.add(createLabeledComponent("Your Most Valued Posession", valuedPosessionComboBox), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.0;
    gbc.anchor = GridBagConstraints.SOUTH;
    JButton button = new JButton("Roll");
    button.addActionListener(evt -> selectRandomItemFrom(valuedPosessionComboBox));
    panel.add(button, gbc);

    return panel;
  }

  private Container createRoleContainer() {
    JButton prevButton = new JButton("<<= Previous");
    prevButton.addActionListener(evt -> switchCardTo(MOTIVATION_PANE));

    JButton doneButton = new JButton("Done!");
    doneButton.addActionListener(evt -> {
      updatePlayer();
      dispose();
    });

    return createNavigatableContainer( //
        "ROLES", TITLE1_FONT, //
        "The core of CYBERPUNK Role-playing", SUBTITLE1_FONT, //
        createRoleContent(), //
        prevButton, doneButton);
  }

  private JComponent createRoleContent() {
    JPanel panel = new JPanel(new BorderLayout());

    panel.add(createRoleSelectorComponent(), BorderLayout.NORTH);
    panel.add(createRoleSkillsComponent(), BorderLayout.CENTER);

    return panel;
  }

  private JComponent createRoleSelectorComponent() {
    JPanel panel = new JPanel(new GridBagLayout());

    panel.add(roleComboBox);

    return panel;
  }

  private JComponent createRoleSkillsComponent() {
    JPanel panel = new JPanel(new CardLayout());

    for (int i = 0; i < roleComboBox.getItemCount(); i++) {
      Role r = roleComboBox.getItemAt(i);
      List<JComboBox<String>> comboBoxes = comboBoxesByRole.get(r);
      JPanel p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

      for (int j = 0; j < comboBoxes.size(); j++) {
        JComboBox<String> cb = comboBoxes.get(j);
        cb.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        p.add(createRoleSkillContainer("Option " + (j + 1) + ":", cb));
      }

      panel.add(p, r.getName());
    }

    roleComboBox.addActionListener(evt -> {
      Role r = (Role) roleComboBox.getSelectedItem();
      CardLayout layout = (CardLayout) panel.getLayout();
      layout.show(panel, r.getName());
    });

    return panel;
  }

  private Container createRoleSkillContainer(String title, JComboBox<String> comboBox) {
    int paddingWidth = 3;
    JPanel panel = new JPanel(new GridLayout(0, 2));

    JLabel label = new JLabel(title, SwingConstants.RIGHT);
    label.setFont(SUBTITLE2_FONT);
    panel.add(label);

    comboBox.setFont(SUBTITLE2_FONT);
    comboBox.setBorder(BorderFactory.createEmptyBorder( //
        paddingWidth, //
        paddingWidth, //
        paddingWidth, //
        paddingWidth));
    panel.add(comboBox);

    return panel;
  }

  private void updatePlayer() {
    setPlayerAlias();
    setPlayerAge();
    addPlayerSiblings();
    setPlayerRole();
  }

  private void setPlayerAlias() {
    String str = aliasTextField.getText();
    player.setAlias(new Name(str));
  }

  private void setPlayerAge() {
    String str = ageTextField.getText();
    player.setAge(new Age(Integer.parseInt(str)));
  }

  private void addPlayerSiblings() {
    for (int i = 0; i < siblingCount; i++) {
      final Integer key = new Integer(i);
      JTextField nameTextField = siblingNameTextFieldsByInteger.get(key);
      JComboBox<Gender> genderComboBox = siblingGenderComboBoxesByInteger.get(key);
      JComboBox<RelativeAge> ageComboBox = siblingAgeComboBoxesByInteger.get(key);
      JComboBox<String> relationshipComboBox = siblingRelationshipComboBoxesByInteger.get(key);

      player.addSibling(new Sibling( //
          nameTextField.getText(), //
          (Gender) genderComboBox.getSelectedItem(), //
          (RelativeAge) ageComboBox.getSelectedItem(), //
          (String) relationshipComboBox.getSelectedItem()));
    }
  }

  private void setPlayerRole() {
    Role r = (Role) roleComboBox.getSelectedItem();
    List<JComboBox<String>> comboBoxes = comboBoxesByRole.get(r);
    String[] skillNames = new String[Role.OPTION_COUNT];
    for (int i = 0; i < comboBoxes.size(); i++) {
      skillNames[i] = (String) comboBoxes.get(i).getSelectedItem();
    }
    player.setCareerSkillNames(Arrays.asList(skillNames));
  }

}
