import { module, test } from 'qunit';
import { normalizedState, normalizedStateExpanded } from './data';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../helpers/patch-flash';
import { patchSocket, throwSocket } from '../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { waitUntil, fillIn, find, click, render } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { selectors } from '../../forms/form-element/selectors';
import { fieldsetSync, formGroupExists, formGroup } from '../../forms/form-element/helpers';
import { labels } from './helpers';
import { Promise } from 'rsvp';

let label, input, options, selectedItem, legend, labelOne, radioOne, labelTwo, radioTwo;

const timeout = 10000;

module('Integration | Component | Respond Risk Scoring', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    patchReducer(this, normalizedStateExpanded);
    initialize(this.owner);
  });

  test('The form fields displays the risk scoring settings data', async function(assert) {
    assert.expect(34);

    await render(hbs`{{respond/risk-scoring}}`);

    ({ legend, labelOne, radioOne, labelTwo, radioTwo } = fieldsetSync(1));
    assert.equal(legend.textContent.trim(), labels(this, 'fileThresholdEnabled'));
    assert.equal(labelOne.textContent.trim(), labels(this, 'enabled'));
    assert.equal(radioOne.value, 'true');
    assert.equal(radioOne.getAttribute('aria-checked'), '');
    assert.equal(labelTwo.textContent.trim(), labels(this, 'disabled'));
    assert.equal(radioTwo.value, 'false');
    assert.equal(radioTwo.getAttribute('aria-checked'), null);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), labels(this, 'fileThreshold'));
    assert.equal(input.value, '80');

    ({ label, input } = await formGroup(2));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindow'));
    assert.equal(input.value, '24');

    ({ label, selectedItem, options } = await formGroup(3));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindowUnit'));
    assert.equal(label.classList.contains('sr-only'), true);

    assert.equal(selectedItem.textContent.trim(), labels(this, 'hours'));
    assert.equal(options.length, 2);
    assert.equal(options[0].textContent.trim(), labels(this, 'days'));
    assert.equal(options[1].textContent.trim(), labels(this, 'hours'));

    ({ legend, labelOne, radioOne, labelTwo, radioTwo } = fieldsetSync(2));
    assert.equal(legend.textContent.trim(), labels(this, 'hostThresholdEnabled'));
    assert.equal(labelOne.textContent.trim(), labels(this, 'enabled'));
    assert.equal(radioOne.value, 'true');
    assert.equal(radioOne.getAttribute('aria-checked'), '');
    assert.equal(labelTwo.textContent.trim(), labels(this, 'disabled'));
    assert.equal(radioTwo.value, 'false');
    assert.equal(radioTwo.getAttribute('aria-checked'), null);

    ({ label, input } = await formGroup(4));
    assert.equal(label.textContent.trim(), labels(this, 'hostThreshold'));
    assert.equal(input.value, '75');

    ({ label, input } = await formGroup(5));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindow'));
    assert.equal(input.value, '1');

    ({ label, selectedItem, options } = await formGroup(6));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindowUnit'));
    assert.equal(label.classList.contains('sr-only'), true);

    assert.equal(selectedItem.textContent.trim(), labels(this, 'days'));
    assert.equal(options.length, 2);
    assert.equal(options[0].textContent.trim(), labels(this, 'days'));
    assert.equal(options[1].textContent.trim(), labels(this, 'hours'));
  });

  test('clicking save will flash success message when update succeeds', async function(assert) {
    assert.expect(12);

    await render(hbs`{{respond/risk-scoring}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'updateRecord');
      assert.equal(modelName, 'risk-scoring-settings');
      assert.deepEqual(query, {
        data: [{
          type: 'HOST',
          threshold: 75,
          timeWindow: '1h',
          enabled: true
        }, {
          type: 'FILE',
          threshold: 80,
          timeWindow: '24h',
          enabled: true
        }]
      });
    });

    patchFlash((flash) => {
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, labels(this, 'updateSuccess'));
    });

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    const { select } = await formGroup(6);
    await selectChoose(select, labels(this, 'hours'));

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    await click(selectors.saveButton);

    await waitUntil(() => find(selectors.saveButton).disabled === true, { timeout });

    selectedItem = find(selectors.powerSelectItem);
    assert.equal(selectedItem.textContent.trim(), labels(this, 'hours'));
    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);
  });

  test('clicking save will flash error message when update fails', async function(assert) {
    assert.expect(8);

    await render(hbs`{{respond/risk-scoring}}`);

    throwSocket();

    patchFlash((flash) => {
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, labels(this, 'updateFailure'));
    });

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    const { select } = await formGroup(3);
    await selectChoose(select, labels(this, 'days'));

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    await click(selectors.saveButton);
    await Promise.resolve();

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);
  });

  test('onclick the icon will toggle form visibility', async function(assert) {
    patchReducer(this, normalizedState);

    assert.expect(6);

    await render(hbs`{{respond/risk-scoring}}`);

    const iconSelector = '[test-id=toggleRiskScoring]';
    assert.equal(find(selectors.formElement).classList.contains('hidden'), true);
    assert.equal(find(iconSelector).classList.contains('rsa-icon-arrow-down-8-filled'), false);
    assert.equal(find(iconSelector).classList.contains('rsa-icon-arrow-right-8-filled'), true);

    await click(iconSelector);

    assert.equal(find(selectors.formElement).classList.contains('hidden'), false);
    assert.equal(find(iconSelector).classList.contains('rsa-icon-arrow-down-8-filled'), true);
    assert.equal(find(iconSelector).classList.contains('rsa-icon-arrow-right-8-filled'), false);
  });

  test('onclick the file radio will toggle threshold & time window visibility', async function(assert) {
    assert.expect(24);

    await render(hbs`{{respond/risk-scoring}}`);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), labels(this, 'fileThreshold'));
    assert.equal(input.value, '80');

    ({ label, input } = await formGroup(2));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindow'));
    assert.equal(input.value, '24');

    ({ label, selectedItem } = await formGroup(3));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindowUnit'));
    assert.equal(selectedItem.textContent.trim(), labels(this, 'hours'));

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    ({ radioOne, radioTwo } = fieldsetSync(1));

    await click(radioTwo);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), labels(this, 'hostThreshold'));
    assert.equal(input.value, '75');

    ({ label, input } = await formGroup(2));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindow'));
    assert.equal(input.value, '1');

    ({ label, selectedItem } = await formGroup(3));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindowUnit'));
    assert.equal(selectedItem.textContent.trim(), labels(this, 'days'));

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    await click(radioOne);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), labels(this, 'fileThreshold'));
    assert.equal(input.value, '80');

    ({ label, input } = await formGroup(2));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindow'));
    assert.equal(input.value, '24');

    ({ label, selectedItem } = await formGroup(3));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindowUnit'));
    assert.equal(selectedItem.textContent.trim(), labels(this, 'hours'));

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);
  });

  test('onclick the host radio will toggle threshold & time window visibility', async function(assert) {
    assert.expect(27);

    await render(hbs`{{respond/risk-scoring}}`);

    ({ label, input } = await formGroup(4));
    assert.equal(label.textContent.trim(), labels(this, 'hostThreshold'));
    assert.equal(input.value, '75');

    ({ label, input } = await formGroup(5));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindow'));
    assert.equal(input.value, '1');

    ({ label, selectedItem } = await formGroup(6));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindowUnit'));
    assert.equal(selectedItem.textContent.trim(), labels(this, 'days'));

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    ({ radioOne, radioTwo } = fieldsetSync(2));

    await click(radioTwo);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), labels(this, 'fileThreshold'));
    assert.equal(input.value, '80');

    ({ label, input } = await formGroup(2));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindow'));
    assert.equal(input.value, '24');

    ({ label, selectedItem } = await formGroup(3));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindowUnit'));
    assert.equal(selectedItem.textContent.trim(), labels(this, 'hours'));

    assert.equal(formGroupExists(4), false);
    assert.equal(formGroupExists(5), false);
    assert.equal(formGroupExists(6), false);

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    await click(radioOne);

    ({ label, input } = await formGroup(4));
    assert.equal(label.textContent.trim(), labels(this, 'hostThreshold'));
    assert.equal(input.value, '75');

    ({ label, input } = await formGroup(5));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindow'));
    assert.equal(input.value, '1');

    ({ label, selectedItem } = await formGroup(6));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindowUnit'));
    assert.equal(selectedItem.textContent.trim(), labels(this, 'days'));

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);
  });

  test('onclick the reset button will undo file threshold & time window visibility', async function(assert) {
    assert.expect(36);

    await render(hbs`{{respond/risk-scoring}}`);

    ({ radioOne, radioTwo } = fieldsetSync(1));
    assert.equal(radioOne.getAttribute('aria-checked'), '');
    assert.equal(radioTwo.getAttribute('aria-checked'), null);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), labels(this, 'fileThreshold'));
    assert.equal(input.value, '80');

    ({ label, input } = await formGroup(2));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindow'));
    assert.equal(input.value, '24');

    ({ label, selectedItem } = await formGroup(3));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindowUnit'));
    assert.equal(selectedItem.textContent.trim(), labels(this, 'hours'));

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    await click(radioTwo);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), labels(this, 'hostThreshold'));
    assert.equal(input.value, '75');

    ({ label, input } = await formGroup(2));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindow'));
    assert.equal(input.value, '1');

    ({ label, selectedItem } = await formGroup(3));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindowUnit'));
    assert.equal(selectedItem.textContent.trim(), labels(this, 'days'));

    assert.equal(formGroupExists(4), false);
    assert.equal(formGroupExists(5), false);
    assert.equal(formGroupExists(6), false);

    ({ radioOne, radioTwo } = fieldsetSync(1));
    assert.equal(radioOne.getAttribute('aria-checked'), null);
    assert.equal(radioTwo.getAttribute('aria-checked'), '');

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    await click(selectors.resetButton);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), labels(this, 'fileThreshold'));
    assert.equal(input.value, '80');

    ({ label, input } = await formGroup(2));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindow'));
    assert.equal(input.value, '24');

    ({ label, selectedItem } = await formGroup(3));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindowUnit'));
    assert.equal(selectedItem.textContent.trim(), labels(this, 'hours'));

    assert.equal(formGroupExists(4), true);
    assert.equal(formGroupExists(5), true);
    assert.equal(formGroupExists(6), true);

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    ({ radioOne, radioTwo } = fieldsetSync(1));
    assert.equal(radioOne.getAttribute('aria-checked'), '');
    assert.equal(radioTwo.getAttribute('aria-checked'), null);
  });

  test('onclick the reset button will undo host threshold & time window visibility', async function(assert) {
    assert.expect(31);

    await render(hbs`{{respond/risk-scoring}}`);

    ({ radioOne, radioTwo } = fieldsetSync(2));
    assert.equal(radioOne.getAttribute('aria-checked'), '');
    assert.equal(radioTwo.getAttribute('aria-checked'), null);

    ({ label, input } = await formGroup(4));
    assert.equal(label.textContent.trim(), labels(this, 'hostThreshold'));
    assert.equal(input.value, '75');

    ({ label, input } = await formGroup(5));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindow'));
    assert.equal(input.value, '1');

    ({ label, selectedItem } = await formGroup(6));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindowUnit'));
    assert.equal(selectedItem.textContent.trim(), labels(this, 'days'));

    await click(radioTwo);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), labels(this, 'fileThreshold'));
    assert.equal(input.value, '80');

    ({ label, input } = await formGroup(2));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindow'));
    assert.equal(input.value, '24');

    ({ label, selectedItem } = await formGroup(3));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindowUnit'));
    assert.equal(selectedItem.textContent.trim(), labels(this, 'hours'));

    assert.equal(formGroupExists(4), false);
    assert.equal(formGroupExists(5), false);
    assert.equal(formGroupExists(6), false);

    ({ radioOne, radioTwo } = fieldsetSync(2));
    assert.equal(radioOne.getAttribute('aria-checked'), null);
    assert.equal(radioTwo.getAttribute('aria-checked'), '');

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    await click(selectors.resetButton);

    ({ label, input } = await formGroup(4));
    assert.equal(label.textContent.trim(), labels(this, 'hostThreshold'));
    assert.equal(input.value, '75');

    ({ label, input } = await formGroup(5));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindow'));
    assert.equal(input.value, '1');

    ({ label, selectedItem } = await formGroup(6));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindowUnit'));
    assert.equal(selectedItem.textContent.trim(), labels(this, 'days'));

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    ({ radioOne, radioTwo } = fieldsetSync(2));
    assert.equal(radioOne.getAttribute('aria-checked'), '');
    assert.equal(radioTwo.getAttribute('aria-checked'), null);
  });

  test('clicking radio to disable form fields will not immediately wipe out any wip values', async function(assert) {
    assert.expect(13);

    await render(hbs`{{respond/risk-scoring}}`);

    assert.equal(formGroupExists(4), true);
    assert.equal(formGroupExists(5), true);
    assert.equal(formGroupExists(6), true);

    ({ label, input } = await formGroup(4));
    assert.equal(label.textContent.trim(), labels(this, 'hostThreshold'));
    assert.equal(input.value, '75');

    await fillIn(input, '');

    ({ radioOne, radioTwo } = fieldsetSync(2));

    await click(radioTwo);

    assert.equal(formGroupExists(4), false);
    assert.equal(formGroupExists(5), false);
    assert.equal(formGroupExists(6), false);

    await click(radioOne);

    assert.equal(formGroupExists(4), true);
    assert.equal(formGroupExists(5), true);
    assert.equal(formGroupExists(6), true);

    ({ label, input } = await formGroup(4));
    assert.equal(label.textContent.trim(), labels(this, 'hostThreshold'));
    assert.equal(input.value, '');
  });

  test('clicking save will send the correct payload when radio disables file fields', async function(assert) {
    assert.expect(24);

    await render(hbs`{{respond/risk-scoring}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'updateRecord');
      assert.equal(modelName, 'risk-scoring-settings');
      assert.deepEqual(query, {
        data: [{
          type: 'HOST',
          threshold: 75,
          timeWindow: '1d',
          enabled: true
        }, {
          type: 'FILE',
          threshold: 80,
          timeWindow: '24h',
          enabled: false
        }]
      });
    });

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    ({ input } = await formGroup(1));
    await fillIn(input, '');

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    ({ radioOne, radioTwo } = fieldsetSync(1));

    await click(radioTwo);
    await click(selectors.saveButton);

    await waitUntil(() => find(selectors.saveButton).disabled === true, { timeout });
    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), labels(this, 'hostThreshold'));
    assert.equal(input.value, '75');

    ({ label } = await formGroup(2));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindow'));

    ({ label } = await formGroup(3));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindowUnit'));

    assert.equal(formGroupExists(4), false);
    assert.equal(formGroupExists(5), false);
    assert.equal(formGroupExists(6), false);

    await click(radioOne);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), labels(this, 'fileThreshold'));
    assert.equal(input.value, '80');

    ({ label } = await formGroup(2));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindow'));

    ({ label } = await formGroup(3));
    assert.equal(label.textContent.trim(), labels(this, 'fileTimeWindowUnit'));

    ({ label, input } = await formGroup(4));
    assert.equal(label.textContent.trim(), labels(this, 'hostThreshold'));
    assert.equal(input.value, '75');

    assert.equal(formGroupExists(5), true);
    assert.equal(formGroupExists(6), true);
  });

  test('clicking save will send the correct payload when radio disables host fields', async function(assert) {
    assert.expect(21);

    await render(hbs`{{respond/risk-scoring}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'updateRecord');
      assert.equal(modelName, 'risk-scoring-settings');
      assert.deepEqual(query, {
        data: [{
          type: 'HOST',
          threshold: 75,
          timeWindow: '1d',
          enabled: false
        }, {
          type: 'FILE',
          threshold: 80,
          timeWindow: '24h',
          enabled: true
        }]
      });
    });

    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    const { select } = await formGroup(6);
    await selectChoose(select, labels(this, 'hours'));

    assert.equal(find(selectors.saveButton).disabled, false);
    assert.equal(find(selectors.resetButton).disabled, false);

    ({ radioOne, radioTwo } = fieldsetSync(2));

    await click(radioTwo);
    await click(selectors.saveButton);

    await waitUntil(() => find(selectors.saveButton).disabled === true, { timeout });
    assert.equal(find(selectors.saveButton).disabled, true);
    assert.equal(find(selectors.resetButton).disabled, true);

    ({ label } = await formGroup(1));
    assert.equal(label.textContent.trim(), labels(this, 'fileThreshold'));

    assert.equal(formGroupExists(4), false);
    assert.equal(formGroupExists(5), false);
    assert.equal(formGroupExists(6), false);

    await click(radioOne);

    ({ label, input } = await formGroup(1));
    assert.equal(label.textContent.trim(), labels(this, 'fileThreshold'));
    assert.equal(input.value, '80');

    ({ label, input } = await formGroup(4));
    assert.equal(label.textContent.trim(), labels(this, 'hostThreshold'));
    assert.equal(input.value, '75');

    ({ label, input } = await formGroup(5));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindow'));
    assert.equal(input.value, '1');

    ({ label, selectedItem } = await formGroup(6));
    assert.equal(label.textContent.trim(), labels(this, 'hostTimeWindowUnit'));
    assert.equal(selectedItem.textContent.trim(), labels(this, 'days'));
  });
});
