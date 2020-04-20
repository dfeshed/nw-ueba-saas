import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { flattenObject } from 'admin-source-management/utils/object-util';

module('Unit | Utils | utils/object-util', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('flattenObject() should flatten a nested object structure into one top level object', function(assert) {
    const deepObject = {
      a: true,
      b: null,
      c: undefined,
      d: 34,
      e: 'e-string',
      f: ['f', 'f', 'f'],
      g: {
        h: true,
        i: null,
        j: undefined,
        k: 34,
        l: 'l-string',
        m: ['m', 'm', 'm'],
        n: {
          o: true,
          p: null,
          q: undefined,
          r: 34,
          s: 's-string',
          t: ['t', 't', 't']
        }
      }
    };
    const flatObjectExpected = {
      a: true,
      b: null,
      c: undefined,
      d: 34,
      e: 'e-string',
      f: ['f', 'f', 'f'],
      h: true,
      i: null,
      j: undefined,
      k: 34,
      l: 'l-string',
      m: ['m', 'm', 'm'],
      o: true,
      p: null,
      q: undefined,
      r: 34,
      s: 's-string',
      t: ['t', 't', 't']
    };
    const flatObjectActual = flattenObject(deepObject);
    assert.deepEqual(flatObjectActual, flatObjectExpected, 'deepObject is flattened as expected');
  });

});
