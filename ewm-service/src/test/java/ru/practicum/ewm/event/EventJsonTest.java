package ru.practicum.ewm.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.ewm.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.event.dto.Location;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class EventJsonTest {
    @Autowired
    private JacksonTester<AdminUpdateEventRequest> jsonEvent;

    private AdminUpdateEventRequest getAdminUpdateEventRequest() {

        AdminUpdateEventRequest event = new AdminUpdateEventRequest();

        event.setAnnotation("Эстрадный мюзикл по роману Льва Толстого");
        event.setCategory(1L);
        event.setDescription("Мюзикл «Московской оперетты» поставлен с эстрадным размахом. " +
                "В оркестровой яме в основном духовые, а из динамиков грохочут барабаны с " +
                "электрогитарой в духе рок-опер восьмедисятых. Вместо декораций — огромный " +
                "экран, превращающий место действия то в вокзал, то в бальный зал, то в " +
                "зимнюю площадь с золотыми куполами и праздничным катком. Либретто написал " +
                "Юлий Ким, поставила спектакль Алина Чевик, музыку сочинил Роман Игнатьев. " +
                "Главный хит: «Не ходите по пу, по путям». Заглавную роль в новом шоу исполняют, " +
                "сменяя друг друга, Екатерина Гусева и Валерия Ланская.");

        event.setEventDate(LocalDateTime.now().plusDays(15L).withNano(0));
        event.setLocation(new Location(55.760016,37.615965));
        event.setPaid(true);
        event.setParticipantLimit(1000);
        event.setRequestModeration(true);
        event.setTitle("Мюзикл");
        return event;
    }

    @Test
    void testSerialize() throws Exception {

        var adminEventDto = getAdminUpdateEventRequest();
        var result = jsonEvent.write(adminEventDto);

        assertThat(result).hasJsonPath("$.annotation");
        assertThat(result).hasJsonPath("$.category");
        assertThat(result).hasJsonPath("$.eventDate");

        assertThat(result).extractingJsonPathStringValue("$.annotation").isEqualTo(adminEventDto.getAnnotation());
        assertThat(result).extractingJsonPathNumberValue("$.category").isEqualTo(adminEventDto.getCategory()
                .intValue());
        assertThat(result).extractingJsonPathStringValue("$.eventDate").isEqualTo(adminEventDto.getEventDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

    }
}
