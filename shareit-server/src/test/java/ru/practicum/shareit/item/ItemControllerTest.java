package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;

    private CommentDto commentDto;

    @BeforeEach
    void create() {
        itemDto = new ItemDto(1L, "Jack", "description", true, null);

        commentDto = new CommentDto(1L, "Lena's comment", "Lena", LocalDateTime.now());
    }

    @Test
    void createWithValidUser() throws Exception {
        Mockito.when(itemService.addItem(Mockito.any(), Mockito.any())).thenReturn(itemDto);

        String result = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemDto), result);
    }

    @Test
    void createItem() throws Exception {
        Mockito.when(itemService.addItem(Mockito.any(), Mockito.any())).thenReturn(itemDto);

        String result = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemDto), result);
    }

    @Test
    void updateItem() throws Exception {
        Long itemId = 1L;

        Mockito.when(itemService.editItem(Mockito.anyLong(), Mockito.any(), Mockito.anyLong()))
                .thenReturn(itemDto);

        String result = mvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemDto), result);
    }

    @Test
    void createComment() throws Exception {
        Long id = 1L;

        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.any(), Mockito.anyLong()))
                .thenReturn(commentDto);

        String result = mvc.perform(post("/items/{id}/comment", id)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(commentDto), result);
    }

    @Test
    void getAll() throws Exception {
        Integer from = 0;
        Integer size = 20;
        Long userId = 1L;

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getByUserId() throws Exception {
        Long userId = 1L;

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getById() throws Exception {
        Long itemId = 1L;

        Mockito.when(itemService.getItem(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(new BookingItemDto(1L, "Jack", "description", true, null, null, List.of()));

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void searchWithEmptyText() throws Exception {
        Integer from = 0;
        Integer size = 20;

        String result = mvc.perform(get("/items/search")
                        .param("text", "")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of()), result);
    }

}
