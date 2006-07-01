/*
 * $Id: GenerateContainer.java 5462 2005-08-05 18:35:48Z jonesde $
 *
 * Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.appservers;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.*;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.template.FreeMarkerWorker;
import org.ofbiz.base.start.Classpath;
import org.ofbiz.base.component.ComponentConfig;

/**
 * GenerateContainer - Generates Configuration Files For Application Servers
 * ** This container requires StartInfoLoader to be loaded at startup.
 * ** This container requires the ComponentContainer to be loaded first.
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.1
 */
public class GenerateContainer implements Container {

    public static final String module = GenerateContainer.class.getName();
    public static final String source = "/framework/appservers/templates/";
    public static final String target = "/setup/";

    protected String configFile = null;
    protected String ofbizHome = null;
    protected String args[] = null;


    /**
     * @see org.ofbiz.base.container.Container#init(java.lang.String[], java.lang.String)
     */
    public void init(String[] args, String configFile) {
        this.ofbizHome = System.getProperty("ofbiz.home");
        this.configFile = configFile;
        this.args = args;
    }

    /**
     * @see org.ofbiz.base.container.Container#start()
     */
    public boolean start() throws ContainerException {
        this.generateFiles();
        System.exit(1);
        return true;
    }

    /**
     * Stop the container
     *
     * @throws org.ofbiz.base.container.ContainerException
     *
     */
    public void stop() throws ContainerException {
    }

    private void generateFiles() throws ContainerException {
        File files[] = getTemplates();
        Map dataMap = buildDataMap();

        //Debug.log("Using Data : " + dataMap, module);
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory() && !files[i].isHidden()) {
                parseTemplate(files[i], dataMap);
            }
        }
    }

    private File[] getTemplates() throws ContainerException {
        if (args == null) {
            throw new ContainerException("Invalid application server type argument passed");
        }

        String templateLocation = args[0];
        if (templateLocation == null) {
            throw new ContainerException("Unable to locate Application Server template directory");
        }

        File parentDir = new File(ofbizHome + source + templateLocation);
        if (!parentDir.exists() || !parentDir.isDirectory()) {
            throw new ContainerException("Template location - " + templateLocation + " does not exist!");
        }

        return parentDir.listFiles();
    }

    private Map buildDataMap() {
        Map dataMap = new HashMap();
        List c[] = getClasspath();
        dataMap.put("classpathJars", c[0]);
        dataMap.put("classpathDirs", c[1]);
        dataMap.put("env", System.getProperties());
        dataMap.put("webApps", ComponentConfig.getAllWebappResourceInfos());
        return dataMap;
    }

    private List[] getClasspath() {
        Classpath classPath = new Classpath(System.getProperty("java.class.path"));
        List elements = classPath.getElements();
        List jar = new ArrayList();
        List dir = new ArrayList();

        Iterator i = elements.iterator();
        while (i.hasNext()) {
            File f = (File) i.next();
            if (f.exists()) {
                if (f.isDirectory()) {
                    dir.add(f.getAbsolutePath());
                } else {
                    jar.add(f.getAbsolutePath());
                }
            }
        }

        List[] lists = { jar, dir };
        return lists;
    }

    private void parseTemplate(File templateFile, Map dataMap) throws ContainerException {
        Debug.log("Parsing template : " + templateFile.getAbsolutePath(), module);
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(templateFile));
        } catch (FileNotFoundException e) {
            throw new ContainerException(e);
        }

        // create the target file/directory
        String targetDirectoryName = args.length > 1 ? args[1] : null;
        if (targetDirectoryName == null) {
            targetDirectoryName = target;
        }
        String targetDirectory = ofbizHome + targetDirectoryName + args[0];
        File targetDir = new File(targetDirectory);
        if (!targetDir.exists()) {
            boolean created = targetDir.mkdirs();
            if (!created) {
                throw new ContainerException("Unable to create target directory - " + targetDirectory);
            }
        }

        if (!targetDirectory.endsWith("/")) {
            targetDirectory = targetDirectory + "/";
        }

        // write the template to the target directory
        Writer writer = null;
        try {
            writer = new FileWriter(targetDirectory + templateFile.getName());
        } catch (IOException e) {
            throw new ContainerException(e);
        }
        try {
            FreeMarkerWorker.renderTemplate(templateFile.getAbsolutePath(), reader, dataMap, writer);
        } catch (Exception e) {
            throw new ContainerException(e);
        }

        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new ContainerException(e);
        }
    }
}
