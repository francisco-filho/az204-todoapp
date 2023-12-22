package com.capimgrosso.azure.az204;

import io.smallrye.common.constraint.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoApp {

    List<Task> taskList = new ArrayList<>();

    @GET
    public Response listTasks(){
        return Response.ok(taskList).build();
    }

    @GET
    @Path("/{id}")
    public Response getTask(@PathParam("id") @NotNull UUID uuid){
        for (Task t : taskList) {
            if (uuid.equals(t.getId())){
                return Response.ok(t).build();
            }
        }
        return Response.status(404).build();
    }

    @POST
    public Response addTask(Task task){
        taskList.add(task);
        return Response.ok().build();
    }
}
