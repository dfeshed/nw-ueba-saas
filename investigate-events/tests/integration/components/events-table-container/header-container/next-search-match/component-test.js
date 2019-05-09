import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { click, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | clear-search', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('renders next-search-match when linear', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState).build();

    this.set('_toSend', (action, argToPass) => {
      assert.equal(action, 'setSearchScroll');
      assert.equal(argToPass, 1);
    });

    await render(hbs`{{events-table-container/header-container/next-search-match _toSend=_toSend searchScrollIndex=0 searchMatchesCount=5}}`);
    click('.next-search-trigger .rsa-icon-arrow-circle-down-1-filled');
  });

  test('renders next-search-match when looping', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState).build();

    this.set('_toSend', (action, argToPass) => {
      assert.equal(action, 'setSearchScroll');
      assert.equal(argToPass, 0);
    });

    await render(hbs`{{events-table-container/header-container/next-search-match _toSend=_toSend searchScrollIndex=4 searchMatchesCount=5}}`);
    click('.next-search-trigger .rsa-icon-arrow-circle-down-1-filled');
  });
});
