import Ember from 'ember';

import packetData from '../../server/subscriptions/reconstruction-packet-data/stream/data';
import encodedTextData from '../../server/subscriptions/reconstruction-text-data/stream/encodedData';
import decodedTextData from '../../server/subscriptions/reconstruction-text-data/stream/decodedData';
import summaryDataInput from '../../server/subscriptions/reconstruction-summary/query/data';
import files from '../../server/subscriptions/reconstruction-file-data/query/data';

const { A } = Ember;

// TODO: this is duplicated from client code
const _generateHeaderItems = (items) => (
  items.reduce(function(headerItems, item) {
    if (item.name === 'destination' || item.name === 'source') {
      headerItems.pushObjects([{
        name: `${item.name} IP:PORT`,
        value: item.value
      }]);
    } else {
      headerItems.pushObject(item);
    }
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
