package ru.practicum.server.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Item item1;
    private Item item2;
    private ItemRequest itemRequest;


    @BeforeEach
    void add() {
        user = new User(null, "Jack", "Jacks@mail.ru");

        userRepository.save(user);

        itemRequest = new ItemRequest(null, "description", user, LocalDateTime.now());

        itemRequestRepository.save(itemRequest);

        item1 = new Item(null, "Podik", "description", true, user,
                itemRequest);

        itemRepository.save(item1);

        item2 = new Item(null, "Telephone", "description", true, user,
                itemRequest);

        itemRepository.save(item2);
    }

    @Test
    void search() {
        List<Item> expected1 = List.of(item1, item2);

        List<Item> response1 = itemRepository
                .findAllByOwnerIdOrderByIdAsc(user.getId()).stream().toList();
        assertEquals(expected1, response1);
        assertEquals(2, response1.size());

        List<Item> expected2 = List.of(item2);

        List<Item> response2 = itemRepository
                .findByQuery("Telephone").stream().toList();
        assertEquals(expected2, response2);
        assertEquals(1, response2.size());

        List<Item> expected3 = List.of(item1, item2);

        List<Item> response3 = itemRepository
                .findByRequestId(itemRequest.getId());
        assertEquals(expected3, response3);
        assertEquals(2, response3.size());
    }

}
