import reselect from 'reselect';
import { bytesAsRows } from './util';
import { getHeaderItem } from 'recon/utils/recon-event-header';

import { isRequestShown, isResponseShown } from 'recon/reducers/visuals/selectors';

const { createSelector } = reselect;
const packets = (recon) => recon.packets.packets;
const renderIds = (recon) => recon.packets.renderIds;
const headerItems = (recon) => recon.header.headerItems;
const isPayloadOnly = (recon) => recon.packets.isPayloadOnly;

/**
 * A selector that returns a sorted Array of all visible packets.
 * @private
 */
const visiblePackets = createSelector(
  [packets, renderIds, isRequestShown, isResponseShown],
  (packets, renderIds, isRequestShown, isResponseShown) => {

    // packets can be null, if so, get out
    // can have no renderIds, if so, get out
    if (!packets || packets.length === 0 || !renderIds || renderIds.length === 0) {
      return [];
    }

    // just want the packets with their id chosen to be rendered
    const renderedPackets = packets.filter((p) => renderIds.includes(p.id));

    // if showing all packets, just return them
    if (isRequestShown && isResponseShown) {
      return renderedPackets;
    }

    // we're not showing req or res, so let's filter them out
    return renderedPackets.filter((p) => {
      return (p.side === 'request' && isRequestShown) ||
        (p.side === 'response' && isResponseShown);
    });
  }
);

// packets can at different times be null, an empty array
// or a populated array. An empty array means the event has
// no text content. If textContent is null, then it is still
// being fetched.
const packetsRetrieved = createSelector(
  [packets],
  (packets) => packets !== null
);

// Do we actually have packets?
// if they have been retrieved and there are none, then nope
export const hasPackets = createSelector(
  [packetsRetrieved, packets],
  (packetsRetrieved, packets) => packetsRetrieved && packets.length !== 0
);

export const numberOfPackets = createSelector(
  [hasPackets, packets],
  (hasPackets, packets) => (!hasPackets) ? 0 : packets.length
);

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
    // reset continuation tracking
    isContinuation(null, null);
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
        // If the current packet is a continuation of the previous,
        // then the bytes need to be concated together
        if (previousPacket && isContinuation(currentPacket.side, currentPacket.sequence)) {
          previousPacket.bytes = previousPacket.bytes.concat(_bytes);
          // Update the byteRows with the new bytes that were added
          previousPacket.byteRows = bytesAsRows(previousPacket.bytes);
        } else {
          // Set initial continuation tracking
          isContinuation(currentPacket.side, currentPacket.sequence);
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
          isContinuation: isContinuation(currentPacket.side, currentPacket.sequence),
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
    const headerItem = getHeaderItem(headerItems, 'payloadSize');

    if (headerItem && headerItem.value !== '0') {
      return true;
    }

    return false;
  }
);
