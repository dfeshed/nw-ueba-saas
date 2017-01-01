package fortscale.domain.ad;

import fortscale.domain.core.AbstractDocument;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;


@Document(collection=AdUserThumbnail.COLLECTION_NAME)
public class AdUserThumbnail extends AbstractDocument {

	private static final long serialVersionUID = -4984590283062489036L;
	public static final String COLLECTION_NAME = "ad_user_thumb";
	public static final String FIELD_OBJECT_GUID = "objectGUID";
	public static final String FIELD_THUMBNAIL_PHOTO = "thumbnailPhoto";
	public static final String FIELD_MODIFIED_AT = "modifiedAt";
	
	
	@Indexed
	@Field(FIELD_OBJECT_GUID)
	private String objectGUID;
	
	@CreatedDate
    @Field(FIELD_MODIFIED_AT)
	@Indexed(unique = false, expireAfterSeconds=60*60*24*2)
    private Instant modifiedAt;
	
	// Contains the users's photo in Base64 format 
	@Field(FIELD_THUMBNAIL_PHOTO)
	private String thumbnailPhoto;

	public String getObjectGUID() {
		return objectGUID;
	}

	public void setObjectGUID(String objectGUID) {
		this.objectGUID = objectGUID;
	}

	public Instant getModifiedAt() {
		return modifiedAt;
	}

	public String getThumbnailPhoto() {
		return thumbnailPhoto;
	}

	public void setThumbnailPhoto(String thumbnailPhoto) {
		this.thumbnailPhoto = thumbnailPhoto;
	}
	
	
	
}
