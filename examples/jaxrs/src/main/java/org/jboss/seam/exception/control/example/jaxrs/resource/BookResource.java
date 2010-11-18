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

package org.jboss.seam.exception.control.example.jaxrs.resource;

import org.jboss.seam.exception.control.example.jaxrs.entity.Book;
import org.jboss.seam.rest.validation.ValidateRequest;

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
import java.util.List;

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
      return this.em.createNamedQuery("books", Book.class).getResultList();
   }

   @GET
   @Path("{title:[A-Za-z]+}")
   public Book getAuthorByName(@PathParam("title") String authorName)
   {
      return this.em.createNamedQuery("booksByTitle", Book.class).setParameter("title", authorName).getSingleResult();
   }
}
