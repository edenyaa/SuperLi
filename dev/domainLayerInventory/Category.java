package domainLayerInventory;

import java.util.ArrayList;
import java.util.List;
public class Category {
    private String name;
    private Category parentCategory;
    private List<Category> subCategories;
    public Category(String name) {
        this.name = name;
        this.subCategories = new ArrayList<>();
        this.parentCategory = null;
    }

    public Category(String name, Category parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
        this.subCategories = new ArrayList<>();
        if (parentCategory != null) {
            parentCategory.addSubCategory(this);
        }
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    // --- Setters ---
    public void setName(String name) {
        this.name = name;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    // --- Utility Methods ---
    public void addSubCategory(Category subCategory) {
        if (!subCategories.contains(subCategory)) {
            subCategories.add(subCategory);
            subCategory.setParentCategory(this);
        }
    }

    public void removeSubCategory(Category subCategory) {
        if (subCategories.contains(subCategory)) {
            subCategories.remove(subCategory);
            subCategory.setParentCategory(null);
        }
    }

    public boolean isASubCategory(Category subCategory) {
        if (subCategories == null || subCategories.isEmpty()) {
            return false;
        }
        for (Category sub : subCategories) {
            if (sub.equals(subCategory)) {
                return true;
            }
            if (sub.isASubCategory(subCategory)) { // Recursive check
                return true;
            }
        }
        return false;
    }

    public String getFullPath() {
        if (parentCategory == null) return name;
        return parentCategory.getFullPath() + "->" + name;
    }

    public Category getChildByName(String name) {
        for (Category child : subCategories) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", parentCategory=" + (parentCategory != null ? parentCategory.getName() : "None") +
                ", subCategories=" + subCategories.stream().map(Category::getName).toList() +
                '}';
    }

    @Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Category category = (Category) o;
    return name.equalsIgnoreCase(category.name);
}

}
