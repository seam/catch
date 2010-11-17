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

package org.jboss.seam.exception.control.example.jaxrs.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ReturnOfCollectionOrArrayField"})
@Entity
@XmlRootElement
@NamedQueries({
   @NamedQuery(name = "authorsByName", query = "select a from Author a where a.name = :name"),
   @NamedQuery(name = "authors", query = "select a from Author a")
})
public class Author
{
   private Long id;
   private String name;
   private List<Book> books;

   public void addBook(Book newBook)
   {
      if (this.books == null)
      {
         this.books = new ArrayList<Book>();
      }
      this.books.add(newBook);
   }

   @ManyToMany
   public List<Book> getBooks()
   {
      return books;
   }

   public void setBooks(List<Book> books)
   {
      this.books = books;
   }

   @XmlAttribute
   @GeneratedValue
   @Id
   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   @NotNull
   @Size(min = 1, max = 100)
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
}
