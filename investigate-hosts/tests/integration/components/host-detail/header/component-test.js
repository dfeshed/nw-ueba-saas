import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { findAll, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { snapShot } from '../../../../data/data';

let setState;

module('Integration | Component | host detail header', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('Overview panel is not visible', async function(assert) {
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .isOverviewPanelVisible(false)
      .build();
    await render(hbs`{{host-detail/header}}`);
    assert.equal(findAll('.showOverviewPanel').length, 0, 'Overview panel is not visible');
  });

  test('Overview panel is visible', async function(assert) {
    new ReduxDataHelper((setState))
      .isOverviewPanelVisible(true)
      .build();
    await render(hbs `{{host-detail/header}}`);
    assert.equal(findAll('.showOverviewPanel').length, 1, 'Overview panel is visible');
  });
});