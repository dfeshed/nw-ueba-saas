package fortscale.domain.ad;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.time.Instant;
import java.util.regex.Pattern;

/**
 * This class represents a Document that represents a an active directory user's thumbnail photo.
 * PLEASE NOTICE - the _id of the documents should be the GUID of the active directory user
 */
@Document(collection=AdUserThumbnail.COLLECTION_NAME)
public class AdUserThumbnail implements Serializable {

	private static final long serialVersionUID = -4984590283062489036L;
	public static final String COLLECTION_NAME = "ad_user_thumb";
	public static final String FIELD_OBJECTGUID = "objectGUID";
	public static final String FIELD_THUMBNAIL_PHOTO = "thumbnailPhoto";
	public static final String MODIFIED_AT_FIELD_NAME = "modifiedAt";
	public static final String GUID_REGEX = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

	@Id
	private String id; //the GUID of the active directory user whose thumbnail we are representing

	@CreatedDate
    @Field(MODIFIED_AT_FIELD_NAME)
	@Indexed(unique = false, expireAfterSeconds=7*60*60*24*2) // retention = 1 week
    private Instant modifiedAt;
	
	// Contains the users's photo in Base64 format 
	@Field(FIELD_THUMBNAIL_PHOTO)
	private String thumbnailPhoto;


	/**
	 *
	 * @param objectGUID the GUID of the active directory user whose thumbnail we are constructing. NOTICE - This is going to be the _id of the document!
	 */
	public AdUserThumbnail(String objectGUID) {
		Assert.notNull(objectGUID);
		Assert.isTrue(Pattern.matches(GUID_REGEX, objectGUID), String.format("Given objectId %s is not a valid GUID (according to regex %s)", objectGUID, GUID_REGEX));
		this.id = objectGUID;
	}

	public String getIdD() {
		return id;
	}

	public void setId(String objectGUID) {
		this.id = objectGUID;
	}

	public Instant getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Instant modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String getThumbnailPhoto() {
		return thumbnailPhoto;
	}

	public void setThumbnailPhoto(String thumbnailPhoto) {
		this.thumbnailPhoto = thumbnailPhoto;
	}



}
