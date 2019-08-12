import Component from '@ember/component';
import { connect } from 'ember-redux';
import { initiateUser } from 'investigate-users/actions/user-details';

const dispatchToActions = {
  initiateUser
};

const AlertRowComponent = Component.extend({
  alertClicked: null,
  actions: {
    expandAlert(alertId) {
      if (this.get('alertClicked') === alertId) {
        this.set('alertClicked', null);
      } else {
        this.set('alertClicked', alertId);
      }
    }
  }
});

export default connect(null, dispatchToActions)(AlertRowComponent);
