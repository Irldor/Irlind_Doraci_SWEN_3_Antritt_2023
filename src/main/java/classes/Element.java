package classes;

public enum Element {

    Water("Water"),
    Fire("Fire"),
    Normal("Normal");

    private final String elementName;

    Element(String elementName) {
        this.elementName = elementName;
    }

    public String getElementName() {
        return this.elementName;
    }
}

