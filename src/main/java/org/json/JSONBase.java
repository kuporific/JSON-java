package org.json;

import java.io.StringWriter;
import java.io.Writer;

/**
 * Holds methods common to both {@link JSONObject} and {@link JSONArray}.
 * The {@code Keydex} is either a <em>key</em> or an <em>index</em> for
 * {@link JSONObject} and {@link JSONArray}, respectively.
 *
 * @author kuporific
 */
abstract class JSONBase<Keydex> {
    /**
     * Get the object value associated with a key/index.
     *
     * @param keydex
     *            The key/index must be within the range of this object.
     * @return An object value.
     * @throws JSONException
     *             If there is no value for the key/index.
     */
    public Object get(Keydex keydex) throws JSONException {
        if (keydex == null) {
            throw new JSONException("Null key/index.");
        }
        Object object = this.opt(keydex);
        if (object == null) {
            throw new JSONException(this.getClass().getSimpleName()
                    + "[" + keydex + "] not found.");
        }
        return object;
    }

    /**
     * Get the boolean value associated with a key/index. The string values "true"
     * and "false" are converted to boolean.
     *
     * @param keydex
     *            The key/index must be within this object's range
     * @return The truth.
     * @throws JSONException
     *             If there is no value for the key/index or if the value is not
     *             convertible to boolean.
     */
    public boolean getBoolean(Keydex keydex) throws JSONException {
        Object object = this.get(keydex);
        if (isBoolean(object, true)) {
            return true;
        } else if (isBoolean(object, false)) {
            return false;
        }
        throw wrongTypeJSONException(keydex, Boolean.class, object.getClass());
    }

    /**
     * Get the double value associated with a key/index.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @return The value.
     * @throws JSONException
     *             If the key/index is not found or if the value cannot be converted
     *             to a number.
     */
    public double getDouble(Keydex keydex) throws JSONException {
        Object object = this.get(keydex);
        try {
            return object instanceof Number ? ((Number) object).doubleValue()
                    : Double.parseDouble((String) object);
        } catch (Exception e) {
            throw wrongTypeJSONException(keydex, Double.class, object.getClass());
        }
    }

    /**
     * Get the int value associated with a key/index.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @return The value.
     * @throws JSONException
     *             If the key/index is not found or if the value is not a number.
     */
    public int getInt(Keydex keydex) throws JSONException {
        Object object = this.get(keydex);
        try {
            return object instanceof Number
                    ? ((Number) object).intValue()
                    : Integer.parseInt((String) object);
        } catch (Exception e) {
            throw wrongTypeJSONException(
                    keydex, Integer.class, object.getClass());
        }
    }

    /**
     * Get the JSONArray associated with a key/index.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @return A JSONArray value.
     * @throws JSONException
     *             If there is no value for the key/index or if the value is not a
     *             JSONArray
     */
    public JSONArray getJSONArray(Keydex keydex) throws JSONException {
        Object object = this.get(keydex);
        if (object instanceof JSONArray) {
            return (JSONArray) object;
        }
        throw wrongTypeJSONException(keydex, JSONArray.class, object.getClass());
    }

    /**
     * Get the JSONObject associated with a key/index.
     *
     * @param keydex
     *            subscript
     * @return A JSONObject value.
     * @throws JSONException
     *             If there is no value for the key/index or if the value is not a
     *             JSONObject
     */
    public JSONObject getJSONObject(Keydex keydex) throws JSONException {
        Object object = this.get(keydex);
        if (object instanceof JSONObject) {
            return (JSONObject) object;
        }
        throw wrongTypeJSONException(keydex, JSONObject.class, object.getClass());
    }

    /**
     * Get the long value associated with a key/index.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @return The value.
     * @throws JSONException
     *             If the key/index is not found or if the value cannot be converted
     *             to a number.
     */
    public long getLong(Keydex keydex) throws JSONException {
        Object object = this.get(keydex);
        try {
            return object instanceof Number ? ((Number) object).longValue()
                    : Long.parseLong((String) object);
        } catch (Exception e) {
            throw wrongTypeJSONException(keydex, Long.class, object.getClass());
        }
    }

    /**
     * Get the string associated with a key/index.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @return A string value.
     * @throws JSONException
     *             If there is no string value for the key/index.
     */
    public String getString(Keydex keydex) throws JSONException {
        Object object = this.get(keydex);
        if (object instanceof String) {
            return (String) object;
        }
        throw wrongTypeJSONException(keydex, String.class, object.getClass());
    }

    /**
     * Determine if the value is null.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @return true if the value at the key/index is null, or if there is no value.
     */
    public boolean isNull(Keydex keydex) {
        return JSONObject.NULL.equals(this.opt(keydex));
    }

    /**
     * Get the optional object value associated with a key/index.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @return An object value, or null if there is no object at that key/index.
     */
    public abstract Object opt(Keydex keydex);

    /**
     * Get the optional boolean value associated with a key/index. It returns false
     * if there is no value at that key/index, or if the value is not Boolean.TRUE
     * or the String "true".
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @return The truth.
     */
    public boolean optBoolean(Keydex keydex) {
        return this.optBoolean(keydex, false);
    }

