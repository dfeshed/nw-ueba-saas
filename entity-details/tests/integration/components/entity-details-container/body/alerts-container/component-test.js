import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, waitUntil, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { clickTrigger, selectChoose } from '../../../../../helpers/ember-power-select';

let setState;
const timeout = 10000;

module('Integration | Component | entity-details-container/body/alerts-container', function(hooks) {
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

  test('it should render alert container with sorting options', async function(assert) {
    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body/alerts-container}}`);

    assert.equal(this.element.textContent.replace(/\s/g, '').indexOf('AlertsSortBy:Severity'), 0);
    assert.equal(find('.ember-power-select-selected-item').innerText, 'SEVERITY');

  });

  test('it should show loader till alerts is not there', async function(assert) {
    new ReduxDataHelper(setState).alerts([]).build();

    await render(hbs`{{entity-details-container/body/alerts-container}}`);

    assert.equal(findAll('.entity-details_loader').length, 1);

  });

  test('it should be able to sort alets by name', async function(assert) {
    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body/alerts-container}}`);
    return waitUntil(() => this.$('.entity-details-container-body_alerts_list_content_alert_details_pill').length > 1, { timeout }).then(async() => {
      await clickTrigger('.entity-details-container-body_alerts_list_header_sort');
      assert.equal(findAll('.ember-power-select-option').length, 2);
      await selectChoose('.ember-power-select-trigger', 'Date');
      assert.equal(find('.ember-power-select-selected-item').innerText, 'DATE');
      return settled();
    });
  });
});
