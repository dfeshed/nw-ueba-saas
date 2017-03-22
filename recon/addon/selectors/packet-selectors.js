import reselect from 'reselect';

/**
 * How many bytes to display per row.
 * @public
 */
export const BYTES_PER_ROW = 16;

const { createSelector } = reselect;
const packets = (recon) => recon.data.packets;
const packetFields = (recon) => recon.data.packetFields;
const isRequestShown = (recon) => recon.visuals.isRequestShown;
const isResponseShown = (recon) => recon.visuals.isResponseShown;
const isPayloadOnly = (recon) => recon.visuals.isPayloadOnly;

/**
 * A Redux selector that returns a sorted Array of all visible packets.
 * @private
 */
const visiblePackets = createSelector(
  [packets, isRequestShown, isResponseShown],
  (packets, isRequestShown, isResponseShown) => {
    let ret = packets;
    if (!isRequestShown || !isResponseShown) {
      // we're not showing req or res, so let's filter them out
      ret = packets.filter((p) => {
        return (p.side === 'request' && isRequestShown) ||
               (p.side === 'response' && isResponseShown);
      });
    }
    return ret;
  }
);

/**
 * A Redux selector that returns a sorted Array of all visible packets that
 * either show or hide headers and footers depending upon `isPayloadOnly`.
 * @public
 */
export const enhancedPackets = createSelector(
  [visiblePackets, packetFields, isPayloadOnly],
  (packets, packetFields, payloadOnly) => {
    return packets.map((p) => {
      const byteCount = p.bytes.get('length') || 0;
      const payloadOffset = byteCount - (p.payloadSize || 0);
      const convertedBytes = convertBytes(p.bytes, payloadOffset, p.footerPosition);
      const bytes = payloadOnly ?
        convertedBytes.filter((b) => !b.isHeader && !b.isFooter) :
        enhanceHeaderBytes(convertedBytes, p.bytes, packetFields);
      const byteRows = bytesAsRows(bytes);
      return {
        ...p,
        byteCount,
        byteRows
      };
    });
  }
);

/**
 * A nesting of `bytes` into a 2-d array, where each array contains
 * `bytesPerRow` number of bytes.
 * @param {Array} bytes An Array of byte objects
 * @return {Array}
 * @private
 */
const bytesAsRows = (bytes) => {
  const rows = [];
  const len = bytes.length;
  for (let i = 0; i < len; i = i + BYTES_PER_ROW) {
    rows.push(bytes.slice(i, i + BYTES_PER_ROW));
  }
  return rows;
};

/**
 * Convert a raw byte String into an Object with integers, hex & ascii
 * representations of the byte. The return Object has the properties:
 * - `index`: The index if the byte
 * - `int`: The byte value as an integer
 * - `hex`: The byte value as a hexadecimal
 * - `ascii`: The byte value as an ascii character
 * - `isHeader`: If true, indicates that this byte is in the packet's header section
 * - `isFooter`: If true, indicates that this byte is in the packet's footer section
 * @param {String} bytes A string of bytes
 * @param {Number} payloadOffset The index of the beginning of the payload
 * @param {Number} footerOffset The index of the beginning of the footer
 * @return {Array}
 * @private
 */
const convertBytes = (bytes, payloadOffset, footerOffset) => {
  return bytes.map((char, index) => {
    const int = char.charCodeAt(0);
    return {
      index,
      int,
      hex: (`0${int.toString(16)}`).slice(-2),
      ascii: (int > 31) ? char : '.',
      isHeader: index < payloadOffset,
      isFooter: index >= footerOffset
    };
  });
};

/**
 * Augments header objects with a `packetField` property with the following
 * properties:
 * - `field`: The field from `packetFields` to which this byte corresponds
 * - `index`: The position of the `field` within `packetFields`
 * - `roles`: Indicates whether this byte is the start, middle or end of a packet field
 * - `values`: A space-delimited string that may contain one or more of the following substrings: `packet-field`, `start` & `end`
 * @param {Array} bytes The array of byte Objects
 * @param {String} decodedBytes The bytes in their original decoded format
 * @param {Array} packetFields List of packetFields from the summary call
 * @return {Array}
 * @private
 */
const enhanceHeaderBytes = (bytes, decodedBytes, packetFields) => {
  return bytes.map((byte, index) => {
    if (byte.isHeader) {
      // Which packet field does this byte's position fall into?
      const found = findPacketFieldForByte(packetFields, index);
      if (found) {
        // Found the field; cache field info in order to use it in template (highlighting, tooltips, etc).
        const roles = ['packet-field-value'];
        if (index === found.field.position) {
          roles.push('start');
        }
        if (index === (found.field.position + found.field.length - 1)) {
          roles.push('end');
        }
        byte.packetField = {
          roles: roles.join(' '),
          field: found.field,
          index: found.index,
          values: decodedBytes.slice(found.field.position, found.field.position + found.field.length)
        };
      }
    }
    return byte;
  });
};

/**
 * Searches `packetFields` for a field whose position and size include the given
 * byte index. If found, returns an Object with the following properties:
 * - `field` The packet's field
 * - `index` The field's index within `packetFields`
 * @param {Array} packetFields From the summary call
 * @param {Number} byteIndex Index of the byte we're looking for
 * @returns {Object}
 * @private
 */
const findPacketFieldForByte = (packetFields, byteIndex) => {
  const field = packetFields.find((field) => {
    return (byteIndex >= field.position) &&
           (byteIndex < (field.position + field.length));
  });
  return field ? { field, index: packetFields.indexOf(field) } : null;
};
