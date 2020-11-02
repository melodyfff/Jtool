package com.xinchen.tool.spi.compiler.support;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class JavaCodeTest {

    public final static AtomicInteger SUBFIX = new AtomicInteger(8);

    String getSimpleCode() {
        StringBuilder code = new StringBuilder();
        code.append("package com.xinchen.tool.spi.compiler.support;");

        code.append("public class HelloServiceImpl" + SUBFIX.getAndIncrement() + " implements HelloService {");
        code.append("   public String sayHello() { ");
        code.append("       return \"Hello world!\"; ");
        code.append("   }");
        code.append("}");
        return code.toString();
    }

    String getSimpleCodeWithoutPackage(){
        StringBuilder code = new StringBuilder();
        code.append("public class HelloServiceImpl" + SUBFIX.getAndIncrement() + "implements com.xinchen.tool.spi.compiler.support.HelloService.HelloService {");
        code.append("   public String sayHello() { ");
        code.append("       return \"Hello world!\"; ");
        code.append("   }");
        code.append("}");
        return code.toString();
    }

    String getSimpleCodeWithSyntax(){
        StringBuilder code = new StringBuilder();
        code.append("package com.xinchen.tool.spi.compiler.support;");

        code.append("public class HelloServiceImpl" + SUBFIX.getAndIncrement() + " implements HelloService {");
        code.append("   public String sayHello() { ");
        code.append("       return \"Hello world!\"; ");
        // code.append("   }");
        // }
        return code.toString();
    }

    // only used for javassist
    String getSimpleCodeWithSyntax0(){
        StringBuilder code = new StringBuilder();
        code.append("package com.xinchen.tool.spi.compiler.support;");

        code.append("public class HelloServiceImpl_0 implements HelloService {");
        code.append("   public String sayHello() { ");
        code.append("       return \"Hello world!\"; ");
        // code.append("   }");
        // }
        return code.toString();
    }

    String getSimpleCodeWithImports() {
        StringBuilder code = new StringBuilder();
        code.append("package com.xinchen.tool.spi.compiler.support;");

        code.append("import java.lang.*;\n");
        code.append("import com.xinchen.tool.spi.compiler.support;\n");

        code.append("public class HelloServiceImpl2" + SUBFIX.getAndIncrement() + " implements HelloService {");
        code.append("   public String sayHello() { ");
        code.append("       return \"Hello world!\"; ");
        code.append("   }");
        code.append("}");
        return code.toString();
    }

    String getSimpleCodeWithWithExtends() {
        StringBuilder code = new StringBuilder();
        code.append("package com.xinchen.tool.spi.compiler.support;");

        code.append("import java.lang.*;\n");
        code.append("import com.xinchen.tool.spi.compiler.support;\n");

        code.append("public class HelloServiceImpl" + SUBFIX.getAndIncrement() + " extends com.xinchen.tool.spi.compiler.support.HelloServiceImpl0 {\n");
        code.append("   public String sayHello() { ");
        code.append("       return \"Hello world3!\"; ");
        code.append("   }");
        code.append("}");
        return code.toString();
    }
}
