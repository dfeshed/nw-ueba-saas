import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import { getLocale, getLocales } from 'sa/reducers/global/preferences/selectors';

module('Unit | Selectors | Global | Preferences', function(hooks) {
  setupTest(hooks);

  test('getLocale will return the correct locale data structure', async function(assert) {
    const result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en-us',
            label: 'english'
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en-us',
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
              id: 'en-us',
              label: 'english'
            },
            {
              id: 'de-de',
              label: 'german',
              fileName: 'german_de-de'
            }
          ]
        }
      }
    });

    assert.deepEqual(result, [
      {
        id: 'en-us',
        label: 'english',
        displayLabel: 'English'
      },
      {
        id: 'de-de',
        label: 'german',
        fileName: 'german_de-de',
        displayLabel: 'German'
      }
    ]);
  });

  test('getLocale will correctly camelcase label value', async function(assert) {
    let result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en-us',
            label: ''
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en-us',
      label: '',
      displayLabel: ''
    });

    result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en-us',
            label: null
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en-us',
      label: null,
      displayLabel: null
    });

    result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en-us'
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en-us',
      displayLabel: undefined
    });

    result = getLocale({
      global: {
        preferences: {
          locale: {
            id: 'en-us',
            label: 'x'
          }
        }
      }
    });

    assert.deepEqual(result, {
      id: 'en-us',
      label: 'x',
      displayLabel: 'X'
    });
  });
});
