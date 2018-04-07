import { test, module } from 'qunit';
import { getLocale, getLocales } from 'sa/reducers/global/preferences/selectors';

module('Unit | Selectors | Global | Preferences', function() {

  test('getLocale will return the correct locale data structure', async function(assert) {
    const result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en_US',
            label: 'english'
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en_US',
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
              label: 'english'
            },
            {
              id: 'de-DE',
              label: 'german',
              fileName: 'german_de-DE.js'
            }
          ]
        }
      }
    });

    assert.deepEqual(result, [
      {
        id: 'en_US',
        label: 'english',
        displayLabel: 'English'
      },
      {
        id: 'de-DE',
        label: 'german',
        fileName: 'german_de-DE.js',
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
            label: ''
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en_US',
      label: '',
      displayLabel: ''
    });

    result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en_US',
            label: null
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en_US',
      label: null,
      displayLabel: null
    });

    result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en_US'
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en_US',
      displayLabel: undefined
    });

    result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en_US',
            label: 'x'
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en_US',
      label: 'x',
      displayLabel: 'X'
    });
  });
});
