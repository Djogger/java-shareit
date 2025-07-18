package ru.practicum.gateway.item;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.item.dto.CommentRequestDto;
import ru.practicum.gateway.item.dto.ItemDto;
import jakarta.validation.Valid;

import java.util.Collections;

@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @NotNull @Valid ItemDto requestDto) {
        log.info("Create Item");
        return itemClient.createItem(userId, requestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto requestDto,
                                             @PathVariable Long id,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Update Item with Id: {}", id);
        return itemClient.updateItem(requestDto, id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get All Items {}", userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Long id,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get Item with Id: {}", id);
        return itemClient.getItem(id, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long id) {
        log.info("Delete Item with Id: {}", id);
        return itemClient.deleteItem(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Search by Name: {}", text);
        if (text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.searchItem(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody CommentRequestDto requestDto) {
        log.info("Adding a comment to Item with Id: {}", itemId);
        return itemClient.createComment(itemId, userId, requestDto);
    }

}
