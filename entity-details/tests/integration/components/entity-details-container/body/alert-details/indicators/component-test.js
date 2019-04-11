import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, waitUntil, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

let setState;
const timeout = 10000;
module('Integration | Component | entity-details-container/body/alert-details/indicators', function(hooks) {
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
  test('it should render list of indicators', async function(assert) {
    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body/alert-details/indicators}}`);

    assert.equal(findAll('.entity-details-container-body-alert-details_indicators_flow_timeline_pill').length, 9);
  });

  test('it should scroll if left or right arrow clicks', async function(assert) {
    assert.expect(3);

    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body/alert-details/indicators}}`);

    return waitUntil(() => this.$('.rsa-icon-arrow-right-12-filled').length === 1, { timeout }).then(() => {

      assert.equal(find('.entity-details-container-body-alert-details_indicators_flow_timeline').scrollLeft, 0);
      click('.rsa-icon-arrow-right-12-filled');
      return waitUntil(() => find('.entity-details-container-body-alert-details_indicators_flow_timeline').scrollLeft > 20, { timeout }).then(() => {
        assert.ok(find('.entity-details-container-body-alert-details_indicators_flow_timeline').scrollLeft > 20);
        click('.rsa-icon-arrow-left-12-filled');
        return waitUntil(() => find('.entity-details-container-body-alert-details_indicators_flow_timeline').scrollLeft === 0, { timeout }).then(() => {
          assert.equal(find('.entity-details-container-body-alert-details_indicators_flow_timeline').scrollLeft, 0);
        });
      });
    });
  });
});
