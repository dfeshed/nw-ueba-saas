import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, click, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import favorite from '../../../../../data/presidio/favorite_filter';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

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
  });

  test('it renders', async function(assert) {
    await render(hbs`{{users-tab/filter/favorites}}`);
    assert.equal(find('.users-tab_filter_favorites').textContent.replace(/\s/g, ''), 'Favorites');
  });

  test('it renders all favorites', async function(assert) {
    new ReduxDataHelper(setState).usersFavorites(favorite.data).build();
    const done = assert.async();
    await render(hbs`{{users-tab/filter/favorites}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length, 2);
    click('.rsa-form-button');
    return settled().then(() => {
      assert.equal(findAll('.rsa-form-button-clicked').length, 1);
      done();
    });
  });
});
