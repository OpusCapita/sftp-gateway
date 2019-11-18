package com.opuscapita.web.service;

import com.opuscapita.sftp.service.uploadlistener.FileUploadListenerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventActionService {

    public class EventEntry extends AbstractMap.SimpleEntry {
        public EventEntry(Object key, Object value) {
            super(key, value);
        }

        @Override
        public String toString() {
            return "{" +
                    "key:" + this.getKey() + "," +
                    "value:" + this.getValue() +
                    "}";
        }
    }

    public List<EventEntry> listFileUploadListener() {
        ArrayList<EventEntry> fileUploadListenerList = new ArrayList<>();
        BeanDefinitionRegistry bdr = new SimpleBeanDefinitionRegistry();
        ClassPathBeanDefinitionScanner s = new ClassPathBeanDefinitionScanner(bdr);

        TypeFilter tf = new AssignableTypeFilter(FileUploadListenerInterface.class);
        s.addIncludeFilter(tf);
        s.setIncludeAnnotationConfig(false);
        s.scan("com.opuscapita.sftp.service.uploadlistener");

        for (String str : bdr.getBeanDefinitionNames()) {
            try {
                Class<?> _class = this.getClass().getClassLoader().loadClass(bdr.getBeanDefinition(str).getBeanClassName());
                FileUploadListenerInterface ful = (FileUploadListenerInterface) _class.newInstance();
                fileUploadListenerList.add(new EventEntry(ful.getId(), ful.getTitle()));
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            }
        }
        return fileUploadListenerList;
    }
}
