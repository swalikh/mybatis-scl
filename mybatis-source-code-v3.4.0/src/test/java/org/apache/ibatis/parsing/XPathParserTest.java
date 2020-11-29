/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.parsing;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.junit.Test;

public class XPathParserTest {

  @Test
  public void shouldTestXPathParserMethods() throws Exception {
    String resource = "resources/nodelet_test.xml";
    InputStream inputStream = Resources.getResourceAsStream(resource);
    XPathParser parser = new XPathParser(inputStream, false, null, null);
    assertEquals((Long)1970l, parser.evalLong("/employee/birth_date/year"));
    assertEquals((short) 6, (short) parser.evalShort("/employee/birth_date/month"));
    assertEquals((Integer) 15, parser.evalInteger("/employee/birth_date/day"));
    assertEquals((Float) 5.8f, parser.evalFloat("/employee/height"));
    assertEquals((Double) 5.8d, parser.evalDouble("/employee/height"));
    assertEquals("${id_var}", parser.evalString("/employee/@id"));
    assertEquals(Boolean.TRUE, parser.evalBoolean("/employee/active"));
    assertEquals("<id>${id_var}</id>", parser.evalNode("/employee/@id").toString().trim());
    assertEquals(7, parser.evalNodes("/employee/*").size());
    XNode node = parser.evalNode("/employee/height");
    assertEquals("employee/height", node.getPath());
    assertEquals("employee[${id_var}]_height", node.getValueBasedIdentifier());
  }

  @Test
  public void testTokenParser() throws IOException {
      String resource = "resources/mybatis-config.xml";
      InputStream inputStream = Resources.getResourceAsStream(resource);
      Properties variables = new Properties();
      variables.put("url","jdbc://10.11.11.11");
      variables.put("driver","com.jdbc.cj.driver");
      variables.put("username","admin");
      variables.put("passor","123456");
      XPathParser parser = new XPathParser(inputStream,false,variables);
      XNode xNode = parser.evalNode("/environments/environment/dataSource");
      Properties children = xNode.getChildrenAsProperties();
      System.out.println(children);
  }

}
