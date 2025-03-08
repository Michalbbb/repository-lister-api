package org.acme.endpoints;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.*;

import org.acme.exceptions.GitHubException;
import org.acme.exceptions.GitHubExceptionMapper;
import org.acme.services.GitHubService;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@RegisterProvider(GitHubExceptionMapper.class)
@Path("/repositories")
public class RepositoryLister {

    @Inject
    GitHubService gitHubService;

    @GET
    @Blocking
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<Map<String,Object>>> getRepositories(@PathParam("username") String username) {
        try {
            List<Map<String,Object>> repos = gitHubService.getPublicRepositories(username);
            return Uni.createFrom().item(repos);
        }
        catch (Exception e) {
            List<Map<String, Object>> exceptionInfo = new ArrayList<>();

            if (e.getCause() instanceof GitHubException gitHubException) {
                Map<String, Object> exception = new LinkedHashMap<>();
                exception.put("status", gitHubException.getStatus());
                exception.put("message", gitHubException.getMessage());
                exceptionInfo.add(exception);
            }
            else {
                Map<String, Object> exception = new HashMap<>();
                exception.put("Class", String.format("Unexpected error of class: %s", e.getClass()));
                exception.put("Message", e.getMessage());
                exceptionInfo.add(exception);
            }

            return Uni.createFrom().item(exceptionInfo);
        }
    }

}