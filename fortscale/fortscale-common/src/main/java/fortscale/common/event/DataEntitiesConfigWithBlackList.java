package fortscale.common.event;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;


public class DataEntitiesConfigWithBlackList {

	@Autowired
	private DataEntitiesConfig dataEntitiesConfig;
	
	private Set<EntityIdFieldIdPair> entityIdFieldIdBlackListSet = new HashSet<>();
	
	public String getFieldColumn(String entityId, String fieldId){
		EntityIdFieldIdPair entityIdFieldIdPairKey = new EntityIdFieldIdPair(entityId, fieldId);
		if(entityIdFieldIdBlackListSet.contains(entityIdFieldIdPairKey)){
			return null;
		}
		
		String ret = null;
		try {
			ret = dataEntitiesConfig.getFieldColumn(entityId, fieldId);
		} catch (InvalidQueryException e) {
			entityIdFieldIdBlackListSet.add(entityIdFieldIdPairKey);
		}
		
		return ret;
	}
	
	public static class EntityIdFieldIdPair {
		private String entityId;
		private String fieldId;
		
		public EntityIdFieldIdPair(String entityId, String fieldId){
			this.entityId = entityId;
			this.fieldId = fieldId;
		}

		public String getEntityId() {
			return entityId;
		}

		public String getFieldId() {
			return fieldId;
		}
		
		 @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EntityIdFieldIdPair that = (EntityIdFieldIdPair) o;

            return new EqualsBuilder()
                    .append(entityId, that.entityId)
                    .append(fieldId, that.fieldId)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return fieldId.hashCode();
        }
	}
}
