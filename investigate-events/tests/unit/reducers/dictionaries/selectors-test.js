import { module, test } from 'qunit';
import {
  metaKeySuggestionsForQueryBuilder,
  validMetaKeySuggestions,
  languageAndAliasesForParser,
  metaMapForColumns,
  defaultMetaGroupEnriched
} from 'investigate-events/reducers/investigate/dictionaries/selectors';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { lookup } from 'ember-dependency-lookup';

// isIndexedByNone meta
const lifetimeLanguageObjectIndexedByNone = {
  format: 'UInt16',
  metaName: 'lifetime',
  flags: -2147483631,
  displayName: 'Session Lifetime',
  formattedName: 'lifetime (Session Lifetime)'
};

// sessionid meta
const sessionidLanguageObject = {
  format: 'UInt16',
  metaName: 'sessionid',
  flags: -2147483631,
  displayName: 'Session Id',
  formattedName: 'Session Id'
};

// isIndexedByKey meta
const fileNameLanguageMetaIndexedByKey = {
  format: 'UInt64',
  metaName: 'filename.size',
  flags: -2147482878,
  displayName: 'File Size',
  formattedName: 'filename.size (File Size)'
};

// Indexedby value meta
const fileNameLanguageMetaIndexedByValue = {
  displayName: 'TCP Destination Port',
  flags: -2147482541,
  format: 'UInt16',
  formattedName: 'tcp.dstport (TCP Destination Port)',
  metaName: 'tcp.dstport'
};

// time
const timeMeta = {
  count: 0,
  format: 'TimeT',
  metaName: 'time',
  flags: -2147482605,
  displayName: 'Time',
  formattedName: 'time (Time)'
};

