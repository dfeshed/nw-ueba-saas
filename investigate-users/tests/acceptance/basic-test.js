import { module, test } from 'qunit';
import { visit, currentURL, find, findAll, fillIn } from '@ember/test-helpers';
import { setupApplicationTest } from 'ember-qunit';
import { patchFetch } from '../helpers/patch-fetch';
import { patchFlash } from '../helpers/patch-flash';
import { Promise } from 'rsvp';
import dataIndex from '../data/presidio';
import { clickTrigger } from '../helpers/ember-power-select';

module('Acceptance | investigate-users', function(hooks) {
  setupApplicationTest(hooks);

  hooks.beforeEach(function() {
    patchFetch((url) => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return dataIndex(url);
          }
        });
      });
    });
  });

  test('visiting /investigate-users', async function(assert) {
    await visit('/investigate/users');
    assert.equal(currentURL(), '/investigate/users');
    assert.equal(find('.user-header_tab > .rsa-nav-tab:nth-child(2)').className, 'rsa-nav-tab is-left-aligned-secondary ember-view');
    assert.equal(find('.user-header_tab > .rsa-nav-tab:nth-child(3)').className, 'rsa-nav-tab is-left-aligned-secondary ember-view');
    assert.equal(find('.user-header_tab > .rsa-nav-tab:nth-child(1)').className, 'rsa-nav-tab is-active is-left-aligned-secondary ember-view');
    assert.equal(findAll('.user-header_search').length, 1);
  });

  test('visiting /investigate-users/users', async function(assert) {
    await visit('/investigate/users/users');
    assert.equal(find('.user-header_tab > .rsa-nav-tab:nth-child(2)').className, 'rsa-nav-tab is-active is-left-aligned-secondary ember-view');
    assert.equal(find('.user-header_tab > .rsa-nav-tab:nth-child(3)').className, 'rsa-nav-tab is-left-aligned-secondary ember-view');
    assert.equal(find('.user-header_tab > .rsa-nav-tab:nth-child(1)').className, 'rsa-nav-tab is-left-aligned-secondary ember-view');
    assert.equal(findAll('.user-header_search').length, 1);
  });

  test('visiting /investigate-users/alerts', async function(assert) {
    await visit('/investigate/users/alerts');
    assert.equal(find('.user-header_tab > .rsa-nav-tab:nth-child(2)').className, 'rsa-nav-tab is-left-aligned-secondary ember-view');
    assert.equal(find('.user-header_tab > .rsa-nav-tab:nth-child(3)').className, 'rsa-nav-tab is-active is-left-aligned-secondary ember-view');
    assert.equal(find('.user-header_tab > .rsa-nav-tab:nth-child(1)').className, 'rsa-nav-tab is-left-aligned-secondary ember-view');
    assert.equal(findAll('.user-header_search').length, 1);
  });

  test('test search for user', async function(assert) {
    const done = assert.async();
    await visit('/investigate/users');
    await clickTrigger('.user-header_search:nth-child(2)');
    assert.equal(findAll('.ember-power-select-option').length, 1);
    // Element identification is failing in jenkins due to unknown reason. Asserting fetch call to ensure options are pulled everytime user search.
    patchFetch((url) => {
      assert.equal(url, '/presidio/api/user?page=1&size=10&sort_field=displayName&sort_direction=ASC&search_field_contains=auth&');
      done();
    });
    await fillIn('.ember-power-select-search-input', 'auth');
  });

  test('test search for user on failure condition', async function(assert) {
    const done = assert.async();
    await visit('/investigate/users');
    await clickTrigger('.user-header_search:nth-child(2)');
    assert.equal(findAll('.ember-power-select-option').length, 1);
    patchFetch(() => {
      return new Promise(function(resolve, reject) {
        reject({
          ok: true,
          error: 'some error'
        });
      });
    });
    patchFlash((flash) => {
      assert.equal(flash.type, 'error');
      done();
    });
    await fillIn('.ember-power-select-search-input', 'auth');
  });
});