package org.json;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author kuporific
 */
public class JSONObjectTest {

    private static final String STRING_KEY = "stringKey";
    private static final String NUMBER_KEY = "numberKey";
    private static final String OBJECT_KEY = "objectKey";
    private static final String ARRAY_KEY = "arrayKey";
    private static final String BOOLEAN_KEY = "booleanKey";
    private static final String NOP_KEY = "notAKey";

    private static final String STRING_VALUE = "stringValue";
    private static final int NUMBER_VALUE = 12;
    private static final String OBJECT_VALUE = "{object: true}";
    private static final String ARRAY_VALUE = "[true, false]";
    private static final boolean BOOLEAN_VALUE = true;

    /**
     * A JSON object that contains all data types.
     */
    private static final String SAMPLE_JSON_OBJECT
            = "{"
            + STRING_KEY + ":\"" + STRING_VALUE + "\","
            + NUMBER_KEY + ":" + NUMBER_VALUE + ","
            + OBJECT_KEY + ":" + OBJECT_VALUE + ","
            + ARRAY_KEY + ":" + ARRAY_VALUE + ","
            + BOOLEAN_KEY + ":" + BOOLEAN_VALUE
            + "}";

    private static final Map<String, Object> KEY_VALUE_PAIRS
            = new ImmutableMap.Builder<String, Object>()
            .put(STRING_KEY, STRING_VALUE)
            .put(NUMBER_KEY, NUMBER_VALUE)
            .put(OBJECT_KEY, new JSONObject(OBJECT_VALUE))
            .put(ARRAY_KEY, new JSONArray(ARRAY_VALUE))
            .put(BOOLEAN_KEY, BOOLEAN_VALUE)
            .build();

    private JSONObject jsonObjectFromString
            = new JSONObject(SAMPLE_JSON_OBJECT);

    @Test(expected = JSONException.class)
    public void constructorWithEmptyString() {
        new JSONObject("");
    }

    /**
     * Ensure this constructor does not fail when passed an empty JSON object.
     */
    @Test
    public void constructorWithEmptyJsonObjectString() {
        new JSONObject("{}");
    }

    @Test
    public void stringConstructor() {
        runTests(new JSONObject(SAMPLE_JSON_OBJECT));
    }

    @Test
    public void copyConstructor() {
        runTests(new JSONObject(
                jsonObjectFromString,
                (String[]) new JSONObject(SAMPLE_JSON_OBJECT)
                        .keySet().toArray(new String[] {})));

    }

    @Test
    public void mapConstructor() {
        runTests(new JSONObject(KEY_VALUE_PAIRS));
    }

    private void runTests(JSONObject jsonObject) {
        testGet(jsonObject);
        testGetNotFound(jsonObject);
        testOpt(jsonObject);
        testOptWithDefaults(jsonObject);
        testOptNotFound(jsonObject);
        testGetValues(jsonObject);
        testGetNames(jsonObject);
        testHas(jsonObject);
        testIsNull(jsonObject);
    }

    /**
     * Get all values from the JSON Object instance.
     */
    private void testGet(JSONObject jsonObject) {
        assertNotNull(jsonObject.get(STRING_KEY));
        assertNotNull(jsonObject.get(NUMBER_KEY));
        assertNotNull(jsonObject.get(OBJECT_KEY));
        assertNotNull(jsonObject.get(ARRAY_KEY));
        assertNotNull(jsonObject.get(BOOLEAN_KEY));
    }

    private void testGetNotFound(JSONObject jsonObject) {
        try
        {
            jsonObject.get(NOP_KEY);
        } catch (JSONException e) {
            // passed.
            return;
        }
        Assert.fail();
    }

    private void testOpt(JSONObject jsonObject) {
        assertNotNull(jsonObject.opt(STRING_KEY));
        assertNotNull(jsonObject.opt(NUMBER_KEY));
        assertNotNull(jsonObject.opt(OBJECT_KEY));
        assertNotNull(jsonObject.opt(ARRAY_KEY));
        assertNotNull(jsonObject.opt(BOOLEAN_KEY));
    }

