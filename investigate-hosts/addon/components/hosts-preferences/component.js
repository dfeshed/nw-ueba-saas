import Component from 'ember-component';
import { connect } from 'ember-redux';

import { isSchemaLoaded, preferencesConfig } from 'investigate-hosts/reducers/schema/selectors';

const stateToComputed = (state) => ({
  isSchemaLoaded: isSchemaLoaded(state),
  preferencesConfig: preferencesConfig(state)
});

const HostsPreference = Component.extend({
});
export default connect(stateToComputed)(HostsPreference);