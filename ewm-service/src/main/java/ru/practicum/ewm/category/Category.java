package ru.practicum.ewm.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "category_name", nullable = false)
    private String name;

    @Override
    public String toString() {
        return String.format("Category = {id: %d, name: %s}", id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return  true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return this.getId().equals(category.getId())
                && this.getName().equals(category.getName());
    }

    @Override
    public int hashCode() {
        return id.hashCode() + name.hashCode();
    }
}


