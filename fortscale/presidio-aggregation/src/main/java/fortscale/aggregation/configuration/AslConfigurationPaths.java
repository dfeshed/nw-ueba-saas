package fortscale.aggregation.configuration;

/**
 * A container of base, overriding and additional configuration paths for an {@link AslConfigurationService}.
 * The base, overriding and additional configurations should be of a certain logical group whose name is "groupName"
 * (for example, "enriched record models").
 *
 * Created by Lior Govrin on 03/07/2017.
 */
public class AslConfigurationPaths {
	private final String groupName;
	private final String baseConfigurationPath;
	private final String overridingConfigurationPath;
	private final String additionalConfigurationPath;

	/**
	 * C'tor.
	 *
	 * @param groupName                   the name of the logical group
	 * @param baseConfigurationPath       the path to the base configurations
	 * @param overridingConfigurationPath the path to the overriding configurations
	 * @param additionalConfigurationPath the path to the additional configurations
	 */
	public AslConfigurationPaths(
			String groupName,
			String baseConfigurationPath,
			String overridingConfigurationPath,
			String additionalConfigurationPath) {

		this.groupName = groupName;
		this.baseConfigurationPath = baseConfigurationPath;
		this.overridingConfigurationPath = overridingConfigurationPath;
		this.additionalConfigurationPath = additionalConfigurationPath;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getBaseConfigurationPath() {
		return baseConfigurationPath;
	}

	public String getOverridingConfigurationPath() {
		return overridingConfigurationPath;
	}

	public String getAdditionalConfigurationPath() {
		return additionalConfigurationPath;
	}
}
