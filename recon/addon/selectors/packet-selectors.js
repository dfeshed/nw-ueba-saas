import reselect from 'reselect';

const { createSelector } = reselect;

export const BYTES_PER_ROW = 16;

const packets = (recon) => recon.data.packets;
const packetFields = (recon) => recon.data.packetFields;
const isRequestShown = (recon) => recon.visuals.isRequestShown;
const isResponseShown = (recon) => recon.visuals.isResponseShown;

const visiblePackets = createSelector(
  packets,
  isRequestShown,
  isResponseShown,
  (packets, isRequestShown, isResponseShown) => {

    // if showing them all, do not iterate, just return them all
    if (isRequestShown && isResponseShown) {
      return packets;
    }

    return packets.map((packet) => {
      const showPacket =
        (packet.side === 'request' && isRequestShown) ||
        (packet.side === 'response' && isResponseShown);
      return showPacket ? packet : null;
    });
  }
);

export const enhancedPackets = createSelector(
  visiblePackets,
  packetFields,
  (packets, packetFields) => {
    const newPackets = (packets || []).map((p) => {
      if (p === null) {
        return p;
      }

      const byteCount = p.bytes.get('length') || 0;
      const payloadOffset = byteCount - (p.payloadSize || 0);
      const bytes = calculateBytes(p.bytes, payloadOffset, packetFields, p.footerPosition);
      const byteRows = calculateByteRows(bytes);
      return {
        ...p,
        byteCount,
        byteRows
      };
    });

    return newPackets;
  }
);

/**
 * A nesting of `bytes` into a 2-d array, where each array contains `bytesPerRow` number of bytes.
 * @type {[]}
 * @public
 */
const calculateByteRows = (bytes) => {
  const rows = [];
  const len = bytes.length;
  let i;
  for (i = 0; i < len; i = i + BYTES_PER_ROW) {
    rows.push(bytes.slice(i, i + BYTES_PER_ROW));
  }
  return rows;
};

/**
 * An array of byte data derived from the base64-encoded `packet.raw`. Each array item is an object with properties:
 * `int`: the byte value as an integer;
 * `hex`: the byte value as a hexadecimal;
 * `ascii`: the byte value as an ascii character;
 * `isHeader`: if true, indicates that this byte is in the packet's header section;
 * `isFooter`: if true, indicates that this byte is in the packet's footer section;
 * `packetField`: if this byte corresponds to a packet field, an object with info about the field & its value;
 * `packetField.name`: the name of the `packetFields` to which this byte corresponds to;
 * `packetField.value`: the value of the `packetFields` to which this byte corresponds to;
 * `packetField.roles`: indicates whether this byte is the start, middle or end of a packet field value;
 * a space-delimited string that may contain one or more of the following substrings: `packet-field`, `start` & `end`;
 * `isSelected`: if true, indicates that this byte falls within the user's selections.
 * @type {[]}
 * @public
 */
const calculateBytes = (decodedBytes, payloadOffset, packetFields, footerPosition) => {
  //  convert it into integers, hex & ascii.
  const bytes = decodedBytes.map((char, index) => {
    const int = char.charCodeAt(0);
    return {
      index,
      int,
      hex: (`0${int.toString(16)}`).slice(-2),
      ascii: (int > 31) ? char : '.',
      isHeader: index < payloadOffset,
      isFooter: index >= footerPosition
    };
  });

  bytes.forEach((byte, index) => {

    // Skip bytes that are not in the packet header.
    if (!byte.isHeader) {
      return;
    }

    // Which packet field does this byte's position fall into?
    const found = findPacketFieldForByte(packetFields, index);
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
    byte.packetField = {
      roles: roles.join(' '),
      field: found.field,
      index: found.index,
      values: decodedBytes.slice(found.field.position, found.field.position + found.field.length)
    };
  });

  return bytes;
};

/**
 * Searches `packetFields` for a field whose position & size include the given byte index.
 * If found, returns the field and its index; otherwise returns `undefined`.
 * @returns {{ field: object, index: number }}
 * @public
 */
const findPacketFieldForByte = (packetFields, byteIndex) => {
  const field = packetFields.find((field) => {
    return (byteIndex >= field.position) && (byteIndex < (field.position + field.length));
  });

  return !field ? undefined : {
    field,
    index: packetFields.indexOf(field)
  };
};
