package ro.msg.learning.bank.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalDateTimeDTO {

    private int year;
    private int month;
    private int dayOfMonth;
    private int hour;
    private int minute;
    private int second;

    public static LocalDateTimeDTO of(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return new LocalDateTimeDTO(year,month,dayOfMonth,hour,minute,second);
    }

    public LocalDateTime toEntity() {
        return LocalDateTime.of(LocalDate.of(year, month, dayOfMonth), LocalTime.of(hour, minute, second));
    }


    public static LocalDateTimeDTO of(LocalDateTime entity) {
        return LocalDateTimeDTO.builder()

                .year(entity.getYear())
                .month(entity.getMonthValue())
                .dayOfMonth(entity.getDayOfMonth())
                .hour(entity.getHour())
                .minute(entity.getMinute())
                .second(entity.getSecond())
                .build();
    }
}
