import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/reset-risk-score', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders reset risk score', async function(assert) {
    await render(hbs`{{endpoint/reset-risk-score}}`);
    assert.equal(findAll('.reset-risk-score').length, 1, 'reset risk score component has rendered.');
  });

  test('reset risk score button is present and its disabled', async function(assert) {
    this.set('buttonType', 'button');
    this.set('selectedList', []);
    await render(hbs`{{endpoint/reset-risk-score buttonType=buttonType selectedList=selectedList}}`);
    assert.equal(findAll('.reset-score-button.is-disabled').length, 1, 'reset risk score button is disabled.');
  });

  test('reset risk score button is enabled', async function(assert) {
    this.set('selectedList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('buttonType', 'button');
    await render(hbs`{{endpoint/reset-risk-score buttonType=buttonType selectedList=selectedList}}`);
    assert.equal(findAll('reset-score-button.is-disabled').length, 0, 'reset risk score button is enabled.');
  });

  test('on click of reset button, confirmation dialog is opened', async function(assert) {
    this.set('selectedList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('buttonType', 'button');
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/reset-risk-score
        buttonType=buttonType
        selectedList=selectedList
      }}
    `);
    assert.equal(findAll('.reset-risk-score-dialog').length, 0, 'confirmation dialog is not opened');
    await click('.reset-score-button');
    assert.equal(findAll('.reset-risk-score-dialog').length, 1, 'confirmation dialog is opened');
  });

  test('it renders reset risk score button and info message is present', async function(assert) {
    this.set('buttonType', 'button');
    this.set('showResetScoreModal', true);
    this.set('isMaxResetRiskScoreLimit', true);
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/reset-risk-score
        buttonType=buttonType
        showResetScoreModal=showResetScoreModal
        isMaxResetRiskScoreLimit=isMaxResetRiskScoreLimit
      }}
    `);
    await click('.reset-score-button');
    assert.equal(findAll('.reset-risk-score-dialog').length, 1, 'confirmation dialog is opened');
    assert.equal(findAll('#modalDestination .reset-risk-score .max-limit-info').length, 1, 'Info message is present in Reset risk score');
  });

  test('on click of cancel button confirmation dialog is closed', async function(assert) {
    this.set('showResetScoreModal', true);
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/reset-risk-score
        showResetScoreModal=showResetScoreModal
      }}
    `);
    await click('.closeReset');
    assert.equal(this.get('showResetScoreModal'), false, 'confirmation dialog is closed');
  });

  test('on click of reset button, showResetScoreModal is set true', async function(assert) {
    this.set('buttonType', 'button');
    this.set('showResetScoreModal', false);
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/reset-risk-score
        buttonType=buttonType
        showResetScoreModal=showResetScoreModal
      }}
    `);
    await click('.reset-score-button');
    assert.equal(this.get('showResetScoreModal'), true, 'showResetScoreModal is set true');
    assert.equal(findAll('.reset-risk-score-dialog').length, 1, 'confirmation dialog is opened');
  });

});
