import { computed } from '@ember/object';
import Component from '@ember/component';
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

  formattedRawAlert: computed('originalAlert', function() {
    if (this.originalAlert) {
      return JSON.stringify(this.originalAlert, undefined, 2);
    }
  })
});

export default connect(stateToComputed, undefined)(AlertOverview);
