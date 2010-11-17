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
   @NamedQuery(name = "booksByTitle", query = "select b from Book b where b.title = :title"),
   @NamedQuery(name = "books", query = "select b from Book b")
})

public class Book
{
   private Long id;
   private String title;
   private List<Author> authors;

   public void addAuthor(Author newAuthor)
   {
      if (this.authors == null)
      {
         this.authors = new ArrayList<Author>();
      }
      this.authors.add(newAuthor);
   }

   @ManyToMany
   public List<Author> getAuthors()
   {
      return authors;
   }

   public void setAuthors(List<Author> authors)
   {
      this.authors = authors;
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
   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }
}
