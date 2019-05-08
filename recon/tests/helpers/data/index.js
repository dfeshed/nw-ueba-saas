import packetData from '../../data/subscriptions/reconstruction-packet-data/stream/data';
import encodedTextData from '../../data/subscriptions/reconstruction-text-data/stream/encodedData';
import decodedTextData from '../../data/subscriptions/reconstruction-text-data/stream/decodedData';
import summaryDataInput from '../../data/subscriptions/reconstruction-summary/query/data';
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
const packetDataWithoutPayload = packetDataWithSide.filter((d) => d.payloadSize === 0);

const summaryData = {
  headerItems: _generateHeaderItems(summaryDataInput.withPayloads.summaryAttributes),
  packetFields: summaryDataInput.withPayloads.packetFields
};

const summaryDataWithoutPayload = {
  headerItems: _generateHeaderItems(summaryDataInput.noPayloads.summaryAttributes),
  packetFields: summaryDataInput.noPayloads.packetFields
};

const summaryDataWithoutPackets = {
  headerItems: _generateHeaderItems(summaryDataInput.noPackets.summaryAttributes),
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
  packetDataWithoutPayload,
  summaryData,
  summaryDataWithoutPayload,
  summaryDataWithoutPackets,
  preferences
};
