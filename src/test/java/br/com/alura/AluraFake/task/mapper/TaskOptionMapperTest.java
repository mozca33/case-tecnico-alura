package br.com.alura.AluraFake.task.mapper;

import br.com.alura.AluraFake.task.dto.TaskOptionDTO;
import br.com.alura.AluraFake.task.models.TaskOption;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TaskOptionMapperTest {

    @Test
    void toEntityList_withValidDTOs_returnsTaskOptionList() {
        var dto1 = new TaskOptionDTO("Option 1", true);
        var dto2 = new TaskOptionDTO("Option 2", false);
        List<TaskOption> result = TaskOptionMapper.toEntityList(List.of(dto1, dto2));
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Option 1", result.get(0).getTaskOption());
        assertTrue(result.get(0).getCorrect());
        assertEquals("Option 2", result.get(1).getTaskOption());
        assertFalse(result.get(1).getCorrect());
    }

    @Test
    void toEntityList_withNullOption_throwsTaskException() {
        var dto = new TaskOptionDTO(null, true);
        var dtos = List.of(dto);
        var exception = assertThrows(
                br.com.alura.AluraFake.task.exceptions.TaskException.class,
                () -> TaskOptionMapper.toEntityList(dtos));
        assertTrue(exception.getMessage().contains("Option fields must not be null or blank"));
    }

    @Test
    void toEntityList_withNullIsCorrect_throwsTaskException() {
        var dto = new br.com.alura.AluraFake.task.dto.TaskOptionDTO("Option", null);
        var dtos = List.of(dto);
        var exception = assertThrows(
                br.com.alura.AluraFake.task.exceptions.TaskException.class,
                () -> TaskOptionMapper.toEntityList(dtos));
        assertTrue(exception.getMessage().contains("Option fields must not be null or blank"));
    }

    @Test
    void toEntityList_withEmptyList_returnsEmptyList() {
        List<TaskOption> result = TaskOptionMapper.toEntityList(List.of());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}