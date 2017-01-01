package fortscale.domain.ad;

import fortscale.domain.core.AbstractDocument;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection=AdUserThumbnail.COLLECTION_NAME)
public class AdUserThumbnail extends AbstractDocument {

	private static final long serialVersionUID = -4984590283062489036L;
	public static final String COLLECTION_NAME = "ad_user_thumb";
	public static final String FIELD_OBJECT_GUID = "objectGUID";
	public static final String FIELD_THUMBNAIL_PHOTO = "thumbnailPhoto";
	public static final String FIELD_MODIFIED_AT = "modifiedAt";
	public static final String FIELD_CREATED_AT = "createdAt";

	// Contains the users's objectGUID in Base64 format
	@Indexed
	@Field(FIELD_OBJECT_GUID)
	private String objectGUID;
	
	@LastModifiedDate
    @Field(FIELD_MODIFIED_AT)
	@Indexed(unique = false, expireAfterSeconds=60*60*24*2)
    private DateTime modifiedAt;

	@CreatedDate
	@Field(FIELD_CREATED_AT)
	@Indexed(unique = false)
	private DateTime createdAt;
	
	// Contains the users's photo in Base64 format 
	@Field(FIELD_THUMBNAIL_PHOTO)
	private String thumbnailPhoto;

	public String getObjectGUID() {
		return objectGUID;
	}

	public void setObjectGUID(String objectGUID) {
		this.objectGUID = objectGUID;
	}

	public DateTime getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(DateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getThumbnailPhoto() {
		return thumbnailPhoto;
	}

	public void setThumbnailPhoto(String thumbnailPhoto) {
		this.thumbnailPhoto = thumbnailPhoto;
	}
	
	
	
}
