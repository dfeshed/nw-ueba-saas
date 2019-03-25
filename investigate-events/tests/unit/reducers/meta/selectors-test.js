import { module, test } from 'qunit';
import {
  remainingMetaKeyBatches,
  initMetaKeyStates,
  isMetaStreaming
} from 'investigate-events/reducers/investigate/meta/selectors';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

module('Unit | Selectors | meta');

test('remainingMetaKeyBatches returns meta for which streaming has not been requested', function(assert) {

  const state = new ReduxDataHelper().metaPanel({ init: true }).build();
  const metaKeyBatches = remainingMetaKeyBatches(state);
  assert.equal(metaKeyBatches.length, 1, 'Should select meta without values and isOpen');
  assert.equal(metaKeyBatches[0].info.metaName, 'ad.computer.src', 'Expected meta');

});

test('initMetaKeyStates prepares meta objects from language', function(assert) {

  const expectedMeta = {
    info: {
      count: 0,
      format: 'UInt8',
      metaName: 'medium',
      flags: -2147482541,
      displayName: 'Medium',
      formattedName: 'medium (Medium)',
      isOpen: true
    }
  };
  const state = new ReduxDataHelper().language().metaPanel({ init: false }).build();
  const metaKeyBatches = initMetaKeyStates(state);
  assert.deepEqual(metaKeyBatches.find((m) => m.info.metaName === 'medium'), expectedMeta, 'Meta info should be merged with isOpen flags');

});

test('isMetaStreaming returns true if we are waiting for response on any meta', function(assert) {
  const metaKeyStatesArray = [{
    info: { metaName: 'action' },
    values: { status: 'streaming' }
  }, {
    info: { metaName: 'ad.computer.src' },
    valuse: { status: 'complete' }
  }];
  const customMeta = {
    meta: metaKeyStatesArray,
    options: {}
  };

  const state = new ReduxDataHelper().metaPanel({ customMeta }).build();
  assert.ok(isMetaStreaming(state), 'Awaiting response on a meta');

});

test('isMetaStreaming returns false if we are not waiting for response on any meta', function(assert) {
  const metaKeyStatesArray = [{
    info: { metaName: 'action' }
  }, {
    info: { metaName: 'ad.computer.src' },
    valuse: { status: 'complete' }
  }];
  const customMeta = {
    meta: metaKeyStatesArray,
    options: {}
  };

  const state = new ReduxDataHelper().metaPanel({ customMeta }).build();
  assert.notOk(isMetaStreaming(state), 'Meta is all populated');

});