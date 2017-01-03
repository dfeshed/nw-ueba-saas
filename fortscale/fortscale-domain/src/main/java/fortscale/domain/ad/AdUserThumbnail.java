package fortscale.domain.ad;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.Instant;
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
	public static final String FIELD_MODIFIED_AT = "modifiedAt";
	public static final String FIELD_CREATED_AT = "createdAt";
	public static final String FIELD_VERSION_FIELD = "version";

	@Id
	private String id; //the GUID of the active directory user whose thumbnail we are representing

	@LastModifiedDate
	@Field(FIELD_MODIFIED_AT)
	@Indexed(unique = false, expireAfterSeconds=7*60*60*24*2) // retention = 1 week
	private Instant modifiedAt;

	@CreatedDate
	@Field(FIELD_CREATED_AT)
	private Instant createdAt;

	@Version
	@Field(FIELD_VERSION_FIELD)
	private Long version;

	// Contains the users's photo in Base64 format
	@Field(FIELD_THUMBNAIL_PHOTO)
	private String thumbnailPhoto;

	public String getId() {
		return id;
	}

	public void setId(String objectGuidAsId) {
		this.id = objectGuidAsId;
	}

	public Instant getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Instant modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
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
