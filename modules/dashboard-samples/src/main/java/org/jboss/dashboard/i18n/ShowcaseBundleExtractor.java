/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.dashboard.i18n;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.commons.text.Base64;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Tool for extracting all the i18n literals from the Showcase UI and saves them into classic resource bundles.
 * The final goal behind this tool is to be able to pass the generated bundles to a translation service like Zanata.
 */
public class ShowcaseBundleExtractor {

    public static void main(String[] args) throws Exception {
        // First of all, init the CDI container.
        WeldContainer weldContainer = new Weld().initialize();
        CDIBeanLocator.beanManager = weldContainer.getBeanManager();

        // Mock up the app directories.
        String rootDir = System.getProperty("user.dir") + "/modules/dashboard-samples";
        Application.lookup().setBaseAppDirectory(rootDir + "/src/main/webapp");
        Application.lookup().setBaseCfgDirectory(rootDir + "/src/main/webapp/WEB-INF/etc");

        // Process the Showcase KPIs file
        XmlToBundleConverter converter = (XmlToBundleConverter) CDIBeanLocator.getBeanByType(KpisFileConverter.class);
        converter.bundleDir = new File(rootDir, "src/main/resources/org/jboss/dashboard/showcase/kpis");
        converter.xmlFile = new File(rootDir, "src/main/webapp/WEB-INF/etc/appdata/initialData/showcaseKPIs.xml");
        Map<Locale,Properties> bundles = converter.extract(); // Extract bundles from source XML
        converter.write(bundles); // Write literals to bundle files.

        // Process the Showcase Workspace file
        converter = (XmlToBundleConverter) CDIBeanLocator.getBeanByType(WorkspaceFileConverter.class);
        converter.bundleDir = new File(rootDir, "src/main/resources/org/jboss/dashboard/showcase/workspace");
        converter.xmlFile = new File(rootDir, "src/main/webapp/WEB-INF/etc/appdata/initialData/showcaseWorkspace.xml");
        bundles = converter.extract(); // Extract bundles from source XML
        converter.write(bundles); // Write literals to bundle files.
    }
}