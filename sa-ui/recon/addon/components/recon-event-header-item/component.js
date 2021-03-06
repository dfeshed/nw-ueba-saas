import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import {
  isNetworkAddress,
  getIpAddressMetaValue,
  getPortMetaValue
} from 'recon/utils/network-addr-utils';
import { isEmpty } from '@ember/utils';
import layout from './template';

const DATE_DATATYPE = 'TimeT';
const NUMBER_DATATYPE = 'UInt64';

export default Component.extend({
  layout,
  tagName: '',
  i18n: service(),

  @computed('name', 'i18n.locale')
  tooltipText(name) {
    return this.get('i18n').t(`recon.eventHeader.${name}Tooltip`);
  },

  @computed('type')
  isDate: (type) => type === DATE_DATATYPE,

  @computed('name', 'type')
  isByteSize: (name, type) => {
    return type === NUMBER_DATATYPE && (name === 'payloadSize' || name === 'packetSize');
  },

  @computed('key')
  hasKey: (key) => !isEmpty(key),

  // Returns an array of objects, each object containing metaName and metaValue. The size of the array would usually be
  // one, except for network addresses, for which the first element would be the IP address, followed by port.
  // MetaFormat is used to evaluate format of meta for context menu item. Some context menu items are supported for
  // specific meta format only.
  @computed('key', 'value', 'name', 'metaFormatMap')
  metaValuePairs(key, value, name, metaFormatMap) {
    let metaValuePairs = null;
    if (isNetworkAddress(name)) {
      const ipAddressMetaValue = getIpAddressMetaValue(key, value);
      ipAddressMetaValue.moduleName = 'EventAnalysisPanel';
      metaValuePairs = [ipAddressMetaValue];
      const portMetaValuePair = getPortMetaValue(key, value);
      if (portMetaValuePair) {
        portMetaValuePair.moduleName = 'EventAnalysisPanel';
        metaValuePairs.push(portMetaValuePair);
      }
    } else {
      metaValuePairs = [{ moduleName: 'EventAnalysisPanel', metaName: key, metaValue: value, displayValue: value, format: metaFormatMap[key] }];
    }
    return metaValuePairs;
  },

  @computed('queryInputs', 'language')
  contextMenuData: (queryInputs, language) => ({ ...queryInputs, language }),

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
