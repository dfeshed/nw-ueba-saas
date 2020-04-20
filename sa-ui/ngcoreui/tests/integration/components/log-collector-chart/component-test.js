import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | log-collector-chart', function(hooks) {

  setupRenderingTest(hooks);

  test('log-collector-chart renders', async function(assert) {
    await render(hbs`{{log-collector-chart}}`);
    assert.equal(this.element.querySelectorAll('.rsa-chart').length, 1);

    const numberOfLineSeries = this.element.querySelectorAll('.rsa-chart .rsa-line-series').length;
    assert.equal(numberOfLineSeries, 3);

    assert.equal(this.element.querySelectorAll('.rsa-chart .rsa-y-axis').length, 1);
  });
});
