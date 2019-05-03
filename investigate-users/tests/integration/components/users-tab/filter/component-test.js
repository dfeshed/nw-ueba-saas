import { module, test } from 'qunit';
import { setupRenderingTest, skip } from 'ember-qunit';
import { render, findAll, find, settled, fillIn, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchFetch } from '../../../../helpers/patch-fetch';
import { Promise } from 'rsvp';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | users-tab/filter', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
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
    await render(hbs`{{users-tab/filter}}`);
    assert.equal(findAll('.users-tab_filter_options').length, 1);
    assert.equal(findAll('.users-tab_filter_filter').length, 1);
    assert.equal(findAll('.users-tab_filter_favorites').length, 1);
    assert.equal(find('.users-tab_filter_user').textContent.replace(/\s/g, ''), 'RiskyUsers(0)WatchlistUsers(0)AdminUsers(0)');
  });

  test('it can save as filter for cancel', async function(assert) {
    const done = assert.async();
    await render(hbs`
      <div id='modalDestination'></div>
      {{users-tab/filter}}
    `);
    await this.$("button:contains('Add')").click();
    return waitUntil(() => this.$('.rsa-application-modal.is-open').length === 1).then(() => {
      assert.equal(find('.users-tab_filter_controls_save-as-favorites_name_label').textContent.replace(/\s/g, ''), 'FilterName:');
      this.$("button:contains('Cancel')").click();
      return waitUntil(() => this.$('.rsa-application-modal.is-open').length === 0).then(() => {
        done();
      });
    });
  });

  // TODO: refactor to not make a bunch of requests
  skip('it can save as filter for save', async function(assert) {
    const done = assert.async();
    await render(hbs`
      <div id='modalDestination'></div>
      {{users-tab/filter}}
    `);
    await this.$("button:contains('Add')").click();
    return waitUntil(() => this.$('.rsa-application-modal.is-open').length === 1).then(() => {
      assert.equal(find('.users-tab_filter_controls_save-as-favorites_name_label').textContent.replace(/\s/g, ''), 'FilterName:');
      fillIn('.ember-text-field', 'TestFilter');
      return settled().then(() => {
        this.$("button:contains('Save')").click();
        return waitUntil(() => this.$('.rsa-application-modal.is-open').length === 0).then(() => {
          done();
        });
      });
    });
  });
});
