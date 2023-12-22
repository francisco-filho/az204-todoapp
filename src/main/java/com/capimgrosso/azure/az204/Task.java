package com.capimgrosso.azure.az204;

import java.util.UUID;

public class Task {
    private final UUID id;
    private String name;
    private boolean done;

    public Task(){
        this.id = UUID.randomUUID();
    }

    public Task(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isDone() {
        return done;
    }

    public void done() {
        this.done = true;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", done=" + done +
                '}';
    }
}
