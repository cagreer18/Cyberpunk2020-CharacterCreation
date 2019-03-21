package rpg.cyberpunk._2020.stats;

public class GetSkillVisitor implements SkillVisitor {
	private String targetSkillName;

	public GetSkillVisitor(String targetSkillName) {
		this.targetSkillName = targetSkillName;
	}

	public CyberpunkSkill visit(SpecializedSkill skill) {
		if (targetSkillName == skill.getName()) {
			return skill;
		} else {
			return NullSkill.getInstance();
		}
	}

	public CyberpunkSkill visit(BroadSkill skill) {
		if (targetSkillName == skill.getName()) {
			return skill;
		} else {
			return skill.getChild(targetSkillName);
		}
	}

	public CyberpunkSkill visit(RoleSkill skill) {
		if (targetSkillName == skill.getName()) {
			return skill;
		} else {
			return NullSkill.getInstance();
		}
	}
}
