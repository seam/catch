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

package org.jboss.seam.exception.control;

import org.jboss.weld.extensions.bean.Beans;
import org.jboss.weld.extensions.reflection.annotated.InjectableMethod;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

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
   private final AnnotatedMethod handler;
   private final TraversalPath traversalPath;
   private final int precedence;
   private final Method javaMethod;

   /**
    * Sole Constructor.
    *
    * @param method found handler
    * @param bm     active BeanManager
    *
    * @throws IllegalArgumentException if method is null, has no params or first param is not annotated with {@link Handles}
    */
   public HandlerMethodImpl(final AnnotatedMethod method, final BeanManager bm)
   {
      if (method == null || method.getParameters() == null || method.getParameters().size() == 0)
      {
         throw new IllegalArgumentException("Method must not be null and must have at least one parameter");
      }

      this.handler = method;
      this.javaMethod = method.getJavaMember();
      if (!this.javaMethod.isAccessible())
      {
         this.javaMethod.setAccessible(true);
      }
      final AnnotatedParameter handlesParam = (AnnotatedParameter) method.getParameters().get(0);

      if (!handlesParam.isAnnotationPresent(Handles.class))
      {
         throw new IllegalArgumentException("Method is not annotated with @Handles");
      }

      this.traversalPath = handlesParam.getAnnotation(Handles.class).during();
      this.precedence = handlesParam.getAnnotation(Handles.class).precedence();
      this.qualifiers = Beans.getQualifiers(bm, handlesParam.getAnnotations());
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
   @SuppressWarnings({"unchecked"})
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
   public TraversalPath getTraversalPath()
   {
      return this.traversalPath;
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

      HandlerMethodImpl that = (HandlerMethodImpl) o;

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
      if (traversalPath != that.traversalPath)
      {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode()
   {
      int result = beanClass.hashCode();
      result = 5 * result + qualifiers.hashCode();
      result = 5 * result + exceptionType.hashCode();
      result = 5 * result + handler.hashCode();
      result = 5 * result + traversalPath.hashCode();
      result = 5 * result + precedence;
      result = 5 * result + javaMethod.hashCode();
      return result;
   }

   @Override public String toString()
   {
      return new StringBuilder("Qualifiers: ").append(this.qualifiers).append(" ").append(
         this.handler.toString()).toString();
   }
}
