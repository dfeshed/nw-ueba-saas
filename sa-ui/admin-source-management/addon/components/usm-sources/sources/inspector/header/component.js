import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  focusedSource
} from 'admin-source-management/reducers/usm/sources-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  focusedSource: focusedSource(state)
});

const UsmSourcesInspectorHeader = Component.extend({
  classNames: ['usm-sources-inspector-header']
});

export default connect(stateToComputed, dispatchToActions)(UsmSourcesInspectorHeader);