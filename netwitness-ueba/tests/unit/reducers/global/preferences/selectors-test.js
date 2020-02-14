import { test, module } from 'qunit';
import { getLocale, getLocales } from 'netwitness-ueba/reducers/global/preferences/selectors';

module('Unit | Selectors | Global | Preferences', function() {

  test('getLocale will return the correct locale data structure', async function(assert) {
    const result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en_US',
            key: 'en-us',
            label: 'english'
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en_US',
      key: 'en-us',
      langCode: 'en',
      label: 'english',
      displayLabel: 'English'
    });
  });

  test('getLocales will return the correct locales data structure', async function(assert) {
    const result = getLocales({
      global: {
        preferences: {
          locales: [
            {
              id: 'en_US',
              key: 'en-us',
              label: 'english'
            },
            {
              id: 'de_DE',
              key: 'de-de',
              label: 'german',
              fileName: 'german_de-de.js'
            }
          ]
        }
      }
    });

    assert.deepEqual(result, [
      {
        id: 'en_US',
        key: 'en-us',
        langCode: 'en',
        label: 'english',
        displayLabel: 'English'
      },
      {
        id: 'de_DE',
        key: 'de-de',
        langCode: 'de',
        label: 'german',
        fileName: 'german_de-de.js',
        displayLabel: 'German'
      }
    ]);
  });

  test('getLocale will correctly camelcase label value', async function(assert) {
    let result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en_US',
            key: 'en-us',
            label: ''
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en_US',
      key: 'en-us',
      langCode: 'en',
      label: '',
      displayLabel: ''
    });

    result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en_US',
            key: 'en-us',
            label: null
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en_US',
      key: 'en-us',
      langCode: 'en',
      label: null,
      displayLabel: null
    });

    result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en_US',
            key: 'en-us'
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en_US',
      key: 'en-us',
      langCode: 'en',
      displayLabel: undefined
    });

    result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en_US',
            key: 'en-us',
            label: 'x'
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en_US',
      key: 'en-us',
      langCode: 'en',
      label: 'x',
      displayLabel: 'X'
    });
  });
});
