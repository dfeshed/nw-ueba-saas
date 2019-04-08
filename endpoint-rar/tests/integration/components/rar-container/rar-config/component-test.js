import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, fillIn, click, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../../helpers/vnext-patch';

import { revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | rar-container/rar-config', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
  });
  hooks.afterEach(function() {
    revertPatch();
  });

  test('rar-config component content renders', async function(assert) {
    const rarConfig = {
      enabled: true,
      esh: 'esh-domain',
      servers: [
        {
          address: 'localhost',
          httpsPort: 443,
          httpsBeaconIntervalInSeconds: 900
        }
      ],
      address: 'localhost',
      httpsPort: 443,
      httpsBeaconIntervalInSeconds: 15
    };

    new ReduxDataHelper(setState).defaultRARConfig(rarConfig).build();

    await render(hbs`{{rar-container/rar-config}}`);

    assert.equal(find('.esh-name input').value, 'esh-domain', 'ESH value rendered');
    assert.equal(find('.host-ip input').value, 'localhost', 'ESH value rendered');
    assert.equal(find('.host-port input').value, '443', 'ESH value rendered');
    assert.equal(find('.beacon-interval-value input').value, '15', 'ESH value rendered');
    assert.equal(find('.beacon-interval-unit').textContent.trim(), 'mins', 'Beacon interval value rendered');
    assert.equal(find('.button-wrapper .is-primary span').textContent.trim(), 'Save configuration', 'Save configuration button present');
    assert.equal(find('.button-wrapper .is-standard span').textContent.trim(), 'Cancel', 'Cancel button present');
  });

  test('Save configuration button is disabled when config fields are empty', async function(assert) {
    const rarConfig = {
      enabled: true,
      esh: '',
      address: 'localhost',
      httpsPort: 443,
      httpsBeaconIntervalInSeconds: 15
    };

    new ReduxDataHelper(setState).defaultRARConfig(rarConfig).build();

    await render(hbs`{{rar-container/rar-config}}`);

    assert.equal(findAll('.button-wrapper .is-primary.is-disabled').length, 1, 'Save configuration button is disabled');
  });

  test('rar-config Error message for ESH Name', async function(assert) {
    const rarConfig = {
      esh: 'esh-domain test',
      address: 'localhost',
      httpsPort: 443,
      httpsBeaconIntervalInSeconds: 15
    };

    new ReduxDataHelper(setState).defaultRARConfig(rarConfig).build();

    await render(hbs`{{rar-container/rar-config}}`);
    await click('.button-wrapper .is-primary button');
    assert.equal(find('.esh-name .input-error').textContent.trim(), 'Enter a valid hostname.', 'ESH error message rendered');
    assert.equal(find('.host-ip input').value, 'localhost', 'ESH value rendered');
    assert.equal(find('.host-port input').value, '443', 'ESH value rendered');
    assert.equal(find('.beacon-interval-value input').value, '15', 'ESH value rendered');
    assert.equal(find('.beacon-interval-unit').textContent.trim(), 'mins', 'Beacon interval value rendered');
    assert.equal(find('.button-wrapper .is-primary span').textContent.trim(), 'Save configuration', 'Save configuration button present');
    assert.equal(find('.button-wrapper .is-standard span').textContent.trim(), 'Cancel', 'Cancel button present');
  });

  test('rar-config Error message for host address', async function(assert) {
    const rarConfig = {
      esh: 'esh-domain',
      address: 'localhost 32423',
      httpsPort: 443,
      httpsBeaconIntervalInSeconds: 15
    };

    new ReduxDataHelper(setState).defaultRARConfig(rarConfig).build();

    await render(hbs`{{rar-container/rar-config}}`);
    await click('.button-wrapper .is-primary button');
    assert.equal(find('.host-ip .input-error').textContent.trim(), 'Enter valid IP address or hostname.', 'Host address error message rendered');
  });

  test('rar-config Error message for host port', async function(assert) {
    const rarConfig = {
      esh: 'esh-domain',
      address: 'localhost',
      httpsPort: '44 3',
      httpsBeaconIntervalInSeconds: 15
    };

    new ReduxDataHelper(setState).defaultRARConfig(rarConfig).build();

    await render(hbs`{{rar-container/rar-config}}`);
    await click('.button-wrapper .is-primary button');
    assert.equal(find('.host-port .input-error').textContent.trim(), 'Enter valid port number.', 'Host port error message rendered');
  });

  test('rar-config Error message for beacon interval', async function(assert) {
    const rarConfig = {
      esh: 'esh-domain',
      address: 'localhost',
      httpsPort: 443,
      httpsBeaconIntervalInSeconds: 150000
    };

    new ReduxDataHelper(setState).defaultRARConfig(rarConfig).build();

    await render(hbs`{{rar-container/rar-config}}`);
    await click('.button-wrapper .is-primary button');
    assert.equal(find('.beacon-interval-value .input-error').textContent.trim(), 'Interval value ranges from 60-1440 minutes.', 'Invalid beacon interval error message rendered');
  });

  test('rar-config Error message for beacon interval', async function(assert) {
    const defaultRARConfig = {
      esh: 'esh-domain',
      address: 'localhost',
      httpsPort: 443,
      httpsBeaconIntervalInSeconds: 150000
    };
    const initialRarConfig = {
      esh: 'esh-domain',
      address: 'localhost',
      httpsPort: 443,
      httpsBeaconIntervalInSeconds: 150000
    };

    new ReduxDataHelper(setState).defaultRARConfig(defaultRARConfig).initialRARConfig(initialRarConfig).build();

    await render(hbs`{{rar-container/rar-config}}`);
    await fillIn('.host-ip input', 'localhostOne');
    assert.equal(find('.host-ip input').value, 'localhostOne', 'Edited value present');
    await click('.button-wrapper .is-standard button');
    assert.equal(find('.host-ip input').value, 'localhost', 'Cancel brings back the initial value');
  });

  test('info icon present', async function(assert) {
    await render(hbs`{{rar-container/rar-config}}`);
    assert.equal(findAll('i.rsa-icon-information-circle-filled').length, 1, 'information icon present');
    await triggerEvent('i.rsa-icon-information-circle-filled', 'mouseover');
    assert.equal(find('.tool-tip-value').textContent.trim(), 'ESH is a hostname which can be resolved only within the specified network.', 'information icon present');
  });
});