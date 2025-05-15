package br.com.alura.AluraFake.task;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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

    public Task() {
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
        return this.statement.equals(task.statement) &&
                this.order.equals(task.order) &&
                this.type.equals(task.type) &&
                this.courseId.equals(task.courseId);
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
}
