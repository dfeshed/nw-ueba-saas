import reselect from 'reselect';
import { processPacketPayloads } from './util';
import { getHeaderItem } from 'recon/utils/recon-event-header';

import { isRequestShown, isResponseShown } from 'recon/reducers/visuals/selectors';
import { packetTotal } from 'recon/reducers/header/selectors';

const { createSelector } = reselect;
const packets = (recon) => recon.packets.packets || [];
const packetsPageSize = (recon) => recon.packets.packetsPageSize;
const pageNumber = (recon) => recon.packets.pageNumber;
const renderIds = (recon) => recon.packets.renderIds;
const headerItems = (recon) => recon.header.headerItems;
const isPayloadOnly = (recon) => recon.packets.isPayloadOnly;

// packets can at different times be null, an empty array
// or a populated array. An empty array means the event has
// no text content. If textContent is null, then it is still
// being fetched.
const packetsRetrieved = createSelector(
  [packets],
  (packets) => packets !== null
);

export const hasRenderIds = createSelector(
  [renderIds],
  (renderIds) => !!renderIds && renderIds.length > 0
);

/**
 * Selector, determines packet set based on whether payloads are included
 *
 * @private
 */
const payloadProcessedPackets = createSelector(
  [packets, isPayloadOnly],
  processPacketPayloads
);

/**
 * Selector, those packets that are CANDIDATES for rendering
 *
 * @public
 */
export const toBeRenderedPackets = createSelector(
  [payloadProcessedPackets, isRequestShown, isResponseShown],
  (packets, isRequestShown, isResponseShown) => {

    // packets can be null, if so, get out
    // can have no renderIds, if so, get out
    if (!packets || packets.length === 0 || !renderIds || renderIds.length === 0) {
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

/**
 * Selector, returns those packets that are to be rendered
 *
 * @public
 */
export const renderedPackets = createSelector(
  [toBeRenderedPackets, renderIds],
  (packets, renderIds) => {
    // packets can be null, if so, get out
    // can have no renderIds, if so, get out
    if (!packets || packets.length === 0 || !renderIds || renderIds.length === 0) {
      return [];
    }
    // just want the packets with their id chosen to be rendered
    return packets.filter((p) => renderIds.includes(p.id));
  }
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

const _rawNumberOfPages = createSelector(
  [packetTotal, packetsPageSize],
  (packetTotal, packetsPageSize) => Math.ceil(packetTotal / packetsPageSize)
);

export const lastPageNumber = createSelector(
  [_rawNumberOfPages],
  (_rawNumberOfPages) => (_rawNumberOfPages === 0) ? 1 : _rawNumberOfPages
);

export const cannotGoToNextPage = createSelector(
  [_rawNumberOfPages, pageNumber],
  (_rawNumberOfPages, pageNumber) => _rawNumberOfPages <= pageNumber
);

export const cannotGoToPreviousPage = createSelector(
  [pageNumber],
  (pageNumber) => pageNumber === 1
);

export const packetRenderingUnderWay = createSelector(
  [toBeRenderedPackets, renderedPackets],
  (toBeRenderedPackets, renderedPackets) => toBeRenderedPackets.length > renderedPackets.length
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

