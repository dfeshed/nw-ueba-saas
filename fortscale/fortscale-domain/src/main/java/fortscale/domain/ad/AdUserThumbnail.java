package fortscale.domain.ad;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import fortscale.domain.core.AbstractDocument;


@Document(collection=AdUserThumbnail.COLLECTION_NAME)
public class AdUserThumbnail extends AbstractDocument{
	public static final String COLLECTION_NAME = "ad_user_thumb";
	public static final String objectGUIDField = "objectGUID";
	public static final String thumbnailPhotoField = "thumbnailPhoto";
	public static final String CREATED_AT_FIELD_NAME = "createdAt";
	
	
	@Indexed
	@Field(objectGUIDField)
	private String objectGUID;
	
	@CreatedDate
    @Field(CREATED_AT_FIELD_NAME)
	@Indexed(unique = false, expireAfterSeconds=60*60*24*2)
    private DateTime createdAt;
	
	// Contains the users's photo in Base64 format 
	@Field(thumbnailPhotoField)
	private String thumbnailPhoto;

	public String getObjectGUID() {
		return objectGUID;
	}

	public void setObjectGUID(String objectGUID) {
		this.objectGUID = objectGUID;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public String getThumbnailPhoto() {
		return thumbnailPhoto;
	}

	public void setThumbnailPhoto(String thumbnailPhoto) {
		this.thumbnailPhoto = thumbnailPhoto;
	}
	
	
	
}
