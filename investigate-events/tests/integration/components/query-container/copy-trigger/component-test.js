import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | Copy Trigger', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('renders the correct dom', async function(assert) {
    new ReduxDataHelper(setState).build();
    await render(hbs`
      {{query-container/copy-trigger}}
    `);
    assert.equal(findAll('.copy-trigger.is-disabled .rsa-icon-copy-1-lined').length, 1);
    assert.equal(find('.copy-trigger.is-disabled i').getAttribute('title').trim(), 'Click to copy will become available when filters are present.');
  });

  test('enables when pills are present', async function(assert) {
    new ReduxDataHelper(setState).pillsDataPopulated().build();
    await render(hbs`
      {{query-container/copy-trigger}}
    `);
    assert.equal(findAll('.copy-trigger.is-disabled').length, 0);
    assert.equal(findAll('.copy-trigger').length, 1);
    assert.equal(find('.copy-trigger i').getAttribute('title').trim(), "Click to copy query filters: a = 'x' && b = 'y'");
  });

});
