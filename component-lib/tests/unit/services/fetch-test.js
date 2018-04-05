import { Promise } from 'rsvp';
import { module, test } from 'qunit';
import { patchFetch } from '../../helpers/patch-fetch';
import fetch from 'component-lib/services/fetch';

module('Unit | Fetch', function() {

  test('fetch will throw when exception occurs', async function(assert) {
    assert.expect(1);

    patchFetch(() => {
      return new Promise(function(resolve, reject) {
        reject('boom!');
      });
    });

    return fetch('/foo').then(() => {
      assert.ok(false, 'this promise should have failed');
    }).catch(() => {
      assert.ok(true, 'any vanilla reject will be thrown');
    });
  });

  test('fetch will throw when response is not ok (ex: 404)', async function(assert) {
    assert.expect(1);

    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: null,
          text() {
          }
        });
      });
    });

    return fetch('/bar').then(() => {
      assert.ok(false, 'this promise should have failed');
    }).catch(() => {
      assert.ok(true, '404 is represented when ok is falsy');
    });
  });

  test('fetch will resolve correctly when response has ok (ie: 200)', async function(assert) {
    assert.expect(2);

    patchFetch((url) => {
      assert.equal(url, '/bar');
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          text() {
            return new Promise(function(r) {
              r('hello world');
            });
          }
        });
      });
    });

    return fetch('/bar').then((fetched) => fetched.text()).then((body) => {
      assert.equal(body, 'hello world');
    }).catch(() => {
      assert.ok(false, 'this promise should have succeeded');
    });
  });

});
