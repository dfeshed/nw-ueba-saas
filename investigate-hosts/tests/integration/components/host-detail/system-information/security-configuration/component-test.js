import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { findAll, find, render, click } from '@ember/test-helpers';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import { windows, mac } from '../../../state/overview.hostdetails';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';

let setState;


module('Integration | Component | host-detail/system-information', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    setState = (machine) => {
      const { overview } = machine;
      const state = Immutable.from({ endpoint: { overview } });
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  // Test for the number of tabs rendered based on the OS selected
  // Mac
  test('Security configuration content rendered for Mac', async function(assert) {
    setState(mac);
    await render(hbs`{{host-detail/system-information/security-configuration}}`);
    const numberOfSecurityConfigurationColumn = findAll('.security-configuration__column').length;
    assert.equal(numberOfSecurityConfigurationColumn, 7, 'Number of tabs rendered based on the OS selected');
  });
  // Windows
  test('Security configuration content rendered for Windows', async function(assert) {
    setState(windows);
    await render(hbs`{{host-detail/system-information/security-configuration}}`);
    const numberOfSecurityConfigurationColumn = findAll('.security-configuration__column').length;
    assert.equal(numberOfSecurityConfigurationColumn, 19, 'Number of tabs rendered based on the OS selected');
  });

  test('Security configuration radio buttons rendered', async function(assert) {
    setState(windows);
    await render(hbs`{{host-detail/system-information/security-configuration}}`);
    const numberOfRadioButtons = findAll('.security-configuration .rsa-form-radio-label').length;
    assert.equal(numberOfRadioButtons, 2, 'Number of radio buttons rendered');
    const defaultRadioButtonSelected = find('.security-configuration .rsa-form-radio-label:nth-of-type(1)').textContent.trim();
    assert.equal(defaultRadioButtonSelected, 'Alphabetical', 'Default sorting Alphabetical');
  });

  test('Security configuration radio buttons sorting on selecting radio button', async function(assert) {
    setState(windows);
    await render(hbs`{{host-detail/system-information/security-configuration}}`);
    const defaultRadioButtonSelected = find('.security-configuration .rsa-form-radio-label:nth-of-type(1)').textContent.trim();
    assert.equal(defaultRadioButtonSelected, 'Alphabetical', 'Default sorting Alphabetical');
    assert.equal(find('.security-configuration__column:nth-of-type(1) .securityConfigDisc span').textContent.trim(), 'Allow Access Datasource Domain', 'According to the alphabetic order');
    await click('.security-configuration .rsa-form-radio-label:nth-of-type(2)');
    assert.equal(find('.security-configuration__column:nth-of-type(1) .securityConfigDisc span').textContent.trim(), 'Warning On Zone Crossing', 'Order changes according to status');
  });
});