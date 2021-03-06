import { lookup } from 'ember-dependency-lookup';

/**
 * How many bytes to display per row.
 * @public
 */
export const BYTES_PER_ROW = 16;

const _enhanceEachPacket = (packets, previousPackets, cacheService) => {
  // console.log('processPacketPayloads(): _enhancePackets, starting with', previousPackets.length, 'packets, processing', packets.length, 'new packets');// eslint-disable-line
  const lastCachedPacket = cacheService.retrieveLast();
  if (lastCachedPacket) {
    // For a packet's `isContinuation` to be calculated properly, we
    // need to know what the last processed packet was. Since we're
    // pre-loading from cache, we may be side-stepping the call to
    // isContinuation() which tracks the current side/sequence.
    // In that case, call isContinuation() with the last known
    // packet to set a baseline for continuity tracking.
    isContinuation(lastCachedPacket.side, lastCachedPacket.sequence);
  }
  const len = packets.length;
  const ppLen = previousPackets.length;
  for (let i = 0; i < len; i++) {
    let cur = packets[i];
    cur = cur.asMutable({ deep: true });
    previousPackets[i + ppLen] = cacheService.add({
      ...cur,
      isContinuation: isContinuation(cur.side, cur.sequence),
      byteRows: bytesAsRows(cur.bytes)
    });
  }
  return previousPackets;
};

const _enhancePayloadOnlyPackets = (packets, previousPackets, cacheService) => {
  // console.log('processPacketPayloads(): _enhancePayloadOnlyPackets, starting with', previousPackets.length, 'packets, processing', packets.length, 'new packets');// eslint-disable-line
  return packets.reduce((acc, cur, idx, src) => {
    cur = cur.asMutable({ deep: true });
    const { bytes } = cur;
    const isLastIteration = src.length - 1 === idx;
    // Only process if there are bytes
    if (cur.payloadSize > 0 && bytes?.length > 0) {
      // Filter out header/footer items from the current packet
      const _bytes = bytes.filter((b) => !b.isHeader && !b.isFooter);
      // Get the previous packet
      const previousPacket = acc.lastItem;
      // If the current packet is a continuation of the previous,
      // then the bytes need to be concated together
      if (isContinuation(cur.side, cur.sequence) && previousPacket) {
        previousPacket.bytes = previousPacket.bytes.concat(_bytes);
        // We've merged this packet into the previous one,
        // so we can ignore it from now on
        cacheService.add({ id: cur.id, ignore: true });
        if (isLastIteration) {
          previousPacket.byteRows = bytesAsRows(previousPacket.bytes);
        }
      } else {
        // This is not a continuation of the previous packet, so we need to
        // do two things:
        // 1) Run bytesAsRows on the previous packet if it exists.
        if (previousPacket) {
          previousPacket.byteRows = bytesAsRows(previousPacket.bytes);
        }
        // 2) Save off the current packet. We'll override isContinuation to
        // mean that the current packet is the same side as the previous
        const newPacket = cacheService.add({
          ...cur,
          isContinuation: (!!previousPacket && previousPacket.side === cur.side),
          bytes: _bytes
        });
        if (isLastIteration) {
          newPacket.byteRows = bytesAsRows(newPacket.bytes);
        }
        acc.push(newPacket);
      }
    } else {
      // No bytes, so cache it and ignore it
      cacheService.add({ id: cur.id, ignore: true });
      // The last packet could be a no-bytes packet, so let's check to see
      // if we need to run bytesAsRows() on the previous packet.
      if (isLastIteration && acc.length > 0) {
        const previousPacket = acc.lastItem;
        previousPacket.byteRows = bytesAsRows(previousPacket.bytes);
      }
    }
    return acc;
  }, previousPackets);
};

/**
 * Processes visible packets. There are several outcomes depending upon what is
 * desired to be shown to the user.
 *
 * If we're showing all packet bytes, we group packets of the same sequence
 * number, then group the individual bytes into rows to be displayed on the UI.
 *
 * If we're showing only payload bytes, we disreguard any packets that have no
 * payload. The packets that are left over that have the same sequence number
 * are merged together. Also, continuous same-side packets are marked as
 * continuous packets so that they appear visually under the same header on the
 * UI.
 * @param {Array} packets All of a sessions packets
 * @param {Boolean} isPayloadOnly Should we remove header/footer bytes
 * @public
 */
