package kz.arta.synergy.components.client;

import java.util.List;

/**
 * User: vsl
 * Date: 05.11.14
 * Time: 17:08
 */
public interface ServerResult {
    String getComment();
    String getErrorMessage();
    List<String> getMessages();
    ErrorCode getErrorCode();

    enum ErrorCode {
        NO_ERROR, ERROR;
    }
}
