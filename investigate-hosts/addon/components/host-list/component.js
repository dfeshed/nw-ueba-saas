import { connect } from 'ember-redux';
import Component from '@ember/component';
import { setDataSourceTab, setHostPropertyTabView } from 'investigate-hosts/actions/data-creators/details';
import { getDataSourceTab, getContext, getAlertsCount, getIncidentsCount } from 'investigate-hosts/reducers/visuals/selectors';

import {
  resetFilters
} from 'investigate-hosts/actions/data-creators/filter';

const stateToComputed = (state) => ({
  schemaLoading: state.endpoint.schema.schemaLoading,
  activeDataSourceTab: state.endpoint.visuals.activeDataSourceTab,
  contextError: state.endpoint.visuals.contextError,
  dataSourceTabs: getDataSourceTab(state),
  context: getContext(state),
  alertsCount: getAlertsCount(state),
  incidentsCount: getIncidentsCount(state)
});

const dispatchToActions = {
  resetFilters,
  setDataSourceTab,
  setHostPropertyTabView
};
const Container = Component.extend({

  tagName: '',

  classNames: 'host-list show-more-filter main-zone'

});

export default connect(stateToComputed, dispatchToActions)(Container);
