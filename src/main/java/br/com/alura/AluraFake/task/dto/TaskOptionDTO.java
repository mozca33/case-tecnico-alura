package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.task.models.TaskOption;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskOptionDTO(
        @Size(min = 4, max = 80, message = "The alternative must have between 4 and 80 characters.") @NotNull @NotBlank String option,
        @NotNull(message = "isCorrect field must be true or false.") Boolean isCorrect) {

    public TaskOption toEntity() {
        return new TaskOption(option(), isCorrect());
    }

}
