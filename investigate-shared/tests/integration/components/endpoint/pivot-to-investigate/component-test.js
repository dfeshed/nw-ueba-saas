import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/pivot-to-investigate', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Renders the pivot to investigate button', async function(assert) {
    await render(hbs`{{endpoint/pivot-to-investigate}}`);
    assert.equal(findAll('.pivot-to-investigate-button').length, 1, 'Pivot to investigate has rendered.');
    assert.equal(findAll('.pivot-to-investigate-button')[0].textContent.trim(), 'Pivot to Investigate', 'Pivot to Investigate button text verified.');
  });

  test('Enabled pivot to investigate button', async function(assert) {
    await render(hbs`{{endpoint/pivot-to-investigate selectedFilesList=1}}`);
    assert.equal(findAll('.pivot-to-investigate-button')[0].classList.contains('is-disabled'), false, 'Pivot-to-investigate Button is enabled for single selection');
  });

  test('Pivot to investigate button disabled', async function(assert) {
    await render(hbs`{{endpoint/pivot-to-investigate disabled=true}}`);
    assert.equal(findAll('.pivot-to-investigate-button')[0].classList.contains('is-disabled'), true, 'Pivot-to-investigate Button is disabled.');
  });
});