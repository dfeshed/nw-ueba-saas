import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

let setState;
module('Integration | Component | entity-details-container/body/alert-details/details', function(hooks) {
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

  test('it should render details like ContributioninScore and Sources', async function(assert) {
    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body/alert-details/details}}`);
    assert.equal(findAll('.entity-details-container-body-alert-details_details').length, 1);
    assert.equal(findAll('.entity-details-container-body-alert-details_details_params').length, 1);
    assert.equal(findAll('.entity-details-container-body-alert-details_details_desc').length, 1);
    assert.equal(find('.entity-details-container-body-alert-details_details_desc').innerText.indexOf('Missing Translation'), -1);
  });
});
