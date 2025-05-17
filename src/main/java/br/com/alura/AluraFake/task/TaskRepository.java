package br.com.alura.AluraFake.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.alura.AluraFake.task.models.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

        boolean existsByCourseIdAndStatementAndIdNot(Long courseId, String statement, Long id);

        boolean existsByCourseIdAndStatement(Long id, String statement);

        boolean existsByCourseIdAndOrder(Long courseId, Integer order);

        int countByCourseId(Long courseId);

        Task findTopByCourseIdAndOrderAndIdNot(Long courseId, Integer order, Long idToIgnore);

        Task findTopByCourseIdAndOrder(Long courseId, Integer order);

        List<Task> findByCourseId(Long courseId);

        @Modifying
        @Query("UPDATE Task t SET t.order = t.order + 1 WHERE t.courseId = :courseId AND t.order >= :newOrder")
        /***
         * Update the order of tasks when a new task is inserted.
         * This method increments the order of all tasks that have an order greater than
         * the previous order.
         * 
         * @param courseId The ID of the course to which the task belongs.
         * @param newOrder The new order of the task being inserted.
         */
        void updateTaskOrderForInsert(@Param("courseId") Long courseId, @Param("newOrder") Integer newOrder);

        @Modifying(clearAutomatically = true)
        @Query("UPDATE Task t SET t.order = t.order + 1 WHERE t.courseId = :courseId AND t.order BETWEEN :start AND :end")
        void incrementOrderRange(@Param("courseId") Long courseId, @Param("start") Integer start,
                        @Param("end") Integer end);

        @Modifying(clearAutomatically = true)
        @Query("UPDATE Task t SET t.order = t.order - 1 WHERE t.courseId = :courseId AND t.order BETWEEN :start AND :end")
        void decrementOrderRange(@Param("courseId") Long courseId, @Param("start") Integer start,
                        @Param("end") Integer end);

}
