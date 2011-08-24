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
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
public final class BaseWebArchive {
    private static String SOLDER_PATH;
    private static String SOLDER_LOGGING_PATH;
    private static String SOLDER_API_PATH;

    static {
        try {
            SOLDER_PATH = File.createTempFile("solder", ".jar").getParent();
            SOLDER_LOGGING_PATH = File.createTempFile("solder-logging", ".jar").getParent();
            SOLDER_API_PATH = File.createTempFile("solder-api", ".jar").getParent();
        } catch (IOException e) {
            throw new RuntimeException("Error creating temp files", e);
        }
    }

    private static volatile JavaArchive solderJar = null;
    private static volatile JavaArchive solderLoggingJar = null;
    private static volatile JavaArchive solderApiJar = null;

    private static final String SOLDER_VERSION = "3.1.0.Beta2";

    private static final String SOLDER_NAME = "seam-solder-" + SOLDER_VERSION + ".jar";
    private static final String SOLDER_LOGGING_NAME = "seam-solder-logging-" + SOLDER_VERSION + ".jar";
    private static final String SOLDER_API_NAME = "seam-solder-api-" + SOLDER_VERSION + ".jar";

    private static final String SOLDER_JBOSS_REPO = "https://repository.jboss.org/nexus/content/groups/public/org/jboss/seam/solder/seam-solder/";
    private static final String SOLDER_LOGGING_JBOSS_REPO = "https://repository.jboss.org/nexus/content/groups/public/org/jboss/seam/solder/seam-solder-logging/";
    private static final String SOLDER_API_JBOSS_REPO = "https://repository.jboss.org/nexus/content/groups/public/org/jboss/seam/solder/seam-solder-api/";

    private static final String SOLDER_URL_STRING = SOLDER_JBOSS_REPO + SOLDER_VERSION + "/" + SOLDER_NAME;
    private static final String SOLDER_LOGGING_URL_STRING = SOLDER_LOGGING_JBOSS_REPO + SOLDER_VERSION + "/" + SOLDER_LOGGING_NAME;
    private static final String SOLDER_API_URL_STRING = SOLDER_API_JBOSS_REPO + SOLDER_VERSION + "/" + SOLDER_API_NAME;

    public synchronized static WebArchive createBase(final String name) {
        // Look to see if we have solder saved already
        if (solderJar == null && solderLoggingJar == null && solderApiJar == null) {
            if (new File(SOLDER_PATH + SOLDER_NAME).exists()
                    && new File(SOLDER_API_PATH + SOLDER_API_NAME).exists()
                    && new File(SOLDER_LOGGING_PATH + SOLDER_LOGGING_NAME).exists()) {
                ZipFile solder_jar_zip = null;
                ZipFile solder_logging_jar_zip = null;
                ZipFile solder_api_jar_zip = null;

                try {
                    solder_jar_zip = new ZipFile(SOLDER_PATH + SOLDER_NAME);
                    solderJar = ShrinkWrap.create(ZipImporter.class, SOLDER_NAME).importFrom(solder_jar_zip).as(JavaArchive.class);

                    solder_logging_jar_zip = new ZipFile(SOLDER_LOGGING_PATH + SOLDER_LOGGING_NAME);
                    solderLoggingJar = ShrinkWrap.create(ZipImporter.class, SOLDER_LOGGING_NAME).importFrom(solder_logging_jar_zip).as(JavaArchive.class);

                    solder_api_jar_zip = new ZipFile(SOLDER_API_PATH + SOLDER_API_NAME);
                    solderApiJar = ShrinkWrap.create(ZipImporter.class, SOLDER_API_NAME).importFrom(solder_api_jar_zip).as(JavaArchive.class);
                } catch (IOException ioe) {
                    // Not there or couldn't open it, go fetch solder
                    // Not sure how we'd ever get here, honestly
                } finally {
                    try {
                        if (solder_jar_zip != null) {
                            solder_jar_zip.close();
                        }
                        if (solder_api_jar_zip != null) {
                            solder_api_jar_zip.close();
                        }
                        if (solder_logging_jar_zip != null) {
                            solder_logging_jar_zip.close();
                        }
                    } catch (IOException e) {
                        // Swallow
                    }
                }
            } else {
                try {
                    final URL solderUrl = new URL(SOLDER_URL_STRING);
                    final Asset solderAsset = new UrlAsset(solderUrl);

                    final URL solderLoggingUrl = new URL(SOLDER_LOGGING_URL_STRING);
                    final Asset solderLoggingAsset = new UrlAsset(solderLoggingUrl);

                    final URL solderApiUrl = new URL(SOLDER_API_URL_STRING);
                    final Asset solderApiAsset = new UrlAsset(solderApiUrl);

                    // Save it off for future fast opening done above
                    solderJar = ShrinkWrap.create(ZipImporter.class).importFrom(solderAsset.openStream()).as(JavaArchive.class);
                    solderLoggingJar = ShrinkWrap.create(ZipImporter.class).importFrom(solderLoggingAsset.openStream()).as(JavaArchive.class);
                    solderApiJar = ShrinkWrap.create(ZipImporter.class).importFrom(solderApiAsset.openStream()).as(JavaArchive.class);

                    final FileOutputStream solderOutputStream = new FileOutputStream(new File(SOLDER_PATH, SOLDER_NAME));
                    final FileOutputStream solderLoggingOutputStream = new FileOutputStream(new File(SOLDER_LOGGING_PATH, SOLDER_LOGGING_NAME));
                    final FileOutputStream solderApiOutputStream = new FileOutputStream(new File(SOLDER_API_PATH, SOLDER_API_NAME));

                    solderJar.as(ZipExporter.class).exportTo(solderOutputStream);
                    solderLoggingJar.as(ZipExporter.class).exportTo(solderLoggingOutputStream);
                    solderApiJar.as(ZipExporter.class).exportTo(solderApiOutputStream);
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
                .addAsLibraries(solderJar, solderLoggingJar, solderApiJar)
                .addAsWebInfResource(new StringAsset("<jboss-deployment-structure>\n" +
                        "  <deployment>\n" +
                        "    <dependencies>\n" +
                        "      <module name=\"org.jboss.logmanager\" />\n" +
                        "    </dependencies>\n" +
                        "  </deployment>\n" +
                        "</jboss-deployment-structure>"), "jboss-deployment-structure.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
    }
}
