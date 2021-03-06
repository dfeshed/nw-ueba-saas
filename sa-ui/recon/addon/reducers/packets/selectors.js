import reselect from 'reselect';
import { processPacketPayloads } from './util';
import { getHeaderItem } from 'recon/utils/recon-event-header';

import { isRequestShown, isResponseShown } from 'recon/reducers/visuals/selectors';
import { packetTotal } from 'recon/reducers/header/selectors';

const { createSelector } = reselect;
const packets = (recon) => recon.packets.packets;
const packetsPageSize = (recon) => recon.packets.packetsPageSize;
const pageNumber = (recon) => recon.packets.pageNumber;
const renderIds = (recon) => recon.packets.renderIds;
const headerItems = (recon) => recon.header.headerItems;
const isPayloadOnly = (recon) => recon.packets.isPayloadOnly;
const defaultPacketFormat = (recon) => recon.visuals.defaultPacketFormat;
const packetFields = (recon) => recon.packets.packetFields;

// packets can at different times be null, an empty array
// or a populated array. An empty array means the event has
// no text content. If textContent is null, then it is still
// being fetched.
export const _packetsRetrieved = (recon) => packets(recon) !== null;
export const numberOfPackets = (recon) => {
  const _packets = packets(recon);
  return _packets ? _packets.length : 0;
};

export const hasRenderIds = createSelector(
  [renderIds],
  (renderIds) => !!renderIds && renderIds.length > 0
);

/**
 * Selector, those packets that are CANDIDATES for rendering
 * Exporting it for testing purposes
 * @public
 */
export const _toBeRenderedPackets = createSelector(
  [packets, isPayloadOnly, packetFields, isRequestShown, isResponseShown],
  (packets, isPayloadOnly, packetFields, isRequestShown, isResponseShown) => {

    // packets can be null, if so, get out
    // can have no renderIds, if so, get out
    if (!packets || packets.length === 0 || !renderIds || renderIds.length === 0) {
      return [];
    }

    packets = processPacketPayloads(packets, isPayloadOnly, packetFields);

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
  [_toBeRenderedPackets, renderIds],
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

const _headerHasPackets = createSelector(
  headerItems,
  (headerItems) => {
    const headerItem = getHeaderItem(headerItems, 'packetSize');
    if (headerItem && headerItem.value) {
      return headerItem.value > 0;
    }
  },
);

// Do we actually have packets?
// if they have been retrieved and there are none, then nope
export const hasPackets = createSelector(
  [_packetsRetrieved, numberOfPackets, _headerHasPackets],
  (packetsRetrieved, numberOfPackets, headerHasPackets) => headerHasPackets || (packetsRetrieved && numberOfPackets !== 0)
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
  [_toBeRenderedPackets, renderedPackets, packetFields, _packetsRetrieved],
  (toBeRenderedPackets, renderedPackets, packetFields, packetsRetrieved) => {
    return !packetsRetrieved || packetFields === null || toBeRenderedPackets.length > renderedPackets.length;
  }
);

export const hasPayload = createSelector(
  headerItems,
  (headerItems) => {
    const headerItem = getHeaderItem(headerItems, 'payloadSize');
    if (headerItem && headerItem.value) {
      return headerItem.value > 0;
    }
  },
);

const _hasRequestPayload = createSelector(
  headerItems,
  (headerItems) => {
    const headerItem = getHeaderItem(headerItems, 'requestPayloadSize');
    if (headerItem && headerItem.value) {
      return headerItem.value > 0;
    }
  },
);

const _hasResponsePayload = createSelector(
  headerItems,
  (headerItems) => {
    const headerItem = getHeaderItem(headerItems, 'responsePayloadSize');
    if (headerItem && headerItem.value) {
      return headerItem.value > 0;
    }
  },
);

export const getNetworkDownloadOptions = createSelector(
  [hasPackets, hasPayload, _hasRequestPayload, _hasResponsePayload],
  (hasPackets, hasPayload, hasRequestPayload, hasResponsePayload) => {
    const downloadFormat = [
      {
        key: 'PCAP',
        value: 'downloadPCAP',
        isEnabled: hasPackets // total packet header and payload bytes request and response
      },
      {
        key: 'PAYLOAD',
        value: 'downloadPayload',
        isEnabled: hasPayload // total packet payload only bytes request and response
      },
      {
        key: 'PAYLOAD1',
        value: 'downloadPayload1',
        isEnabled: hasRequestPayload // total packet payload only bytes request only
      },
      {
        key: 'PAYLOAD2',
        value: 'downloadPayload2',
        isEnabled: hasResponsePayload // total packet payload only bytes response only
      }
    ];
    return downloadFormat;
  }
);

export const getDefaultDownloadFormat = createSelector(
  [defaultPacketFormat, getNetworkDownloadOptions],
  (defaultPacketFormat, networkDownloadOptions) => {
    return networkDownloadOptions.find((x) => x.key === defaultPacketFormat);
  }
);
