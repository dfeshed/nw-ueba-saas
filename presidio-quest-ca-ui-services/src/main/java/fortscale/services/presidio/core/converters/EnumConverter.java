package fortscale.services.presidio.core.converters;

import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import presidio.output.client.model.UserQuery;

import java.util.*;
import java.util.stream.Collectors;

/**
 * T - The enum as part of the backend model
 * C - The enum as part of the ui filter and the ui pojo
 * G - The enum as part of the backend query
 */
public class EnumConverter<T extends Enum<T>,C extends Enum<C>, G extends Enum<G>> extends AbstractItemConverter<T,C>{

    Logger logger = Logger.getLogger(this.getClass());

    private Map<T,C> queryEnumToUiEnum;
    private Map<C,T> uiEnumToQueryEnum;
    private Set<G> queryEnumValues = new HashSet<G>();


    //public costructor is not enforcing generings, use, createInstance
    protected EnumConverter(Map<T,C> queryEnumToUiEnum ,Class<G> backendQueryClass){

        queryEnumValues = new HashSet<G>(Arrays.asList(backendQueryClass.getEnumConstants()));
        validateSizeAndTypes(queryEnumToUiEnum);

        this.queryEnumToUiEnum = queryEnumToUiEnum;

        uiEnumToQueryEnum = new HashMap();
        for (Map.Entry<T,C> entry:queryEnumToUiEnum.entrySet()){
            uiEnumToQueryEnum.put(entry.getValue(),entry.getKey());
        }
        logger.debug("Finish load enum converter");
    }

    public static <T extends Enum<T>,C extends Enum<C>,G extends Enum<G>> EnumConverter<T,C,G> createInstance(Map<T,C> queryEnumToUiEnum, Class<G> backendQueryClass){
        return new EnumConverter<T,C,G>(queryEnumToUiEnum,backendQueryClass);
    }

    private void validateSizeAndTypes(Map<T, C> uiEnumToQueryEnum) {
        if (MapUtils.isEmpty(uiEnumToQueryEnum)){
            throw new IllegalArgumentException("Enums mapping must have at least on entry");
        }

        Map.Entry<T,C> oneEntry= uiEnumToQueryEnum.entrySet().stream().findAny().get();
        validateEnumLength(oneEntry.getKey().getClass(),uiEnumToQueryEnum.keySet());
        validateEnumLength(oneEntry.getValue().getClass(),uiEnumToQueryEnum.values());
        validateEnumLength(this.queryEnumValues.stream().findAny().get().getClass(),uiEnumToQueryEnum.keySet());
    }

    private <K extends Enum<K>> void validateEnumLength(Class<K> clazz, Collection definedValues){
        if (clazz.getEnumConstants().length!=definedValues.size()){
            throw new IllegalArgumentException("Some enums missing for enum: "+clazz.getName());
        }


        Set<String> definedValuesLowerCaseStrings = (Set<String>)definedValues.stream().map(x->x.toString().toLowerCase()).collect(Collectors.toSet());


        for (K value:clazz.getEnumConstants()){
            if (!definedValuesLowerCaseStrings.contains(value.name().toLowerCase())){
                throw new IllegalArgumentException("Enum: "+clazz.getName()+" is missing value "+value);
            }

        }


    }


    public List<G> convertUiFilterToQueryDto(String commaSeperatedString){
        if (StringUtils.isEmpty(commaSeperatedString)){
            return Collections.EMPTY_LIST;
        }

        List<T> severityEnumList = new ArrayList<>();

        Set<String> stringValuesForFilterLowerCase = splitAndTrim(commaSeperatedString,true);
        for (String stringValue:stringValuesForFilterLowerCase){
            T relevantValue = getMatchingQueryEnum(stringValue);
            if (relevantValue !=null){
                severityEnumList.add(relevantValue);
            } else {
                logger.error("Can't convert {}, no such value",stringValue);
            }

        }

        List<G> queryEnumList =new ArrayList<G>();
        this.queryEnumValues.forEach(x->{

           severityEnumList.stream().forEach(y->{
              if (y.name().toLowerCase().equals(x.name().toLowerCase())){
                queryEnumList.add(x);
              }
           });
        });


        return  queryEnumList;
    }

    private T getMatchingQueryEnum(String stringValue) {
        for(Map.Entry<C,T> entry : this.uiEnumToQueryEnum.entrySet()){
            if (entry.getKey().name().toLowerCase().equals(stringValue)){
                return entry.getValue();
            }
        }
        return  null;
    }

    public C convertResponseToUiDto(T value){
        if (value==null){
            return  null;
        }
        C response = queryEnumToUiEnum.get(value);
        if (response == null){
            logger.error("Cannot parse enum {}",value);
        }
        return  response;
    }



}
