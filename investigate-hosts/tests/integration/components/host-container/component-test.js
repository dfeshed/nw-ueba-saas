import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import engineResolver from '../../../helpers/engine-resolver';
import { findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | host-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders host container', async function(assert) {
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.host-container').length, 1, 'host container rendered');
  });

  test('it renders host container detail', async function(assert) {
    new ReduxDataHelper(setState)
      .hasMachineId(true)
      .build();
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.host-container-detail').length, 1, 'host container detail rendered');
  });

  test('it renders host container list', async function(assert) {
    new ReduxDataHelper(setState)
      .hasMachineId(false)
      .build();
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.host-container-list').length, 1, 'host container list rendered');
  });
});