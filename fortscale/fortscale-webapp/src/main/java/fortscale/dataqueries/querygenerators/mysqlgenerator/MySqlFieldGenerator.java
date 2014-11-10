package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.DataEntitiesConfig;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querydto.DataQueryField;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Yossi on 03/11/2014.
 * Generates SQL for a field
 */
@Component
public class MySqlFieldGenerator {
    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;

    @Autowired
    MySqlValueGenerator mySqlValueGenerator;

    @Autowired
    MySqlFieldFunctionGenerator mySqlFieldFunctionGenerator;

    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO, Boolean aliasAsId) throws InvalidQueryException {
        StringBuilder fieldSB = new StringBuilder();

        if (field.getValue() != null){
            if (field.getAlias() == null)
                throw new InvalidQueryException(String.format("An alias should be specified for field value '%s'.", field.getValue()));

            fieldSB.append(mySqlValueGenerator.generateSql(field.getValue(), field.getValueType()));
            fieldSB.append(" as '").append(field.getAlias()).append("'");
        }
        else if (field.getFunc() != null){
            fieldSB.append(mySqlFieldFunctionGenerator.generateSql(field, dataQueryDTO));
            if (field.getAlias() != null)
                fieldSB.append(" as '").append(field.getAlias()).append("'");
        }
        else{
            if (dataQueryDTO.getEntities().length > 1 && field.getEntity() != null)
                fieldSB.append(field.getEntity()).append(".");

            String columnName = dataEntitiesConfig.getFieldColumn(field.getEntity() != null ? field.getEntity() : dataQueryDTO.getEntities()[0], field.getId() );
            fieldSB.append(columnName);

            if (field.getAlias() != null)
                fieldSB.append(" as '").append(field.getAlias()).append("'");
            else if (aliasAsId && !columnName.equals(field.getId()))
                fieldSB.append(" as '").append(field.getId()).append("'");
        }

        return fieldSB.toString();
    }

    public String generateSql(DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        return generateSql(field, dataQueryDTO, false);
    }

    public void setDataEntitiesConfig(DataEntitiesConfig dataEntitiesConfig){
        this.dataEntitiesConfig = dataEntitiesConfig;
    }

    public void setMySqlValueGenerator(MySqlValueGenerator mySqlValueGenerator){
        this.mySqlValueGenerator = mySqlValueGenerator;
    }
}
