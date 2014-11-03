package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.DataQueryUtils;
import fortscale.dataqueries.querydto.DataQueryDTO;
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
    private DataQueryUtils dataQueryUtils;

    @Autowired
    MySqlValueGenerator mySqlValueGenerator;

    @Autowired
    MySqlFieldFunctionGenerator mySqlFieldFunctionGenerator;

    public String generateSql(DataQueryDTO.DataQueryField field, DataQueryDTO dataQueryDTO, Boolean aliasAsId) throws InvalidQueryException {
        StringBuilder fieldSB = new StringBuilder();

        if (field.getValue() != null){
            if (field.getAlias() == null)
                throw new InvalidQueryException("An alias should be specified for field value '" + field.getValue() + "'.");

            fieldSB.append(mySqlValueGenerator.generateSql(field.getValue(), field.valueType));
            fieldSB.append(" as '" + field.getAlias() + "'");
        }
        else if (field.func != null){
            fieldSB.append(mySqlFieldFunctionGenerator.generateSql(field, dataQueryDTO));
            if (field.getAlias() != null)
                fieldSB.append(" as '" + field.getAlias() + "'");
        }
        else{
            if (dataQueryDTO.entities.length > 1 && field.getEntity() != null)
                fieldSB.append(field.getEntity() + ".");

            String columnName = dataQueryUtils.getFieldColumn(field.getEntity() != null ? field.getEntity() : dataQueryDTO.entities[0], field.getId() );
            fieldSB.append(columnName);

            if (field.getAlias() != null)
                fieldSB.append(" as '" + field.getAlias() + "'");
            else if (aliasAsId && !columnName.equals(field.getId()))
                fieldSB.append(" as '" + field.getId() + "'");
        }

        return fieldSB.toString();
    }

    public String generateSql(DataQueryDTO.DataQueryField field, DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        return generateSql(field, dataQueryDTO, false);
    }
}
