package fortscale.services.configuration.gds.state.gds.entities.properties;

/**
 * holds the configuration needed in order to create a table config in entities.properties
 * Created by galiar on 20/01/2016.
 */
public class GDSEntitiesPropertiesTable {

	private String id;
	private String name;
	private String nameForMenu;
	private String shortName;
	private String isAbstractStr;
	private String showInExploreStr;
	private String extendsEntity;
	private String db;
	private String table;
	private String partition;
	private String performanceTable;
	private String partitionBaseField;

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

	public String getIsAbstractStr() {
		return isAbstractStr;
	}

	public void setIsAbstractStr(String isAbstract) {
		this.isAbstractStr = isAbstract;
	}

	public String getShowInExploreStr() {
		return showInExploreStr;
	}

	public void setShowInExploreStr(String showInExploreStr) {
		this.showInExploreStr = showInExploreStr;
	}

	public String getExtendsEntity() {
		return extendsEntity;
	}

	public void setExtendsEntity(String extendsEntity) {
		this.extendsEntity = extendsEntity;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getPerformanceTable() {
		return performanceTable;
	}

	public void setPerformanceTable(String performanceTable) {
		this.performanceTable = performanceTable;
	}

	public String getPartition() {
		return partition;
	}

	public void setPartition(String partition) {
		this.partition = partition;
	}



	public String getPartitionBaseField() {
		return partitionBaseField;
	}

	public void setPartitionBaseField(String partitionBaseField) {
		this.partitionBaseField = partitionBaseField;
	}
}
