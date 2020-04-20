import { module, test } from 'qunit';
import {
  remainingMetaKeyBatches,
  initMetaKeyStates,
  isMetaStreaming,
  emptyMetaKeys,
  canFetchMeta
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
    values: { status: 'complete' }
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
    values: { status: 'complete' }
  }];
  const customMeta = {
    meta: metaKeyStatesArray,
    options: {}
  };

  const state = new ReduxDataHelper().metaPanel({ customMeta }).build();
  assert.notOk(isMetaStreaming(state), 'Meta is all populated');

});

test('emptyMetaKeys returns list of metaKeyStates that are open and returned no values in response', function(assert) {
  const metaKeyStatesArray = [{
    info: { metaName: 'action', isOpen: true },
    values: {
      data: [
        {
          value: 'foo',
          count: 9821
        }
      ],
      complete: true
    }
  }, {
    info: { metaName: 'ad.computer.src', isOpen: true },
    values: { complete: true, data: [] }
  }, {
    info: { metaName: 'ad.computer.src', isOpen: true },
    values: {}
  }];
  const customMeta = {
    meta: metaKeyStatesArray,
    options: {}
  };

  const state = new ReduxDataHelper().metaPanel({ customMeta }).build();
  assert.equal(emptyMetaKeys(state).length, 1, 'Should have one metaKeyState that has no values');
  assert.equal(emptyMetaKeys(state)[0].info.metaName, 'ad.computer.src', 'Expected meta with no values');

});

test('canFetchMeta returns false if query was executed by columnGroup change', function(assert) {
  const state = {
    investigate: {
      meta: {
        metaPanelSize: 'default'
      },
      data: {
        isQueryExecutedByColumnGroup: true
      }
    }
  };
  assert.notOk(canFetchMeta(state), 'Meta should not be retrieved');
});

test('canFetchMeta returns false if metaPanelSize is min', function(assert) {
  const state = {
    investigate: {
      meta: {
        metaPanelSize: 'min'
      },
      data: {
        isQueryExecutedByColumnGroup: false
      }
    }
  };
  assert.notOk(canFetchMeta(state), 'Meta should not be retrieved');
});

test('canFetchMeta returns true if metaPanelSize is visible and query is executed through search button', function(assert) {
  const state = {
    investigate: {
      meta: {
        metaPanelSize: 'default'
      },
      data: {
        isQueryExecutedByColumnGroup: false
      }
    }
  };
  assert.ok(canFetchMeta(state), 'Meta should be retrieved');
});