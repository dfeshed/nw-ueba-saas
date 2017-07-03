import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import service from 'ember-service/inject';

import layout from './template';

const DATE_DATATYPE = 'TimeT';
const NUMBER_DATATYPE = 'UInt64';

export default Component.extend({
  layout,
  tagName: '',
  i18n: service(),

  @computed('name')
  tooltipText(name) {
    return this.get('i18n').t(`recon.eventHeader.${name}Tooltip`);
  },

  @computed('type')
  isDate: (type) => type === DATE_DATATYPE,

  @computed('name', 'type')
  isByteSize: (name, type) => {
    return type === NUMBER_DATATYPE && (name === 'payloadSize' || name === 'packetSize');
  },

  @computed('value')
  asInteger: (dateString) => parseInt(dateString, 10),

  // There is a request in to core-ui to add seconds and milliseconds to the
  // rsa-content-datetime component. Once that lands, this hack can be removed.
  @computed('type', 'asInteger')
  extendedDate: (type, dateInt) => {
    let ret = '';
    if (type === DATE_DATATYPE) {
      const date = new Date(dateInt);
      // Create a zero padded millisecond string to match what Moment gives us
      const ms = `00${date.getMilliseconds()}`.slice(-3);
      ret = `:${date.getSeconds()}.${ms}`;
    }
    return ret;
  }
});
