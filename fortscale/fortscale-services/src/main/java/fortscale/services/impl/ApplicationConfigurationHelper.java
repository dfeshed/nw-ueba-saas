package fortscale.services.impl;

/**
 * Created by shays on 29/03/2016.
 */

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
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

    /**
     * That method retrive object which contain collection of type T, and check if the collection exists in the application configuration
     * If it doesn't exists - the collection will be saved to the application configuration.
     * If it already exists - the collection will be overide by the values in the application configuration.
     *
     * @param prefix - the prfix all all the relevant keys of the collection the application configuration
     * @param originalObject - the object which contains the collection
     * @param collectionAttributeName - the property name of the collection in original object.
     *                                 1) the property must have getters and setters
     *                                 2) the property must be initilized (event empty collection) - null will throw exception
     * @param tClazz - the class of the elements in the collection
     * @param keyToPropertyNames - describe the key name and the property name of each attribue of class T (the elements in the collection)
     * @param <T> - the type of the elements in the collection
     *
     *  The collection will be saved in the mongo in the following way:
     *          {
                "key" : "alerts.congiruations.0.alertTitle",
                "value" : "vpn_geo_hopping"
                }
                {
                "key" : "alerts.congiruations.0.evidenceType",
                "value" : "vpn_geo_hopping"
                }
                {
                "key" : "alerts.congiruations.0.namePriority",
                "value" : "2"
                }
                {
                "key" : "alerts.congiruations.1.alertTitle",
                "value" : "smart"
                }
                {
                "key" : "alerts.congiruations.1.evidenceType",
                "value" : "smart"
                }
                {
                "key" : "alerts.congiruations.1.namePriority",
                "value" : "1"
                }

     *
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public <T> void syncListOfObjectsWithConfiguration(String prefix, Object originalObject,String collectionAttributeName,
                                                       Class<T> tClazz, Collection<Pair<String,String>> keyToPropertyNames )
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {


        //Get the collection itself from originalObject and check that the collection initilized
        PropertyDescriptor collectionDescriptor = PropertyUtils.getPropertyDescriptor(originalObject, collectionAttributeName);
        Method reader = collectionDescriptor.getReadMethod();
        Collection<T> listOfObjcts = (Collection<T>)reader.invoke(originalObject);

        if (listOfObjcts == null){
            throw new RuntimeException("Collection "+collectionAttributeName+" must be initilized");
        }

        //Check if the values prefix exits in the application configuration

        Map<String, String> valuesInNamespace = applicationConfigurationService.getApplicationConfigurationByNamespace(prefix);
        //Not found in mongo - add to mongo
        if (MapUtils.isEmpty(valuesInNamespace)){

            saveAllElementsToApplicationConfiguration(prefix, keyToPropertyNames, listOfObjcts);

        } else {
            readAllElementsFromApplicationConfiguration(prefix, tClazz, keyToPropertyNames, listOfObjcts);
        }


    }

    private <T> void readAllElementsFromApplicationConfiguration(String prefix, Class<T> tClazz, Collection<Pair<String, String>> keyToPropertyNames, Collection<T> listOfObjcts) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        int counter = 0;
        listOfObjcts.clear();

        Map<String, String> singleInstanceNamespace =  applicationConfigurationService.getApplicationConfigurationByNamespace(prefix+"."+counter);
        //Iterate each element in the list and init each property of the element from the application configuration
        while (MapUtils.isNotEmpty(singleInstanceNamespace)){
            //For each entry in the set create new object of T and fill its fields
            T objectToSync = tClazz.newInstance();
            for (Pair<String, String> property: keyToPropertyNames) {

                String valueAsString = singleInstanceNamespace.get(prefix+"."+counter+"."+property.getKey());

                //Read value from configuration
                Method writer = getWriterMethod(objectToSync,property.getValue());

                Object value = getValueAsObject(objectToSync, property.getValue(), valueAsString);
                writer.invoke(objectToSync, value);


            }
            listOfObjcts.add(objectToSync);
            counter++;
            singleInstanceNamespace =  applicationConfigurationService.getApplicationConfigurationByNamespace(prefix+"."+counter);
        }
    }


    private <T> void saveAllElementsToApplicationConfiguration(String prefix, Collection<Pair<String, String>> keyToPropertyNames, Collection<T> listOfObjcts) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        int counter = 0;
        //Iterate each element in the list and insert each property in the element to the application configuration
        for (T objectToSync: listOfObjcts){
            for (Pair<String, String> property: keyToPropertyNames) {

                //Get key and update the configuration
                String key = prefix+"."+counter+"."+property.getKey();
                String valueAsString = getValueAsString(objectToSync, property.getValue());
                applicationConfigurationService.insertConfigItem(key,valueAsString);
            }
            counter++;
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


}
