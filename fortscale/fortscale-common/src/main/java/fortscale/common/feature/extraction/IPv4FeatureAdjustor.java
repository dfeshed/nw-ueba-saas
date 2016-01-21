package fortscale.common.feature.extraction;

import fortscale.common.event.Event;
import fortscale.common.feature.FeatureStringValue;
import fortscale.common.feature.FeatureValue;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fortscale.utils.logging.Logger;

@JsonTypeName(IPv4FeatureAdjustor.IPV4_FEATURE_ADJUSTOR_TYPE)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class IPv4FeatureAdjustor implements FeatureAdjustor {
	private static final Logger logger = Logger.getLogger(IPv4FeatureAdjustor.class);
	protected static final String IPV4_FEATURE_ADJUSTOR_TYPE = "ipv4_feature_adjustor";
	private static final int IPV4_FEATURE_ADJUSTOR_TYPE_HASH_CODE = IPV4_FEATURE_ADJUSTOR_TYPE.hashCode();

	private int subnetMask = 24;

	public IPv4FeatureAdjustor(@JsonProperty("subnetMask") int subnetMask) {
		setSubnetMask(subnetMask);
	}

	@Override
	public FeatureValue adjust(FeatureValue value, Event event) {
		FeatureStringValue ret = new FeatureStringValue();

		try {
			String ip = ((FeatureStringValue)value).toString();
			String classes[] = ip.split("\\.");
			if (classes.length != 4) {
				logger.info("wrong ip format: {}", ip);
			}

			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < 4; i++) {
				if (i > 0) {
					builder.append(".");
				}

				int mask = subnetMask - i * 8;
				if (mask >= 8) {
					builder.append(classes[i]);
				} else if (mask <= 0) {
					builder.append("0");
				} else {
					builder.append(maskClass(classes[i], 8 - mask));
				}
			}

			ret.setValue(builder.toString());
		} catch (Exception e) {
			logger.info("wrong ip format: {}", value);
		}

		return ret;
	}

	private String maskClass(String classValueStr, int mask) {
		int classValue = Integer.valueOf(classValueStr);
		classValue = classValue >> mask;
		return String.valueOf(classValue << mask);
	}

	public int getSubnetMask() {
		return subnetMask;
	}

	public void setSubnetMask(int subnetMask) {
		Assert.isTrue(subnetMask >= 0 && subnetMask <= 31, "subnetMask should be between 0 to 31");
		this.subnetMask = subnetMask;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		IPv4FeatureAdjustor that = (IPv4FeatureAdjustor)o;
		if (subnetMask != that.subnetMask)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return IPV4_FEATURE_ADJUSTOR_TYPE_HASH_CODE;
	}
}
