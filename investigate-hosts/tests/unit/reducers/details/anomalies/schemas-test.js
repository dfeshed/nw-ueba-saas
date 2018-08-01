import { test, module } from 'qunit';

import {
  fileContextHooksSchema,
  fileContextThreadsSchema
} from 'investigate-hosts/reducers/details/anomalies/schemas';

import { normalize } from 'normalizr';
import { hooksData } from '../../../state/state';

module('Unit | Reducers | Anomalies');

test('Test for fileContextHooksSchema', function(assert) {
  const { entities: { hooks } } = normalize(hooksData, fileContextHooksSchema);

  const hookObjsKeys = Object.keys(hooks);

  assert.equal(hookObjsKeys.length, 7);
  assert.notEqual(hooks[hookObjsKeys[0]].hooks, undefined);
});

test('Test for fileContextThreadsSchema', function(assert) {
  const { entities: { threads } } = normalize(hooksData, fileContextThreadsSchema);

  const threadObjsKeys = Object.keys(threads);

  assert.equal(threadObjsKeys.length, 5);
  assert.notEqual(threads[threadObjsKeys[0]].threads, undefined);
});