    private void testOptWithDefaults(JSONObject jsonObject) {
        assertNotNull(jsonObject.optString(STRING_KEY));
        assertEquals(jsonObject.optString(NOP_KEY, "empty"), "empty");
        assertNotNull(jsonObject.optInt(NUMBER_KEY));
        assertEquals(jsonObject.optInt(NOP_KEY, 20), 20);
        assertNotNull(jsonObject.optDouble(NUMBER_KEY));
        assertEquals(jsonObject.optDouble(NOP_KEY, 20.0), 20.0, 0.001);
        assertNotNull(jsonObject.optLong(NUMBER_KEY));
        assertEquals(jsonObject.optLong(NOP_KEY, 20L), 20L);
        assertNotNull(jsonObject.optJSONObject(OBJECT_KEY));
        // Note: no JSONObject#optJSONObject(key, default);
        assertNotNull(jsonObject.optJSONArray(ARRAY_KEY));
        // Note: no JSONObject#optJSONArray(key, default);
        assertNotNull(jsonObject.optBoolean(BOOLEAN_KEY));
        assertEquals(jsonObject.optBoolean(NOP_KEY, true), true);
    }

    private void testOptNotFound(JSONObject jsonObject) {
        assertNull(jsonObject.opt(NOP_KEY));
    }

    private void testGetValues(JSONObject jsonObject) {
        assertEquals(jsonObject.getString(STRING_KEY), STRING_VALUE);
        assertEquals(jsonObject.getInt(NUMBER_KEY), NUMBER_VALUE);
        assertEquals(jsonObject.getDouble(NUMBER_KEY), (double) NUMBER_VALUE, 0.00001);
        assertEquals(jsonObject.getLong(NUMBER_KEY), (long) NUMBER_VALUE);

        // JSONObject and JSONArray do not (yet) implement equals.
        assertEquals(
                jsonObject.getJSONObject(OBJECT_KEY).getClass(),
                JSONObject.class);
        assertEquals(jsonObject.getJSONArray(ARRAY_KEY).getClass(), JSONArray.class);

        assertEquals(jsonObject.getBoolean(BOOLEAN_KEY), BOOLEAN_VALUE);
    }

    private void testGetNames(JSONObject jsonObject) {
        assertEquals(
                KEY_VALUE_PAIRS.keySet(),
                new HashSet<>(Arrays.asList(JSONObject.getNames(jsonObject))));
    }

    private void testHas(JSONObject jsonObject) {

        KEY_VALUE_PAIRS.keySet()
                .stream()
                .forEach(
                        key -> {
                            if (!jsonObject.has(key))
                            {
                                Assert.fail(
                                        "JSONObject does not have key \""
                                                + key + "\" but should.");
                            }
                        });
    }

    private void testIsNull(JSONObject jsonObject) {
        assertTrue(jsonObject.isNull(NOP_KEY));
    }

    @Test
    public void testPut() {

        JSONObject jsonObject = new JSONObject();

        KEY_VALUE_PAIRS.entrySet()
                .stream()
                .forEach(
                        entry -> {
                            jsonObject.put(entry.getKey(), entry.getValue());
                            assertTrue(
                                    "jsonObject does not have the key, "
                                            + entry.getKey(),
                                    jsonObject.has(entry.getKey())
                            );
                            assertEquals(
                                    jsonObject.get(entry.getKey()),
                                    entry.getValue());
                        });
    }

    @Test
    public void testIncrementInt() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", 7);
        jsonObject.increment("number");
        assertEquals(jsonObject.get("number"), 8);
    }

    @Test
    public void testIncrementLong() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", 7L);
        jsonObject.increment("number");
        assertEquals(jsonObject.get("number"), 8L);
    }

    @Test
    public void testIncrementFloat() {
        JSONObject jsonObject = new JSONObject();
        // This float is cast to a double inside the "put" call.
        jsonObject.put("number", 5.6f);
        jsonObject.increment("number");
        assertEquals((double) jsonObject.get("number"), 6.6f, 0.0001);
    }


    @Test
    public void testIncrementDouble() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", 5.6);
        jsonObject.increment("number");
        assertEquals((double) jsonObject.get("number"), 6.6, 0.0001);
    }

    @Test
    public void testIncrement() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.increment("number");
        assertEquals(jsonObject.get("number"), 1);
    }

    @Test
    public void testNumberToString() {
        assertEquals(JSONObject.numberToString(10.0), "10");
    }

    @Test(expected = JSONException.class)
    public void testNumberToStringNull() {
        JSONObject.numberToString(null);
    }
}
