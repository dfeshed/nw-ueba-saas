import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | severity-bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  test('it should render empty severy bar', async function(assert) {
    await render(hbs`{{severity-bar}}`);
    assert.equal(this.element.textContent.replace(/\s/g, ''), 'CriticalHighMediumLow');
  });

  test('it should render severy bar for given severity', async function(assert) {

    this.set('data', {
      Critical: 10,
      High: 12,
      Medium: 11,
      Low: 4
    });

    await render(hbs`{{severity-bar data=data selected='Critical'}}`);
    assert.equal(this.element.textContent.replace(/\s/g, ''), '10Critical12High11Medium4Low');
    assert.equal(find('.selected').textContent.replace(/\s/g, ''), '10Critical', 'Should have selected severity');
  });

  test('it should render perform action on click', async function(assert) {
    assert.expect(2);
    this.set('data', {
      Critical: 10,
      High: 12,
      Medium: 11,
      Low: 4
    });

    this.set('updateSeverity', (severity) => {
      assert.equal(severity, 'Critical', 'Should have selected severity');
    });

    await render(hbs`{{severity-bar data=data updateSeverity=updateSeverity}}`);
    assert.equal(this.element.textContent.replace(/\s/g, ''), '10Critical12High11Medium4Low');
    click('.rsa-form-button');
  });
});
