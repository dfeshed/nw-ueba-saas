import Ember from 'ember';
import intToHex from 'recon/utils/int-to-hex';
import hexToInt from 'recon/utils/hex-to-int';
import layout from './template';
import computed from 'ember-computed-decorators';
const { Component, observer, run } = Ember;

// Default data types for known fields in packet headers.
const DEFAULT_TYPE_OF_FIELD = {
  'eth.dst': 'mac',
  'eth.src': 'mac',
  'eth.type': 'int',
  'ip.addr': 'ip',
  'ip.src': 'ip',
  'ip.dst': 'ip',
  'ip.dstport': 'int',
  'ip.proto': 'int',
  'ipv6.src': 'ipv6',
  'ipv6.dst': 'ipv6',
  'ipv6.proto': 'int',
  'tcp.srcport': 'int',
  'tcp.dstport': 'int'
};

function valsToHexArray(arr) {
  return arr.map((part) => {
    return intToHex(part.charCodeAt(0));
  });
}
function valsToIPV6(values) {
  return valsToHexArray(values).reduce((ip, value, index, arr) => {
    // Remove leading zeros
    const parsedValue = value.replace(/\b(0(?!\b))+/g, '');

    // If all zeros, we should check and see if the next one is also all zeros
    const nextIsZeros = index < (arr.length - 1) && arr[index + 1].replace(/\b(0(?!\b))+/g, '') === '0';

    if (parsedValue === '0') {
      // If IP already ends with '::' we don't want to print the zero
      if (ip.endsWith('::')) {
        return ip;
      }
      if (!ip.includes('::') && nextIsZeros) {
        return `${ip}:`;
      }
    }

    return `${ip}${index !== 0 && !ip.endsWith('::') ? ':' : ''}${parsedValue}`;
  }, '');
}
function hexArrayToInt(arr) {
  return hexToInt(arr.join(''));
}
function valsToInt(arr) {
  return hexArrayToInt(valsToHexArray(arr));
}

export default Component.extend({
  layout,
  tagName: 'section',
  classNames: 'rsa-byte-table-tooltip',
  classNameBindings: ['visible'],
  label: '',
  values: null,
  type: null,
  position: null,

  @computed('label', 'values', 'type')
  displayValue(label, values, type) {
    if (!values) {
      return '';
    }
    // Read the type from either `type` attr, or from list of defaults.
    switch (type || DEFAULT_TYPE_OF_FIELD[label]) {
      case 'ip':
        return [
          valsToInt([values[0]]),
          valsToInt([values[1]]),
          valsToInt([values[2]]),
          valsToInt([values[3]])
        ].join('.');

      case 'ipv6':
        return valsToIPV6(values);

      case 'mac':
        return valsToHexArray(values).join(':');

      case 'int':
        return valsToInt(values);

      default:
        return valsToInt(values);
    }
  },

  positionDidChange: observer('position', function() {
    if (this.element) {
      run.schedule('afterRender', this, '_move');
    }
  }),

  // @todo Use ember-wormhole!
  didInsertElement() {
    run.schedule('afterRender', this, '_tunnel');
  },

  _tunnel() {
    document.body.appendChild(this.element);
  },

  _move() {
    let { position, visible } = this.getProperties('position', 'visible');
    if (visible && position) {
      this.element.style.left = `${position.x - 125}px`;
      this.element.style.top = `${position.y - 65}px`;
    }
  }
});
