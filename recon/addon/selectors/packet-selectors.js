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

    const newPackets = packets.map((packet) => {
      let { bytes } = packet;
      if (isPayloadOnly) {
        bytes = bytes.filter((b) => !b.isHeader && !b.isFooter);
      }
      return {
        ...packet,
        byteRows: bytesAsRows(bytes)
      };
    });

    return newPackets;
  }
);