import { module, test } from 'qunit';
import { visit, currentURL, find } from '@ember/test-helpers';
import { setupApplicationTest } from 'ember-qunit';
import { patchFetch } from '../helpers/patch-fetch';
import { Promise } from 'rsvp';
import dataIndex from '../data/presidio';

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
    assert.equal(find('.ember-view > div:nth-child(3) > .rsa-nav-tab:nth-child(2)').className, 'rsa-nav-tab is-left-aligned-secondary ember-view');
    assert.equal(find('.ember-view > div:nth-child(3) > .rsa-nav-tab:nth-child(3)').className, 'rsa-nav-tab is-left-aligned-secondary ember-view');
    assert.equal(find('.ember-view > div:nth-child(3) > .rsa-nav-tab:nth-child(1)').className, 'rsa-nav-tab is-active is-left-aligned-secondary ember-view');
  });

  test('visiting /investigate-users/users', async function(assert) {
    await visit('/investigate/users/users');
    assert.equal(find('.ember-view > div:nth-child(3) > .rsa-nav-tab:nth-child(2)').className, 'rsa-nav-tab is-active is-left-aligned-secondary ember-view');
    assert.equal(find('.ember-view > div:nth-child(3) > .rsa-nav-tab:nth-child(3)').className, 'rsa-nav-tab is-left-aligned-secondary ember-view');
    assert.equal(find('.ember-view > div:nth-child(3) > .rsa-nav-tab:nth-child(1)').className, 'rsa-nav-tab is-left-aligned-secondary ember-view');
  });

  test('visiting /investigate-users/alerts', async function(assert) {
    await visit('/investigate/users/alerts');
    assert.equal(find('.ember-view > div:nth-child(3) > .rsa-nav-tab:nth-child(2)').className, 'rsa-nav-tab is-left-aligned-secondary ember-view');
    assert.equal(find('.ember-view > div:nth-child(3) > .rsa-nav-tab:nth-child(3)').className, 'rsa-nav-tab is-active is-left-aligned-secondary ember-view');
    assert.equal(find('.ember-view > div:nth-child(3) > .rsa-nav-tab:nth-child(1)').className, 'rsa-nav-tab is-left-aligned-secondary ember-view');
  });
});