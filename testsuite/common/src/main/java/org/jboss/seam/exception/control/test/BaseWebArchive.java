/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.seam.exception.control.test;

import java.util.Collection;
import java.util.HashSet;

import javax.enterprise.inject.spi.Extension;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.extension.CatchExtension;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

/**
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
public final class BaseWebArchive {
    private static Collection<JavaArchive> libraries = new HashSet<JavaArchive>(5);

    public static WebArchive createBase(final String name) {
        if (libraries.isEmpty()) {
            // The JBoss Repository must be defined in the user's settings.xml for this to work.
            libraries.addAll(DependencyResolvers.use(MavenDependencyResolver.class)
                    .artifacts("org.jboss.seam.solder:seam-solder:3.0.0.Final").resolveAs(JavaArchive.class));
        }

        return ShrinkWrap.create(WebArchive.class, name)
                .addPackage(CaughtException.class.getPackage())
                .addClass(CatchExtension.class)
                .addAsServiceProvider(Extension.class, CatchExtension.class)
                .addAsLibraries(libraries)
                .addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
    }
}
