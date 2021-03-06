package com.revature.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Taken from Quizzard project at https://github.com/210119-java-enterprise/quizzard
 */
public class ErrorResponse {
    private static final Logger logger = LogManager.getLogger(ErrorResponse.class);

    private int status;
    private String message;
    private long timestamp;

    public ErrorResponse() {
        super();
    }

    public ErrorResponse(int status, String message, long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public ErrorResponse setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ErrorResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ErrorResponse setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String toJSON() {

        ObjectMapper mapper = new ObjectMapper();
        String json = "";

        try {
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.error(e.getStackTrace());
        }

        return json;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return status == that.status &&
                timestamp == that.timestamp &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message, timestamp);
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

}