import reselect from 'reselect';
import { bytesAsRows } from 'recon/reducers/packets/util';

const { createSelector } = reselect;
const packets = (recon) => recon.data.packets;
const isRequestShown = (recon) => recon.visuals.isRequestShown;
const isResponseShown = (recon) => recon.visuals.isResponseShown;
const isPayloadOnly = (recon) => recon.visuals.isPayloadOnly;

/**
 * A selector that returns a sorted Array of all visible packets.
 * @private
 */
const visiblePackets = createSelector(
  [packets, isRequestShown, isResponseShown],
  (packets, isRequestShown, isResponseShown) => {

    // packets can be null
    let returnPackets = packets || [];

    if (!isRequestShown || !isResponseShown) {
      // we're not showing req or res, so let's filter them out
      returnPackets = packets.filter((p) => {
        return (p.side === 'request' && isRequestShown) ||
               (p.side === 'response' && isResponseShown);
      });
    }

    return returnPackets;
  }
);

/**
 * Processes visible packets and removes header/footer bytes
 * and then once those bytes are gone, calculates the rows
 * @public
 */
export const payloadProcessedPackets = createSelector(
  [visiblePackets, isPayloadOnly],
  (packets, isPayloadOnly) => {
    return packets.reduce((acc, currentPacket) => {
      let { bytes } = currentPacket;
      if (isPayloadOnly) {
        // Get the previous packet
        const previousPacket = acc[acc.length - 1];
        // Filter out header/footer items from the current packet
        bytes = bytes.filter((b) => !b.isHeader && !b.isFooter);
        // See if it's the same side
        if (previousPacket && previousPacket.side === currentPacket.side) {
          // Same side, so concat bytes to previous packet's bytes
          previousPacket.bytes = previousPacket.bytes.concat(bytes);
          // Update the byteRows with the new bytes that were added
          previousPacket.byteRows = bytesAsRows(previousPacket.bytes);
        } else {
          // It's not the same side, so we'll add the current packet to the
          // accumulator
          acc.push({
            ...currentPacket,
            bytes,
            byteRows: bytesAsRows(bytes)
          });
        }
      } else {
        // Augment each packet with a byteRows property.
        // This code path performs just like a map().
        acc.push({
          ...currentPacket,
          byteRows: bytesAsRows(bytes)
        });
      }
      return acc;
    }, []);
  }
);
