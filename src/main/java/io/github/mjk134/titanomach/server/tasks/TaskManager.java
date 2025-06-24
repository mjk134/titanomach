package io.github.mjk134.titanomach.server.tasks;

import java.util.HashMap;

public class TaskManager {
    public HashMap<String, Task> tasks= new HashMap<>();
    public void UpdateTask(String taskID) {
        this.tasks.get(taskID);
    }
    public Task getTask(String taskID) {
        return tasks.get(taskID);
    }
    public void AddTask(Task task) {
        tasks.put(task.name, task);
    }
}
