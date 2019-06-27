package com.xinchen.xml;

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * 参考 ： https://blog.csdn.net/freelk/article/details/79656950
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/6/27 22:53
 */
public class Client {
    public static void main(String[] args) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Rick.class);
        final Marshaller marshaller = context.createMarshaller();
        // 格式化输出
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        // 设置namespace前缀
        marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper",new NamespacePrefixMapper() {
            @Override
            public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
                if (XMLSchemaDict.NAMESPACE_S1.equals(namespaceUri)){
                    return XMLSchemaDict.NAMESPACE_S1_PREF;
                }
                return suggestion;
            }
        });

        Rick rick = new Rick();
        rick.setAge(20);
        rick.setAddress("ok");

        rick.getMorties().add(new Morty());
        rick.getMorties().add(new Morty());
        marshaller.marshal(rick,System.out);

    }
}
