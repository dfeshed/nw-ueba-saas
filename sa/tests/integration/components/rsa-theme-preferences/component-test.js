import { getOwner } from '@ember/application';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import waitFor from 'sa/tests/helpers/wait-for';
import { applyPatch, revertPatch } from 'sa/tests/helpers/patch-reducer';
import { patchSocket, throwSocket } from 'sa/tests/helpers/patch-socket';
import { patchFlash } from 'sa/tests/helpers/patch-flash';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const labelSelector = '.rsa-form-radio-label';

moduleForComponent('rsa-theme-preferences', 'Integration | Component | rsa-theme-preferences', {
  integration: true,
  beforeEach() {
    const initState = { global: { preferences: { theme: 'LIGHT' } } };
    applyPatch(Immutable.from(initState));
    this.inject.service('redux');
    this.inject.service('i18n');
    this.inject.service('flash-messages');
    initialize(this);
  },
  afterEach() {
    revertPatch();
  }
});

test('includes group label with correct text value', function(assert) {
  this.render(hbs `{{rsa-theme-preferences}}`);
  const groupLabelSelector = '.rsa-form-radio-group-label';
  const groupLabel = this.$(groupLabelSelector);
  assert.equal(groupLabel.text(), 'Theme');
});

test('includes radio button with label for both light and dark theme', function(assert) {
  this.render(hbs `{{rsa-theme-preferences}}`);
  const labels = this.$(labelSelector);
  assert.equal(labels.length, 2);
  assert.equal(this.$(`${labelSelector}:eq(0)`).text().trim(), 'Dark');
  assert.equal(this.$(`${labelSelector}:eq(1)`).text().trim(), 'Light');
});

test('the correct radio button is selected', function(assert) {
  this.render(hbs `{{rsa-theme-preferences}}`);
  const darkRadioSelector = `${labelSelector}:eq(0) input[type=radio]`;
  const lightRadioSelector = `${labelSelector}:eq(1) input[type=radio]`;
  assert.equal(this.$(darkRadioSelector).prop('checked'), false);
  assert.equal(this.$(lightRadioSelector).prop('checked'), true);
  const darkRadioLabel = `${labelSelector}:eq(0)`;
  const lightRadioLabel = `${labelSelector}:eq(1)`;
  assert.ok(!this.$(darkRadioLabel).hasClass('checked'));
  assert.ok(this.$(lightRadioLabel).hasClass('checked'));
});

test('onclick the radio button should alter the active theme', function(assert) {
  assert.expect(7);

  this.render(hbs `{{rsa-theme-preferences}}`);

  patchSocket((method, modelName, query) => {
    assert.equal(method, 'setPreference');
    assert.equal(modelName, 'preferences');
    assert.deepEqual(query, {
      data: {
        themeType: 'DARK'
      }
    });
  });

  const darkRadioSelector = `${labelSelector}:eq(0) input[type=radio]`;
  const lightRadioSelector = `${labelSelector}:eq(1) input[type=radio]`;
  return waitFor(() => this.$(darkRadioSelector).trigger('click'))().then(() => {
    assert.equal(this.$(darkRadioSelector).prop('checked'), true);
    assert.equal(this.$(lightRadioSelector).prop('checked'), false);
    const darkRadioLabel = `${labelSelector}:eq(0)`;
    const lightRadioLabel = `${labelSelector}:eq(1)`;
    assert.ok(this.$(darkRadioLabel).hasClass('checked'));
    assert.ok(!this.$(lightRadioLabel).hasClass('checked'));
  });
});

test('onclick will display flash error when socket throws', function(assert) {
  assert.expect(6);

  this.render(hbs `{{rsa-theme-preferences}}`);

  throwSocket();

  patchFlash((flash) => {
    const translation = getOwner(this).lookup('service:i18n');
    const expectedError = translation.t('userPreferences.theme.error');
    assert.equal(flash.type, 'error');
    assert.equal(flash.message.string, expectedError);
  });

  const darkRadioSelector = `${labelSelector}:eq(0) input[type=radio]`;
  const lightRadioSelector = `${labelSelector}:eq(1) input[type=radio]`;
  return waitFor(() => this.$(darkRadioSelector).trigger('click'))().then(() => {
    assert.equal(this.$(darkRadioSelector).prop('checked'), true);
    assert.equal(this.$(lightRadioSelector).prop('checked'), false);
    const darkRadioLabel = `${labelSelector}:eq(0)`;
    const lightRadioLabel = `${labelSelector}:eq(1)`;
    assert.ok(this.$(darkRadioLabel).hasClass('checked'));
    assert.ok(!this.$(lightRadioLabel).hasClass('checked'));
  });
});
