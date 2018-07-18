import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/pivot-to-investigate/button', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Pivot to investigate button is rendered', async function(assert) {
    await render(hbs`{{endpoint/pivot-to-investigate/button}}`);
    assert.equal(findAll('.pivot-to-investigate-button').length, 1, 'Pivot to investigate button has rendered.');
  });

  test('Pivot to investigate button is enabled', async function(assert) {
    await render(hbs`{{endpoint/pivot-to-investigate/button disabled=false}}`);
    assert.equal(findAll('.pivot-to-investigate-button')[0].classList.contains('is-disabled'), false, 'Pivot-to-investigate Button is enabled.');
  });

  test('Pivot to investigate button is disabled', async function(assert) {
    await render(hbs`{{endpoint/pivot-to-investigate/button disabled=true}}`);
    assert.equal(findAll('.pivot-to-investigate-button')[0].classList.contains('is-disabled'), true, 'Pivot-to-investigate Button is disabled.');
  });

  test('Pivot to investigate button as Icon only', async function(assert) {
    await render(hbs`{{endpoint/pivot-to-investigate/button showOnlyIcons=true}}`);
    assert.equal(findAll('.pivot-to-investigate-button')[0].textContent.trim(), '', 'Pivot to Investigate Button appears as icon only, has no text.');
    assert.equal(findAll('.pivot-to-investigate-button .rsa-form-button .rsa-icon').length, 1, 'Pivot to Investigate Button has rsa-icon class.');
  });

  test('Pivot to investigate button with title', async function(assert) {
    await render(hbs`{{endpoint/pivot-to-investigate/button showOnlyIcons=false}}`);
    assert.equal(findAll('.pivot-to-investigate-button')[0].textContent.trim(), 'Pivot to Investigate', 'Pivot to Investigate button has title.');
    assert.equal(findAll('.pivot-to-investigate-button .rsa-form-button .rsa-icon').length, 1, 'Pivot to Investigate Button has rsa-icon class.');
  });


  test('Pivot to investigate click function', async function(assert) {
    this.set('defaultAction', function() {
      assert.ok('External function called on click of button');
    });
    await render(hbs`{{endpoint/pivot-to-investigate/button defaultAction=(action defaultAction)}}`);
    assert.equal(findAll('.pivot-to-investigate-button').length, 1, 'Pivot to investigate button is present.');
    await click('.pivot-to-investigate-button .rsa-form-button');
  });

});
