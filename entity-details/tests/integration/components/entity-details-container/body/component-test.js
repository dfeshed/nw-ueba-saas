import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | entity-details-container/body', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('entity-details')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('helper:mount', function() {});
  });

  test('it renders', async function(assert) {

    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body}}`);
    assert.equal(findAll('.entity-details-container-body').length, 1, 'Should render entity container body');
  });

  test('it renders Alert container', async function(assert) {

    new ReduxDataHelper(setState).build();
    await render(hbs`{{entity-details-container/body}}`);
    assert.equal(findAll('.entity-details-container-body_alerts_list').length, 1);
    assert.equal(findAll('.entity-details-container-body_details').length, 1);
  });

  test('it renders Indicator container if indicator id passed', async function(assert) {

    new ReduxDataHelper(setState).build();
    await render(hbs`{{entity-details-container/body}}`);
    assert.equal(findAll('.entity-details-container-body_alerts_list').length, 1);
    assert.equal(findAll('.entity-details-container-body_details').length, 1);
  });
});