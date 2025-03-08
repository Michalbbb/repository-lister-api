package org.acme.exceptions;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class GitHubExceptionMapper implements ResponseExceptionMapper<Throwable> {
    // Returning GitHubException will result in getting ProcessingError instead, because apparently
    // Quarkus treats all exceptions from ResponseExceptionMapper as "processing failures" rather than expected results.
    // So GitHubException is wrapped inside WebApplicationException as it is recognized HTTP error type in JAX-RS
    @Override
    public Throwable toThrowable(Response response) {
        if (response.getStatus() == 404) {
            GitHubException gitHubException = new GitHubException(response.getStatus(),"Resource not found");

            return new WebApplicationException(gitHubException,response);
        }
        else if (response.getStatus() == 403 || response.getStatus() == 429) {
            long epoch = Long.parseLong(response.getHeaderString("X-RateLimit-Reset"));
            String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (epoch*1000));
            GitHubException gitHubException = new GitHubException(response.getStatus(),String.format("GitHub rate limit has been exceeded (60 request per hour). Limit will be reset at %s",date));

            return new WebApplicationException(gitHubException,response);
        }
        else if (response.getStatus() == 500) {
            GitHubException gitHubException = new GitHubException(response.getStatus(),"Internal Server Error occurred.");

            return new WebApplicationException(gitHubException,response);
        }
        GitHubException gitHubException = new GitHubException(response.getStatus(),"Internal Server Error occurred.");
        return new WebApplicationException(gitHubException,response);
    }
}
