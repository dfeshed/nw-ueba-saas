import { module, test } from 'qunit';
import { normalizedState, normalizedStateExpanded } from './data';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../helpers/patch-flash';
import { patchSocket, throwSocket } from '../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { find, click, settled, render } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { selectors } from '../../forms/form-element/selectors';
import { formGroup } from '../../forms/form-element/helpers';
import { labels } from './helpers';

let label, input, options, selectedItem;

module('Integration | Component | Respond Risk Scoring', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    patchReducer(this, normalizedStateExpanded);
    initialize(this.owner);
  });

  test('The form fields displays the risk scoring settings data', async function(assert) {
    assert.expect(20);

    await render(hbs`{{respond/risk-scoring}}`);

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
          timeWindow: '1h'
        }, {
          type: 'FILE',
          threshold: 80,
          timeWindow: '24h'
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

    return settled().then(async() => {
      const selectedItem = find(selectors.powerSelectItem);
      assert.equal(selectedItem.textContent.trim(), labels(this, 'hours'));
      assert.equal(find(selectors.saveButton).disabled, true);
      assert.equal(find(selectors.resetButton).disabled, true);
    });
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

    return settled().then(async() => {
      assert.equal(find(selectors.saveButton).disabled, true);
      assert.equal(find(selectors.resetButton).disabled, true);
    });
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
});
