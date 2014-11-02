package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.DataQueryPartition;
import fortscale.dataqueries.DataQueryPartitionType;
import fortscale.dataqueries.DataQueryUtils;
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
public class MySqlUtils implements EmbeddedValueResolverAware {
    StringValueResolver stringValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.stringValueResolver = resolver;
    }

    @Autowired
    private DataQueryUtils dataQueryUtils;

    public String getFieldSql(DataQueryDTO.DataQueryField field, DataQueryDTO dataQueryDTO, Boolean aliasAsId) throws InvalidQueryException {
        StringBuilder fieldSB = new StringBuilder();

        if (field.getValue() != null){
            if (field.getAlias() == null)
                throw new InvalidQueryException("An alias should be specified for field value '" + field.getValue() + "'.");

            fieldSB.append(getValueSql(field.getValue(), field.valueType));
            fieldSB.append(" as " + field.getAlias());
        }
        else{
            if (dataQueryDTO.entities.length > 1 && field.getEntity() != null)
                fieldSB.append(field.getEntity() + ".");

            String columnName = dataQueryUtils.getFieldColumn(field.getEntity() != null ? field.getEntity() : dataQueryDTO.entities[0], field.getId() );
            fieldSB.append(columnName);

            if (field.getAlias() != null)
                fieldSB.append(" as " + field.getAlias());
            else if (aliasAsId && !columnName.equals(field.getId()))
                fieldSB.append(" as " + field.getId());
        }

        return fieldSB.toString();
    }

    public String getFieldSql(DataQueryDTO.DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        return getFieldSql(field, dataQueryDTO, false);
    }

    public String getValueSql(String value, QueryValueType type){
        if (value == null)
            return "null";

        switch (type){
            case STRING:
                return "\"" + value + "\"";
            default:
                return value;
        }
    }

    public String getConditionFieldSql(DataQueryDTO.ConditionField conditionField, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        StringBuilder sb = new StringBuilder();

        sb.append(getFieldSql(conditionField.field, dataQueryDTO));
        sb.append(" ");

        MySqlOperator operator;
        try {
            operator = MySqlOperator.valueOf(conditionField.operator.toString());
        }
        catch(Exception error){
            throw new InvalidQueryException("Unknown operator for MySql: " + conditionField.operator.toString() + ".");
        }

        if (operator.requiresValue && conditionField.getValue() == null)
            throw new InvalidQueryException("Can't create MySQL query, the " + operator.name() + " operator requires a value, but none was specified.");

        sb.append(operator.sqlOperator);
        sb.append(" ");

        String entityId = conditionField.field.getEntity();
        if (entityId == null)
            entityId = dataQueryDTO.entities[0];

        sb.append(getValueSql(conditionField.getValue(), dataQueryUtils.getFieldType(entityId , conditionField.field.getId())));

        return sb.toString();
    }

    public void setDataQueryUtils(DataQueryUtils dataQueryUtils){
        this.dataQueryUtils = dataQueryUtils;
    }

    public static enum MySqlOperator{
        equals ("="),
        notEquals ("!="),
        greaterThan (">"),
        greaterThanOrEquals (">="),
        lesserThan ("<="),
        lesserThanOrEquals ("<="),
        in ("IN"),
        like ("LIKE"),
        hasValue ("IS NOT NULL", false),
        hasNoValue ("IS NULL");

        public final String sqlOperator;
        public final Boolean requiresValue;

        MySqlOperator(String operator){
            this.sqlOperator = operator;
            this.requiresValue = true;
        }

        MySqlOperator(String operator, Boolean requiresValue){
            this.sqlOperator = operator;
            this.requiresValue = requiresValue;
        }
    }
}
