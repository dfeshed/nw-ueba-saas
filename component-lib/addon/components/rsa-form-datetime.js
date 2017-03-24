import Component from 'ember-component';
import { or } from 'ember-computed';
import layout from '../templates/components/rsa-form-datetime';

export default Component.extend({
  layout,

  tagName: 'label',

  classNames: ['rsa-form-input'],

  classNameBindings: [
    'isReadOnly',
    'isDisabled',
    'isError',
    'isSuccess',
    'isInline'],

  dateFormat: null,
  errorMessage: null,
  firstDay: 1,
  incrementHourBy: null,
  incrementMinuteBy: null,
  incrementSecondBy: null,
  isDisabled: false,
  isError: false,
  isInline: false,
  isReadOnly: false,
  isSuccess: false,
  label: null,
  maxDate: null,
  minDate: null,
  onDateClose() {},
  onDateDraw() {},
  onDateOpen() {},
  onDateSelection() {},
  options: null,
  placeholder: null,
  showSeconds: null,
  showTime: null,
  theme: null,
  timeLabel: null,
  type: 'text',
  use24hour: null,
  useUTC: null,
  value: null,
  yearRange: null,

  resolvedDisabled: or('isDisabled', 'isReadOnly')
});
