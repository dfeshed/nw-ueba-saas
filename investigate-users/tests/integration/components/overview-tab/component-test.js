import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | overview-tab', function(hooks) {
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
    await render(hbs`{{overview-tab}}`);
    assert.equal(findAll('.user-overview-tab_upper_users').length, 1);
    assert.equal(findAll('.user-overview-tab_upper_alerts').length, 1);
    assert.equal(findAll('.user-overview-tab_lower_users').length, 1);
    assert.equal(findAll('.user-overview-tab_lower_alerts').length, 1);
  });
});
