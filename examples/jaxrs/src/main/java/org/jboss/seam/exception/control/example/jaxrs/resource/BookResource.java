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

package org.jboss.seam.exception.control.example.jaxrs.resource;

import java.security.AccessControlException;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.seam.exception.control.example.jaxrs.entity.Book;
import org.jboss.seam.rest.validation.ValidateRequest;

@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
@Path("book")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@ValidateRequest
@Stateless
public class BookResource
{
   @PersistenceContext
   private EntityManager em;

   @GET
   public List<Book> getAllAuthors()
   {
      List<Book> books = em.createNamedQuery("books", Book.class).getResultList();
      // initialize collectin; could do this with a join fetch
      for (Book b : books)
      {
         b.getAuthors().size();
      }
      return books;
   }

   @GET
   @Path("{id:[1-9][0-9]*}")
   //@Path("{id}")
   public Book getBookById(@PathParam("id") Long bookId)
   {
      if (bookId == 2)
      {
         throw new AccessControlException("No access");
      }
      Book b = em.createNamedQuery("booksById", Book.class).setParameter("id", bookId).getSingleResult();
      // initialize collection; could do this with a join fetch
      b.getAuthors().size();
      return b;
   }
}
