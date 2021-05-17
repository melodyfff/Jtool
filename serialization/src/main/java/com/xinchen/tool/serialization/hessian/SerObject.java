package com.xinchen.tool.serialization.hessian;

import java.io.Serializable;
import java.util.List;

/**
 * @author xin chen
 * @version 1.0.0
 * @date 2021/3/3 13:22
 */
public class SerObject implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private List<String> addresses;

    public SerObject(long id, String name, List<String> addresses) {
        this.id = id;
        this.name = name;
        this.addresses = addresses;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "SerObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", addresses=" + addresses +
                '}';
    }
}
