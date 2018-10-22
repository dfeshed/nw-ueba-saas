import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, click, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import favorite from '../../../../../data/presidio/favorite_filter';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchFetch } from '../../../../../helpers/patch-fetch';
import { Promise } from 'rsvp';
import { later } from '@ember/runloop';

let setState;

module('Integration | Component | users-tab/filter/favorites', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return {};
          }
        });
      });
    });
  });


  test('it renders', async function(assert) {
    await render(hbs`{{users-tab/filter/favorites}}`);
    assert.equal(find('.users-tab_filter_favorites').textContent.replace(/\s/g, ''), 'Favorites');
  });

  test('it renders all favorites', async function(assert) {
    new ReduxDataHelper(setState).usersFavorites(favorite.data).build();
    const done = assert.async();
    await render(hbs`{{users-tab/filter/favorites}}`);
    assert.equal(findAll('.users-tab_filter_favorites_filter').length, 2);
    click('.rsa-form-button');
    return settled().then(() => {
      assert.equal(findAll('.rsa-form-button-clicked').length, 2);
      done();
    });
  });

  test('it should delete favorite', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState).usersFavorites(favorite.data).build();
    await render(hbs`{{users-tab/filter/favorites}}`);
    assert.equal(findAll('.users-tab_filter_favorites_filter_close').length, 2);
    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return {
              data: []
            };
          }
        });
      });
    });
    await this.$('.rsa-icon-close-filled:first').click();
    later(() => {
      assert.equal(findAll('.users-tab_filter_favorites_filter_close').length, 0);
      done();
    }, 1000);
  });
});
