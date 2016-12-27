import Ember from 'ember';
import computed from 'ember-computed-decorators';

import layout from './template';
import { not, readOnly } from 'ember-computed-decorators';
import { SpanielObserver } from 'spaniel';
const { Component, K, run, set } = Ember;

export default Component.extend({
  layout,
  packet: null,
  packetFields: null,
  packetIsExpanded: true,
  index: null,
  selection: null,
  tooltipData: null,
  viewportEntered: false,
  @readOnly @not('viewportEntered') viewportExited: null,

  /**
   * The number of bytes to display in a single row.
   * @type {number}
   * @public
   */
  bytesPerRow: 16,

  /**
   * The number of bytes to display closely packed together, without a blank space.
   * @type {number}
   * @public
   */
  byteGrouping: 1,

  /**
   * Configurable callback to be invoked whenever `selection` changes.
   * The callback will be passed `selection` as its single single argument.
   * @type function
   * @public
   */
  onselect: K,

  /**
   * Determine the direction, request or response, for the arrow
   * @param side Request or response
   * @returns {string} right or left
   * @public
   */
  @computed('packet.side')
  arrowDirection(side) {
    return side === 'request' ? 'right' : 'left';
  },

  /**
   * The number of bytes included in `packet.raw` after it has been decoded.
   * @type {number}
   * @public
   */
  @computed('decodedData')
  byteCount(decodedData) {
    return decodedData.get('length') || 0;
  },

  /**
   * Determine the expand/collapse arrow direction for a single packet
   * @param packetIsExpanded If expanded or not
   * @returns {string} down or right
   * @public
   */
  @computed('packetIsExpanded')
  collapseArrowDirection(packetIsExpanded) {
    return packetIsExpanded ? 'down' : 'right';
  },

  /**
   * An array of data values (as string characters) derived from decoding the base64-encoded `packet.raw`.
   * @type {string[]}
   * @public
   */
  @computed('packet.bytes')
  decodedData(bytes) {
    return atob(bytes || '').split('');
  },

  /**
   * The index of the first decoded values that correspond to the packet's payload (i.e., not the packet header).
   * If the packet has no payload, then this value will be the index after the  index of the last decoded value in the packet.
   * @type {number}
   * @public
   */
  @computed('byteCount', 'packet.payloadSize')
  payloadOffset(byteCount, payloadSize) {
    return byteCount - (payloadSize || 0);
  },

  /**
   * An array of byte data derived from the base64-encoded `packet.raw`. Each array item is an object with properties:
   * `int`: the byte value as an integer;
   * `hex`: the byte value as a hexadecimal;
   * `ascii`: the byte value as an ascii character;
   * `isHeader`: if true, indicates that this byte is in the packet's header section;
   * `packetField`: if this byte corresponds to a packet field, an object with info about the field & its value;
   * `packetField.name`: the name of the `packetFields` to which this byte corresponds to;
   * `packetField.value`: the value of the `packetFields` to which this byte corresponds to;
   * `packetField.roles`: indicates whether this byte is the start, middle or end of a packet field value;
   * a space-delimited string that may contain one or more of the following substrings: `packet-field`, `start` & `end`;
   * `isSelected`: if true, indicates that this byte falls within the user's selections.
   * @type {[]}
   * @public
   */
  @computed('decodedData', 'payloadOffset', 'packetFields')
  bytes(decodedData, payloadOffset, packetFields) {
    let bytes;

    // Has the `decodedData` remained the same since last time we were here?
    // This might happen, for example, if the `packetFields` was fetched async in parallel and arrived after the `decodedData`.
    if (this._lastDecodedDataForBytes === decodedData) {

      // No change in decoded data, so reuse the last result.
      bytes = [].concat(this._lastBytes);
      this._lastBytes = bytes;
    } else {

      // Decoded data has changed, so convert it into integers, hex & ascii.
      bytes = decodedData.map((char, index) => {
        const int = char.charCodeAt(0);
        return {
          index,
          int,
          hex: (`0${int.toString(16)}`).slice(-2),
          ascii: (int > 31) ? char : '.',
          isHeader: index < payloadOffset
        };
      });

      // Cache result for future comparison next time we are here.
      this._lastDecodedDataForBytes = decodedData;
      this._lastBytes = bytes;
    }

    // Do we have packetFields info for this session?
    // If so, add 'packetField' properties to the bytes that fall within the packet's header section.
    if (packetFields) {
      bytes.forEach((byte, index) => {

        // Skip bytes that are not in the packet header.
        if (!byte.isHeader) {
          return;
        }

        // Which packet field does this byte's position fall into?
        const found = this.findPacketFieldForByte(index);
        if (!found) {
          return;
        }

        // Found the field; cache field info in order to use it in template (highlighting, tooltips, etc).
        const roles = ['packet-field-value'];
        if (index === found.field.position) {
          roles.push('start');
        }
        if (index === (found.field.position + found.field.length - 1)) {
          roles.push('end');
        }
        set(byte, 'packetField', {
          roles: roles.join(' '),
          field: found.field,
          index: found.index,
          values: decodedData.slice(found.field.position, found.field.position + found.field.length)
        });
      });
    }

    return bytes;
  },

  /**
   * A nesting of `bytes` into a 2-d array, where each array contains `bytesPerRow` number of bytes.
   * @type {[]}
   * @public
   */
  @computed('bytes', 'bytesPerRow')
  byteRows(bytes, bytesPerRow) {
    const rows = [];
    const len = bytes.length;
    let i;
    for (i = 0; i < len; i = i + bytesPerRow) {
      rows.push(bytes.slice(i, i + bytesPerRow));
    }
    return rows;
  },

  @computed('packet.side', 'isRequestShown', 'isResponseShown')
  shouldShowPacket(side, isRequestShown, isResponseShown) {
    return (side === 'request' && isRequestShown) || (side === 'response' && isResponseShown);
  },

  /**
   * Observe the component's this.element intersecting with the root element
   * @private
   */
  didInsertElement() {
    const options = {
      rootMargin: '-1000px 0px -1000px 0px',
      threshold: [{
        ratio: 0.01,
        time: 0
      }]
    };

    const observer = new SpanielObserver(([entry]) => {
      run.join(() => {
        this.set('viewportEntered', entry.entering);
      });
    }, options);

    observer.observe(this.$('.rsa-packet')[0]);

    this.set('observer', observer);
  },

  /**
   * Searches `packetFields` for a field whose position & size include the given byte index.
   * If found, returns the field and its index; otherwise returns `undefined`.
   * @returns {{ field: object, index: number }}
   * @public
   */
  findPacketFieldForByte(byteIndex) {
    const field = (this.get('packetFields') || []).find((field) => {
      return (byteIndex >= field.position) && (byteIndex < (field.position + field.length));
    });

    return !field ? undefined : {
      field,
      index: this.get('packetFields').indexOf(field)
    };
  },
  willDestroyElement() {
    this.get('observer').disconnect();
  },
  actions: {
    expandPacket() {
      this.toggleProperty('packetIsExpanded');
    }
  }
});