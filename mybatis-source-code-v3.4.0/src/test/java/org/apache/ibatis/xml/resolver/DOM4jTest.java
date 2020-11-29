package org.apache.ibatis.xml.resolver;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;

public class DOM4jTest {
    public static void main(String[] args) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File("C:\\Users\\huanglei\\Desktop\\mybatis-3.4.0\\src\\test\\java\\resources\\nodelet_test.xml"));
        Element rootElement = document.getRootElement();
    }

}
