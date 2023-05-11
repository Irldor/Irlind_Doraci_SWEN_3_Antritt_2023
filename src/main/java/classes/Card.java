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
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor that initializes a card with id, name, damage points, monster type, and element type.
     *
     * @param inputId    the unique id for the card
     * @param inputName         the name of the card
     * @param inputDamage  the amount of damage this card can inflict
     * @param inputMonsterCategory the monster type associated with this card
     * @param inputElementType   the element type associated with this card
     */
// This is the constructor for the Card class. It sets the properties of the card.
    public Card(String inputId, String inputName, float inputDamage, MonsterCategory inputMonsterCategory, Element inputElementType) {
        setId(inputId);
        setName(inputName);
        setDamage(inputDamage);
        setMonsterCategory(inputMonsterCategory);
        setElement(inputElementType);
    }

    // Methods to set all the specific attributes
    private void setId(String id) {
        this.id = id;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setDamage(float damage) {
        this.damage = damage;
    }

    private void setMonsterCategory(MonsterCategory monsterCategory) {
        this.monsterCategory = monsterCategory;
    }

    private void setElement(Element element) {
        this.element = element;
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
