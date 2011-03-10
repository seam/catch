/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.seam.exception.control;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

public interface HandlerMethod<T extends Throwable> {
    /**
     * Obtains the bean class of the bean that declares the observer method.
     */
    Class<?> getBeanClass();

    /**
     * Obtains the Bean reference that declares the observer method.
     *
     * @param bm Active BeanManager
     */
    Bean<?> getBean(BeanManager bm);

    /**
     * Obtains the set of handled event qualifiers.
     */
    Set<Annotation> getQualifiers();

    /**
     * Obtains the handled event type.
     */
    Type getExceptionType();

    /**
     * Calls the handler method, passing the given event object.
     *
     * @param event event to pass to the handler.
     * @param bm    Active BeanManager
     */
    void notify(CaughtException<T> event, BeanManager bm);

    /**
     * Obtains the direction of the traversal path the handler will be listening.
     */
    TraversalMode getTraversalMode();

    /**
     * Obtains the precedence of the handler.
     */
    int getPrecedence();

    /**
     * Obtains the actual method of the handler.
     */
    Method getJavaMethod();

    /**
     * Obtains the actual parameter defining this as a handler.
     */
    AnnotatedParameter<?> getHandlerParameter();
}
