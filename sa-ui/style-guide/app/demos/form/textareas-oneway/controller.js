import Controller from '@ember/controller';
import { scheduleOnce } from '@ember/runloop';
import { equal } from '@ember/object/computed';

export default Controller.extend({

  eventsLog: '',
  textareaValue: '',
  textareaIsError: equal('textareaValue', ''),
  textareaErrorMessage: 'Some awesome error message here aye!',

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
      this.set('textareaValue', event.target.value);
      this.logEvent('handleOnEnter', event);
    },
    handleOnFocusOut(event) {
      this.set('textareaValue', event.target.value);
      this.logEvent('handleOnFocusOut', event);
    },
    handleOnKeyDown(event) {
      this.set('textareaValue', event.target.value);
      this.logEvent('handleOnKeyDown', event);
    },
    handleOnKeyUp(event) {
      this.set('textareaValue', event.target.value);
      this.logEvent('handleOnKeyUp', event);
    }
  }
});
