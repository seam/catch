/*
 * JBoss, Home of Professional Open Source
 * Copyright [2010], Red Hat, Inc., and individual contributors
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

package org.jboss.seam.exception.control.example.jaxrs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.seam.exception.control.example.jaxrs.handler.CatchExceptionMapper;
import org.jboss.seam.exception.control.example.jaxrs.resource.AuthorResource;
import org.jboss.seam.exception.control.example.jaxrs.resource.BookResource;

@ApplicationPath("/api")
public class LibraryApplication extends Application
{
   @Override
   public Set<Class<?>> getClasses()
   {
      final Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.addAll(Arrays.asList(CatchBridge.class, AuthorResource.class, BookResource.class));

      return classes;
   }
}
