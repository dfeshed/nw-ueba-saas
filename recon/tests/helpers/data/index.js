import packetData from '../../data/subscriptions/reconstruction-packet-data/stream/data';
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

const requestTextData = [
  {
    'byteCount': 1063,
    'charset': 'UTF-8',
    'contentDecoded': true,
    'contentType': 'HTTP',
    'firstPacketId': 4804965123547,
    'firstPacketTime': 1485792552870,
    'packetCount': 69,
    'side': 'Client',
    'text': 'GET /stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421 HTTP/1.1$\r\nHost: www.saavn.com'
  }
];

const responseTextData = [
  {
    'byteCount': 397,
    'charset': 'UTF-8',
    'contentDecoded': true,
    'contentType': 'HTTP',
    'firstPacketId': 4804965132302,
    'firstPacketTime': 1485792553440,
    'packetCount': 74,
    'side': 'Server',
    'text': 'HTTP/1.1 200 OK\r\nCache-control: no-store, no-cache, must-revalidate, private, max-age=0\r\nContent-Encoding: gzip\r\nDate: Mon, 30 Jan 2017 16:09:13 GMT\r\nServer: Apache/2.2.15 (CentOS)\r\nVary: Accept-Encoding\r\nX-Powered-By: PHP/5.4.30\r\nContent-Length: 20\r\nContent-Type: text/html; charset=UTF-8\r\nVia: 1.1 inprthop10p.corp.emc.com:80 (Cisco-WSA/9.0.1-162)\r\nConnection: keep-alive\r\n\r\n'
  }
];

const augmentedTextData = augmentResult(decodedTextData);

const packetDataWithSide = augmentResult(packetData.slice(0, 10));
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
  packetDataWithoutPayload,
  summaryData,
  summaryDataWithoutPayload,
  summaryDataWithoutPackets,
  preferences,
  requestTextData,
  responseTextData
};
