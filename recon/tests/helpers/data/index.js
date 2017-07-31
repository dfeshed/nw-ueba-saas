import Ember from 'ember';

import packetData from '../../data/subscriptions/reconstruction-packet-data/stream/data';
import encodedTextData from '../../data/subscriptions/reconstruction-text-data/stream/encodedData';
import decodedTextData from '../../data/subscriptions/reconstruction-text-data/stream/decodedData';
import summaryDataInput from '../../data/subscriptions/reconstruction-summary/query/data';
import files from '../../data/subscriptions/reconstruction-file-data/query/data';

const { A } = Ember;

// TODO: this is duplicated from client code
const _generateHeaderItems = (items) => (
  items.reduce(function(headerItems, item) {
    if (!item.id && item.name) {
      item.id = item.name;
    }

    headerItems.pushObject(Object.create(item));

    return headerItems;
  }, A([]))
);

// TODO: this is duplicated from client code
const packetDataWithSide = packetData
  .slice(0, 10)
  .map((p) => {
    p.side = (p.side === 1) ? 'request' : 'response';
    return p;
  });

const summaryData = {
  headerItems: _generateHeaderItems(summaryDataInput.summaryAttributes),
  packetFields: summaryDataInput.packetFields
};

export {
  decodedTextData,
  encodedTextData,
  files,
  packetDataWithSide,
  summaryData
};
