import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import dataIndex from '../../../../../../data/presidio';
import { patchFetch } from '../../../../../../helpers/patch-fetch';
import userAlerts from '../../../../../../data/presidio/user_alerts';
import _ from 'lodash';

let setState;

module('Integration | Component | entity-details-container/body/alert-details/header', function(hooks) {
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

  test('it should render alert header with alert name', async function(assert) {
    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body/alert-details/header}}`);
    assert.equal(findAll('.entity-details-container-body-alert-details_header').length, 1);
  });

  test('it should mark not a risk', async function(assert) {

    assert.expect(1);

    new ReduxDataHelper(setState).build();
    await render(hbs`{{entity-details-container/body/alert-details/header}}`);
    patchFetch((url) => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            assert.equal(url, '/presidio/api/alerts/0bd963d0-a0ae-4601-8497-b0c363becd1f');
            return dataIndex(url);
          }
        });
      });
    });
    click('.rsa-form-button');
  });

  test('it should mark as risk', async function(assert) {
    assert.expect(1);

    const alerts = _.map(userAlerts.data, (alert) => ({ ...alert, userScoreContribution: 0 }));
    new ReduxDataHelper(setState).alerts(alerts).build();
    await render(hbs`{{entity-details-container/body/alert-details/header}}`);
    patchFetch((url) => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            assert.equal(url, '/presidio/api/alerts/0bd963d0-a0ae-4601-8497-b0c363becd1f');
            return dataIndex(url);
          }
        });
      });
    });
    click('.rsa-form-button');
  });
});
