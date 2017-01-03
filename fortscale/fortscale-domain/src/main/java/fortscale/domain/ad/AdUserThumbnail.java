package fortscale.domain.ad;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class represents a Document that represents a an active directory user's thumbnail photo.
 * PLEASE NOTICE - the _id of the documents should be the GUID of the active directory user
 */
@Document(collection=AdUserThumbnail.COLLECTION_NAME)
@EnableMongoAuditing
public class AdUserThumbnail implements Serializable {

	private static final long serialVersionUID = -4984590283062489036L;
	public static final String ID_FIELD = "_id";
	public static final String COLLECTION_NAME = "ad_user_thumb";
	public static final String FIELD_THUMBNAIL_PHOTO = "thumbnailPhoto";
	public static final String MODIFIED_AT_FIELD_NAME = "modifiedAt";
	public static final String CREATED_AT_FIELD_NAME = "createdAt";

	@Id
	private String id; //the GUID of the active directory user whose thumbnail we are representing

	@LastModifiedDate
	@Field(MODIFIED_AT_FIELD_NAME)
	@Indexed(unique = false, expireAfterSeconds=7*60*60*24*2) // retention = 1 week
	private DateTime modifiedAt;

	@CreatedDate
	@Field(CREATED_AT_FIELD_NAME)
	@Indexed(unique = false)
	private DateTime createdAt;

	// Contains the users's photo in Base64 format
	@Field(FIELD_THUMBNAIL_PHOTO)
	private String thumbnailPhoto;

	public String getId() {
		return id;
	}

	public void setId(String objectGuidAsId) {
		this.id = objectGuidAsId;
	}

	public DateTime getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(DateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String getThumbnailPhoto() {
		return thumbnailPhoto;
	}

	public void setThumbnailPhoto(String thumbnailPhoto) {
		this.thumbnailPhoto = thumbnailPhoto;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AdUserThumbnail that = (AdUserThumbnail) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(thumbnailPhoto, that.thumbnailPhoto);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, thumbnailPhoto);
	}
}
