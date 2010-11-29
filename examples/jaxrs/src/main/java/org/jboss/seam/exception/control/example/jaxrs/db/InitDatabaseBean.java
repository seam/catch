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

package org.jboss.seam.exception.control.example.jaxrs.db;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.exception.control.example.jaxrs.entity.Author;
import org.jboss.seam.exception.control.example.jaxrs.entity.Book;

@Stateless
public class InitDatabaseBean
{
   @PersistenceContext
   private EntityManager em;

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
