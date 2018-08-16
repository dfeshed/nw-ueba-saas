import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-risk-score', function(hooks) {
  setupRenderingTest(hooks);

  test('it includes the proper class', async function(assert) {
    await render(hbs`{{rsa-risk-score}}`);
    assert.equal(this.$('.rsa-risk-score').length, 1);
  });

  test('it sets the low circle stroke and class when size is small', async function(assert) {
    await render(hbs`{{rsa-risk-score score=10 size='small' }}`);
    assert.equal(this.element.textContent.trim(), 10);
    assert.equal(this.$('.is-small').length, 1);
    assert.equal(this.$('.is-low').length, 1);
  });

  test('it sets the medium circle stroke and class when size is small', async function(assert) {
    await render(hbs`{{rsa-risk-score score=40 size='small' }}`);
    assert.equal(this.element.textContent.trim(), 40);
    assert.equal(this.$('.is-small').length, 1);
    assert.equal(this.$('.is-medium').length, 1);
  });

  test('it sets the high circle stroke and class when size is large', async function(assert) {
    await render(hbs`{{rsa-risk-score score=70 size='large' }}`);
    assert.equal(this.element.textContent.trim(), 70);
    assert.equal(this.$('.is-large').length, 1);
    assert.equal(this.$('.is-high').length, 1);
  });

  test('it sets the danger circle stroke', async function(assert) {
    await render(hbs`{{rsa-risk-score score=100 size='large' radius=30}}`);
    assert.equal(this.element.textContent.trim(), 100);
    assert.equal(this.$('.is-large').length, 1);
    assert.equal(this.$('.is-danger').length, 1);
  });

});
