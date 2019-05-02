import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { getNetworkDownloadOptions } from 'recon/reducers/packets/selectors';

import summaryDataInput from '../../../data/subscriptions/reconstruction-summary/query/data';

module('Unit | selector | packets');

test('getNetworkDownloadOptions', function(assert) {
  const result = getNetworkDownloadOptions(Immutable.from({
    header: {
      headerItems: summaryDataInput.withPayloads.summaryAttributes
    }
  }));
  assert.ok(result[0].isEnabled, 'PCAP download is enabled');
  assert.notOk(result[3].isEnabled, 'Response payload download is not enabled');
});