    /**
     * Get the optional boolean value associated with a key/index. It returns the
     * defaultValue if there is no value at that key/index or if it is not a Boolean
     * or the String "true" or "false" (case insensitive).
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @param defaultValue
     *            A boolean default.
     * @return The truth.
     */
    public boolean optBoolean(Keydex keydex, boolean defaultValue) {
        try {
            return this.getBoolean(keydex);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional double value associated with a key/index. NaN is returned
     * if there is no value for the key/index, or if the value is not a number and
     * cannot be converted to a number.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @return The value.
     */
    public double optDouble(Keydex keydex) {
        return this.optDouble(keydex, Double.NaN);
    }

    /**
     * Get the optional double value associated with a key/index. The defaultValue
     * is returned if there is no value for the key/index, or if the value is not a
     * number and cannot be converted to a number.
     *
     * @param keydex
     *            subscript
     * @param defaultValue
     *            The default value.
     * @return The value.
     */
    public double optDouble(Keydex keydex, double defaultValue) {
        try {
            return this.getDouble(keydex);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional int value associated with a key/index. Zero is returned if
     * there is no value for the key/index, or if the value is not a number and
     * cannot be converted to a number.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @return The value.
     */
    public int optInt(Keydex keydex) {
        return this.optInt(keydex, 0);
    }

    /**
     * Get the optional int value associated with a key/index. The defaultValue is
     * returned if there is no value for the key/index, or if the value is not a
     * number and cannot be converted to a number.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @param defaultValue
     *            The default value.
     * @return The value.
     */
    public int optInt(Keydex keydex, int defaultValue) {
        try {
            return this.getInt(keydex);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional JSONArray associated with a key/index.
     *
     * @param keydex
     *            subscript
     * @return A JSONArray value, or null if the key/index has no value, or if the
     *         value is not a JSONArray.
     */
    public JSONArray optJSONArray(Keydex keydex) {
        Object o = this.opt(keydex);
        return o instanceof JSONArray ? (JSONArray) o : null;
    }

    /**
     * Get the optional JSONObject associated with a key/index. Null is returned if
     * the key/index is not found, or null if the key/index has no value, or if the value
     * is not a JSONObject.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @return A JSONObject value.
     */
    public JSONObject optJSONObject(Keydex keydex) {
        Object o = this.opt(keydex);
        return o instanceof JSONObject ? (JSONObject) o : null;
    }

    /**
     * Get the optional long value associated with a key/index. Zero is returned if
     * there is no value for the key/index, or if the value is not a number and
     * cannot be converted to a number.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @return The value.
     */
    public long optLong(Keydex keydex) {
        return this.optLong(keydex, 0);
    }

    /**
     * Get the optional long value associated with a key/index. The defaultValue is
     * returned if there is no value for the key/index, or if the value is not a
     * number and cannot be converted to a number.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @param defaultValue
     *            The default value.
     * @return The value.
     */
    public long optLong(Keydex keydex, long defaultValue) {
        try {
            return this.getLong(keydex);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional string value associated with a key/index. It returns an
     * empty string if there is no value at that key/index. If the value is not a
     * string and is not null, then it is converted to a string.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @return A String value.
     */
    public String optString(Keydex keydex) {
        return this.optString(keydex, "");
    }

    /**
     * Get the optional string associated with a key/index. The defaultValue is
     * returned if the key/index is not found.
     *
     * @param keydex
     *             The key/index must be within this object's range
     * @param defaultValue
     *            The default value.
     * @return A String value.
     */
    public String optString(Keydex keydex, String defaultValue) {
        Object object = this.opt(keydex);
        return JSONObject.NULL.equals(object) ? defaultValue : object
                .toString();
    }

    /**
     * Make a prettyprinted JSON text of this instance. Warning: This method
     * assumes that the data structure is acyclical.
     *
     * @param indentFactor
     *            The number of spaces to add to each level of indentation.
     * @return a printable, displayable, transmittable representation of the
     *         object.
     * @throws JSONException
     */
    public String toString(int indentFactor) throws JSONException {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            return this.write(sw, indentFactor, 0).toString();
        }
    }

    /**
     * Write the contents of this instance as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     * @throws JSONException
     */
    public Writer write(Writer writer) throws JSONException {
        return this.write(writer, 0, 0);
    }

    /**
     * Write the contents of this instance as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     * @throws JSONException
     */
    abstract Writer write(Writer writer, int indentFactor, int indent);

    private JSONException wrongTypeJSONException(
            Keydex keydex, Class<?> expectedType, Class<?> actualType) {
        return new JSONException(
                this.getClass().getSimpleName() + "[" + keydex + "]" +
                        " is not a " + expectedType.getSimpleName()
                        + ", it is a " + actualType.getSimpleName() + ".");
    }

    /**
     * Returns {@code true} if the {@code object} is a Boolean equal to the
     * {@code desiredValue} or a String equal to the {@code toString()} of
     * Boolean.
     *
     * @param object the object to test
     * @param desiredValue the desired boolean value that {@code object} should
     *    be.
     *
     * @return {@code true} if object equals desiredValue.
     */
    private boolean isBoolean(Object object, boolean desiredValue) {
        return object.equals(desiredValue)
                || (object instanceof String && ((String) object)
                .equalsIgnoreCase(Boolean.toString(desiredValue)));
    }
}
