import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import $ from 'jquery';

// Lookup of key names that correspond to Long integer timestamps.
const isTimestampKey = {
  timestamp: true,
  created_dateNetWitness: true,
  updated_dateNetWitness: true,
  expires_dateNetWitness: true
};

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
  @computed('key', 'value')
  isTimestamp: (key, value) => $.isNumeric(value) && !!isTimestampKey[key],

  // Converts `value` in a number of hours.
  @computed('value')
  valueAsHours(value) {
    if (!$.isNumeric(value)) {
      return null;
    }
    return Math.floor(((value / 1000) / 60) / 60);
  },

  // Parses `value` into a POJO with `days`, `hours`, `minutes` & `seconds` properties.
  @computed('value')
  valueAsPeriod(value) {
    if (!$.isNumeric(value)) {
      return null;
    }
    const totalSeconds = Math.round(value / 1000);
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
  }
});
