import { module, test } from 'qunit';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

module('Unit | Selectors | dictionaries');

test('metaKeySuggestionsForQueryBuilder selector filters out IndexedBy None meta keys, except sessionid',
  function(assert) {
    const sessionIdLanguageObject = {
      count: 0,
      format: 'UInt64',
      metaName: 'sessionid',
      flags: -2147483631,
      displayName: 'Session ID',
      formattedName: 'sessionid (Session ID)' };

    const state = new ReduxDataHelper().language([ sessionIdLanguageObject ]).build();
    const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);
    assert.ok(metaKeysForGuidedMode && metaKeysForGuidedMode.length === 1,
      'Sessionid is still available and has not been filtered out');
    assert.equal(metaKeysForGuidedMode[0].metaName, 'sessionid', 'metaName is \'sessionid\'');
  }
);

test('_enrichedLanguage selector adds three boolean properties to meta: isIndexedByNone, isIndexedByKey, isIndexedByValue',
  function(assert) {

    // Indexedby None meta
    const lifetimeLanguageObject = {
      format: 'UInt16',
      metaName: 'lifetime',
      flags: -2147483631,
      displayName: 'Session Lifetime',
      formattedName: 'lifetime (Session Lifetime)'
    };

    // Indexedby key meta
    const fileNameLanguageMeta = {
      count: 0,
      format: 'UInt64',
      metaName: 'filename.size',
      flags: -2147482878,
      displayName: 'File Size',
      formattedName: 'filename.size (File Size)'
    };

    const state = new ReduxDataHelper().language([ lifetimeLanguageObject, fileNameLanguageMeta ]).build();
    const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);
    const [meta] = metaKeysForGuidedMode;
    const keys = Object.keys(meta);
    assert.ok(keys.includes('isIndexedByNone'));
    assert.ok(keys.includes('isIndexedByValue'));
    assert.ok(keys.includes('isIndexedByKey'));
  }
);

test('_enrichedLanguage selector assigns correct values to the three boolean properties when meta is indexed by key',
  function(assert) {
    // Indexedby key meta
    const fileNameLanguageMeta = {
      count: 0,
      format: 'UInt64',
      metaName: 'filename.size',
      flags: -2147482878,
      displayName: 'File Size',
      formattedName: 'filename.size (File Size)'
    };

    const state = new ReduxDataHelper().language([ fileNameLanguageMeta ]).build();
    const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);

    assert.equal(metaKeysForGuidedMode.length, 1, 'Indexed by key meta has not been filtered out');

    const keys = Object.keys(metaKeysForGuidedMode[0]);

    assert.ok(keys.includes('isIndexedByNone') && !metaKeysForGuidedMode[0].isIndexedByNone);
    assert.ok(keys.includes('isIndexedByValue') && !metaKeysForGuidedMode[0].isIndexedByValue);
    assert.ok(keys.includes('isIndexedByKey') && metaKeysForGuidedMode[0].isIndexedByKey);
  }
);

test('_enrichedLanguage selector assigns correct values to the three boolean properties when meta is indexed by value',
  function(assert) {
    // Indexedby value meta
    const fileNameLanguageMeta = {
      count: 0,
      displayName: 'TCP Destination Port',
      flags: -2147482541,
      format: 'UInt16',
      formattedName: 'tcp.dstport (TCP Destination Port)',
      metaName: 'tcp.dstport'
    };

    const state = new ReduxDataHelper().language([ fileNameLanguageMeta ]).build();
    const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);

    assert.equal(metaKeysForGuidedMode.length, 1, 'Indexed by value meta has not been filtered out');

    const keys = Object.keys(metaKeysForGuidedMode[0]);

    assert.ok(keys.includes('isIndexedByNone') && !metaKeysForGuidedMode[0].isIndexedByNone);
    assert.ok(keys.includes('isIndexedByValue') && metaKeysForGuidedMode[0].isIndexedByValue);
    assert.ok(keys.includes('isIndexedByKey') && !metaKeysForGuidedMode[0].isIndexedByKey);
  }
);

test('metaKeySuggestionsForQueryBuilder selector filters out meta indexed by none', function(assert) {
  // Indexedby None meta
  const lifetimeLanguageObject = {
    format: 'UInt16',
    metaName: 'lifetime',
    flags: -2147483631,
    displayName: 'Session Lifetime',
    formattedName: 'lifetime (Session Lifetime)'
  };

  const state = new ReduxDataHelper().language([ lifetimeLanguageObject ]).build();
  const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);

  assert.equal(metaKeysForGuidedMode.length, 0, 'Indexed by none meta has been filtered out');
});
