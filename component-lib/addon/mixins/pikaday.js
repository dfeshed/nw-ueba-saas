/* globals Pikaday */
import computed from 'ember-computed';
import service from 'ember-service/inject';
import Ember from 'ember';
import moment from 'moment';

const {
  isPresent,
  run,
  assign,
  Mixin,
  isEmpty
} = Ember;

export default Mixin.create({
  _options: computed('options', 'i18n', {
    get() {
      const options = this._defaultOptions();

      if (isPresent(this.get('i18n'))) {
        if (isPresent(this.get('i18n').t)) {
          options.i18n = {
            previousMonth: this.get('i18n').t('previousMonth').toString(),
            nextMonth: this.get('i18n').t('nextMonth').toString(),
            months: this.get('i18n').t('months').toString().split(','),
            weekdays: this.get('i18n').t('weekdays').toString().split(','),
            weekdaysShort: this.get('i18n').t('weekdaysShort').toString().split(','),
            midnight: this.get('i18n').t('midnight').toString(),
            noon: this.get('i18n').t('noon').toString()
          };
        } else {
          options.i18n = this.get('i18n');
        }
      }
      if (isPresent(this.get('position'))) {
        options.position = this.get('position');
      }
      if (isPresent(this.get('reposition'))) {
        options.reposition = this.get('reposition');
      }

      assign(options, this.get('options') || {});
      return options;
    }
  }),
  // Could not find a service that gets this value, need to add a service to
  // pull this from local storage.
  localeKey: 'rsa-i18n-default-locale',

  dateFormat: service(),

  timeFormat: service(),

  _defaultOptions() {
    const firstDay = this.get('firstDay');
    const prefTimeKey = this.get('timeFormat.selected.key') || 'HR24';
    const is24 = (prefTimeKey === 'HR24') || false;
    const lc = localStorage[this.get('localeKey')] || 'en';

    // Set this locale for this instance of moment.
    moment.locale(lc);

    return {
      field: this.get('field'),
      container: this.get('pikadayContainer'),
      bound: !this.get('pikadayContainer'),
      onOpen: run.bind(this, this.onPikadayOpen),
      onClose: run.bind(this, this.onPikadayClose),
      onSelect: run.bind(this, this.onPikadaySelect),
      onDraw: run.bind(this, this.onPikadayRedraw),
      firstDay: (typeof firstDay !== 'undefined') ? parseInt(firstDay, 10) : 1,
      format: this.get('outputDateTimeFormat'),
      yearRange: this.determineYearRange(),
      minDate: this.get('minDate') || null,
      maxDate: this.get('maxDate') || null,
      theme: (!isEmpty(this.get('theme')) && typeof(this.get('theme')) === 'string') ? this.get('theme') : 'dark-theme',
      showTime: this.get('showTime'),
      showSeconds: this.get('showSeconds') || false,
      use24hour: this.get('use24hour') || is24,
      incrementHourBy: this.get('incrementHourBy') || 1,
      incrementMinuteBy: this.get('incrementMinuteBy') || 1,
      incrementSecondBy: this.get('incrementSecondBy') || 1,
      timeLabel: this.get('timeLabel') || null
    };
  },

  outputDateTimeFormat: computed('dateFormat.selected.format', 'timeFormat.selected.format', function() {
    if (!isEmpty(this.get('format'))) {
      return this.get('format');
    } else if (isEmpty(this.get('format')) && this.get('showTime')) {
      return `${this.get('dateFormat.selected.format') || 'MM/DD/YYYY'} ${this.get('timeFormat.selected.format') || 'HH:mm'}`;
    } else {
      return this.get('dateFormat.selected.format') || 'MM/DD/YYYY';
    }
  }),

  didUpdateAttrs() {
    this._super(...arguments);
    this.setMinDate();
    this.setMaxDate();
    this.setPikadayDate();

    if (this.get('options')) {
      this._updateOptions();
    }
  },

  didRender() {
    this._super(...arguments);
    this.autoHideOnDisabled();
  },

  setupPikaday() {
    const pikaday = new Pikaday(this.get('_options'));

    this.set('pikaday', pikaday);
    this.setPikadayDate();
  },

  willDestroyElement() {
    this._super(...arguments);
    this.get('pikaday').destroy();
  },

  setPikadayDate() {
    const format = 'YYYY-MM-DD';
    const value = this.get('value');
    if (!value) {
      this.get('pikaday').setDate(value, true);
    } else {
      const date = this.get('useUTC') ? moment(moment.utc(value).format(format), format).toDate() : value;
      this.get('pikaday').setDate(date, true);
    }
  },

  setMinDate() {
    if (this.get('minDate')) {
      run.later(() => {
        this.get('pikaday').setMinDate(this.get('minDate'));
      });
    }
  },

  setMaxDate() {
    if (this.get('maxDate')) {
      run.later(() => {
        this.get('pikaday').setMaxDate(this.get('maxDate'));
      });
    }
  },

  onOpen() {},
  onClose() {},
  onSelection() {},
  onDraw() {},

  onPikadaySelect() {
    this.userSelectedDate();
  },

  onPikadayRedraw() {
    this.get('onDraw')();
  },

  userSelectedDate() {
    let selectedDate = this.get('pikaday').getDate();
    if (this.get('useUTC')) {
      selectedDate = moment.utc([selectedDate.getFullYear(), selectedDate.getMonth(), selectedDate.getDate()]).toDate();
    }

    this.get('onSelection')(selectedDate);
  },

  determineYearRange() {
    const yearRange = this.get('yearRange');

    if (yearRange) {
      if (yearRange.indexOf(',') > -1) {
        const yearArray = yearRange.split(',');

        if (yearArray[1] === 'currentYear') {
          yearArray[1] = new Date().getFullYear();
        }

        return yearArray;
      } else {
        return yearRange;
      }
    } else {
      return 10;
    }
  },

  autoHideOnDisabled() {
    if (this.get('disabled') && this.get('pikaday')) {
      this.get('pikaday').hide();
    }
  },

  _updateOptions() {
    this.get('pikaday').config(this.get('_options'));
  }
});
