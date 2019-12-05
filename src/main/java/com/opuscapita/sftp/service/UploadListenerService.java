package com.opuscapita.sftp.service;

import com.opuscapita.sftp.model.SftpServiceConfigRepository;
import com.opuscapita.sftp.service.uploadlistener.FileUploadListenerInterface;
import com.opuscapita.transaction.service.TxService;
import lombok.Getter;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class UploadListenerService {


    private final SftpServiceConfigRepository serviceConfigRepository;

    public UploadListenerService(
            final SftpServiceConfigRepository _serviceConfigRepository
    ) {
        this.serviceConfigRepository = _serviceConfigRepository;
    }

    public static class EventEntry extends AbstractMap.SimpleEntry {
        @Getter
        private final Class<FileUploadListenerInterface> uploadListener;
        @Getter
        private final String description;

        public EventEntry(String _key, String _value, String _description, Class<FileUploadListenerInterface> _uploadListenerClass) {
            super(_key, _value);
            this.description = _description;
            this.uploadListener = _uploadListenerClass;
        }

        public FileUploadListenerInterface getInstance() throws IllegalAccessException, InstantiationException {
            return this.uploadListener.newInstance();
        }

        @Override
        public String toString() {
            return "{" +
                    "key:" + this.getKey() + "," +
                    "value:" + this.getValue() +
                    "description:" + this.getDescription() +
                    "}";
        }
    }

    public FileUploadListenerInterface getFileUploadListenerById(String evntActionId) throws InstantiationException, IllegalAccessException {
        for (EventEntry entry : this.listFileUploadListener()) {
            if (entry.getKey().equals(evntActionId)) {
                return entry.getInstance();
            }
        }
        return null;
    }

    public FileUploadListenerInterface getFileUploadListenerById(String evntActionId, TxService txService) throws InstantiationException, IllegalAccessException {
        FileUploadListenerInterface fileUploadListener = this.getFileUploadListenerById(evntActionId);
        if (fileUploadListener != null) {
            fileUploadListener.setTxService(txService);
        }
        return fileUploadListener;
    }

    public EventEntry getFileUploadListenerAsEventEntryById(String evntActionId) {
        List<EventEntry> entryList = this.listFileUploadListener();
        AtomicReference<EventEntry> eventEntry = new AtomicReference<>();
        entryList.forEach(entry -> {
            if (entry.getKey().equals(evntActionId)) {
                eventEntry.set(entry);
            }
        });
        return eventEntry.get();
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
                Class<FileUploadListenerInterface> _class = (Class<FileUploadListenerInterface>) this.getClass().getClassLoader().loadClass(bdr.getBeanDefinition(str).getBeanClassName());
                FileUploadListenerInterface ful = _class.newInstance();
                fileUploadListenerList.add(new EventEntry(ful.getId(), ful.getTitle(), ful.getDescription(), _class));
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            }
        }
        return fileUploadListenerList;
    }
}
