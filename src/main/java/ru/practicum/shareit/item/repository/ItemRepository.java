package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByOwnerId(Integer id);

    @Query("SELECT i FROM Item i WHERE lower(i.name) LIKE lower(concat('%', ?1, '%')) OR" +
            " lower(i.description) like lower(concat('%', ?1, '%')) AND i.available = true")
    List<Item> findAllItemsByNameOrDescriptionContainingIgnoreCase(String text);
}
