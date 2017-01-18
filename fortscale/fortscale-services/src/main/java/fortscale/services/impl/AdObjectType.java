package fortscale.services.impl;

/**
 * Created by alexp on 12/01/17.
 */
public enum AdObjectType {
        GROUP("Group"), OU("OU"), USER("User"), COMPUTER("Computer");

        private final String displayName;

        AdObjectType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
}
