package com.xinchen.tool.spi.context;

import com.xinchen.tool.spi.extension.ExtensionLoader;

/**
 *
 * App component Lifecycle
 *
 *
 * @see ExtensionLoader#createExtension - initExtension(T instance)
 *
 * @author xinchen
 * @version 1.0
 * @date 30/10/2020 16:01
 */
public interface Lifecycle {
    /**
     * Initialize the component before {@link #start() start}
     *
     * @return current {@link Lifecycle}
     * @throws IllegalStateException
     */
    void initialize() throws IllegalStateException;

    /**
     * Start the component
     *
     * @return current {@link Lifecycle}
     * @throws IllegalStateException
     */
    void start() throws IllegalStateException;

    /**
     * Destroy the component
     *
     * @throws IllegalStateException
     */
    void destroy() throws IllegalStateException;
}
