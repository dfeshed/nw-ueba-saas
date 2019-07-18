import packetData from '../../data/subscriptions/reconstruction-packet-data/stream/data';
import emailData from '../../data/subscriptions/reconstruction-email-data/stream/data';
import encodedTextData from '../../data/subscriptions/reconstruction-text-data/stream/encodedData';
import decodedTextData from '../../data/subscriptions/reconstruction-text-data/stream/decodedData';
import { withPayloads, noPayloads, noPackets } from '../../data/subscriptions/reconstruction-summary/query/data';
import files from '../../data/subscriptions/reconstruction-file-data/query/data';
import { augmentResult } from 'recon/reducers/util';

const _generateHeaderItems = (items) => (
  items.reduce(function(headerItems, item) {
    if (!item.id && item.name) {
      item.id = item.name;
    }
    headerItems.push(item);
    return headerItems;
  }, [])
);

const augmentedTextData = augmentResult(decodedTextData);

const packetDataWithSide = augmentResult(packetData.slice(0, 10));
const slicedEmailData = emailData.slice(0, 2);
const packetDataWithoutPayload = packetDataWithSide.filter((d) => d.payloadSize === 0);

const summaryData = {
  headerItems: _generateHeaderItems(withPayloads.summaryAttributes),
  packetFields: withPayloads.packetFields
};

const summaryDataWithoutPayload = {
  headerItems: _generateHeaderItems(noPayloads.summaryAttributes),
  packetFields: noPayloads.packetFields
};

const summaryDataWithoutPackets = {
  headerItems: _generateHeaderItems(noPackets.summaryAttributes),
  packetFields: []
};

const preferences = {
  queryTimeFormat: 'DB',
  eventAnalysisPreferences: {
    currentReconView: 'TEXT',
    isHeaderOpen: true,
    isMetaShown: true,
    isReconExpanded: true,
    isReconOpen: true,
    isRequestShown: true,
    isResponseShown: true,
    defaultLogFormat: 'TEXT',
    defaultPacketFormat: 'PCAP',
    autoDownloadExtractedFiles: true,
    packetsPageSize: 100
  }
};

export {
  augmentedTextData,
  decodedTextData,
  encodedTextData,
  files,
  packetDataWithSide,
  slicedEmailData,
  packetDataWithoutPayload,
  summaryData,
  summaryDataWithoutPayload,
  summaryDataWithoutPackets,
  preferences
};
