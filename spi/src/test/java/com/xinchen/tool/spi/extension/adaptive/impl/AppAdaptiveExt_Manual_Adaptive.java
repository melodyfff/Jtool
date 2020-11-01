package com.xinchen.tool.spi.extension.adaptive.impl;

import com.xinchen.tool.spi.URL;
import com.xinchen.tool.spi.extension.Adaptive;
import com.xinchen.tool.spi.extension.ExtensionLoader;
import com.xinchen.tool.spi.extension.adaptive.AppAdaptiveExt;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 17:43
 */
@Adaptive
public class AppAdaptiveExt_Manual_Adaptive implements AppAdaptiveExt {

    @Override
    public String echo(URL url, String message) {
        // 这里不会有AppAdaptiveExt_Manual_Adaptive这个类的信息，即使指定URL key=adaptive，也会报错
        // 在cachedClass中没存@Adaptive标记的类的信息
        AppAdaptiveExt ext = ExtensionLoader.getExtensionLoader(AppAdaptiveExt.class).getExtension(url.getParameter("key"));
        return ext.echo(url, message);
    }
}
