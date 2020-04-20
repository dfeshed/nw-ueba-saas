package presidio.data.generators.activedirectoryop;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE_2_CATEGORIES_MAP;
import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.IOperationTypeGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is one element data provider from a cyclic list of string values - ADE File
 */
public class ActiveDirOperationTypeCyclicGenerator extends CyclicValuesGenerator<OperationType> implements IOperationTypeGenerator {

    private static final Map<String,List<String>> opType2OpCategoryMap = AD_OPERATION_TYPE_2_CATEGORIES_MAP.INSTANCE.getOperation2CategoryMap();

    private static final OperationType[] DEFAULT_ACTIVE_DIRECTORY_OPERATION_TYPES = getDefaultAdOperationTypes();

    private static OperationType[] getDefaultAdOperationTypes(){

        List<OperationType> ret = new ArrayList();
        for (AD_OPERATION_TYPE opType : AD_OPERATION_TYPE.values()) {
            ret.add(new OperationType(opType.value, opType2OpCategoryMap.get(opType.value)));
        }
        return ret.toArray(new OperationType[ret.size()]);
    }

    public ActiveDirOperationTypeCyclicGenerator() {
        super(DEFAULT_ACTIVE_DIRECTORY_OPERATION_TYPES);
    }


    public ActiveDirOperationTypeCyclicGenerator(OperationType[] customList) {
        super(customList);
    }

    public ActiveDirOperationTypeCyclicGenerator(OperationType customType) {
        super(customType);
    }

    public ActiveDirOperationTypeCyclicGenerator(String customTypeName) {
        super(new OperationType(customTypeName, opType2OpCategoryMap.get(customTypeName)));
    }
}