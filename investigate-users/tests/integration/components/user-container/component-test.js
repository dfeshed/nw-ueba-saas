import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;
module('Integration | Component | user-container', function(hooks) {
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
    new ReduxDataHelper(setState).build();
    await render(hbs`{{user-container}}`);
    assert.equal(findAll('.user-overview-tab_upper_users').length, 1);
    assert.equal(findAll('.user-overview-tab_upper_alerts').length, 1);
    assert.equal(findAll('.user-overview-tab_lower_users').length, 1);
    assert.equal(findAll('.user-overview-tab_lower_alerts').length, 1);
  });
});
