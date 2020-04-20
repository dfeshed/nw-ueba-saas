import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | Console Panel Progress Bar', function(hooks) {

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
    new ReduxDataHelper(setState).queryStats().queryStatsIsPartiallyComplete().build();
    await render(hbs`
      {{query-container/console-panel/progress-bar progress="50" isDisabled=false}}
    `);
    assert.equal(findAll('.progress-bar .current-progress').length, 1);
    assert.equal(find('.progress-bar .current-progress').getAttribute('style').trim(), 'width:1%;');
    assert.equal(find('.progress-bar').getAttribute('title').trim(), 'Query 1% completed');
  });

});
