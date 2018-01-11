import Component from 'ember-component';
import layout from '../templates/components/rsa-form-datetime';
import service from 'ember-service/inject';
import computed, { equal } from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';

export default Component.extend({
  layout,

  i18n: service(),

  dateFormat: service(),

  timeFormat: service(),

  classNames: ['rsa-form-input'],

  classNameBindings: ['isDisabled', 'isError'],

  enableSeconds: true,

  errorMessage: null,

  isDisabled: false,

  isError: false,

  label: null,

  placeholder: null,

  tagName: 'label',

  defaultHour: 12,

  defaultMinute: 0,

  static: false,

  enableTime: true,

  @equal('timeFormat.selected.key', 'HR24') time24HR: null,

  @computed('i18n.locale')
  locale: (locale) => {
    return locale.split('-')[0];
  },

  @computed('dateFormat.selected.key')
  convertedDateFormat: (dateFormat) => {
    let format;
    switch (dateFormat) {
      case 'MM/dd/yyyy':
        format = 'm/d/Y';
        break;
      case 'dd/MM/yyyy':
        format = 'Y/m/d';
        break;
      case 'yyyy/MM/dd':
        format = 'Y/m/d';
        break;
    }

    return format;
  },

  @computed('time24HR')
  convertedTimeFormat: (time24HR) => {
    return time24HR ? 'H:i:S' : 'h:i:S K';
  },

  @computed('convertedTimeFormat', 'convertedDateFormat', 'enableTime')
  convertedFullFormat: (timeFormat, dateFormat, enableTime) => {
    if (!enableTime) {
      return dateFormat;
    }
    return `${dateFormat} ${timeFormat}`;
  },

  actions: {
    forceChangeOnClose(selectedDates, dateString, flatpikr) {
      const inputDate = new Date(flatpikr.element.value);
      const validDate = inputDate.getTime() - inputDate.getTime() === 0;

      if (validDate) {
        const selectedDate = selectedDates.get('firstObject');

        if (selectedDate) {
          const selectedDateMs = selectedDate.getTime();
          const inputDateMs = inputDate.getTime();

          if ((!isEmpty(inputDateMs) && inputDateMs !== selectedDateMs)) {
            flatpikr.setDate(inputDate);
            this.onChange([inputDate]);
          }
        } else {
          flatpikr.setDate(inputDate);
          this.onChange([inputDate]);
        }
      } else {
        flatpikr.setDate(null);
        this.onChange([]);
      }
    }
  }

});
