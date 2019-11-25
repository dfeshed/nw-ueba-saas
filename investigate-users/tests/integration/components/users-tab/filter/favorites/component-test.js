import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import favorite from '../../../../../data/presidio/favorite_filter';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchFetch } from '../../../../../helpers/patch-fetch';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
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
    assert.equal(findAll('.users-tab_filter_favorites').length, 1);
    assert.equal(findAll('.ember-power-select-placeholder').length, 1);
  });

  test('it renders all favorites', async function(assert) {
    new ReduxDataHelper(setState).usersFavorites(favorite.data).build();
    await render(hbs`{{users-tab/filter/favorites}}`);
    await clickTrigger('.users-tab_filter_filter_select');
    assert.equal(findAll('.ember-power-select-option').length, 3);
  });

  test('it should test selected favorites', async function(assert) {
    new ReduxDataHelper(setState).usersFavorites(favorite.data).build();
    await render(hbs`{{users-tab/filter/favorites}}`);
    await clickTrigger('.users-tab_filter_filter_select');
    assert.equal(findAll('.ember-power-select-option').length, 3);
    assert.equal(findAll('.users-tab_filter_favorites_delete').length, 2);
    await selectChoose('.users-tab_filter_filter_select', 'Test1');
    await clickTrigger('.users-tab_filter_filter_select');
    assert.equal(findAll('.users-tab_filter_favorites_delete').length, 1);
  });

  test('it should delete favorite', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState).usersFavorites(favorite.data).build();
    await render(hbs`{{users-tab/filter/favorites}}`);
    await clickTrigger('.users-tab_filter_filter_select');
    assert.equal(findAll('.users-tab_filter_favorites_delete').length, 2);
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

    // get first filled icon
    await click(findAll('.rsa-icon-bin-1').shift());
    later(() => {
      assert.equal(findAll('.users-tab_filter_favorites_delete').length, 0);
      done();
    }, 1000);
  });
});
