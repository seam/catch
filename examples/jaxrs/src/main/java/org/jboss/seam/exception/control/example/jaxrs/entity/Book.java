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

package org.jboss.seam.exception.control.example.jaxrs.entity;

import java.util.ArrayList;
import java.util.List;

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

@SuppressWarnings({"ReturnOfCollectionOrArrayField"})
@Entity
@XmlRootElement
@NamedQueries({
   @NamedQuery(name = "booksById", query = "select b from Book b where b.id = :id"),
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
