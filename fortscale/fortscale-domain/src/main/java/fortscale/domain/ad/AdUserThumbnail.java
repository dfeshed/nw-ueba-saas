package fortscale.domain.ad;

import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

/**
 * This class represents a Document that represents a an active directory user's thumbnail photo.
 * PLEASE NOTICE - the _id of the documents should be the GUID of the active directory user
 */
@Document(collection=AdUserThumbnail.COLLECTION_NAME)
@EnableMongoAuditing
public class AdUserThumbnail extends AbstractAuditableDocument {

	private static final long serialVersionUID = -4984590283062489036L;
	public static final String COLLECTION_NAME = "ad_user_thumb";
	public static final String FIELD_THUMBNAIL_PHOTO = "thumbnailPhoto";


	// Contains the users's photo in Base64 format
	@Field(FIELD_THUMBNAIL_PHOTO)
	private String thumbnailPhoto;

	/**
	 * sets the GUID (in base64) of the user whose thumbnail this is as the document id
	 * @param objectGuidAsId the GUID (in base64) of the user whose thumbnail this is
	 */
	@Override
	public void setId(String objectGuidAsId) {
		this.id = objectGuidAsId;
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
