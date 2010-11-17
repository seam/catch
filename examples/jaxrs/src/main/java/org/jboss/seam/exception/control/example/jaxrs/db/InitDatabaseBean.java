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

package org.jboss.seam.exception.control.example.jaxrs.db;

import org.jboss.seam.exception.control.example.jaxrs.entity.Author;
import org.jboss.seam.exception.control.example.jaxrs.entity.Book;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

public class InitDatabaseBean
{
   @Inject private EntityManager em;

   @TransactionAttribute(TransactionAttributeType.REQUIRED)
   public void seedDatabase()
   {
      Author hal_fulton = createAuthor("Hal Fulton");
      Author max_katz = createAuthor("Max Katz");
      Author gavin_king = createAuthor("Gavin King");
      Author christian_bauer = createAuthor("Christian Bauer");
      Author dan_allen = createAuthor("Dan Allen");

      Book ruby_way = createBook("The Ruby Way");
      Book practical_richfaces = createBook("Practical RichFaces");
      Book java_persistence = createBook("Java Persistence with Hibernate");
      Book seam_in_action = createBook("Seam in Action");

      hal_fulton.addBook(ruby_way);
      ruby_way.addAuthor(hal_fulton);
      em.persist(hal_fulton);
      em.persist(ruby_way);

      max_katz.addBook(practical_richfaces);
      practical_richfaces.addAuthor(max_katz);
      em.persist(max_katz);
      em.persist(practical_richfaces);

      java_persistence.addAuthor(gavin_king);
      java_persistence.addAuthor(christian_bauer);
      gavin_king.addBook(java_persistence);
      christian_bauer.addBook(java_persistence);
      em.persist(java_persistence);
      em.persist(gavin_king);
      em.persist(christian_bauer);

      seam_in_action.addAuthor(dan_allen);
      dan_allen.addBook(seam_in_action);
      em.persist(dan_allen);
      em.persist(seam_in_action);
   }

   @TransactionAttribute(TransactionAttributeType.REQUIRED)
   public void clear()
   {
      this.em.createQuery("delete from Book").executeUpdate();
      this.em.createQuery("delete from Author").executeUpdate();
   }

   private Author createAuthor(String authorName)
   {
      final Author a = new Author();
      a.setName(authorName);
      return a;
   }

   private Book createBook(String bookTitle)
   {
      final Book b = new Book();
      b.setTitle(bookTitle);
      return b;
   }
}
