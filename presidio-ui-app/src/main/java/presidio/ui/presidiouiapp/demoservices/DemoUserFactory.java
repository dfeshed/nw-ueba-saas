package presidio.ui.presidiouiapp.demoservices;

import fortscale.domain.core.Entity;


import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.BooleanUtils;


/**
 * Created by shays on 23/07/2017.
 */
public class  DemoUserFactory extends DemoFactoryAbstract<Entity>{
    private static final String [] FILE_HEADER_MAPPING = {"Username","Risk Score","Total Alerts","Admin"};

    protected String getFileName(){
        return "demo_users.csv";
    }

    protected String[] headers(){
        return  FILE_HEADER_MAPPING;
    }
    protected Entity getRecord(CSVRecord csvRecord){
        String username = csvRecord.get("Username");
        int riskScore = Integer.parseInt(csvRecord.get("Risk Score"));
        boolean isAdmin = BooleanUtils.toBoolean(csvRecord.get("Admin"));

        Entity entity = new Entity();
        entity.setAlertsCount(0);
        entity.setScore(riskScore);
        entity.setUsername(username);
        entity.setMockId(username);
        if(isAdmin) {
            entity.addTag("admin");
        }
        return entity;
    }

    public List getUsers() throws Exception {
        return getData(csvRecord -> this.getRecord(csvRecord),getFileName(),headers());
    }

}
