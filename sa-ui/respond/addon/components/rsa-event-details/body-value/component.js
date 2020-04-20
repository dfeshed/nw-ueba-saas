import { computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';
import moment from 'moment';
import { isNumeric } from 'component-lib/utils/jquery-replacement';
// Lookup of key names that correspond to Long integer timestamps.
const isTimestampKey = {
  timestamp: true,
  created_dateNetWitness: true,
  updated_dateNetWitness: true,
  expires_dateNetWitness: true
};

const validDate = (value) => moment(value, moment.ISO_8601, true).isValid();

/**
 * @class Event Details Body Value component
 * Renders a given scalar value for a given property name, presumably from a normalized event object.
 *
 * @example
 * ```hbs
 * <span class="scalar-value" data-meta-key="{{property.key}}" data-entity-id="{{property.value}}">{{property.value}}</span>
 * ```
 * @public
 */
export default Component.extend({
  tagName: 'span',
  layout,
  classNames: ['rsa-event-details-body-value', 'entity'],
  attributeBindings: ['key:data-meta-key', 'value:data-entity-id'],
  key: null,
  fullPath: null,
  value: null,

  // Indicates whether a given key-value pair is a Long integer that should be rendered as a formatted timestamp.
  isTimestamp: computed('key', 'value', function() {
    return (isNumeric(this.value) || validDate(this.value)) && !!isTimestampKey[this.key];
  }),

  // Converts `value` in a number of hours.
  valueAsHours: computed('value', function() {
    if (!isNumeric(this.value)) {
      return null;
    }
    return Math.floor(((this.value / 1000) / 60) / 60);
  }),

  // Parses `value` into a POJO with `days`, `hours`, `minutes` & `seconds` properties.
  valueAsPeriod: computed('value', function() {
    if (!isNumeric(this.value)) {
      return null;
    }
    const totalSeconds = Math.round(this.value / 1000);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor(totalSeconds / 60) % 60;
    const seconds = totalSeconds % 60;

    // Don't bother showing seconds if they are zero and we have hours and/or minutes to show.
    const hideSeconds = !seconds && (hours || minutes);

    return {
      hours:
      minutes,
      seconds: hideSeconds ? null : seconds
    };
  })
});
