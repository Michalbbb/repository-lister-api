# Repository lister API made with Quarkus and Java
___
## Project that list repositories and associated branches with some basic information based on given username
___
### Endpoints
> /repositories/${username}

E.g.
> /repositories/michalbbb

Will return list of all non-forked public repositories that belongs to account with username "michalbbb", given such username exist and rate limit for GitHub API has not been exceeded

#### Expected responses

When backing api returns code 200 :
```text
[
  {
    "name": ${repositoryName},
    "ownerLogin": ${username},
    "branches": [
      {
        "name": ${branchName},
        "sha": ${lastCommitSha}
      },
      {...}	
    ]..
  },
  {...}
]
```
In other cases:
```text
{
    “status”: ${responseCode}
    “message”: ${whyHasItHappened}
}
```
___
### Technical information
* Project does not handle pagination, which means part of data might be lost (GitHub API returns up to 30 results per request)
* As requests are send while not being authorized there is rate limit equal to 60 per hour
* Repositories that are fork will be ignored when returning result
* Only public repositories will be considered when processing request
___
# Backing API
> https://developer.github.com/v3