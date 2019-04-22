import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  isSourcesLoading
} from 'admin-source-management/reducers/usm/sources-selectors';
import creators from 'admin-source-management/actions/creators/sources-creators';
import columns from './columns';

const stateToComputed = (state) => ({
  isSourcesLoading: isSourcesLoading(state)
});

const UsmSources = Component.extend({
  classNames: ['usm-sources'],
  columns,
  creators
});

export default connect(stateToComputed)(UsmSources);