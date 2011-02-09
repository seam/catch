<%--
  JBoss, Home of Professional Open Source
  Copyright 2011, Red Hat, Inc., and individual contributors
  by the @authors tag. See the copyright.txt in the distribution for a
  full listing of individual contributors.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<fmt:bundle basename="org.jboss.seam.exception.example.basic.servlet.messages">
<html>
   <head>
      <title><fmt:message key="index_title" /></title>
   </head>
   <body>
      <h1></h1>
         <p><fmt:message key="index_links_desc" /></p>
         <ul>
            <li><a href="Navigation/NullPointerException"><fmt:message key="index_links_nullpointer" /></a></li>
            <li><a href="Navigation/ServletException"><fmt:message key="index_links_servletexception" /></a></li>
         </ul>
   </body>
</html>
</fmt:bundle>
