package classes;

public enum MonsterCategory{

    Goblin("Goblin"),
    Dragon("Dragon"),
    Wizard("Wizard"),
    Ork("Ork"),
    Knight("Knight"),
    Troll("Troll"),
    Kraken("Kraken"),
    FireElf("FireElf"),
    Spell("Spell"),
    magicdice("MagicDice");

    private final String monsterCategoryName;

    MonsterCategory(String monsterCategoryName) {
        this.monsterCategoryName = monsterCategoryName;
    }

    public String getMonsterCategoryName() {
        return this.monsterCategoryName;
    }
}

