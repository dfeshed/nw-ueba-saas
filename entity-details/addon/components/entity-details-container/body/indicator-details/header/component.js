import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { getSelectedAlertData } from 'entity-details/reducers/alerts/selectors';
import { resetIndicators } from 'entity-details/actions/indicator-details';

const stateToComputed = (state) => ({
  alertDetails: getSelectedAlertData(state)
});

const dispatchToActions = {
  resetIndicators
};

const IndicatorDetailsHeaderComponent = Component.extend({
  layout,
  classNames: ['entity-details-container-body-indicator-details_header']
});

export default connect(stateToComputed, dispatchToActions)(IndicatorDetailsHeaderComponent);
