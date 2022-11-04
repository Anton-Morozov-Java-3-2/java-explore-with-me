package ru.practicum.ewm.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.ewm.event.dto.Location;
import ru.practicum.ewm.event.dto.NewEventDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class NewEventDtoJsonTest {

    @Autowired
    private JacksonTester<NewEventDto> jsonNewEventDto;

    @Test
    void testSerialize() throws Exception {
        var newEventDto = new NewEventDto("test test test test test test",
                1L,
                "test test test test test test",
                LocalDateTime.now().withNano(0),
                new Location(5.245, 0.045),
                true, 5, false,
                "test test");

        var resultDto = jsonNewEventDto.write(newEventDto);
        assertThat(resultDto).hasJsonPath("$.annotation");
        assertThat(resultDto).hasJsonPath("$.category");
        assertThat(resultDto).hasJsonPath("$.description");
        assertThat(resultDto).hasJsonPath("$.eventDate");
        assertThat(resultDto).hasJsonPath("$.location");
        assertThat(resultDto).hasJsonPath("$.paid");
        assertThat(resultDto).hasJsonPath("$.participantLimit");
        assertThat(resultDto).hasJsonPath("$.requestModeration");
        assertThat(resultDto).hasJsonPath("$.title");

        assertThat(resultDto).extractingJsonPathStringValue("$.annotation").isEqualTo(newEventDto
                .getAnnotation());

        assertThat(resultDto).extractingJsonPathNumberValue("$.category").isEqualTo(newEventDto
                .getCategory().intValue());

        assertThat(resultDto).extractingJsonPathStringValue("$.description").isEqualTo(newEventDto
                .getDescription());

        assertThat(resultDto).extractingJsonPathStringValue("$.eventDate").isEqualTo(newEventDto
                .getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        assertThat(resultDto).extractingJsonPathNumberValue("$.location.lat").isEqualTo(newEventDto
                .getLocation().getLat());

        assertThat(resultDto).extractingJsonPathNumberValue("$.location.lon").isEqualTo(newEventDto
                .getLocation().getLon());

        assertThat(resultDto).extractingJsonPathBooleanValue("$.paid").isEqualTo(newEventDto.getPaid());
        assertThat(resultDto).extractingJsonPathNumberValue("$.participantLimit").isEqualTo(newEventDto
                .getParticipantLimit());

        assertThat(resultDto).extractingJsonPathBooleanValue("$.requestModeration").isEqualTo(newEventDto
                .getRequestModeration());

        assertThat(resultDto).extractingJsonPathStringValue("$.title").isEqualTo(newEventDto.getTitle());
    }
}
