package org.json;

/**
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 *
 * @author JSON.org
 * @version 2013-02-10
 */
public class JSONException extends RuntimeException {
    private static final long serialVersionUID = 0;

    /**
     * Constructs a JSONException with an explanatory message.
     *
     * @param message
     *     Detail about the reason for the exception.
     */
    public JSONException(String message) {
        super(message);
    }

    /**
     * Constructs a new JSONException with the specified cause.
     *
     * @param cause
     *     The cause of this exception.
     */
    public JSONException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new JSONException with the specified message and cause.
     *
     * @param message
     *     Detail about the reason for the exception.
     * @param cause
     *     The cause of this exception.
     */
    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }
}
