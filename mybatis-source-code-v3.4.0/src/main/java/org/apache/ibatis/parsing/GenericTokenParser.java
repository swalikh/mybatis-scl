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

/**
 * @author Clinton Begin
 * 属性：普通工具类
 * 普通记号转换器---mybatis中用来出来 #{aaa} 和 ${bbb} 之类的占位符
 * 单例 --- 一个对象只能处理一种类型的占位符
 */
public class GenericTokenParser {

  // 开始的标记
  private final String openToken;
  // 结束的标记
  private final String closeToken;
  /**
   * 标记处理器：用来处理替换为什么的逻辑区域 需要实现类
   * {@link org.apache.ibatis.parsing.TokenHandler#handleToken(String)}
   * */
  private final TokenHandler handler;

  // 初始化必要对象
  public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
    this.openToken = openToken;
    this.closeToken = closeToken;
    this.handler = handler;
  }

  // 替换逻辑
  public String parse(String text) {
    // 最终输出文字buffer
    final StringBuilder builder = new StringBuilder();
    final StringBuilder expression = new StringBuilder();
    if (text != null && text.length() > 0) {
      char[] src = text.toCharArray();
      // 补偿 标记开始的地方
      int offset = 0;
      // indexOf("样本",起始位置); 没找到返回 -1 找到了返回第一个匹配的下角标
      int start = text.indexOf(openToken, offset);
      while (start > -1) {
        // 如果有前面的符号是反斜杠 就什么都不做 存aaa#{
        if (start > 0 && src[start - 1] == '\\') {
          // 新版已经没有调用subString了，改为调用如下的offset方式，提高了效率
          // append(char[] str, int 开始位置, int 多少位)
          builder.append(src, offset, start - offset - 1).append(openToken);
          // 起始位置后移一个前标记单位
          offset = start + openToken.length();
        } else {
          // found open token. let's search close token.
          // StringBuilder中的setLength(0)，本意是清空StringBuilder后重新写入
          expression.setLength(0);
          // 将前标记以前的部分存储 存aaa
          builder.append(src, offset, start - offset);
          offset = start + openToken.length();
          // 寻找该前标记为对应的尾标记为地址
          int end = text.indexOf(closeToken, offset);
          while (end > -1) {
            if (end > offset && src[end - 1] == '\\') {
              // this close token is escaped. remove the backslash and continue.
              // 如果未标记位有转义符号\，则将表达式原封不动存下来 bbb}
              expression.append(src, offset, end - offset - 1).append(closeToken);
              offset = end + closeToken.length();
              end = text.indexOf(closeToken, offset);
            } else {
              // 否则就正常情况，存 bbb，退出循环
              expression.append(src, offset, end - offset);
              offset = end + closeToken.length();
              break;
            }
          }
          if (end == -1) {
            // 没找到结尾标志位 close token 则后面全部全存 bbb}ccc
            builder.append(src, start, src.length - start);
            offset = src.length;
          } else {
            // 完成替换、替换逻辑在handleToken方法的实现中
            builder.append(handler.handleToken(expression.toString()));
            offset = end + closeToken.length();
          }
        }
        start = text.indexOf(openToken, offset);
      }
      // 如果后标志位后面还有内容、就把它加上去
      if (offset < src.length) {
        // 后标志位后面的部分
        builder.append(src, offset, src.length - offset);
      }
    }
    return builder.toString();
  }
}
