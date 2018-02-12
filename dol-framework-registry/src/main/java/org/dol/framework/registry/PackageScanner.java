package org.dol.framework.registry;

import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dolphin on 2017/10/16.
 */
public class PackageScanner {
    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static Environment environment = new StandardEnvironment();
    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

    public static String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(environment.resolveRequiredPlaceholders(basePackage));
    }

    public static List<Class> scan(String... basePackages) {
        List<Class> allClassList = new ArrayList<>();
        for (String basePackage : basePackages) {
            allClassList.addAll(doScan(basePackage));
        }
        return allClassList;
    }

    public static List<Class> scan(List<String> basePackages) {
        List<Class> allClassList = new ArrayList<>();
        for (String basePackage : basePackages) {
            allClassList.addAll(doScan(basePackage));
        }
        return allClassList;
    }

    private static List<Class> doScan(String basePackage) {
        List<Class> classes = new ArrayList<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + "/" + DEFAULT_RESOURCE_PATTERN;
        try {
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    String aClass = metadataReader.getClassMetadata().getClassName();
                    classes.add(Class.forName(aClass));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static void main(String[] args) {
        List<Class> classes = scan("org.dol.framework.registry");
        for (Class aClass : classes) {
            System.out.println(aClass.getName());
        }
    }
}
