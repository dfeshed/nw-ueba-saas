package fortscale.web.demoservices;

import fortscale.domain.core.User;


import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.BooleanUtils;


/**
 * Created by shays on 23/07/2017.
 */
public class  DemoUserFactory extends DemoFactoryAbstract<User>{
    private static final String [] FILE_HEADER_MAPPING = {"Username","Risk Score","Total Alerts","Admin"};

    protected String getFileName(){
        return "demo_users.csv";
    }

    protected String[] headers(){
        return  FILE_HEADER_MAPPING;
    }
    protected User getRecord(CSVRecord csvRecord){
        String username = csvRecord.get("Username");
        int riskScore = Integer.parseInt(csvRecord.get("Risk Score"));
        boolean isAdmin = BooleanUtils.toBoolean(csvRecord.get("Admin"));

        User user = new User();
        user.setAlertsCount(0);
        user.setScore(riskScore);
        user.setUsername(username);
        user.setMockId(username);
        if(isAdmin) {
            user.addTag("admin");
        }
        return user;
    }

    public List getUsers() throws Exception {
        return getData(csvRecord -> this.getRecord(csvRecord),getFileName(),headers());
    }
//    public List<User> getList() throws IOException {
//        List<User> users = new ArrayList<>();
//        getRawRecorsIterator("demo-users.csv", FILE_HEADER_MAPPING).forEach((record)->{
//            String username = record.get("Username");
//            int riskScore = Integer.parseInt(record.get("Risk Score"));
//            int totalAlert= Integer.parseInt(record.get("Total Alerts"));
//
//            User user = new User();
//            user.setAlertsCount(totalAlert);
//            user.setScore(riskScore);
//            user.setUsername(username);
//            user.setMockId(username);
//            users.add(user);
//        });
//
//
//        return users;
//    }
}
