package kz.arta.sc3.showcase.client;

import kz.arta.synergy.components.client.ServerResult;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 05.11.14
 * Time: 17:49
 */
public class SimpleServerResult implements ServerResult {
    private ErrorCode errorCode;
    private String comment;
    private String errorMessage;
    private List<String> messages;

    public SimpleServerResult(ErrorCode errorCode, String comment, String errorMessage) {
        this.errorCode = errorCode;
        this.comment = comment;
        this.errorMessage = errorMessage;

        messages = new ArrayList<String>();
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public List<String> getMessages() {
        return messages;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
