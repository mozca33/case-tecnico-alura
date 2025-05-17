package br.com.alura.AluraFake.task.models;

import br.com.alura.AluraFake.task.dto.TaskOptionDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class TaskOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_text")
    private String option;

    private Boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    public TaskOption() {
    }

    public TaskOption(String option, Boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
    }

    public Long getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;

    }

    public String getTaskOption() {
        return option;
    }

    public void setTaskOption(String option) {
        this.option = option;
    }

    public TaskOptionDTO toDTO() {
        return new TaskOptionDTO(this.getTaskOption(), this.getCorrect());
    }
}
