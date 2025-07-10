package ru.practicum.server.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.request.service.ItemRequestServiceImpl;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class ItemRequestServiceImplTest {

    private final ItemRequestServiceImpl itemRequestService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    private ItemRequestDto itemRequestDto;
    private Long userId;
    private Long requestId;
    private LocalDateTime created;

    @BeforeEach
    void beforeEach() {
        created = LocalDateTime.now();

        User user = new User(null, "Jack", "Jack@mail.ru");
        user = userRepository.save(user);
        userId = user.getId();

        ItemRequest itemRequest = new ItemRequest(null, "description", user, created);
        itemRequest = itemRequestRepository.save(itemRequest);
        requestId = itemRequest.getId();

        itemRequestDto = new ItemRequestDto(requestId, "description", created, List.of());
    }

    @Test
    void create() {
        ItemRequestDto result = itemRequestService.createItemRequest(userId, itemRequestDto);

        assertEquals(requestId, result.getId());
        assertEquals("description", result.getDescription());
    }

    @Test
    void getAllItemsRequests() {
        List<ItemRequestDto> result = itemRequestService.getAllItemsRequests();

        assertEquals(1, result.size());
    }

    @Test
    void getItemsRequestsForUser() {
        List<ItemRequestDto> result = itemRequestService.getItemsRequestsForUser(userId);
        List<ItemRequestDto> dtoList = List.of(itemRequestDto);

        assertEquals(dtoList.get(0).getId(), result.get(0).getId());
        assertEquals(dtoList.get(0).getDescription(), result.get(0).getDescription());
        assertEquals(dtoList.get(0).getItems().size(), result.get(0).getItems().size());
    }

    @Test
    void getItemRequest() {
        ItemRequestDto result = itemRequestService.getItemRequest(requestId);

        assertEquals(requestId, result.getId());
        assertEquals("description", result.getDescription());
    }

}
