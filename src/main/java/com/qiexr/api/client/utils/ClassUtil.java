package com.qiexr.api.client.utils;

import com.xingren.v.logging.annotations.Slf4j;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Xinshuai
 * @description 类操作工具类
 * @since 2020-07-03 13:51
 */
@Slf4j
public final class ClassUtil {

    /**
     * 获取类加载器
     *
     * @return
     */
    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     *
     * @param className
     * @param isInitialized
     * @return
     */
    private static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            log.error("load class failure", e);
            throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 获取制定包名下的所有类
     *
     * @param packageNames
     * @return
     */
    public static Set<Class<?>> getClassSet(String[] packageNames) {
        Set<Class<?>> classSet = new HashSet<>();
        try {
            for (String packageName : packageNames) {
                Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    if (url != null) {
                        String protocol = url.getProtocol();
                        if (protocol.equals("jar")) {
                            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                            if (jarURLConnection != null) {
                                JarFile jarFile = jarURLConnection.getJarFile();
                                if (jarFile != null) {
                                    Enumeration<JarEntry> jarEntries = jarFile.entries();
                                    while (jarEntries.hasMoreElements()) {
                                        JarEntry jarEntry = jarEntries.nextElement();
                                        String jarEntryName = jarEntry.getName();
                                        if (jarEntryName.endsWith(".class")) {
                                            String className = jarEntryName
                                                    .substring(0, jarEntryName.lastIndexOf("."))
                                                    .replaceAll("/", ".");
                                            doAddClass(classSet, className);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("get class set failure", e);
            throw new RuntimeException(e);
        }
        return classSet;
    }

    private static void doAddClass(Set<Class<?>> classSet, String className) {
        Class<?> cls = loadClass(className, false);
        classSet.add(cls);
    }

}
