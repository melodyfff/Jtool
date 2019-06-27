package com.xinchen.xml;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *

 * {@link XmlAccessorType} 注解的参数：
 * 1、XmlAccessType.PROPERTY 会绑定类中所有的getter/setter方法，而且每个成员变量的getter和setter方法都必须存在。
 * 2、XmlAccessType.FIELD 会绑定类中所有的非静态和没有@XmlTransient注解的成员变量。
 * 3、XmlAccessType.PUBLIC_MEMBER 会绑定类中所有的getter/setter方法和public修饰的成员变量，但是@XmlTransient注解的除外。
 * 4、XmlAccessType.NONE 没有任何变量和方法会被绑定，但是使用@XmlElement和@XmlAttribute的变量和方法还是会被绑定。
 *
 * 注：以上的4中参数中，除了NONE外其他的都会自动绑定成员变量 或者 是getter/setter方法，
 * 这里需要注意，PROPERTY和PUBLIC_MEMBER参数会自动绑定getter/setter方法，而在成员变量上再使用@XmlElement会报错说“类的两个属性具有相同名称”，
 * 同样的，FIELD参数绑定了成员变量，而在getter/setter方法上使用@XmlElement也会报错说“类的两个属性具有相同名称”。
 * NONE参数虽然不指定任何绑定，但是如果同时在成员变量和getter/setter方法上使用@XmlElement也会报错。
 * 即：不能对同一个变量使用两次绑定。
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/6/27 22:50
 */
@XmlRootElement(name = "rick",namespace = XMLSchemaDict.NAMESPACE_S1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name","age","address","morties"})
@Data
public class Rick {
    @XmlAttribute
    private int no;
    private String name;
    private int age;
    private String address;

    @XmlElementWrapper(name = "Rick's_Morty",namespace = XMLSchemaDict.NAMESPACE_S1)
    @XmlElement(name = "Morty")
    private List<Morty> morties = new ArrayList<>();
}
