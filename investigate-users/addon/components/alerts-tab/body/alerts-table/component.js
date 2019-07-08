import Component from '@ember/component';
import { connect } from 'ember-redux';
import { later } from '@ember/runloop';
import { getAlertsGroupedHourly, allAlertsReceived } from 'investigate-users/reducers/alerts/selectors';
import { getAlertsForGivenTimeInterval } from 'investigate-users/actions/alert-details';
import { columnsDataForIndicatorTable } from 'investigate-users/utils/column-config';
import { initiateUser } from 'investigate-users/actions/user-details';

const stateToComputed = (state) => ({
  groupedAlerts: getAlertsGroupedHourly(state),
  allAlertsReceived: allAlertsReceived(state)
});
const dispatchToActions = {
  getAlertsForGivenTimeInterval,
  initiateUser
};

const scrollHandler = ({ target }) => {
  // This logic to avoid multiple server calls when user is scrolling.
  if (false === this.get('scrolling')) {
    this.set('scrolling', true);
    later(() => {
      if (target.scrollHeight - (target.scrollTop + target.offsetHeight) < 30) {
        if (!this.get('allAlertsReceived')) {
          this.send('getAlertsForGivenTimeInterval');
        }
      }
      this.set('scrolling', false);
    }, 500);
  }
};

const AlertTabTableComponent = Component.extend({
  classNames: 'alerts-tab_body_body-table',
  scrolling: false,
  alertClicked: null,
  columnsData: columnsDataForIndicatorTable,

  didInsertElement() {
    this._super(...arguments);
    document.querySelector('.alerts-tab_body_body-table_body').addEventListener('scroll', scrollHandler);
  },
  willDestroyElement() {
    this._super(...arguments);
    document.querySelector('.alerts-tab_body_body-table_body').removeEventListener('scroll', scrollHandler);
  },
  actions: {
    expandAlert(alertId) {
      this.set('alertClicked', alertId);
    },
    selectUser(alertDetails, { id }) {
      this.send('initiateUser', { ...alertDetails, indicatorId: id });
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(AlertTabTableComponent);
