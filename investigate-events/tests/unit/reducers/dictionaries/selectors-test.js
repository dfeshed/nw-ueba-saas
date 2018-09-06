import { module, test } from 'qunit';

import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

module('Unit | Selectors | dictionaries');

test('metaKeySuggestionsForQueryBuilder selector filters out IndexedBy None meta keys, except sessionid', function(assert) {
  const sessionIdLanguageObject = { count: 0, format: 'UInt64', metaName: 'sessionid', flags: -2147483631, displayName: 'Session ID', formattedName: 'sessionid (Session ID)' };
  const state = new ReduxDataHelper().language([ sessionIdLanguageObject ]).build();
  const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);

  assert.deepEqual(metaKeysForGuidedMode, [sessionIdLanguageObject], 'Sessionid is still available and has not been filtered out');
});

test('metaKeySuggestionsForQueryBuilder selector filters out IndexedBy None', function(assert) {
  // Indexedby None meta
  const lifetimeLanguageObject = { format: 'UInt16', metaName: 'lifetime', flags: -2147483631, displayName: 'Session Lifetime', formattedName: 'lifetime (Session Lifetime)' };
  // Indexedby key meta
  const fileNameLanguageMeta = { count: 0, format: 'UInt64', metaName: 'filename.size', flags: -2147482878, displayName: 'File Size', formattedName: 'filename.size (File Size)' };

  const state = new ReduxDataHelper().language([ lifetimeLanguageObject, fileNameLanguageMeta ]).build();
  const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);

  const [meta] = metaKeysForGuidedMode;
  assert.equal(metaKeysForGuidedMode.length, 1, 'Indexed none meta key has been filtered out');
  assert.equal(meta.metaName, 'filename.size', 'correct meta still present');
});