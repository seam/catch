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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipFile;

import javax.enterprise.inject.spi.Extension;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.extension.CatchExtension;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
public final class BaseWebArchive {
    private static volatile JavaArchive solderJar = null;
    private static final String SOLDER_VERSION = "3.0.0.Final";
    private static final String SOLDER_NAME = "seam-solder-" + SOLDER_VERSION + ".jar";
    private static final String SOLDER_PATH = "target" + File.separator;
    private static final String JBOSS_REPO = "http://repository.jboss.org/nexus/content/groups/public/org/jboss/seam/solder/seam-solder/";
    private static final String SOLDER_URL_STRING = JBOSS_REPO + SOLDER_VERSION + "/" + SOLDER_NAME;

    public synchronized static WebArchive createBase(final String name) {
        // Look to see if we have solder saved already
        if (solderJar == null) {
            if (new File(SOLDER_PATH + SOLDER_NAME).exists()) {
                ZipFile solder_jar_zip = null;
                try {
                    solder_jar_zip = new ZipFile(SOLDER_PATH + SOLDER_NAME);
                    solderJar = ShrinkWrap.create(ZipImporter.class, SOLDER_NAME).importFrom(solder_jar_zip).as(JavaArchive.class);
                } catch (IOException ioe) {
                    // Not there or couldn't open it, go fetch solder
                    // Not sure how we'd ever get here, honestly
                } finally {
                    try {
                        if (solder_jar_zip != null) {
                            solder_jar_zip.close();
                        }
                    } catch (IOException e) {
                        // Swallow
                    }
                }
            } else {
                try {
                    final URL solderUrl = new URL(SOLDER_URL_STRING);
                    final Asset solderAsset = new UrlAsset(solderUrl);

                    // Save it off for future fast opening done above
                    solderJar = ShrinkWrap.create(ZipImporter.class).importFrom(solderAsset.openStream()).as(JavaArchive.class);

                    final FileOutputStream solderOutputStream = new FileOutputStream(new File(SOLDER_PATH, SOLDER_NAME));
                    solderJar.as(ZipExporter.class).exportTo(solderOutputStream);
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Unable to retrieve solder", e);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Unable to export solder jar", e);
                }
            }
        }

        return ShrinkWrap.create(WebArchive.class, name)
                .addPackage(CaughtException.class.getPackage())
                .addClass(CatchExtension.class)
                .addAsServiceProvider(Extension.class, CatchExtension.class)
                .addAsLibraries(solderJar)
                .addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
    }
}
