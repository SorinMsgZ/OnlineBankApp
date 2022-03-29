package ro.msg.learning.bank.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.msg.learning.bank.exceptions.NotFoundException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {
    private String type;
    private String message;

    public static ErrorDTO of(NotFoundException exception) {
        return new ErrorDTO("not_found", exception.getMessage());
    }
}