export const processPacketPayloads = function(packets, isPayloadOnly, packetFields) {
  if (packetFields !== null && packets !== null) {
    // console.log('processPacketPayloads(): processing', packets.length, 'packets');// eslint-disable-line
    // performance.mark('processPacketPayloads');
    const cacheService = lookup('service:processed-packet-cache');
    let processedPackets;
    let previousPackets = [];

    // Reset continuation tracking
    isContinuation(null, null);

    // What setup should we do if we're processing a list of packets we've
    // already partially processed
    if (cacheService.count > 0) {
      // There's stuff in the cache, so we'll be pre-loading the reduce with
      // that data. Some of the cached data are packets we've already merged
      // which are marked as "ignore". Get the non-ignored packets from cache.
      previousPackets = cacheService.retrieveAll();
      // For the new packets coming in, chop off the ones we've already
      // processed so we're only iterating over the new stuff
      packets = packets.slice(cacheService.count);
      // We need to set a baseline for isContinuation to work,
      // so grab the last item from the cache and prime isContinuation.
      const lastPacket = previousPackets.lastItem;
      isContinuation(lastPacket.side, lastPacket.sequence);
    }

    if (isPayloadOnly) {
      // Reduce packets, removing empty packets and header/footer of packets
      processedPackets = _enhancePayloadOnlyPackets(packets, previousPackets, cacheService);
    } else {
      processedPackets = _enhanceEachPacket(packets, previousPackets, cacheService);
    }

    // performance.measure('Total time to run processPacketPayloads()', 'processPacketPayloads');
    // const lastMeasure = performance.getEntriesByType('measure').lastItem;
    // console.log(`processPacketPayloads() took ${parseInt(lastMeasure.duration, 10)}ms to process ${packets.length} packets\n\n`);// eslint-disable-line
    return processedPackets;
  } else {
    return undefined;
  }
};

/**
 * A function that enhances all the packets so they are
 * prepared to be displayed.
 * @public
 */
export const enhancePackets = (packets, lastPosition, packetFields, packetsRowIndex) => {
  const newPackets = packets.map((p, i) => {
    const bytes = atob(p.bytes || '').split('');
    const byteCount = bytes.get('length') || 0;
    const convertedBytes = convertBytes(bytes, p.payloadPosition, p.footerPosition);
    const enhancedBytes = enhanceHeaderBytes(convertedBytes, bytes, packetFields);
    return {
      ...p,
      byteCount,
      bytes: enhancedBytes,
      pageRowIndex: (packetsRowIndex + 1) + lastPosition + i
    };
  });
  return newPackets;
};

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
 * Convert an Array of raw byte into Objects with integer, hex, & ascii
 * representations of the byte. The returned Objects have the properties:
 * - `index`: The index if the byte
 * - `int`: The byte value as an integer
 * - `hex`: The byte value as a hexadecimal
 * - `ascii`: The byte value as an ascii character
 * - `isHeader`: If true, indicates that this byte is in the packet's header section
 * - `isFooter`: If true, indicates that this byte is in the packet's footer section
 * - `isKnown`: Is this byte part of a known file signature
 * This also markes a byte if it has a known signature. It will mark a byte with
 * the first known signature.
 * @param {String} bytes A string of bytes
 * @param {Number} payloadOffset The index of the beginning of the payload
 * @param {Number} footerOffset The index of the beginning of the footer
 * @return {Array}
 * @private
 */
