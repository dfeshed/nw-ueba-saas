import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | entity-details-container', function(hooks) {

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('entity-details')
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
    await render(hbs`{{entity-details-container entityId='123' entityType='user' alertId='0bd963d0-a0ae-4601-8497-b0c363becd1f' indicatorId='8614aa7f-c8ee-4824-9eaf-e0bb199cd006'}}`);
    assert.equal(findAll('.entity-details-container-header').length, 1);
  });
});
