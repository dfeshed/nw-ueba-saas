import Controller from '@ember/controller';
import { scheduleOnce } from '@ember/runloop';

export default Controller.extend({

  eventsLog: '',
  inputValue: '',

  logEvent(handler, event) {
    const log = `${handler} :: ${event.type} :: '${event.target.value}'`;
    this.set('eventsLog', `${this.get('eventsLog') + log}\n`);
    scheduleOnce('afterRender', this, 'scrollEventsLog');
  },

  scrollEventsLog() {
    const eventsLogEl = document.querySelector('.events-log');
    eventsLogEl.scrollTop = eventsLogEl.scrollHeight;
  },

  actions: {
    handleOnEnter(event) {
      this.set('inputValue', event.target.value);
      this.logEvent('handleOnEnter', event);
    },
    handleOnFocusOut(event) {
      this.set('inputValue', event.target.value);
      this.logEvent('handleOnFocusOut', event);
    },
    handleOnKeyDown(event) {
      this.set('inputValue', event.target.value);
      this.logEvent('handleOnKeyDown', event);
    },
    handleOnKeyUp(event) {
      this.set('inputValue', event.target.value);
      this.logEvent('handleOnKeyUp', event);
    }
  }
});