module('Unit | Selectors | dictionaries', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('defaultMetaGroupEnriched selector correctly sets the disabled property', function(assert) {
    const language1 = [
      lifetimeLanguageObjectIndexedByNone,
      fileNameLanguageMetaIndexedByKey
    ];
    const state = new ReduxDataHelper().language([language1]).build();
    const defaultMetaGroup = defaultMetaGroupEnriched(state);
    assert.ok(defaultMetaGroup.keys[0].disabled, 'the disabled property shall be true');
  });

  test('metaKeySuggestionsForQueryBuilder selector filters out IndexedBy None meta keys, except sessionid',
    function(assert) {
      const sessionIdLanguageObject = {
        count: 0,
        format: 'UInt64',
        metaName: 'sessionid',
        flags: -2147483631,
        displayName: 'Session ID',
        formattedName: 'sessionid (Session ID)'
      };

      const state = new ReduxDataHelper().language([sessionIdLanguageObject]).build();
      const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);
      assert.ok(metaKeysForGuidedMode && metaKeysForGuidedMode.length === 1,
        'Sessionid is still available and has not been filtered out');
      assert.equal(metaKeysForGuidedMode[0].metaName, 'sessionid', 'metaName is \'sessionid\'');
    }
  );

  test('_enrichedLanguage selector adds three boolean properties to meta: isIndexedByNone, isIndexedByKey, isIndexedByValue',
    function(assert) {
      const state = new ReduxDataHelper()
        .language([lifetimeLanguageObjectIndexedByNone, fileNameLanguageMetaIndexedByKey])
        .build();
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
      const state = new ReduxDataHelper().language([fileNameLanguageMetaIndexedByKey]).build();
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
      const state = new ReduxDataHelper().language([fileNameLanguageMetaIndexedByValue]).build();
      const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);

      assert.equal(metaKeysForGuidedMode.length, 1, 'Indexed by value meta has not been filtered out');

      const keys = Object.keys(metaKeysForGuidedMode[0]);

      assert.ok(keys.includes('isIndexedByNone') && !metaKeysForGuidedMode[0].isIndexedByNone);
      assert.ok(keys.includes('isIndexedByValue') && metaKeysForGuidedMode[0].isIndexedByValue);
      assert.ok(keys.includes('isIndexedByKey') && !metaKeysForGuidedMode[0].isIndexedByKey);
    }
  );

  test('metaKeySuggestionsForQueryBuilder selector sets indexed by none meta disabled', function(assert) {
    const state = new ReduxDataHelper().language([lifetimeLanguageObjectIndexedByNone]).build();
    const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);

    assert.equal(metaKeysForGuidedMode.length, 1, 'Indexed by none meta was not filtered out');
    assert.equal(metaKeysForGuidedMode[0].disabled, true, 'Indexed by none meta is not disabled');
  });

  test('metaKeySuggestionsForQueryBuilder selector correctly sets properties for icon for sessionid meta',
    function(assert) {
      const i18n = lookup('service:i18n');
      const state = new ReduxDataHelper().language([sessionidLanguageObject]).build();
      const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);

      assert.equal(metaKeysForGuidedMode[0].iconClass, 'is-sessionid sessionid-indicator',
        'Incorrect value for iconClass when metaName is sessionid');
      assert.equal(metaKeysForGuidedMode[0].iconTitle, i18n.t('queryBuilder.sessionid'),
        'Incorrect value for iconTitle when metaName is sessionid');
    });

  test('metaKeySuggestionsForQueryBuilder selector correctly sets properties for icon for indexed by value meta',
    function(assert) {
      const i18n = lookup('service:i18n');
      const state = new ReduxDataHelper().language([fileNameLanguageMetaIndexedByValue]).build();
      const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);

      assert.equal(metaKeysForGuidedMode[0].iconClass, 'is-indexed-by-value value-index-indicator',
        'Incorrect value for iconClass when meta is indexed by value');
      assert.equal(metaKeysForGuidedMode[0].iconTitle, i18n.t('queryBuilder.indexedByValue'),
        'Incorrect value for iconTitle when meta is indexed by value');
    });

  test('metaKeySuggestionsForQueryBuilder selector correctly sets properties for icon for indexed by key meta',
    function(assert) {
      const i18n = lookup('service:i18n');
      const state = new ReduxDataHelper().language([fileNameLanguageMetaIndexedByKey]).build();
      const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);

      assert.equal(metaKeysForGuidedMode[0].iconClass, 'is-indexed-by-key key-index-indicator',
        'Incorrect value for iconClass when meta is indexed by key');
      assert.equal(metaKeysForGuidedMode[0].iconTitle, i18n.t('queryBuilder.indexedByKey'),
        'Incorrect value for iconTitle when meta is indexed by key');
    });

  test('metaKeySuggestionsForQueryBuilder selector correctly sets properties for icon for indexed by none meta',
    function(assert) {
      const i18n = lookup('service:i18n');
      const state = new ReduxDataHelper().language([lifetimeLanguageObjectIndexedByNone]).build();
      const metaKeysForGuidedMode = metaKeySuggestionsForQueryBuilder(state);

      assert.equal(metaKeysForGuidedMode[0].iconClass, 'is-indexed-by-none none-index-indicator',
        'Incorrect value for iconClass when meta is indexed by none');
      assert.equal(metaKeysForGuidedMode[0].iconTitle, i18n.t('queryBuilder.indexedByNone'),
        'Incorrect value for iconTitle when meta is indexed by none');
    });

  test('validMetaKeySuggestions selector filters out isIndexedByNone meta', function(assert) {
    const state = new ReduxDataHelper().language([lifetimeLanguageObjectIndexedByNone]).build();
    const metaKeySuggestions = validMetaKeySuggestions(state);
    assert.equal(metaKeySuggestions.length, 0, 'Indexed by none meta was not filtered out');
  });

  test('validMetaKeySuggestions selector does not filter out if metaName is sessionid',
    function(assert) {
      // Indexedby None meta
      const sessionidMetaIndexedByNone = {
        format: 'UInt16',
        metaName: 'sessionid',
        flags: -2147483631
      };

      const state = new ReduxDataHelper().language([sessionidMetaIndexedByNone]).build();
      const metaKeySuggestions = validMetaKeySuggestions(state);
      assert.equal(metaKeySuggestions.length, 1, 'isIndexedByNone meta shall not be filtered out if metaName is sessionid');
    });

  test('languageAndAliasesForParser selector filters out only time, and also returns aliases',
    function(assert) {

      const state = new ReduxDataHelper().language([timeMeta]).aliases().build();
      const { language, aliases } = languageAndAliasesForParser(state);
      assert.ok(language && language.length === 0, 'time has been filtered out');
      assert.ok(aliases && aliases.medium['1'] === 'Ethernet', 'aliases have been included');
    }
  );

  test('languageAndAliasesForParser selector does not filter out regular meta, and also returns aliases',
    function(assert) {

      const state = new ReduxDataHelper().language().aliases().build();
      const { language, aliases } = languageAndAliasesForParser(state);
      assert.ok(language && language.length !== 0, 'regular meta are not filtered out');
      assert.ok(aliases && aliases.medium['1'] === 'Ethernet', 'aliases have been included');
    }
  );

  test('metaMapForColumns selector returns array of field/title objects for columnGroup candidate columns',
    function(assert) {

      const state = new ReduxDataHelper().metaKeyCache().build();
      const result = metaMapForColumns(state);
      assert.ok(result[0].hasOwnProperty('field'));
      assert.ok(result[0].hasOwnProperty('title'));
      assert.notOk(result[0].hasOwnProperty('metaName'));
      assert.notOk(result[0].hasOwnProperty('displayName'));
    }
  );
});
