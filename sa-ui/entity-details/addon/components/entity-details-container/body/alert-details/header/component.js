import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getSelectedAlertData } from 'entity-details/reducers/alerts/selectors';
import { alertIsNotARisk } from 'entity-details/actions/alert-details';

const stateToComputed = (state) => ({
  alertDetails: getSelectedAlertData(state)
});

const dispatchToActions = {
  alertIsNotARisk
};


const AlertDetailsHeaderComponent = Component.extend({
  classNames: ['entity-details-container-body-alert-details_header'],
  actions: {
    updateRisk() {
      if (this.get('alertDetails.userScoreContribution') === 0) {
        this.send('alertIsNotARisk', { status: 'Open', feedback: 'None' });
      } else {
        this.send('alertIsNotARisk', { status: 'Closed', feedback: 'Rejected' });
      }
    }
  }
});


export default connect(stateToComputed, dispatchToActions)(AlertDetailsHeaderComponent);
