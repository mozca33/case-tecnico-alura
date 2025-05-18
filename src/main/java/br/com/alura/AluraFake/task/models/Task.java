package br.com.alura.AluraFake.task.models;

import java.util.List;

import org.springframework.http.HttpStatus;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.task.dto.BaseTaskDTO;
import br.com.alura.AluraFake.task.dto.MultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.exceptions.TaskException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long courseId;
    private String statement;

    @Column(name = "task_order", nullable = false)
    private Integer order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskOption> options;

    public Task() {
    }

    public Task(Long id, String statement, Type type, Integer order, Course course) {
        this.id = id;
        this.statement = statement;
        this.type = type;
        this.order = order;
        this.course = course;
    }

    public Task(Long id, String statement, Type type, Integer order) {
        this.id = id;
        this.statement = statement;
        this.type = type;
        this.order = order;
        this.courseId = courseId;
    }

    public boolean isSameAs(Task task) {
        boolean result = this.statement.equals(task.statement) &&
                this.order.equals(task.order) &&
                this.course.equals(task.course);

        if (this.isSingleChoice() || this.isMultipleChoice()) {
            return optionsAreSame(task.getOptions()) && result;
        }

        return result;
    }

    public boolean isEmpty() {
        return this.statement == null &&
                this.type == null &&
                this.order == null &&
                this.course == null &&
                this.id == null;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<TaskOption> getOptions() {
        return options;
    }

    public void setOptions(List<TaskOption> options) {
        this.options = options;
    }

    public boolean isSingleChoice() {
        return this.type == Type.SINGLE_CHOICE;
    }

    public boolean isOpenText() {
        return this.type == Type.OPEN_TEXT;
    }

    public boolean isMultipleChoice() {
        return this.type == Type.MULTIPLE_CHOICE;
    }

    private boolean optionsAreSame(List<TaskOption> options) {
        if (options == null) {
            return false;
        }

        if (this.options.size() != options.size()) {
            return false;
        }

        for (TaskOption option : options) {
            if (this.options.stream().noneMatch(o -> o.getTaskOption().equals(option.getTaskOption()) &&
                    o.getCorrect().equals(option.getCorrect()))) {
                return false;
            }
        }

        return true;
    }

    public void ensureSameTypeAs(Task other) {
        if (!this.type.equals(other.getType())) {
            throw new TaskException(
                    "Task type mismatch. Expected " + this.getType() + " type but got " + other.getType() + " type.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public Integer mergeFrom(Task other) {
        Integer newOrder = null;

        if (other.getStatement() != null) {
            this.setStatement(other.getStatement());
        }

        if (other.getOrder() != null) {
            newOrder = other.getOrder();
        }

        if (other.getCourse() != null)
            this.setCourse(other.getCourse());

        if (other.getOptions() != null) {
            if ((other.getType() == Type.SINGLE_CHOICE
                    || other.getType() == Type.MULTIPLE_CHOICE)
                    && other.getType().equals(this.getType())) {
                this.getOptions().clear();
                this.getOptions().addAll(other.getOptions());
                this.attachOptionsToTask();
            }
        }

        return newOrder;
    }

    public void attachOptionsToTask() {
        if (this.getOptions() != null) {
            this.getOptions().forEach(option -> option.setTask(this));
        }
    }

    public BaseTaskDTO toDTO() {
        return switch (this.type) {
            case OPEN_TEXT -> new OpenTextTaskDTO(id, course.getId(), statement, order, type);
            case SINGLE_CHOICE -> new SingleChoiceTaskDTO(id, course.getId(), statement, order, type,
                    options.stream().map(TaskOption::toDTO).toList());
            case MULTIPLE_CHOICE -> new MultipleChoiceTaskDTO(id, course.getId(), statement, order, type,
                    options.stream().map(TaskOption::toDTO).toList());
        };
    }
}
