import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';

const stateToComputed = (state) => {
  const { respond: { alert: { originalAlert, originalAlertStatus } } } = state;

  return {
    originalAlert,
    originalAlertStatus
  };
};

/**
 * @class AlertOverview
 * Represents the Overview view in the Inspector to display the metadata properties of a Remdiation Task
 *
 * @public
 */
const AlertOverview = Component.extend({
  tagName: 'vbox',
  classNames: ['rsa-alert-overview'],
  actions: {
    update() {}
  },
  @computed('originalAlert')
  formattedRawAlert(originalAlert) {
    if (originalAlert) {
      return JSON.stringify(originalAlert, undefined, 2);
    }
  }
});

export default connect(stateToComputed, undefined)(AlertOverview);
