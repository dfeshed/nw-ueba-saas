import reselect from 'reselect';
import { bytesAsRows } from './util';
import { getHeaderItem } from 'recon/utils/recon-event-header';

const { createSelector } = reselect;
const packets = (recon) => recon.packets.packets;
const headerItems = (recon) => recon.data.headerItems;
const isRequestShown = (recon) => recon.visuals.isRequestShown;
const isResponseShown = (recon) => recon.visuals.isResponseShown;
const isPayloadOnly = (recon) => recon.packets.isPayloadOnly;


/**
 * A selector that returns a sorted Array of all visible packets.
 * @private
 */
export const visiblePackets = createSelector(
  [packets, isRequestShown, isResponseShown],
  (packets, isRequestShown, isResponseShown) => {

    // packets can be null, eject
    if (!packets) {
      return [];
    }

    // if showing all packets, just return them
    if (isRequestShown && isResponseShown) {
      return packets;
    }

    // we're not showing req or res, so let's filter them out
    return packets.filter((p) => {
      return (p.side === 'request' && isRequestShown) ||
             (p.side === 'response' && isResponseShown);
    });
  }
);

const isSameSequence = (() => {
  let _previousSequence;
  return (sequence) => {
    if (sequence === _previousSequence) {
      return true;
    } else {
      _previousSequence = sequence;
      return false;
    }
  };
})();

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
export const payloadProcessedPackets = createSelector(
  [visiblePackets, isPayloadOnly],
  (packets, isPayloadOnly) => {
    // reset sequence tracking
    isSameSequence(null);
    return packets.reduce((acc, currentPacket) => {
      const { bytes, payloadSize } = currentPacket;
      if (isPayloadOnly) {
        // if there are no bytes, eject
        if (payloadSize === 0) {
          return acc;
        }
        // Filter out header/footer items from the current packet
        const _bytes = bytes.filter((b) => !b.isHeader && !b.isFooter);
        // Get the previous packet
        const previousPacket = acc[acc.length - 1];
        // if the current packet has the same sequence number as the previous,
        // then the bytes need to be concated together
        if (previousPacket && isSameSequence(currentPacket.sequence)) {
          previousPacket.bytes = previousPacket.bytes.concat(_bytes);
          // Update the byteRows with the new bytes that were added
          previousPacket.byteRows = bytesAsRows(previousPacket.bytes);
        } else {
          // Set initial sequence number
          isSameSequence(currentPacket.sequence);
          // Override isContinuation to mean that the current packet is the
          // same side as the previous
          acc.push({
            ...currentPacket,
            isContinuation: (previousPacket && currentPacket.side === previousPacket.side),
            bytes: _bytes,
            byteRows: bytesAsRows(_bytes)
          });
        }
      } else {
        // This code path performs just like a map().
        acc.push({
          ...currentPacket,
          isContinuation: isSameSequence(currentPacket.sequence),
          byteRows: bytesAsRows(bytes)
        });
      }
      return acc;
    }, []);
  }
);

export const hasPayload = createSelector(
  headerItems,
  (headerItems) => {
    const headerItem = getHeaderItem(headerItems, 'payload size');

    if (headerItem && headerItem.value !== '0 bytes') {
      return true;
    }

    return false;
  }
);
