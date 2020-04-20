import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { fetchData, exportData } from 'investigate-users/actions/fetch/data';
import { later } from '@ember/runloop';
import { initialFilterState } from 'investigate-users/reducers/users/selectors';
import { patchFetch } from '../../../helpers/patch-fetch';
import { Promise } from 'rsvp';
import dataIndex from '../../../data/presidio';

const riskyUserCount = { data: 57, total: 57, offset: 0, warning: null, info: null };
const severityBar = {
  data: {
    Critical: {
      userCount: 0
    },
    High: {
      userCount: 2
    },
    Low: {
      userCount: 182
    },
    Medium: {
      userCount: 0
    }
  },
  info: null,
  offset: 0,
  total: 184,
  warning: null
};

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
    const response = fetchData('riskyUserCount');
    later(() => {
      assert.deepEqual(response._result, riskyUserCount);
    }, 400);
  });

  test('it can test fetch data for given filter', async(assert) => {
    const response = fetchData('severityBarForUser', initialFilterState);
    later(() => {
      assert.deepEqual(response._result, severityBar);
    }, 400);
  });

  test('it can test export data for given filter', async(assert) => {
    assert.expect(1);
    window.URL.createObjectURL = () => {
      assert.ok(true, 'This function supposed to be called for altert export');
    };
    exportData('severityBarForUser', initialFilterState);
  });

  test('it can test fetch data for error cases', async(assert) => {
    patchFetch(() => {
      return new Promise(function(resolve, reject) {
        reject({
          ok: true,
          error: 'some error'
        });
      });
    });

    const response = fetchData('severityBarForUser');
    later(() => {
      assert.equal(response._result, 'error');
    }, 400);
  });

  test('it can test fetch data if fetched data is not proper', async(assert) => {
    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          error: 'some error'
        });
      });
    });
    const response = fetchData('severityBarForUser');
    later(() => {
      assert.equal(response._result, 'error');
    }, 400);
  });
});