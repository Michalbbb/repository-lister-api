package org.acme.services;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.*;
import org.acme.interfaces.GitHubRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.StringReader;
import java.util.*;

@ApplicationScoped
public class GitHubService {

    @Inject
    @RestClient
    GitHubRestClient gitHubRestClient;

    public List<Map<String,Object>> getPublicRepositories(String username) {
        JsonArray repos = convertToJsonArray(gitHubRestClient.getUserRepositories(username));


        List<Map<String,Object>> filteredRepos = new ArrayList<>();
        for (JsonObject repo : repos.getValuesAs(JsonObject.class)) {
            if (!repo.getBoolean("fork")) {
                List<Map<String,Object>> branches = getBranchesInfo(gitHubRestClient.getUserBranches(username, repo.getString("name")));
                Map<String,Object> repoMap = new LinkedHashMap<>();

                repoMap.put("name", repo.getString("name"));
                repoMap.put("ownerLogin", repo.getJsonObject("owner").getString("login"));
                repoMap.put("branches", branches);

                filteredRepos.add(repoMap);
            }
        }
        return filteredRepos;
    }

    private List<Map<String,Object>> getBranchesInfo(String branches) {
        JsonArray branchesArray = convertToJsonArray(branches);
        List<Map<String,Object>> branchesInfo = new ArrayList<>();
        for (JsonObject branch : branchesArray.getValuesAs(JsonObject.class)) {
            Map<String,Object> branchMap = new LinkedHashMap<>();
            branchMap.put("name", branch.getString("name"));
            branchMap.put("sha", branch.getJsonObject("commit").getString("sha"));
            branchesInfo.add(branchMap);
        }

        return branchesInfo;
    }

    private JsonArray convertToJsonArray(String jsonString) {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonArray result = jsonReader.readArray();
        jsonReader.close();

        return result;
    }
}