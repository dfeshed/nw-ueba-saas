// *******
// BEGIN - Should be moved with the api
// *******
import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';

import fetchExtractJobId from 'investigate-events/actions/fetch/file-extract';
import { patchSocket } from '../../../helpers/patch-socket';

const queryWithSessionIds = {
  filter: [
    { field: 'endpointId', value: 2 },
    { field: 'filename', value: '2_NETWORK_AS_PCAP' },
    { field: 'outputContentType', value: 'PCAP' },
    { field: 'sessionIds', values: [3, 7] }
  ]
};

const queryWithoutSessionIds = {
  filter: [
    { field: 'endpointId', value: 2 },
    { field: 'filename', value: '2_NETWORK_AS_PCAP' },
    { field: 'outputContentType', value: 'PCAP' },
    { field: 'query', value: '' },
    { field: 'timeRange', range: { from: 1540237800, to: 1540324199 } }
  ]
};

const queryforMETAWithoutSessionIds = {
  filter: [
    { field: 'endpointId', value: 2 },
    { field: 'filename', value: '2_META_AS_TSV' },
    { field: 'outputContentType', value: 'TSV' },
    { field: 'query', value: '' },
    { field: 'timeRange', range: { from: 1540237800, to: 1540324199 } },
    { field: 'exportSelections', values: [ 'a', 'b'] }
  ]
};
module('Unit | API | extract-file', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('Should create a query with sessionids when all events are not selected', function(assert) {
    const done = assert.async();
    const queryNode = {
      endTime: 1540324199,
      serviceId: '2',
      startTime: 1540237800
    };
    const endpointId = 2;
    const eventIds = [3, 7];
    const fileType = 'PCAP';
    const fileName = '2_NETWORK_AS_PCAP';
    const eventDownloadType = 'NETWORK';
    const isSelectAll = false;
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(modelName, 'extract-NETWORK-job-id');
      assert.equal(method, 'query');
      assert.deepEqual(query, queryWithSessionIds);
      done();
    });
    fetchExtractJobId(queryNode, endpointId, eventIds, fileType, fileName, eventDownloadType, isSelectAll);
  });


  test('Should create a query with params when all events are selected', function(assert) {
    const done = assert.async();
    const queryNode = {
      endTime: 1540324199,
      serviceId: '2',
      startTime: 1540237800,
      metaFilter: []
    };
    const endpointId = 2;
    const eventIds = [3, 7];
    const fileType = 'PCAP';
    const fileName = '2_NETWORK_AS_PCAP';
    const eventDownloadType = 'NETWORK';
    const isSelectAll = true;
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(modelName, 'extract-NETWORK-job-id');
      assert.equal(method, 'query');
      assert.deepEqual(query, queryWithoutSessionIds);
      done();
    });
    fetchExtractJobId(queryNode, endpointId, eventIds, fileType, fileName, eventDownloadType, isSelectAll);
  });

  test('Should create a query with params for meta download when all events are selected', function(assert) {
    const done = assert.async();
    const queryNode = {
      endTime: 1540324199,
      serviceId: '2',
      startTime: 1540237800,
      metaFilter: []
    };
    const endpointId = 2;
    const eventIds = [3, 7];
    const fileType = 'TSV';
    const fileName = '2_META_AS_TSV';
    const eventDownloadType = 'META';
    const isSelectAll = true;
    const columnList = ['a', 'b'];
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(modelName, 'extract-META-job-id');
      assert.equal(method, 'query');
      assert.deepEqual(query, queryforMETAWithoutSessionIds);
      done();
    });
    fetchExtractJobId(queryNode, endpointId, eventIds, fileType, fileName, eventDownloadType, isSelectAll, columnList);
  });


});
// *******
// END - Should be moved with the api
// *******
