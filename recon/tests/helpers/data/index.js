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

const summaryData = {
  headerItems: _generateHeaderItems(summaryDataInput.summaryAttributes),
  packetFields: summaryDataInput.packetFields
};

export {
  augmentedTextData,
  decodedTextData,
  encodedTextData,
  files,
  packetDataWithSide,
  summaryData
};
