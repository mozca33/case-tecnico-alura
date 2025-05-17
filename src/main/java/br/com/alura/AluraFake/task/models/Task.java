package br.com.alura.AluraFake.task.models;

import java.util.List;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.task.Type;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskOption> options;

    public Task() {
    }

    public Task(String statement, Type type, Integer order, Long courseId, List<TaskOption> options) {
        this.statement = statement;
        this.type = type;
        this.order = order;
        this.courseId = courseId;
        this.options = options;
    }

    public Task(Long id, String statement, Type type, Integer order, Long courseId) {
        this.id = id;
        this.statement = statement;
        this.type = type;
        this.order = order;
        this.courseId = courseId;
    }

    public Task(String statement, Type type, Integer order, Long courseId) {
        this.statement = statement;
        this.type = type;
        this.order = order;
        this.courseId = courseId;
    }

    public boolean isSameAs(Task task) {
        boolean result = this.statement.equals(task.statement) &&
                this.order.equals(task.order) &&
                this.type.equals(task.type) &&
                this.courseId.equals(task.courseId);
        
        if (this.isSingleChoice()) {
            return optionsAreSame(task.getOptions()) && result;
        }

        return result;
    }

    public boolean isEmpty() {
        return this.statement == null &&
                this.type == null &&
                this.order == null &&
                this.courseId == null &&
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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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
        if (options == null){
            return false;
        }

        if (this.options.size() != options.size()) {
            return false;
        }

        for (TaskOption option : options) {
            if (this.options.stream().noneMatch(o -> 
                    o.getTaskOption().equals(option.getTaskOption()) &&
                    o.getCorrect().equals(option.getCorrect()))) {
                return false;
            }
        }
        
        return true;
    }
}
