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

package org.jboss.seam.exception.control.example.jaxrs.handler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.seam.exception.control.ExceptionToCatchEvent;

@Provider
@ApplicationScoped
public class CatchBridge implements ExceptionMapper<Throwable>
{
//   @Inject @RestCatch
//   private Response.ResponseBuilder responseBuilder;

   @Inject Event<ExceptionToCatchEvent> event;

   @Inject BeanManager bm;

   public Response toResponse(Throwable exception)
   {
      final Class<Response.ResponseBuilder> responseBuilderType = Response.ResponseBuilder.class;

      final Bean<?> bean = this.bm.resolve(this.bm.getBeans(responseBuilderType, RestCatchLiteral.INSTANCE));
      final CreationalContext<?> ctx = this.bm.createCreationalContext(bean);
      final Response.ResponseBuilder responseBuilder = (Response.ResponseBuilder) this.bm.getReference(bean,
                                                                                                       responseBuilderType,
                                                                                                       ctx);

      this.bm.fireEvent(new ExceptionToCatchEvent(exception, RestCatchLiteral.INSTANCE));
//      event.fire(new ExceptionToCatchEvent(exception, RestCatchLiteral.INSTANCE));
      return responseBuilder.build();
   }
}
