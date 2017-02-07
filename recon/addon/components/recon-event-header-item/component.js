import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';

const { Component } = Ember;
const DATE_DATATYPE = 2;

export default Component.extend({
  layout,
  tagName: '',

  @computed('type')
  isDate: (type) => type === DATE_DATATYPE,

  @computed('value')
  asInteger: (dateString) => parseInt(dateString, 10),

  // There is a request in to core-ui to add seconds and milliseconds to the
  // rsa-content-datetime component. Once that lands, this hack can be removed.
  @computed('type', 'asInteger')
  extendedDate: (type, dateInt) => {
    let ret = '';
    if (type === DATE_DATATYPE) {
      const date = new Date(dateInt);
      ret = `:${date.getSeconds()}.${date.getMilliseconds()}`;
    }
    return ret;
  }
});
