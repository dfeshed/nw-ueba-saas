import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, find, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import details from '../../../../data/presidio/user_details';

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

    new ReduxDataHelper(setState).entityId({ entityId: 123, entityType: 'user' }).entityDetails(details.data[0]).build();

    await render(hbs`{{entity-details-container/body}}`);
    assert.equal(findAll('#ueba-iframe').length, 1, 'Should render ueba iframe');
  });

  test('it renders Alert container', async function(assert) {

    new ReduxDataHelper(setState).entityId({ entityId: 123, entityType: 'user' }).entityDetails(details.data[0]).build();
    this.set('isClassic', false);
    await render(hbs`{{entity-details-container/body isClassic=isClassic}}`);
    assert.equal(find('.entity-details-container-body').textContent.replace(/\s/g, ''), 'AlertListContainerAlertDetails', 'Should render Alert container');
  });

  test('it renders Indicator container if indicator id passed', async function(assert) {

    new ReduxDataHelper(setState).entityId({ entityId: 123, entityType: 'user' }).entityDetails(details.data[0]).alertId('alertId').indicatorId('Inc-1').build();
    this.set('isClassic', false);
    await render(hbs`{{entity-details-container/body isClassic=isClassic}}`);
    assert.equal(find('.entity-details-container-body').textContent.replace(/\s/g, ''), 'AlertListContainerIndicatorDetails', 'Should render Indicator container');
  });
});
