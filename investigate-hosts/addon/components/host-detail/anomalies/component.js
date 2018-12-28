import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setAnomaliesTabView } from 'investigate-hosts/actions/data-creators/details';
import { getAnomaliesTabs, selectedAnomaliesTab } from 'investigate-hosts/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  anomaliesTabs: getAnomaliesTabs(state),
  selectedAnomaliesTab: selectedAnomaliesTab(state)
});

const dispatchToActions = {
  setAnomaliesTabView
};

const HostAnomalies = Component.extend({
  tagName: 'box',
  classNames: ['host-anomalies']
});

export default connect(stateToComputed, dispatchToActions)(HostAnomalies);
