## Mybatis源码学习---XML解析（day2）

### 一、问题汇总

#### 1.xml底层解析有哪几种常用解析方式、各有什么特点？

有两种解析方式，分别是DOM解析和SAX解析

**DOM（Document Object Model 文档对象模型）：**DOM 是基于树形结构的 XML 解析方式，它会将整个 XML 文档 入内存并构建 DOM 树，基于这棵树形结构对各个节点（Node）进行操作，XML 文档中的每个成分都是一个节点，整个文档是一个文档节点，每个标签对应一个元素节点，标签中的文本是文本节点，标签属性是属性节点，注释属于注释节点

```java
// 1.创建文档构建器工厂
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
// 2.文档构建器工厂属性设置（NS是否生效 是否忽略注释 是否忽略空格  ）
factory.setXxx();
// 3.创建文档构建器
DocumentBuilder db = factory.newDocumentBuilder();
// 4.文档构建器加载XML文件、构建Document文档
Document parse = db.parse(new File("/xxx"));
// 5.解析
NodeList nodeList = document.getElementsByTagName("tagname");
```

**SAX（Simple API for XML ）：**SAX是基于事件模型的 XML 解析方式，不支持XPath，它并不需要将整个XML文档加载到内存中，而只需将 XML文档的部分加载到内存中，即可开始解析，在处理过程中井不会在内存中记录XML 中的数据，所以占用的资源比较小。当程序处理过程中满足条件时，也可以立即停止解析过程，这样就不必解析剩余的 XML 内容，随意它能解析大于系统内存的文档。

- SAX的优点：

  1. 解析速度快
  2. 占用内存少

- SAX的缺点：

  1. 无法知道当前解析标签（节点）的上层标签，及其嵌套结构，仅仅知道当前解析的标签的名字和属性，要知道其他信息需要程序猿自己编码
  2. 只能读取XML，无法修改XML
  3. 无法随机访问某个标签（节点）

- SAX解析适用场合 

  1. 对于CPU资源宝贵的设备，如Android等移动设备

  2. 对于只需从xml读取信息而无需修改xml

```java
// 1.获取解析工厂
SAXParserFactory factory = SAXParserFactory.newInstance();
// 2.从解析工厂获取解析器
SAXParser parse = factory.newSAXParser();
// 3.得到解读器
XMLReader reader=parse.getXMLReader();
// 4.设置内容处理器
reader.setContentHandler(new MyHandler());
// 5.读取xml的文档内容
reader.parse("src/main/java/cn/xxx.xml");
```

**对比：**

1.资源占用：DOM将文档全部加载到内存中、而SAX是不纪录XML中的数据，所以SAX相比DOM占用内存更少

2.解析方式：DOM是程序驱动（拉模式-程序主动驱动解析）SAX是解析器驱动（推模式-事件驱动回调）

3.便捷属性：DOM文档结构清晰、理解和编码更加简洁，SAX需要在回调方法中处理结果，在编码上复杂性更高

4.采用频率：在内存成本低的现代，DOM方式更加流行。SAX更适用于内存资源紧张的智能家居、智能手机等各种移动终端

#### 2.java常用的xml解析框架有哪些？各有什么特点？

**JDOM：** JDOM是一个开源项目，它基于树型结构，利用纯JAVA的技术对XML文档实现解析、生成、序列化以及多种操作，它用强有力的JAVA语言的诸多特性（方法重载、集合概念以及映射），把SAX和DOM的功能有效地结合起来 

```java
// 代码示例
SAXBuilder builder = new SAXBuilder();
Document doc = builder.build(new File("resources:xxx.xml"));
Element root = doc.getRootElement();
List<Attribute> attrs = root.getAttributes();
attrs.forEach(attr-> System.out.println(attr.getName() + ":" + attr.getValue()));
root.removeChild("tagName");
XMLOutputter out = new XMLOutputter();
out.output(doc, new FileOutputStream("jdom.xml"));
```

**DOM4j：**是JDOM的优秀分支、使用方便简洁

