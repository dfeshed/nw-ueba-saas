import { find, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | alerts-tab/body', function(hooks) {
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

  test('it should render alert tab body and shoud have header', async function(assert) {
    await render(hbs`{{alerts-tab/body}}`);
    assert.equal(find('.severity-bar').textContent.replace(/\s/g, ''), 'CriticalHighLowMedium');
  });

  test('it should render alert tab body and shoud have header with severity bar', async function(assert) {
    new ReduxDataHelper(setState).alertsSeverity({
      total_severity_count: {
        Critical: 10,
        High: 12,
        Low: 4,
        Medium: 2
      }
    }).build();
    await render(hbs`{{alerts-tab/body}}`);
    assert.equal(find('.severity-bar').textContent.replace(/\s/g, ''), '10Critical12High4Low2Medium');
    assert.equal(find('.alerts-tab_body_body-table_header').textContent.replace(/\s/g, ''), 'AlertNameEntityNameStartTimeIndicatorCountStatus');
  });

  test('it should render alert tab body and shoud loader if data is not there', async function(assert) {
    await render(hbs`{{alerts-tab/body}}`);
    assert.equal(find('.rsa-loader__text').textContent.replace(/\s/g, ''), 'Loading');
  });

});
