// *******
// BEGIN - Should be moved with the api
// *******
import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';

import fetchExtractJobId from 'investigate-events/actions/fetch/file-extract';
import { patchSocket } from '../../../helpers/patch-socket';

const queryForLogAndNetwork = {
  filter: [
    { field: 'endpointId', value: 2 },
    { field: 'filename', value: '2_NETWORK_AS_PCAP' },
    { field: 'outputContentType', value: 'PCAP' },
    { field: 'sessionIds', values: [3, 7] }
  ]
};

const queryforMETA = {
  filter: [
    { field: 'endpointId', value: 2 },
    { field: 'filename', value: '2_META_AS_TSV' },
    { field: 'outputContentType', value: 'TSV' },
    { field: 'sessionIds', values: [3, 7] },
    { field: 'exportSelections', values: [ 'a', 'b'] }
  ]
};
module('Unit | API | extract-file', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('Should create a valid query for Log and Network downloads', function(assert) {
    const done = assert.async();
    const endpointId = 2;
    const eventIds = [3, 7];
    const fileType = 'PCAP';
    const fileName = '2_NETWORK_AS_PCAP';
    const eventDownloadType = 'NETWORK';
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(modelName, 'extract-NETWORK-job-id');
      assert.equal(method, 'query');
      assert.deepEqual(query, queryForLogAndNetwork);
      done();
    });
    fetchExtractJobId(endpointId, eventIds, fileType, fileName, eventDownloadType);
  });

  test('Should create a valid query for Meta downloads', function(assert) {
    const done = assert.async();
    const endpointId = 2;
    const eventIds = [3, 7];
    const fileType = 'TSV';
    const fileName = '2_META_AS_TSV';
    const eventDownloadType = 'META';
    const columnList = ['a', 'b'];
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(modelName, 'extract-META-job-id');
      assert.equal(method, 'query');
      assert.deepEqual(query, queryforMETA);
      done();
    });
    fetchExtractJobId(endpointId, eventIds, fileType, fileName, eventDownloadType, columnList);
  });


});
// *******
// END - Should be moved with the api
// *******
