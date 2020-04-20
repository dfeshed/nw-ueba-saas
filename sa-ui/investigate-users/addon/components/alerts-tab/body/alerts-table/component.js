import Component from '@ember/component';
import { connect } from 'ember-redux';
import { later } from '@ember/runloop';
import { alertsGroupedDaily, allAlertsReceived } from 'investigate-users/reducers/alerts/selectors';
import { getAlertsForGivenTimeInterval } from 'investigate-users/actions/alert-details';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  groupedAlerts: alertsGroupedDaily(state),
  allAlertsReceived: allAlertsReceived(state)
});
const dispatchToActions = {
  getAlertsForGivenTimeInterval
};

const AlertTabTableComponent = Component.extend({
  classNames: 'alerts-tab_body_body-table',
  scrolling: false,
  _scrollHandler({ target }) {
    // This logic to avoid multiple server calls when user is scrolling.
    if (false === this.get('scrolling')) {
      this.set('scrolling', true);
      later(() => {
        if (target.scrollHeight - (target.scrollTop + target.offsetHeight) < 30) {
          if (!this.get('allAlertsReceived')) {
            this.set('scrolling', true);
            this.send('getAlertsForGivenTimeInterval');
          }
        } else {
          this.set('scrolling', false);
        }
      }, 500);
    }
  },

  didInsertElement() {
    this._super(...arguments);
    const scrollHandler = this._scrollHandler.bind(this);
    document.querySelector('.alerts-tab_body_body-table_body').addEventListener('scroll', scrollHandler);
  },
  willDestroyElement() {
    this._super(...arguments);
    const scrollHandler = this._scrollHandler;
    document.querySelector('.alerts-tab_body_body-table_body').removeEventListener('scroll', scrollHandler);
  },

  @computed('groupedAlerts')
  groupedAlertsByDay(groupedAlerts) {
    document.querySelector('.alerts-tab_body_body-table_body').scrollTop = document.querySelector('.alerts-tab_body_body-table_body').scrollTop - 120;
    this.set('scrolling', false);
    return groupedAlerts;
  }
});

export default connect(stateToComputed, dispatchToActions)(AlertTabTableComponent);
