package tests;

/* Copyright 2015  hbz, Pascal Christoph.
 * Licensed under the Eclipse Public License 1.0 */

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

/**
 * Builds a map with json paths as keys with aggregated values, thus comparing
 * to JsonNodes becomes easy. If a key ends with "Order" it is assumed that the
 * values must be in the given order, so this tests also ordered lists.
 * Successfully compared elements will be removed from the actual map, thus a
 * successful comparison leads to an empty 'actual' map (see last line).
 * 
 * @author Pascal Christoph (dr0i)
 * @author Jan Schnasse
 *
 */
@SuppressWarnings("javadoc")
public final class CompareJsonMaps {

    Stack<String> stack = new Stack<>();
    static final String JSON_LD_CONTEXT = "[@context";
    @SuppressWarnings("unused")
    private static boolean IGNORE_CONTEXT = true;
    private static String filename1;
    private static String filename2;

    public static void main(String... args) {
        if (args.length < 2)
            play.Logger.info("Usage: <filename1> <filename2> {<false> if @context should be taken into acount}");
        filename1 = args[0];
        filename2 = args[1];
        play.Logger.info("\n" + filename1 + " may be referenced in the logs as 'actual'\n" + filename2
                + " may be referenced as 'expected'");
        if (args.length >= 3 && args[2].equals("false"))
            IGNORE_CONTEXT = false;
        try {
            if (new CompareJsonMaps().compare(new ObjectMapper().readValue(new File(args[0]), JsonNode.class),
                    new ObjectMapper().readValue(new File(args[1]), JsonNode.class)))
                play.Logger.info("OK. The content is equal.");
            else
                play.Logger.info("Sadeness. The content differs.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean compare(final JsonNode actual, final JsonNode expected) {
        // generated data to map
        final HashMap<String, String> actualMap = new HashMap<>();
        extractFlatMapFromJsonNode(actual, actualMap);
        // expected data to map
        final HashMap<String, String> expectedMap = new HashMap<>();
        extractFlatMapFromJsonNode(expected, expectedMap);
        play.Logger.trace("\n##### remove good entries ###");
        for (final Entry<String, String> e : expectedMap.entrySet()) {
            play.Logger.trace("Trying to remove " + e.getKey() + "...");
            if (!actualMap.containsKey(e.getKey())) {
                play.Logger.warn("At least this element is missing in actual: " + e.getKey());
                return false;
            }
            if (e.getKey().endsWith("Order]")) {
                handleOrderedValues(actualMap, e);
            } else {
                handleUnorderedValues(actualMap, e);
            }
        }
        if (!actualMap.isEmpty()) {
            play.Logger.warn("These elements were not expected or the values are not proper:");
            actualMap.forEach((key, val) -> play.Logger.warn("KEY=" + key + " VALUE=" + val));
        }
        return actualMap.size() == 0;
    }

    private static void handleUnorderedValues(final HashMap<String, String> actualMap, final Entry<String, String> e) {
        if (checkIfAllValuesAreContainedUnordered(actualMap.get(e.getKey()), e.getValue())) {
            actualMap.remove(e.getKey());
            play.Logger.trace("Removed " + e.getKey());
        } else {
            play.Logger.trace("Missing/wrong: " + e.getKey() + ", will fail");
        }
    }

    private static void handleOrderedValues(final HashMap<String, String> actualMap, final Entry<String, String> e) {
        play.Logger.debug("Test if proper order for: " + e.getKey());
        if (actualMap.containsKey(e.getKey())) {
            play.Logger.trace("Existing as expected: " + e.getKey());
            if (e.getValue().equals(actualMap.get(e.getKey()))) {
                play.Logger.trace("Equality:\n" + e.getValue() + "\n" + actualMap.get(e.getKey()));
                actualMap.remove(e.getKey());
            } else
                play.Logger.debug("...but not equal! Will fail");
        } else {
            play.Logger.warn("Missing: " + e.getKey() + " , will fail");
        }
    }

    /**
     * Construct a map with json paths as keys with aggregated values form json
     * nodes.
     * 
     * @param jnode
     *            the JsonNode which should be transformed into a map
     * @param map
     *            the map constructed out of the JsonNode
     */
    public void extractFlatMapFromJsonNode(final JsonNode jnode, final HashMap<String, String> map) {
        if (jnode.getNodeType().equals(JsonNodeType.OBJECT)) {
            final Iterator<Map.Entry<String, JsonNode>> it = jnode.fields();
            while (it.hasNext()) {
                final Map.Entry<String, JsonNode> entry = it.next();
                stack.push(entry.getKey());
                extractFlatMapFromJsonNode(entry.getValue(), map);
                stack.pop();
            }
        } else if (jnode.isArray()) {
            final Iterator<JsonNode> it = jnode.iterator();
            while (it.hasNext()) {
                extractFlatMapFromJsonNode(it.next(), map);
            }
        } else if (jnode.isValueNode()) {
            String value = jnode.toString();
            if (map.containsKey(stack.toString()))
                value = map.get(stack.toString()).concat("," + jnode.toString());
            map.put(stack.toString(), value);
            play.Logger.trace("Stored this path as key into map:" + stack.toString(), value);
        }
    }

    /*
     * Values may be an unorderd set.
     */
    private static boolean checkIfAllValuesAreContainedUnordered(final String actual, final String expected) {
        play.Logger.trace("\nActual   value: " + actual + "\nExpected value: " + expected);
        return valuesToList(actual).containsAll(valuesToList(expected));
    }

    private static List<String> valuesToList(final String values) {
        return Arrays.asList(values.substring(1, values.length() - 1).split("\",\""));
    }
}
