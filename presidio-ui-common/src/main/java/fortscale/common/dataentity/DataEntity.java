package fortscale.common.dataentity;


import java.util.HashMap;
import java.util.List;

/**
 * Created by Yossi on 27/10/2014.
 * Represents a DataQuery entity, to send to the front-end
 */
public class DataEntity {
    String id;
    String name;
    String nameForMenu;
    String shortName;
    String extendsEntity;
    Boolean isAbstract;
    Boolean showInExplore;
    List<DataEntityField> fields;
    HashMap<String, DataEntityField> fieldsIndex;


    /**
     * eventsEntity is used to connect the entity to another entity, which contains the related events.
     * For example, the vpn_session entity should refer to the vpn entity as events_entity
     */
    String eventsEntity;

    /**
     * sessionEntity is used to connect the entity to another entity, which contains sessions for the entity's events.
     * For example, the vpn entity should refer to the vpn_session entity as session_entity
     */
    String sessionEntity;

	public DataEntity(){

	}

	public DataEntity(String id){
		this.id = id;
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameForMenu() {
        return nameForMenu;
    }

    public void setNameForMenu(String nameForMenu) {
        this.nameForMenu = nameForMenu;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getEventsEntity() {
        return eventsEntity;
    }

    public void setEventsEntity(String eventsEntity) {
        this.eventsEntity = eventsEntity;
    }

    public String getSessionEntity() {
        return sessionEntity;
    }

    public void setSessionEntity(String sessionEntity) {
        this.sessionEntity = sessionEntity;
    }

    public void setExtendsEntity(String extendsEntity){
        this.extendsEntity = extendsEntity;
    }
    public String getExtendsEntity(){return extendsEntity;}

    public Boolean getIsAbstract(){return isAbstract;}

    public void setIsAbstract(Boolean isAbstract){this.isAbstract = isAbstract;}

    public void setShowInExplore(Boolean showInExplore){this.showInExplore = showInExplore;}
    public Boolean getShowInExplore(){return showInExplore;}

    public List<DataEntityField> getFields() {
        return fields;
    }

    public void setFields(List<DataEntityField> fields) {
        this.fields = fields;
        this.fieldsIndex = new HashMap<>(fields.size());
        for(DataEntityField field: fields){
            this.fieldsIndex.put(field.getId(), field);
        }
    }

    public DataEntityField getField(String fieldId){
        return fieldsIndex.get(fieldId);
    }


	@Override
	public boolean equals( Object toThat)
	{
		if (!(toThat instanceof DataEntity)) return false;
		return this.id.equals(((DataEntity)toThat).getId());

	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
