import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getAnomaliesTabs, selectedAnomaliesTab } from 'investigate-hosts/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  anomaliesTabs: getAnomaliesTabs(state),
  selectedAnomaliesTab: selectedAnomaliesTab(state)
});

const HostAnomalies = Component.extend({
  tagName: 'box',
  classNames: ['host-anomalies']
});

export default connect(stateToComputed)(HostAnomalies);
