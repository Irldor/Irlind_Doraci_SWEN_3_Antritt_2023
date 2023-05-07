package classes;


/**
 * This class represents a card object that contains various properties such as
 * id, name, damage points, monster type, and element type.
 */
public class Card {

    // Unique id for the card
    private String id;

    // The name of the card
    private String name;

    // The amount of damage this card can inflict
    private float damage;

    // The monster type associated with this card
    private MonsterCategory monsterCategory;

    // The element type associated with this card
    private Element element;

    public Card(){

    }
    /**
     * Constructor that initializes a card with id, name, and damage points.
     *
     * @param id the unique id for the card
     * @param name      the name of the card
     * @param damage the amount of damage this card can inflict
     */
    public Card(String id, String name, float damage) {
        this.id = id;
        this.name = name;
        this.damage = damage;
    }

    /**
     * Constructor that initializes a card with id, name, damage points, monster type, and element type.
     *
     * @param id    the unique id for the card
     * @param name         the name of the card
     * @param damage  the amount of damage this card can inflict
     * @param monsterCategory the monster type associated with this card
     * @param elementType   the element type associated with this card
     */
    public Card(String id, String name, float damage, MonsterCategory monsterCategory, Element elementType) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.monsterCategory = monsterCategory;
        this.element = elementType;
    }

    // Getter methods for each private attribute

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getDamage() {
        return damage;
    }

    public MonsterCategory getMonsterCategory() {
        return monsterCategory;
    }

    public Element getElementType() {
        return element;
    }
}
