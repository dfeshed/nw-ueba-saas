import Component from 'ember-component';
import { connect } from 'ember-redux';

import { isSchemaLoaded, columns, preferenceConfig } from 'investigate-files/reducers/schema/selectors';

const stateToComputed = (state) => ({
  columnsConfig: columns(state),
  isSchemaLoaded: isSchemaLoaded(state),
  preferencesConfig: preferenceConfig(state)
});

const FilesPreference = Component.extend({
});
export default connect(stateToComputed)(FilesPreference);