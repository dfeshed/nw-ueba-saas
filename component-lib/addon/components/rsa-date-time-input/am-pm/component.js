import Component from '@ember/component';
import moment from 'moment';
import computed from 'ember-computed-decorators';
import layout from './template';

export default Component.extend({
  layout,
  classNames: ['am-pm'],
  classNameBindings: ['hasFocus'],
  hasFocus: false,

  @computed('timezone', 'timestamp')
  value(timezone, timestamp) {
    return moment.tz(timestamp, timezone).hour() >= 12 ? 'pm' : 'am';
  },

  onChange() {},

  selectValueText() {
    const value = this.get('value');
    this.element.querySelector('input').setSelectionRange(0, value.length);
  },

  keyUp(event) {
    const toggleKeyCodes = [32, 38, 40]; // spacebar, up arrow, down arrow keys
    const key = event.which || event.keyCode;
    if (this.get('hasFocus') && toggleKeyCodes.includes(key)) {
      this.toggle();
    }
  },

  toggle() {
    const value = this.get('value');
    const updatedValue = this.set('value', value === 'pm' ? 'am' : 'pm');
    this.get('onChange')(updatedValue);
  },

  actions: {
    handleClick() {
      this.toggle();
    },

    handleFocusIn() {
      this.set('hasFocus', true);
      this.selectValueText();
    },

    handleFocusOut() {
      this.set('hasFocus', false);
    }
  }
});
