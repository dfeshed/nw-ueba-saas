import { test, module } from 'qunit';

import {
  fileContextImageHooksSchema,
  fileContextKernelHooksSchema,
  fileContextThreadsSchema
} from 'investigate-hosts/reducers/details/anomalies/schemas';

import { normalize } from 'normalizr';
import { anomaliesData } from '../../../state/state';

module('Unit | Reducers | Anomalies');

test('Test for fileContextImageHooksSchema', function(assert) {
  const { entities: { imageHooks } } = normalize(anomaliesData, fileContextImageHooksSchema);

  const hookObjsKeys = Object.keys(imageHooks);

  assert.equal(hookObjsKeys.length, 7);
  assert.notEqual(imageHooks[hookObjsKeys[0]].hookLocation, undefined);
});

test('Test for fileContextThreadsSchema', function(assert) {
  const { entities: { threads } } = normalize(anomaliesData, fileContextThreadsSchema);

  const threadObjsKeys = Object.keys(threads);

  assert.equal(threadObjsKeys.length, 5);
  assert.notEqual(threads[threadObjsKeys[0]].tid, undefined);
});

test('Test for fileContextKernelHooksSchema', function(assert) {
  const { entities: { kernelHooks } } = normalize(anomaliesData, fileContextKernelHooksSchema);

  const hookObjsKeys = Object.keys(kernelHooks);

  assert.equal(hookObjsKeys.length, 6);
  assert.notEqual(kernelHooks[hookObjsKeys[0]].hookLocation, undefined);
});
