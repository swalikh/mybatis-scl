package org.apache.ibatis.xml.resolver;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class JDomTest {
    public static void main(String[] args) throws Exception {
        //通过SAXBuilder解析xml
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new File("C:\\Users\\huanglei\\Desktop\\mybatis-3.4.0\\src\\test\\java\\resources\\nodelet_test.xml"));
        Element root = doc.getRootElement();
        List<Attribute> attrs = root.getAttributes();
        attrs.forEach(attr-> System.out.println(attr.getName() + ":" + attr.getValue()));
        //删除属性url，并保存到jdom2.xml
        root.removeChild("tagName");
        XMLOutputter out = new XMLOutputter();
        out.output(doc, new FileOutputStream("xmlResolver.xml"));
    }

}
