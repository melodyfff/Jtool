package com.xinchen.copy;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/1/1 16:02
 */
public class Test {

    public static void main(String[] args){
        Coffee a = new Coffee("Latte","China");
        Coffee b = a.serialClone();

        System.out.println(a);
        System.out.println(b);

        System.out.println(a.getOrigin());
        System.out.println(b.getOrigin());


        final Coffee copy = (Coffee) CopyUtils.copy(a);
        if (null != copy){
            System.out.println(copy.getCoffeeName());
            System.out.println(copy.getOrigin().getOriginName());
        }
    }
}
