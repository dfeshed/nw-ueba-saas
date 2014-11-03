package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.DataQueryPartition;
import fortscale.dataqueries.DataQueryPartitionType;
import fortscale.dataqueries.DataQueryUtils;
import fortscale.dataqueries.QueryFieldFunction;
import fortscale.dataqueries.QueryValueType;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;

import org.omg.CORBA.DynAnyPackage.Invalid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;



import java.util.ArrayList;
import java.util.List;

/**
 * Some static functions that should be available to multiple MySql part generators
 */
@Component
public class MySqlUtils {
    @Autowired
    private DataQueryUtils dataQueryUtils;


    public void setDataQueryUtils(DataQueryUtils dataQueryUtils){
        this.dataQueryUtils = dataQueryUtils;
    }
}
