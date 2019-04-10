import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { fetchData } from 'entity-details/actions/fetch/data';
import { later } from '@ember/runloop';
import { patchFetch } from '../../../helpers/patch-fetch';
import { Promise } from 'rsvp';
import dataIndex from '../../../data/presidio';

module('Unit | API | data', (hooks) => {
  setupTest(hooks);

  hooks.beforeEach(function() {
    patchFetch((url) => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return dataIndex(url);
          }
        });
      });
    });
  });

  test('it can test fetch data', async(assert) => {
    const fetchObj = {
      restEndpointLocation: 'userDetails',
      data: null,
      method: 'GET',
      urlParameters: '1212'
    };
    const response = fetchData(fetchObj);
    later(() => {
      assert.deepEqual(response._result.data[0].displayName, 'file_qa_1_101');
    }, 400);
  });
});