import Controller from '@ember/controller';

export default Controller.extend({
  use12HourClock: false,
  start: 193874400000,
  end: 323413200000,
  dateFormat: 'MM/DD/YYYY',
  includeSeconds: true,
  isValid: true,
  timezone: 'UTC',
  actions: {
    handleTimestampChange(property, event) {
      const value = parseInt(event.target.value, 10);
      this.set(property, value);
    },
    toggleUse12HourClock() {
      this.set('use12HourClock', !this.get('use12HourClock'));
    },
    handleDateFormatChanged(format) {
      this.set('dateFormat', format);
    },
    toggleIncludeSeconds() {
      this.set('includeSeconds', !this.get('includeSeconds'));
    },
    handleRangeChange(start, end) {
      this.set('isValid', true);
      this.set('start', start);
      this.set('end', end);
    },
    handleRangeError() {
      this.set('isValid', false);
    },
    handleTimezoneChange(timezone) {
      this.set('timezone', timezone);
    }
  }
});
