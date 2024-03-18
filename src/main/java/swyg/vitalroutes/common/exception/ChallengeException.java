package swyg.vitalroutes.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import swyg.vitalroutes.common.response.ResponseType;

@Getter
public class ChallengeException extends RuntimeException{
    public HttpStatus status;
    public ResponseType type;

    public ChallengeException(HttpStatus status, ResponseType type, String message) {
        super(message);
        this.status = status;
        this.type = type;
    }
}
