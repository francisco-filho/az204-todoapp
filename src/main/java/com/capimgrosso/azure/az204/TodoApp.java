package com.capimgrosso.azure.az204;

import com.azure.cosmos.*;
import com.azure.cosmos.implementation.ImplementationBridgeHelpers;
import com.azure.cosmos.models.*;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import io.smallrye.common.constraint.NotNull;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Path("/tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoApp {
    private static final Logger LOG = Logger.getLogger(TodoApp.class.getSimpleName());
    private static final String partitionKey = "name";
    @ConfigProperty(name = "cosmos.key")
    String key;
    @ConfigProperty(name = "app.hostname")
    String appHostname;

    List<Task> taskList = new ArrayList<>();
    CosmosClient cosmosClient;
    CosmosContainer container;

    @PostConstruct
    private void initCosmosDb(){
//        var credential = new DefaultAzureCredentialBuilder().build();

        LOG.info("-- Connecting to CosmosDb database ---");
        cosmosClient = new CosmosClientBuilder()
                .key(key)
                .consistencyLevel(ConsistencyLevel.SESSION)
                .endpoint("https://az204cg.documents.azure.com/")
                .buildClient();
        container = getContainer("tasks");
    }

    private CosmosContainer getContainer(String name){
        return cosmosClient.getDatabase("TodoApp")
                .getContainer(name);
    }

    @GET
    public Response listTasks(){
        var tasks = getContainer("tasks")
                .queryItems("select * from c", new CosmosQueryRequestOptions(), Task.class)
                .stream()
                .toList();
        return Response.ok(tasks).build();
    }

    @GET
    @Path("/{uuid}")
    public Response getTask(@PathParam("uuid") @NotNull UUID uuid){
        var paramList = List.of(new SqlParameter("@id", uuid.toString()));
        var task = getContainer("tasks")
                .queryItems(new SqlQuerySpec("select * from c where c.id=@id", paramList),
                        new CosmosQueryRequestOptions(),
                        Task.class)
                .stream()
                .findFirst()
                .get();

        return Response.ok(task).build();
    }

    @POST
    public Response addTask(Task task){
        var container = getContainer("tasks");
        container.createItem(task);
        URI uri = URI.create("http://%s/tasks/%s".formatted(appHostname, task.getId()));
        return Response.created(uri).build();
    }

    @POST
    @Path("/error")
    public Response triggerError(Map<String, String> error){
        throw new RuntimeException("Triggered a Error: " + error.get("msg"));
    }
}
