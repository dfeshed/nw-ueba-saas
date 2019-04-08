// *******
// BEGIN - Should be moved with the api
// *******
import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';

import fetchExtractJobId from 'recon/actions/fetch/file-extract';
import { patchSocket } from '../../../helpers/patch-socket';


const queryForLog = {
  filter: [
    { field: 'endpointId', value: 2 },
    { field: 'filename', value: '2_LOG_AS_TEXT' },
    { field: 'sessionIds', values: [ 3 ] },
    { field: 'outputContentType', value: 'TEXT' }
  ]
};

const queryForNetwork = {
  filter: [
    { field: 'endpointId', value: 2 },
    { field: 'filename', value: '2_NETWORK_AS_PCAP' },
    { field: 'sessionIds', values: [ 3 ] },
    { field: 'outputContentType', value: 'PCAP' }
  ]
};

const queryForFiles = {
  filter: [
    { field: 'endpointId', value: 2 },
    { field: 'filename', value: '2_FILES' },
    { field: 'sessionIds', values: [ 3 ] },
    { field: 'exportSelections', values: [ 'a', 'b'] }
  ]
};

module('Unit | API | extract-file', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('Should create a valid query for log download', function(assert) {
    const done = assert.async();

    const endpointId = 2;
    const eventId = 3;
    const fileType = 'TEXT';
    const filename = '2_LOG_AS_TEXT';
    const filenames = [ ];
    const eventType = 'LOG';
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(modelName, 'reconstruction-extract-LOG-job-id');
      assert.equal(method, 'query');
      assert.deepEqual(query, queryForLog);
      done();
    });
    fetchExtractJobId(endpointId, eventId, fileType, filename, filenames, eventType);
  });

  test('Should create a valid query for netwirk download', function(assert) {
    const done = assert.async();

    const endpointId = 2;
    const eventId = 3;
    const fileType = 'PCAP';
    const filename = '2_NETWORK_AS_PCAP';
    const filenames = [ ];
    const eventType = 'NETWORK';
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(modelName, 'reconstruction-extract-NETWORK-job-id');
      assert.equal(method, 'query');
      assert.deepEqual(query, queryForNetwork);
      done();
    });
    fetchExtractJobId(endpointId, eventId, fileType, filename, filenames, eventType);
  });

  test('Should create a valid query for Files download', function(assert) {
    const done = assert.async();

    const endpointId = 2;
    const eventId = 3;
    const fileType = 'FILES';
    const filename = '2_FILES';
    const filenames = [ 'a', 'b' ];
    const eventType = 'NETWORK';
    assert.expect(3);
    patchSocket((method, modelName, query) => {
      assert.equal(modelName, 'reconstruction-extract-FILES-job-id');
      assert.equal(method, 'query');
      assert.deepEqual(query, queryForFiles);
      done();
    });
    fetchExtractJobId(endpointId, eventId, fileType, filename, filenames, eventType);
  });

});
// *******
// END - Should be moved with the api
// *******
