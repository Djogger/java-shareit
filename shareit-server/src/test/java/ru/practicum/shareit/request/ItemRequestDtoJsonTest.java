package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;


    @Test
    void testItemRequestDtoSerialize() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", LocalDateTime.of(2025, 7, 9, 0, 0, 0), List.of());

        JsonContent<ItemRequestDto> jsonContext = json.write(itemRequestDto);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        assertThat(jsonContext).hasJsonPath("$.id");
        assertThat(jsonContext).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestDto.getId().intValue());
        assertThat(jsonContext).hasJsonPath("$.description");
        assertThat(jsonContext).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
        assertThat(jsonContext).hasJsonPath("$.created");
        assertThat(jsonContext).extractingJsonPathStringValue("$.created").isEqualTo(itemRequestDto.getCreated().format(formatter));

        ItemRequestDto itemRequestDtoForTest = json.parseObject(jsonContext.getJson());

        assertThat(itemRequestDtoForTest).isEqualTo(itemRequestDto);
    }

}