```java
SAXReader reader = new SAXReader();
Document document = reader.read(new File("C:\\xxx.xml"));
Element root = document.getRootElement();
```

#### 3.xpath是什么玩意儿？

MyBatis 在初始化过程中处理 mybatis-config 配置文件以及mapper映射文件时，使用的是 DOM 解析方式，井结合使用 XPath 解析 XML 配置文件。DOM会将整个XML文档加载到内存中并形成树状数据结构，而XPath是为查询 XML文档而设计的语言， 就好比SQL语言和数据库的关系。 

| 表达式   | 描述                                                       | 示例           | 结果                              |
| -------- | ---------------------------------------------------------- | -------------- | --------------------------------- |
| nodename | 选取此节点的所有子节点                                     | bookstore      | 选取bookstore下所有的子节点       |
| /        | 如果在最前面,代表从根节点选取.否则选择某节点下的某个子节点 | /bookstore     | 选取根元素下所有的bookstore节点   |
| //       | 从全局节点中选择节点,随便在哪个位置                        | //book         | 从全局节点中找到所有的book节点    |
| @        | 选取某个节点的属性                                         | //book[@class] | 选择所有book中拥有class属性的节点 |

谓语用来查找某个特定的节点或者包含某个指定的值的节点,被嵌在方括号中，在下面的表格中,我们列出了带有谓语的一些路径表达式,以及表达的结果

| 路径表达式                      | 描述                              |
| ------------------------------- | --------------------------------- |
| /bookstore/book[1]              | 选取bookstore下的第一个子元素     |
| /bookstore/book[last()]         | 选取bookstore下的最后一个book元素 |
| `/bookstore/book[position()<3]` | 选取bookstore下前面两个子元素     |
| /book[@price]                   | 选取拥有price属性的book元素       |
| /book[@price=10]                | 选取所有属性price等于10的book元素 |
| /book[contains(@class, ‘fl’)]   | 模糊匹配class属性中有`fl`值的标签 |

#### 4.mybatis框架中是如何解析xml文件的？
mybatis中的xml文件主要有两类，一类是本身的mybatis-config.xml配置文件，另一类是mapper文件。这两种文件单个文件不会很大，所以mybatis采用的是DOM的方式读取的，并且没有使用任何第三方框架。但是有根据需要写了一套xml读取的工具包，在org.apache.ibatis.parsing包下。里面包含6个类，其中核心类为XPathparser和XNode，XPathParser实现了xml的读取和DOM解析，XNode实现了Node封装，可以更加方便的以树形结构展示XML信息

GenericTokenParser: 标准标记转换器 根据有标记key取代为理想的值 核心方法是parse逻辑有点多
ParsingException: xml转换异常实体 继承了runtimeException
PropertyParser: 键值对标记转换器 核心静态方法PropertyParser.parse("",键值对)---策略模式，根据键值对文件来取代有标记的${key}，填补value值 对GenericTokenParser进行了封装
TokenHandler: 标记处理器 用来处理转换逻辑
XNode: XML节点 对原生node进行了一次封装，包含了XPathParser对象，用来根据层级关系来解析xml内容
XPathParser: XPath转换器 里面封装了 XPath Document properties对象 用来根据XPath语言解析xml内容

##### 以解析mybatis-config.xml中的datasource为例，来讲述mybatis中xml解析的大致流程

```
<environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
</environments>
1.创建XPathParser对象，构造方法传入mybatis-config.xml文件输入流，和db.properties文件，初始化XpathParser对象
2.XpathParser对象初始化过程中会使用原生的DOM树，将配置文件转换成Document对象，赋值成员变量上面
3.调用该对象的evalNode("/environments/environment/dataSource")方法，得到dataSource节点XNode对象，与此同时会将XpathParser赋值到XNode的成员变量上
4.XNode对象的getChildrenAsProperties方法就可以将datasource的四个信息name,value转换成properties对象，同时会根据传入的key-value键值对将其中的${key}替换为value
```


### 二、重点纪录

#### 1.
