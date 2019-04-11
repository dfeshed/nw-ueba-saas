import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

let setState;

module('Integration | Component | entity-details-container/body/alerts-container/alert-pill', function(hooks) {
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

  test('it should render entity details container body', async function(assert) {
    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body/alerts-container/alert-pill}}`);

    assert.equal(this.element.textContent.replace(/\s/g, ''), '+Missingtranslation:investigateUsers.alerts.alertNames..name|un44fin44');
    assert.equal(findAll('.entity-details-container-body_alerts_list_content_alert_details_pill').length, 1);
    assert.equal(findAll('.entity-details-container-body_alerts_list_content_alert_details_pill_details').length, 1);
    assert.equal(findAll('.entity-details-container-body_alerts_list_content_alert_details_pill_details_name').length, 1);
    assert.equal(findAll('.entity-details-container-body_alerts_list_content_alert_details_pill_details_time').length, 1);
    assert.equal(findAll('.entity-details-container-body_alerts_list_content_alert_details_pill_rating_score').length, 1);
  });
});
