package fortscale.services.impl;

/**
 * Created by shays on 29/03/2016.
 */

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.dao.ApplicationConfigurationRepository;
import fortscale.services.ApplicationConfigurationService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Helper tools which are not Integral part of the Application Configuration
 */
@Service
public class ApplicationConfigurationHelper {

    @Autowired
    ApplicationConfigurationService applicationConfigurationService;

//    @PostConstruct
//    public void init(){
//        PropertyEditorManager.registerEditor(java.util.List.class,ListEditorSopport.class);
//    }

    /**
     * This method get and object, and list of pairs (property key for configuration and property name
     * @param prefix
     * @param objectToSync
     * @param keyToPropertyNames
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public void syncWithConfiguration(String prefix, Object objectToSync, Collection<Pair<String,String>> keyToPropertyNames)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        for (Pair<String, String> property: keyToPropertyNames) {

            //1. Check if exists in application configuration
            //2. Add to configuration if not exists
            //3. Get from configuration if do exists

            //Get key and look for configuration
            String key = prefix+"."+property.getKey();
            ApplicationConfiguration configuration =
                    applicationConfigurationService.getApplicationConfigurationByKey(key);


            if (configuration == null){
                //Key not found, add default value to application configuration
                String valueAsString = getValueAsString(objectToSync, property.getValue());
                applicationConfigurationService.insertConfigItem(key,valueAsString);

            } else {
                //Read value from configuration
                Method writer = getWriterMethod(objectToSync,property.getValue());

                Object value = getValueAsObject(objectToSync, property.getValue(), configuration.getValue());
                writer.invoke(objectToSync, value);

            }
        }
    }

    /*
        Get object and property name and return the Method object
     */
    private Method getWriterMethod(Object pojo, String propertyName)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(pojo, propertyName);

        Method writer = propertyDescriptor.getWriteMethod();
        if (writer == null){
            throw new RuntimeException(propertyName+" must have getter and setter");
        }
        return  writer;
    }

    /**
     *
     * @param pojo
     * @param propertyName
     * @param valueAsString
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private Object getValueAsObject(Object pojo, String propertyName, String valueAsString)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        //Create propertyDescripor, type, and editor
        PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(pojo, propertyName);
        Class<?> propertyType = propertyDescriptor.getPropertyType();
        PropertyEditor editor = PropertyEditorManager.findEditor(propertyType);

        editor.setAsText(valueAsString);
        return editor.getValue();
    }

    private String getValueAsString(Object objectToSync, String propertyName ) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //Create propertyDescripor, type, and editor
        PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(objectToSync, propertyName);
        Class<?> propertyType = propertyDescriptor.getPropertyType();
        PropertyEditor editor = PropertyEditorManager.findEditor(propertyType);
        Method reader = propertyDescriptor.getReadMethod();
        if (reader == null){
            throw new RuntimeException(propertyName+" must have getter and setter");
        }
        Object value = reader.invoke(objectToSync);

        editor.setValue(value);
        return editor.getAsText();
    }


    public <T> void syncListOfObjectsWithConfiguration(String prefix, Collection<T> listOfObjcts,
                                     Collection<Pair<String,String>> keyToPropertyNames, Class<T> tClazz)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {

        Map<String, String> valuesInNamespace = applicationConfigurationService.getApplicationConfigurationByNamespace(prefix);
        //Not found in mongo - add to mongo
        if (valuesInNamespace == null){
            int counter = 0;
            for (T objectToSync: listOfObjcts){
                for (Pair<String, String> property: keyToPropertyNames) {

                    //Get key and look for configuration
                    String key = prefix+"."+counter+"."+property.getKey();

                    String valueAsString = getValueAsString(objectToSync, property.getValue());
                    applicationConfigurationService.insertConfigItem(key,valueAsString);
                }

            }
            counter++;
        } else {
            int counter = 0;

            Map<String, String> singleInstanceNamespace =  applicationConfigurationService.getApplicationConfigurationByNamespace(prefix+"."+counter);
            while (singleInstanceNamespace != null){
                for (Pair<String, String> property: keyToPropertyNames) {

                        String valueAsString = singleInstanceNamespace.get(prefix+"."+counter+"."+property);

                        T objectToSync = tClazz.newInstance();

                        //Read value from configuration
                        Method writer = getWriterMethod(objectToSync,property.getValue());

                        Object value = getValueAsObject(objectToSync, property.getValue(), valueAsString);
                        writer.invoke(objectToSync, value);


                }

                counter++;
                singleInstanceNamespace =  applicationConfigurationService.getApplicationConfigurationByNamespace(prefix+"."+counter);
            }
        }


    }


}