const convertBytes = (bytes, payloadOffset, footerOffset) => {
  const signatures = findKnownSignatures(bytes.join(''));
  return bytes.map((char, index) => {
    const int = char.charCodeAt(0);
    let isKnown;
    if (signatures.length > 0) {
      // see if this byte falls within the range for each known signature found
      isKnown = signatures.find((sig) => {
        const start = sig.index;
        const end = sig.index + sig.length;
        return index >= start && index < end;
      });
    }
    let ascii = char;
    if (int <= 31) {
      // Non-printable characters will be represented as a period
      ascii = '.';
    } else if (int === 32 || int === 173) {
      // "Space" characters are converted to "&nbsp;"
      ascii = String.fromCharCode(160);
    }
    return {
      index,
      int,
      hex: (`0${int.toString(16)}`).slice(-2),
      ascii,
      isHeader: index < payloadOffset,
      isFooter: index >= footerOffset,
      isKnown
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
 * - `field`: The packet's field
 * - `index`: The field's index within `packetFields`
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

/**
 * List of known file signatures that we're going to look for. Each signature
 * has the properties:
 * - `hex`: The signature of the file as hex. This may not be the full signature
 * - `length`: The full length of the signature
 * - `type`: The common name for the type of file
 * Order is important when you have signatures that have similar hex.
 * @return {Array}
 * @private
 */
const FILE_SIGNATURES = [
  { hex: '4D 5A', length: 2, type: 'DOS Executable / Windows PE' },
  { hex: '5A 4D', length: 2, type: 'Non Portable Executable' },
  { hex: '89 50 4E 47 0D 0A 1A 0A', length: 8, type: 'PNG' },
  { hex: 'FF D8 FF', length: 4, type: 'JPEG' },
  { hex: '4A 46 49 46', length: 4, type: 'JPEG/JFIF' },
  { hex: '45 78 69 66', length: 4, type: 'JPEG/EXIF' },
  { hex: '47 49 46 38 37 61', length: 6, type: 'GIF87a' },
  { hex: '47 49 46 38 39 61', length: 6, type: 'GIF89a' },
  { hex: '42 4D', length: 2, type: 'BMP' },
  { hex: '25 50 44 46', length: 4, type: 'PDF' },
  { hex: 'D0 CF 11 E0', length: 8, type: 'MS Office Document' },
  { hex: '50 4B 03 04 14 00 01 00', length: 8, type: 'ZLock Pro encrypted ZIP' },
  { hex: '50 4B 4C 49 54 45', length: 6, type: 'PKLITE archive' },
  { hex: '50 4B 53 70 58', length: 5, type: 'PKSFX archive' },
  { hex: '50 4B', length: 4, type: 'PKZIP archive' },
  { hex: '57 69 6E 5A 69 70', length: 6, type: 'WinZip compressed archive' },
  { hex: '37 7A BC AF 27 1C', length: 6, type: '7z' },
  { hex: 'CA FE BA BE', length: 4, type: 'Java Class' },
  { hex: '25 21 50 53', length: 4, type: 'Postscript' },
  { hex: '23 21', length: 2, type: 'Unix/Linux Shell Script' },
  { hex: '7F 45 4C 46', length: 4, type: 'Executable and Linkable Format' }
];

/**
 * Known file signatures augmented with a searchable `signature` string. We
 * create this list from FILE_SIGNATURES because some of the signatures
 * contain unprintable characters that would difficult or confusing to type
 * out. The `signature` string is meant to match what we get from the server
 * after performing `atob(p.bytes)` in `enhancePackets()`.
 * @return {Array}
 * @private
 */
const KNOWN_SIGNATURES = FILE_SIGNATURES.map((file) => {
  const hexValues = file.hex.split(' ');
  const intValues = hexValues.map((hex) => parseInt(hex, 16));
  const signature = String.fromCharCode(...intValues);
  return {
    ...file,
    signature
  };
});

/**
 * Search a byte string looking for known file signatures. If we find any,
 * augment the signature with the index at which it was found.
 * @param {String} bytes Bytes in String format
 * @return {Array} List of know file signatures
 * @private
 */
const findKnownSignatures = (bytes) => {
  return KNOWN_SIGNATURES.reduce((acc, value) => {
    const index = bytes.indexOf(value.signature);
    if (index >= 0) {
      acc.push({
        ...value,
        index
      });
      return acc;
    } else {
      return acc;
    }
  }, []);
};

const isContinuation = (() => {
  let _previousSide, _previousSequence;
  return (side, sequence) => {
    if (side === _previousSide && sequence === _previousSequence) {
      return true;
    } else {
      _previousSide = side;
      _previousSequence = sequence;
      return false;
    }
  };
})();