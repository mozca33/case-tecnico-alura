package br.com.alura.AluraFake.task.mapper;

import br.com.alura.AluraFake.task.models.TaskOption;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TaskOptionMapperTest {

    @Test
    void toEntityList_withNullInput_returnsEmptyList() {
        List<TaskOption> result = TaskOptionMapper.toEntityList(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toEntityList_withEmptyList_returnsEmptyList() {
        List<TaskOption> result = TaskOptionMapper.toEntityList(List.of());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}