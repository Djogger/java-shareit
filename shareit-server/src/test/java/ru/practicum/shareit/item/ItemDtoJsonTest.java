package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDtoSerialize() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Telephone", "Calling", true, null);

        JsonContent<ItemDto> jsonContent = json.write(itemDto);

        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(jsonContent).hasJsonPath("$.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(jsonContent).hasJsonPath("$.description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(jsonContent).hasJsonPath("$.available");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(jsonContent).hasJsonPath("$.requestId");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(null);

    }

}
