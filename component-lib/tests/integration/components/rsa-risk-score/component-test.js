import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-risk-score', function(hooks) {
  setupRenderingTest(hooks);

  test('it should not display risk score when risk score is not specified', async function(assert) {
    await render(hbs`{{rsa-risk-score}}`);
    assert.equal(findAll('.rsa-risk-score').length, 1);
    assert.notOk(this.element.textContent.trim(), 'Should not display risk score');
  });

  test('it sets the low circle stroke and class when size is small', async function(assert) {
    await render(hbs`{{rsa-risk-score score=10 size='small' }}`);
    assert.equal(this.element.textContent.trim(), 10);
    assert.equal(findAll('.is-small').length, 1);
    assert.equal(findAll('.is-low').length, 1);
  });

  test('it sets the medium circle stroke and class when size is small', async function(assert) {
    await render(hbs`{{rsa-risk-score score=69 size='small' }}`);
    assert.equal(this.element.textContent.trim(), 69);
    assert.equal(findAll('.is-small').length, 1);
    assert.equal(findAll('.is-medium').length, 1);
  });

  test('it sets the high circle stroke and class when size is large', async function(assert) {
    await render(hbs`{{rsa-risk-score score=70 size='large' }}`);
    assert.equal(this.element.textContent.trim(), 70);
    assert.equal(findAll('.is-large').length, 1);
    assert.equal(findAll('.is-high').length, 1);
  });

  test('it sets the danger circle stroke', async function(assert) {
    await render(hbs`{{rsa-risk-score score=100 size='large' radius=30}}`);
    assert.equal(this.element.textContent.trim(), 100);
    assert.equal(findAll('.is-large').length, 1);
    assert.equal(findAll('.is-danger').length, 1);
  });

});
