package ru.practicum.server.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.service.ItemRequestService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createItemRequest() throws Exception {
        Long userId = 1L;
        List<ItemDto> items = new ArrayList<>();

        ItemRequestDto dtoShortRequestShort = new ItemRequestDto(userId, "description",
                null, null);

        ItemRequestDto dtoResponse = new ItemRequestDto(userId, "description", null, items);

        Mockito.when(itemRequestService.createItemRequest(Mockito.any(), Mockito.any())).thenReturn(dtoResponse);

        String result = mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dtoShortRequestShort)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(dtoResponse), result);
    }

    @Test
    void getAllItemsRequests() throws Exception {
        Integer from = 0;
        Integer size = 20;
        Long userId = 1L;

        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description",
                null, List.of());

        Mockito.when(itemRequestService.getAllItemsRequests())
                .thenReturn(List.of(itemRequestDto));

        String result = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(itemRequestDto)), result);
    }

    @Test
    void getAllByRequester() throws Exception {
        Integer from = 0;
        Integer size = 20;
        Long userId = 1L;

        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description",
                null, List.of());

        Mockito.when(itemRequestService.getItemsRequestsForUser(Mockito.anyLong()))
                .thenReturn(List.of(itemRequestDto));

        String result = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(itemRequestDto)), result);
    }

    @Test
    void getItemRequest() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;

        ItemRequestDto itemRequestDtoLong = new ItemRequestDto(1L, "description",
                null, List.of());
        Mockito.when(itemRequestService.getItemRequest(Mockito.anyLong())).thenReturn(itemRequestDtoLong);

        String result = mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemRequestDtoLong), result);
    }

}
