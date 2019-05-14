import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | process-item', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  const data = {
    processName: 'process1',
    machineCount: '100',
    riskScore: 60,
    events: {
      hasNetworkEvent: true,
      hasFileEvent: false,
      hasRegistryEvent: true
    }
  };
  test('it renders static legends', async function(assert) {
    await render(hbs`{{process-item}}`);
    assert.equal(findAll('.process-item-container').length, 1, 'Row component renders.');
    assert.equal(findAll('.process-item-container div').length, 5, 'Component has 5 sub items.');
    assert.equal(findAll('.risk-score').length, 1, 'risk score element is present.');
    assert.equal(findAll('.process-name').length, 1, 'process-name box is rendered');
    assert.equal(findAll('.process-icon').length, 1, 'process-icon is present.');
    assert.equal(findAll('.machine-count').length, 1, 'machine count element is displayed.');
    assert.equal(findAll('.events').length, 1, 'events element is present.');
  });

  test('displays risk score, as per the data passed to the component', async function(assert) {
    this.set('data', data);
    await render(hbs`{{process-item data=data}}`);
    assert.equal(findAll('.risk-score').length, 1, 'risk score element is present.');
    assert.equal(findAll('.risk-score')[0].innerText, 60, 'risk score is displayed properly.');
  });

  test('process name on select, toggles isProcessSelected and calls external function', async function(assert) {
    assert.expect(2);
    this.set('data', data);
    this.set('onSelection', () => {
      assert.ok(true);
    });
    await render(hbs`{{process-item data=data onSelection=onSelection}}`);
    await click('.process-name');
    assert.ok(findAll('.process-name')[0].classList.contains('selected'));
  });

  test('machine count is displayed, as is passed from data', async function(assert) {
    this.set('data', data);
    await render(hbs`{{process-item data=data}}`);
    assert.equal(findAll('.machine-count')[0].innerText.substr(1, 4).trim(), '100', 'machine count is displayed aptly.');
  });

  test('Events icons appear, as per the events flags', async function(assert) {
    this.set('data', data);
    await render(hbs`{{process-item data=data}}`);
    assert.equal(findAll('.events .network-event .rsa-icon-network').length, 1, 'Network event icon is present');
    assert.equal(findAll('.events .file-event .rsa-icon-common-file-empty').length, 1, 'File event icon is present');
    assert.equal(findAll('.events .registry-event .rsa-icon-cell-border-bottom').length, 1, 'Registry event icon is present');
    assert.equal(findAll('.events .file-event')[0].classList.contains('disabled'), true, 'File event icon is disabled, as the process has no file event.');
  });
});
