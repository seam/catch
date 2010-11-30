package org.jboss.seam.exception.control.example.jaxrs.handler;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExceptionResponseMappings
{
   private List<ExceptionResponse> mappings;

   public List<ExceptionResponse> getMappings()
   {
      return mappings;
   }

   public void setMappings(List<ExceptionResponse> mappings)
   {
      this.mappings = mappings;
   }
}
