/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.exceptionhandling;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Finds and invokes all {@link ExceptionHandler} instants for a particular exception and {@link State}. <p> If any handlers are
 * found and invoked the the {@link ExceptionEvent#setExceptionHandled(boolean)} is set to true. </p>
 */
public class ExceptionHandlerExecutor {
    /**
     * Observes the event, finds the correct exception handler(s) and invokes them.
     *
     * @param event Event Payload
     */
    @SuppressWarnings("unchecked")
    public void executeHandlers(@Observes ExceptionEvent event) {
        final HandlerChain chain = new HandlerChainImpl();
        final Stack<Throwable> unwrappedExceptions = new Stack<Throwable>();
        final State state = event.getState();
        final BeanManager beanManager = state.getBeanManager();
        final List<ExceptionHandler> allHandlers = this.getContextualExceptionHandlerInstances(beanManager);

        Throwable exception = event.getException();
        MethodParameterTypeHelper methodParameterTypeHelper;

        Collections.sort(allHandlers, new ExceptionHandlerComparator());

        do {
            unwrappedExceptions.push(exception);
        }
        while ((exception = exception.getCause()) != null);

        // Finding the correct exception handlers using reflection based on the method
        // to determine if it's the correct
        Throwable unwrapped;
        while (!unwrappedExceptions.isEmpty() && !((HandlerChainImpl) chain).isChainEnd()) {
            unwrapped = unwrappedExceptions.pop();
            for (ExceptionHandler handler : allHandlers) {
                methodParameterTypeHelper = new MethodParameterTypeHelper(handler);

                if (methodParameterTypeHelper.containsExceptionTypeOrSuperType(unwrapped.getClass())
                        && methodParameterTypeHelper.containsStateTypeOrSuperType(state.getClass())) {
                    handler.handle(chain, state, unwrapped);
                    event.setExceptionHandled(true);

                    if (((HandlerChainImpl) chain).isChainEnd()) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Method taken from Seam faces BeanManagerUtils.
     *
     * @param manager BeanManager instance
     * @return List of instantiated  found by the bean manager
     */
    @SuppressWarnings("unchecked")
    private List<ExceptionHandler> getContextualExceptionHandlerInstances(BeanManager manager) {
        List<ExceptionHandler> result = new ArrayList<ExceptionHandler>();
        for (Bean<?> bean : manager.getBeans(ExceptionHandler.class)) {
            CreationalContext<ExceptionHandler> context = (CreationalContext<ExceptionHandler>) manager.createCreationalContext(
                    bean);
            if (context != null) {
                result.add((ExceptionHandler) manager.getReference(bean, ExceptionHandler.class, context));
            }
        }
        return result;
    }
}
