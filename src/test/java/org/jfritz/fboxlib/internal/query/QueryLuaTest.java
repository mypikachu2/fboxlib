package org.jfritz.fboxlib.internal.query;

import junit.framework.Assert;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.util.Vector;

public class QueryLuaTest {

    private QueryLua query = new QueryLua(null);

    @Test
    public void testSimple() throws ParseException {
        String testString = "{\"0\":\"113.06.51\"}";

        Vector<String> result = query.parseResponseToVectorString(testString);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("113.06.51", result.get(0));
    }

    @Test
    public void testCompact() throws ParseException {
        String testString = "{\"0\":\"113.06.51\",\"1\":\"20\",\"2\": \"0\"}";

        Vector<String> result = query.parseResponseToVectorString(testString);
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("113.06.51", result.get(0));
        Assert.assertEquals("20", result.get(1));
        Assert.assertEquals("0", result.get(2));
    }

    @Test
    public void testPrettyPrint() throws ParseException {
        String testString = "\n  {\n    \"0\": \"113.06.51\",\n    \"1\": \"20\",\n		 \"2\": \"0\"\n	}\n";

        Vector<String> result = query.parseResponseToVectorString(testString);
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("113.06.51", result.get(0));
        Assert.assertEquals("20", result.get(1));
        Assert.assertEquals("0", result.get(2));
    }
}
