import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/reset-risk-score/modal', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders reset risk score modal', async function(assert) {
    await render(hbs`{{endpoint/reset-risk-score/modal}}`);
    assert.equal(findAll('.modal-content.reset-risk-score').length, 1, 'reset risk score component has rendered.');
  });

  test('renders limit message when isMaxResetRiskScoreLimit is set to true', async function(assert) {
    await render(hbs`{{endpoint/reset-risk-score/modal isMaxResetRiskScoreLimit=true}}`);
    assert.equal(findAll('.modal-content.reset-risk-score').length, 1, 'reset risk score component has rendered.');
    assert.equal(findAll('.max-limit-info').length, 1, 'limit info message is present');
  });

});
