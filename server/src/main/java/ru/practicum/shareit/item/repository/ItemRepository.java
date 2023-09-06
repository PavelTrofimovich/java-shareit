package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findAllByOwnerId(Integer id, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE lower(i.name) LIKE lower(concat('%', ?1, '%')) OR" +
            " lower(i.description) like lower(concat('%', ?1, '%')) AND i.available = true")
    Page<Item> findAllItemsByNameOrDescriptionContainingIgnoreCase(String text, Pageable pageable);

    List<Item> findAllByItemRequestIn(List<ItemRequest> requestList);

    List<Item> findAllByItemRequestId(Integer requestId);
}