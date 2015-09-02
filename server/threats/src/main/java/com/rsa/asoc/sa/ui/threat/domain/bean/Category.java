package com.rsa.asoc.sa.ui.threat.domain.bean;

/**
 * Represents list of <a href="http://veriscommunity.net/actions.html">VERIS</a> categories and subcategories
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public class Category {

    private String parent;

    private String name;

    public Category() {
    }

    public Category(String parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Category category = (Category) obj;

        if (!parent.equals(category.parent)) {
            return false;
        }
        return name.equals(category.name);

    }

    @Override
    public int hashCode() {
        int result = parent.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

}
