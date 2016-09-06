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
  'eth.type': 'mac',
  'ip.src': 'ip',
  'ip.dst': 'ip',
  'ip.proto': 'int',
  'tcp.srcport': 'int',
  'tcp.dstport': 'int'
};

function valsToHexArray(arr) {
  return arr.map((part) => {
    return intToHex(part.charCodeAt(0));
  });
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
