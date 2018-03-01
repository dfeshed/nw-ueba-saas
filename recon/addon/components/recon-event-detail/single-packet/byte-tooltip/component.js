import Component from '@ember/component';
import { observer } from '@ember/object';
import { schedule } from '@ember/runloop';
import { inject as service } from '@ember/service';
import $ from 'jquery';
import { connect } from 'ember-redux';
import computed, { alias, notEmpty, equal } from 'ember-computed-decorators';

import intToHex from 'recon/utils/int-to-hex';
import hexToInt from 'recon/utils/hex-to-int';
import layout from './template';

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

const HEADER_ATTRIBUTES = [
  'ip.ttl',
  'ip.checksum',
  'ip.frag_offset',
  'ipv6.hlim'
];

function valsToHexArray(arr) {
  return arr.map((part) => {
    return intToHex(part.charCodeAt(0));
  });
}
/**
 * The bytes array is passed in, converted to a hex array, then split into sections of 4 characters.
 * fourCharHexArray is then reduced to format to the IPV6 standard, which dictates that:
 * - sections of '0000' can be condensed to '0'
 * - multiple zero sections in a row can be condensed to '::', but only one time
 * For more info on IPV6 see https://www.tutorialspoint.com/ipv6/ipv6_address_types.htm
 * @param values The array of single bytes
 * @returns {string} the IPV6 formatted address
 * @private
 */
function valsToIPV6(values) {
  const fourCharHexArray = valsToHexArray(values).join('').match(/.{1,4}/g);
  return fourCharHexArray.reduce((ip, value, index, arr) => {
    // Remove leading zeros
    const parsedValue = value.replace(/\b(0(?!\b))+/g, '');

    // If all zeros, we should check and see if the next one is also all zeros
    const nextIsZeros = index < (arr.length - 1) && arr[index + 1].replace(/\b(0(?!\b))+/g, '') === '0';

    if (parsedValue === '0') {
      // If IP already ends with '::' we don't want to print the zero
      if (ip.endsWith('::')) {
        return ip;
      }
      // If IP does not have '::', and we have multiple zero sections, condense to '::'
      if (!ip.includes('::') && nextIsZeros) {
        return `${ip}:`;
      }
    }

    return `${ip}${index !== 0 && !ip.endsWith('::') ? ':' : ''}${parsedValue}`;
  }, '');
}
/**
 * Takes an array of hex values and converts them to int
 * @param arr The array of hex values
 * @private
 */
function hexArrayToInt(arr) {
  return hexToInt(arr.join(''));
}

/**
 * Converts array of bytes to ints
 * @param arr The array of bytes
 * @private
 */
function valsToInt(arr) {
  return hexArrayToInt(valsToHexArray(arr));
}

const stateToComputed = ({ recon: { packets } }) => ({
  tooltipData: packets.packetTooltipData
});

const ByteTooltipComponent = Component.extend({
  layout,
  tagName: 'section',
  classNames: 'rsa-byte-table-tooltip',
  classNameBindings: ['visible', 'isSignature'],
  tooltipData: null,
  i18n: service(),

  @alias('tooltipData.field.name') label: null,
  @alias('tooltipData.field.type') type: null,
  @alias('tooltipData.values') values: null,
  @alias('tooltipData.position') position: null,

  @equal('headerType', 'attribute') isAttribute: null,
  @equal('headerType', 'meta') isMeta: null,
  @equal('headerType', 'signature') isSignature: null,

  @notEmpty('tooltipData') visible: null,

  @computed('isMeta', 'isAttribute')
  tooltipTitle(isMeta, isAttribute) {
    let lbl;
    if (isMeta) {
      lbl = 'recon.packetView.headerMeta';
    } else if (isAttribute) {
      lbl = 'recon.packetView.headerAttribute';
    } else {
      lbl = 'recon.packetView.headerSignature';
    }
    return this.get('i18n').t(lbl);
  },

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

      case 'sig':
        return values;

      default:
        return valsToInt(values);
    }
  },

  @computed('label')
  headerType(label) {
    let lbl;
    if (HEADER_ATTRIBUTES.includes(label)) {
      lbl = 'attribute';
    } else if (label === 'signature') {
      lbl = 'signature';
    } else {
      lbl = 'meta';
    }
    return lbl;
  },

  positionDidChange: observer('position', function() {
    if (this.element) {
      schedule('afterRender', this, '_move');
    }
  }),

  // @todo Use ember-wormhole!
  didInsertElement() {
    schedule('afterRender', this, '_tunnel');
  },

  // clean up DOM
  willDestroyElement() {
    $(this.element).remove();
  },

  _tunnel() {
    document.body.appendChild(this.element);
  },

  _move() {
    const position = this.get('position');
    if (position) {
      this.element.style.left = `${position.x - ($(this.element).width() / 2) - 7}px`;
      this.element.style.top = `${position.y - 63}px`;
    }
  }
});

export default connect(stateToComputed)(ByteTooltipComponent);
