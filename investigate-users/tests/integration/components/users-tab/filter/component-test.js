import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchFetch } from '../../../../helpers/patch-fetch';
import { Promise } from 'rsvp';

module('Integration | Component | users-tab/filter', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
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
    await this.$("button:contains('Reset')").click();
    return settled();
  });
});
