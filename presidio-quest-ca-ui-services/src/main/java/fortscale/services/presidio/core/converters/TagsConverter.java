package fortscale.services.presidio.core.converters;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import presidio.output.client.model.PatchOperation;
//import presidio.output.client.model.PatchOperation;

import java.util.*;

/**
 * Created by shays on 09/09/2017.
 */
public class TagsConverter extends AbstractItemConverter<String,String>{

    public static final String WATECHED_TAG_STRING = "watched";
    public static final String ADMIN_TAG_STRING = "admin";

    public List<String> convertUiFilterToQueryDto(String tagsCommaSeperatedString,Boolean isWatched) {

        List<String> tagsResult = new ArrayList<>();
        if (StringUtils.isEmpty(tagsCommaSeperatedString)){
            return Collections.EMPTY_LIST;
        }

        //Support only admin tags
        Set<String> tags = splitAndTrim(tagsCommaSeperatedString,true);
        if (tags.contains(ADMIN_TAG_STRING) || tags.contains("any")){
            tagsResult.add(ADMIN_TAG_STRING);
        }

        if (BooleanUtils.isTrue(isWatched)){
            tagsResult.add(WATECHED_TAG_STRING);
        }
        return tagsResult;
    }

    public List<String> convertUiFilterToQueryDto(List<String> tags,Boolean isWatched){

        if (CollectionUtils.isEmpty(tags)){
            tags =  new ArrayList<>();
        }
        if (BooleanUtils.isTrue(isWatched) && !tags.contains(WATECHED_TAG_STRING)){
            tags.add(WATECHED_TAG_STRING);
        }

        if (!BooleanUtils.isTrue(isWatched) && tags.contains(WATECHED_TAG_STRING)){
            tags.remove(WATECHED_TAG_STRING);
        }
        return tags;
    }

    public boolean isWatched(List<String> tags){
        return containsTag(tags,WATECHED_TAG_STRING);
    }

    public boolean isAdmin(List<String> tags){
        return containsTag(tags,ADMIN_TAG_STRING);
    }

    public PatchOperation createWatchPatchOperation(boolean watch) {

        PatchOperation patchOperation = new PatchOperation();
        patchOperation.setOp(watch ? PatchOperation.OpEnum.ADD : PatchOperation.OpEnum.REMOVE);
        patchOperation.setPath("/tags/-");
        patchOperation.setValue(WATECHED_TAG_STRING);
        return patchOperation;
    }

    private boolean containsTag(List<String> tags,String tagName){
        if(CollectionUtils.isNotEmpty(tags)){
            for (String tag:tags){
                if (tag.equalsIgnoreCase(tagName)){
                    return true;
                }
            }
        }

        return false;

    }

}
