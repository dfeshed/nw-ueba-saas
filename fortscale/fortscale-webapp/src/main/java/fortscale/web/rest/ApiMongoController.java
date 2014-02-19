package fortscale.web.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;


@Controller
@RequestMapping("/api/mongo/**")
public class ApiMongoController extends BaseController{
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@RequestMapping(value="/find", method=RequestMethod.POST)
	@LogException
	public DataBean<List<DBObject>> find(@RequestParam(required=true) String collectionName,
			@RequestParam(required=false) String query,
			@RequestParam(required=false) String keys,
			@RequestParam(defaultValue="0") Integer skip,
			@RequestParam(defaultValue="10") Integer limit,
			Model model){
		DBCollection dbCollection = mongoTemplate.getDb().getCollection(collectionName);
		
		DBCursor cursor = null;
		DataBean<List<DBObject>> ret = new DataBean<List<DBObject>>();
		try{
			cursor = dbCollection.find((DBObject) JSON.parse(query), (DBObject) JSON.parse(keys));
		
			cursor.skip(skip);
			cursor.limit(limit);
	
			
			ret.setData(cursor.toArray());
			ret.setTotal(cursor.count());
		} finally{
			if(cursor != null){
				cursor.close();
			}
		}
		
		return ret;
	}
}
