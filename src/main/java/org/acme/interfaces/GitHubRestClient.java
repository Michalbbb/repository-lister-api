package org.acme.interfaces;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.exceptions.GitHubExceptionMapper;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://api.github.com")
@RegisterProvider(GitHubExceptionMapper.class)
public interface GitHubRestClient {
    @GET
    @Path("/users/{username}/repos")
    @Produces(MediaType.APPLICATION_JSON)
    String getUserRepositories(@PathParam("username") String username);

    @GET
    @Path("/repos/{username}/{name}/branches")
    @Produces(MediaType.APPLICATION_JSON)
    String getUserBranches(@PathParam("username") String username, @PathParam("name") String name);
}
