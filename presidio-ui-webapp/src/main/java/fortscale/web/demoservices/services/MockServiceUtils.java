package fortscale.web.demoservices.services;
import fortscale.domain.core.Alert;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by shays on 31/07/2017.
 */
public class MockServiceUtils {

    public static <T> List<T> getPage(List<T> items, PageRequest pageRequest, Class<T> clazz){

        if (items == null){
            return Collections.emptyList();
        }
        if (pageRequest == null){
            return  items;
        }
        items=sort(items, pageRequest,clazz);

        int pageSize = pageRequest.getPageSize();
        int page = pageRequest.getPageNumber();

        int lastItemIndex = Math.min((page + 1) * pageSize,items.size());
        return items.subList(page*pageSize, lastItemIndex);
    }

    public static  <T> List<T> sort(List<T> items, PageRequest pageRequest, Class<T> clazz){
       final Comparator<T>[] comparator=new Comparator[1];//One cell array so it will be final


        if (pageRequest.getSort()==null){
            return items;
        }

        pageRequest.getSort().forEach(order->{
            String fieldName = order.getProperty();
            if (fieldName.equals(Alert.evidencesSizeField)){
                fieldName = "evidenceSize";
            }
            try {
                Comparator<T> c = getComperator(fieldName,clazz, order.getDirection());
                if (c!=null) {
                    if (comparator[0] == null){
                        comparator[0] = c;
                    } else {
                        comparator[0] =  comparator[0].thenComparing(c);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            };

        });


        if ( comparator[0] == null ){
            return items;
        }

        return  items.stream().sorted( comparator[0]).collect(Collectors.toList());

    }

    private static <T> Comparator<T> getComperator(String fieldName, Class<T> clazz, Sort.Direction direction){

        try {


            final Method getter = new PropertyDescriptor(fieldName, clazz).getReadMethod();
            if (getter == null) {
                return null;
            }
            Comparator<T> comparator = (T a1, T a2) -> {
                int compareDirection= 0;
                try {
                    Object value1 = getter.invoke(a1);
                    Object value2 = getter.invoke(a2);

                    if (value1 == null && value2 == null) {
                        compareDirection = 0;
                    } else if (value1 == null) {
                        compareDirection = 1;
                    } else if (value2 == null) {
                        compareDirection = -1;
                    } else if (value1 instanceof Comparable) {
                        compareDirection = ((Comparable) value1).compareTo(value2);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (Sort.Direction.DESC.equals(direction)){
                    compareDirection= compareDirection *-1;
                }
                return compareDirection;

            };
            return comparator;
        } catch (Exception e){
            return  null;
        }
    }
}
