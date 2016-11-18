import Ember from 'ember';
import layout from '../templates/components/rsa-form-datetime';

const {
  Component,
  K,
  computed: {
    or
  }
} = Ember;

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

  value: null,

  label: null,

  placeholder: null,

  type: 'text',

  isInline: false,

  isReadOnly: false,

  isDisabled: false,

  isError: false,

  errorMessage: null,

  isSuccess: false,

  firstDay: 1,

  maxDate: null,

  minDate: null,

  useUTC: null,

  options: null,

  resolvedDisabled: or('isDisabled', 'isReadOnly'),

  yearRange: null,

  theme: null,

  dateFormat: null,

  onDateSelection: K,

  onDateOpen: K,

  onDateClose: K,

  onDateDraw: K,

  showTime: null,

  showSeconds: null,

  use24hour: null,

  incrementHourBy: null,

  incrementMinuteBy: null,

  incrementSecondBy: null,

  timeLabel: null
});
