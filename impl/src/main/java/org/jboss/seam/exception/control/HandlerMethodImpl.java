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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.solder.bean.Beans;
import org.jboss.seam.solder.literal.AnyLiteral;
import org.jboss.seam.solder.reflection.annotated.InjectableMethod;

/**
 * Implementation of {@link org.jboss.seam.exception.control.HandlerMethod}.
 *
 * @param <T> Type of the exception this handler handles.
 */
public class HandlerMethodImpl<T extends Throwable> implements HandlerMethod<T>
{
   private final Class<?> beanClass;
   private Bean<?> bean;
   private final Set<Annotation> qualifiers;
   private final Type exceptionType;
   private final AnnotatedMethod<?> handler;
   private final TraversalMode traversalMode;
   private final int precedence;
   private final Method javaMethod;

   /**
    * Sole Constructor.
    *
    * @param method found handler
    * @param bm     active BeanManager
    * @throws IllegalArgumentException if method is null, has no params or first param is not annotated with {@link
    *                                  Handles}
    */
   public HandlerMethodImpl(final AnnotatedMethod<?> method, final BeanManager bm)
   {
      final Set<Annotation> tmpQualifiers = new HashSet<Annotation>();
      if (method == null || method.getParameters() == null || method.getParameters().size() == 0)
      {
         throw new IllegalArgumentException("Method must not be null and must have at least one parameter");
      }

      this.handler = method;
      this.javaMethod = method.getJavaMember();

      // TODO: Make this use a security manager block or when Solder does it we should be fine
      if (!this.javaMethod.isAccessible())
      {
         this.javaMethod.setAccessible(true);
      }
      final AnnotatedParameter<?> handlesParam = method.getParameters().get(0);

      if (!handlesParam.isAnnotationPresent(Handles.class))
      {
         throw new IllegalArgumentException("Method is not annotated with @Handles");
      }

      this.traversalMode = handlesParam.getAnnotation(Handles.class).during();
      this.precedence = handlesParam.getAnnotation(Handles.class).precedence();
      tmpQualifiers.addAll(Beans.getQualifiers(bm, handlesParam.getAnnotations()));

      if (tmpQualifiers.isEmpty())
      {
         tmpQualifiers.add(AnyLiteral.INSTANCE);
      }

      this.qualifiers = tmpQualifiers;
      this.beanClass = method.getJavaMember().getDeclaringClass();
      this.exceptionType = ((ParameterizedType) handlesParam.getBaseType()).getActualTypeArguments()[0];
   }

   /**
    * {@inheritDoc}
    */
   public Class<?> getBeanClass()
   {
      return this.beanClass;
   }

   /**
    * {@inheritDoc}
    */
   public synchronized Bean<?> getBean(BeanManager bm)
   {
      if (this.bean == null)
      {
         this.bean = bm.resolve(bm.getBeans(this.beanClass));
      }
      return this.bean;  //To change body of implemented methods use File | Settings | File Templates.
   }

   /**
    * {@inheritDoc}
    */
   public Set<Annotation> getQualifiers()
   {
      return Collections.unmodifiableSet(this.qualifiers);
   }

   /**
    * {@inheritDoc}
    */
   public Type getExceptionType()
   {
      return this.exceptionType;
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings( { "unchecked" })
   public void notify(final CaughtException<T> event, final BeanManager bm)
   {
      CreationalContext ctx = null;
      try
      {
         ctx = bm.createCreationalContext(null);
         Object handlerInstance = bm.getReference(getBean(bm), this.beanClass, ctx);
         InjectableMethod im = new InjectableMethod(this.handler, getBean(bm), bm);
         im.invoke(handlerInstance, ctx, new OutboundParameterValueRedefiner(event, bm, getBean(bm)));
      }
      finally
      {
         if (ctx != null)
         {
            ctx.release();
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public TraversalMode getTraversalMode()
   {
      return this.traversalMode;
   }

   /**
    * {@inheritDoc}
    */
   public int getPrecedence()
   {
      return this.precedence;
   }

   /**
    * {@inheritDoc}
    */
   public Method getJavaMethod()
   {
      return this.javaMethod;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      HandlerMethodImpl<?> that = (HandlerMethodImpl<?>) o;

      if (precedence != that.precedence)
      {
         return false;
      }
      if (!beanClass.equals(that.beanClass))
      {
         return false;
      }
      if (!exceptionType.equals(that.exceptionType))
      {
         return false;
      }
      if (!handler.equals(that.handler))
      {
         return false;
      }
      if (!javaMethod.equals(that.javaMethod))
      {
         return false;
      }
      if (!qualifiers.equals(that.qualifiers))
      {
         return false;
      }
      return traversalMode == that.traversalMode;

   }

   @Override
   public int hashCode()
   {
      int result = beanClass.hashCode();
      result = 5 * result + qualifiers.hashCode();
      result = 5 * result + exceptionType.hashCode();
      result = 5 * result + handler.hashCode();
      result = 5 * result + traversalMode.hashCode();
      result = 5 * result + precedence;
      result = 5 * result + javaMethod.hashCode();
      return result;
   }

   @Override
   public String toString()
   {
      return new StringBuilder("Qualifiers: ").append(this.qualifiers).append(" ")
            .append("TraversalMode: ").append(this.traversalMode).append(" ")
            .append("Handles Type: ").append(this.exceptionType.getClass()).append(" ")
            .append("Precedence: ").append(this.precedence).append(" ")
            .append(this.handler.toString()).toString();
   }
}
