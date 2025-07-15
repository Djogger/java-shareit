package ru.practicum.server.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.item.dto.BookingItemDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.request.service.ItemRequestService;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemServiceImplTest {
    private final EntityManager entityManager;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;

    @Test
    void addItem() {
        UserDto userDto = userService.createUser(new UserDto(1L, "Jack", "Jack@mail.ru"));

        ItemDto itemDto = itemService.addItem(new ItemDto(null, "Item", "Item", true, null), userDto.getId());

        TypedQuery<Item> query = entityManager.createQuery("SELECT i from Item i WHERE i.id = :id", Item.class);

        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void getAllItems() {
        UserDto userDto = userService.createUser(new UserDto(1L, "Jack", "Jack@mail.ru"));

        itemService.addItem(new ItemDto(null, "Item1", "Item", true, null), userDto.getId());
        itemService.addItem(new ItemDto(null, "Item2", "Item", true, null), userDto.getId());
        itemService.addItem(new ItemDto(null, "Item3", "Item", true, null), userDto.getId());

        List<BookingItemDto> items = itemService.getAllItems(userDto.getId());

        assertThat(3, equalTo(items.size()));
    }

    @Test
    void getItem() {
        UserDto userDto = userService.createUser(new UserDto(1L, "Jack", "Jack@mail.ru"));

        ItemDto itemDto = itemService.addItem(new ItemDto(null, "Item1", "Item", true, null), userDto.getId());

        BookingItemDto bookingItem = itemService.getItem(itemDto.getId(), userDto.getId());

        assertThat(bookingItem.getId(), equalTo(itemDto.getId()));
    }

    @Test
    void searchForItemByQuery() {
        String query = "TELEPHONE";

        UserDto userDto = userService.createUser(new UserDto(1L, "Jack", "Jack@mail.ru"));

        itemService.addItem(new ItemDto(null, "Telephone", "Item", true, null), userDto.getId());
        itemService.addItem(new ItemDto(null, "TelePHone", "Item", true, null), userDto.getId());

        List<ItemDto> items = itemService.searchForItem(query);

        for (ItemDto item : items) {
            assertThat(item.getName().toLowerCase(), equalTo(query.toLowerCase()));
        }
    }

    @Test
    void editItem() {
        UserDto userDto = userService.createUser(new UserDto(1L, "Jack", "Jack@mail.ru"));

        ItemDto itemDto = itemService.addItem(new ItemDto(null, "Item1", "Item", true, null), userDto.getId());

        ItemDto editedItemDto = itemService.editItem(itemDto.getId(), new ItemDto(null, "Item55", "It was item1", true, null), userDto.getId());

        assertThat(itemDto.getName(), not(editedItemDto.getName()));
        assertThat(itemDto.getDescription(), not(editedItemDto.getDescription()));
    }

}
