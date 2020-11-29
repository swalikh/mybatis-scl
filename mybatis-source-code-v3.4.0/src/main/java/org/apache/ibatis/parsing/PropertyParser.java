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

import java.util.Properties;

/**
 * @author Clinton Begin
 * 属性：静态工具类
 * 键值标记转换器，在普通标记转换器上做了一层封装 用来实现 ${key} 在properties中的 key-value转换
 */
public class PropertyParser {

  private PropertyParser() {
    // Prevent Instantiation
  }

  public static String parse(String string, Properties variables) {
    VariableTokenHandler handler = new VariableTokenHandler(variables);
    GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
    return parser.parse(string);
  }

  // 属性：静态内部类 实现了标记处理器接口TokenHandler 通过 key 查找--->value
  private static class VariableTokenHandler implements TokenHandler {
    // 键值对Map
    private Properties variables;

    public VariableTokenHandler(Properties variables) {
      this.variables = variables;
    }

    @Override
    public String handleToken(String content) {
      // 如果 key 在集合中能找得到、就替换为值，否则就不作处理
      if (variables != null && variables.containsKey(content)) {
        return variables.getProperty(content);
      }
      return "${" + content + "}";
    }
  }
}
