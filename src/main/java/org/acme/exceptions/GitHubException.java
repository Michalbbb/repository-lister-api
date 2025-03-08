package org.acme.exceptions;

public class GitHubException extends Exception{
    String message;
    Integer status;
    public GitHubException(Integer status, String message) {
        super();
        this.message = message;
        this.status = status;

    }
    @Override
    public String getMessage() {
        return message;
    }
    public Integer getStatus(){
        return status;
    }
}
