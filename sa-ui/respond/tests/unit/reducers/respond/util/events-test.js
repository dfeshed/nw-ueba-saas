import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import fixNormalizedEvents from 'respond/reducers/respond/util/events';

module('Unit | Util | respond normalize events fields', function(hooks) {
  setupTest(hooks);

  test('test normalizeEventFiles utility method', function(assert) {
    assert.deepEqual(fixNormalizedEvents([]), [], 'no events- nothing happens');
    assert.deepEqual(fixNormalizedEvents([{ data: undefined }]), [{ data: undefined }], 'data field is falsy, nothing happens');

    const inputEvents = [{
      data: [
        {
          filename: 'foo.exe,bar.exe'
        },
        {
          filename: 'baz.exe,foo.exe'
        }
      ]
    }];

    const expectedOutput = [{
      data: [
        {
          filename: 'foo.exe',
          filename_original: 'foo.exe,bar.exe'
        },
        {
          filename_original: 'baz.exe,foo.exe',
          filename: 'baz.exe'
        },
        {
          filename: 'bar.exe'
        },
        {
          filename: 'foo.exe'
        }
      ]
    }];

    assert.deepEqual(fixNormalizedEvents(inputEvents), expectedOutput, 'filenames correctly normalized. Note: Duplicates possible');
  });

  test('test normalizeEventUsers utility method', function(assert) {
    const inputEvents = [{
      source: { user: { username: 'foo,bar' } }
    }];

    const expectedOutput = [{
      source:
        {
          user: {
            username_original: 'foo,bar',
            username: 'foo'
          }
        }
    }];

    assert.deepEqual(fixNormalizedEvents(inputEvents), expectedOutput, 'normalize multiple usernames');

    const inputEvents2 = [{
      source: { user: { username: 'baz' } }
    }];

    assert.deepEqual(fixNormalizedEvents(inputEvents2), inputEvents2, 'ignore single username case');
  });

  test('test normalizeEventDomain utility method', function(assert) {
    const inputEvents = [{
      domain: 'foo.com,bar.com'
    }];

    const expectedOutput = [{
      domain_original: 'foo.com,bar.com',
      domain: ['foo.com', 'bar.com']
    }];

    assert.deepEqual(fixNormalizedEvents(inputEvents), expectedOutput, 'normalize multiple domains');
  });
});