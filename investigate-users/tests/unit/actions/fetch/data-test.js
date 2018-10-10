import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { fetchData } from 'investigate-users/actions/fetch/data';
import { later } from '@ember/runloop';
import { initialFilterState } from 'investigate-users/reducers/users/selectors';

module('Unit | API | data', (hooks) => {
  setupTest(hooks);

  test('it can test fetch data', async(assert) => {
    const response = fetchData('riskyUserCount');
    later(() => {
      assert.deepEqual(response._result, { data: 57, total: 57, offset: 0, warning: null, info: null });
    }, 400);
  });

  test('it can test fetch data for given filter', async(assert) => {
    const response = fetchData('severityBarForUser', initialFilterState);
    later(() => {
      assert.deepEqual(response._result, {
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
      });
    }, 400);
  });
});