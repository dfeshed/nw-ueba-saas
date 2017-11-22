import Component from 'ember-component';
import layout from './template';
import service from 'ember-service/inject';
import computed, { equal } from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';
import { connect } from 'ember-redux';
import { updateScheduleProperty } from 'hosts-scan-configure/actions/data-creators';
import { startTime } from 'hosts-scan-configure/reducers/schedule/selectors';

const stateToComputed = (state) => ({
  startTime: startTime(state)
});

const dispatchToActions = {
  updateScheduleProperty
};


/**
 * New component for reading input
 * @public
 */
const ScheduleTime = Component.extend({
  layout,

  tagName: 'box',

  i18n: service(),

  timeFormat: service(),

  classNames: 'schedule-time',

  @equal('timeFormat.selected.key', 'HR24') time24HR: null,

  @computed('i18n.locale')
  locale: (locale) => {
    return locale.split('-')[0];
  },

  @computed('time24HR')
  convertedTimeFormat: (time24HR) => {
    return time24HR ? 'H:i:S' : 'h:i:S K';
  },

  actions: {
    onTimeChange(selectedDates, dateString, flatpikr) {
      const selectedTime = flatpikr.element.value;
      if (!isEmpty(selectedTime)) {
        this.send('updateScheduleProperty', 'startTime', selectedTime);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ScheduleTime);
