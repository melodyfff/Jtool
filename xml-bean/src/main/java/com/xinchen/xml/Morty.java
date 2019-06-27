package com.xinchen.xml;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/6/27 23:12
 */
@XmlRootElement(name = "Morty")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name","age","address","ricks"})
@Data
public class Morty {
    @XmlAttribute
    private int no;
    private String name;
    private int age;
    private String address;

    private List<Rick> ricks = new ArrayList<>();

}
