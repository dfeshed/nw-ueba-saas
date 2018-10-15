import { find, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchFetch } from '../../../../helpers/patch-fetch';

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
    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return {};
          }
        });
      });
    });
  });

  test('it should render alert tab body and should have header', async function(assert) {
    await render(hbs`{{alerts-tab/body}}`);
    assert.equal(find('.severity-bar').textContent.replace(/\s/g, ''), 'CriticalHighLowMedium');
  });

  test('it should render alert tab body and should have header with severity bar', async function(assert) {
    assert.expect(3);
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
    window.URL.createObjectURL = () => {
      assert.ok(true, 'This function supposed to be called for altert export');
    };
    await this.$("button:contains('Export')").click();
  });

  test('it should render alert tab body and should loader if data is not there', async function(assert) {
    await render(hbs`{{alerts-tab/body}}`);
    assert.equal(find('.rsa-loader__text').textContent.replace(/\s/g, ''), 'Loading');
  });

});
