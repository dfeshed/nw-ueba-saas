import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click, triggerKeyEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-content-accordion', function(hooks) {
  setupRenderingTest(hooks);

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{#rsa-content-accordion}}foo{{/rsa-content-accordion}}`);
    const accordionCount = findAll('.rsa-content-accordion').length;
    assert.equal(accordionCount, 1);
  });

  test('it sets the label', async function(assert) {
    await render(hbs `{{#rsa-content-accordion label="bar"}}foo{{/rsa-content-accordion}}`);
    const label = find('h3').textContent;
    assert.notEqual(label.indexOf('bar'), -1);
  });

  test('it includes the proper classes when isCollapsed is true', async function(assert) {
    await render(hbs `{{#rsa-content-accordion isCollapsed=true}}foo{{/rsa-content-accordion}}`);
    const accordion = find('.rsa-content-accordion');
    assert.ok(accordion.classList.contains('is-collapsed'));
  });

  test('it toggles content visibility when clicked', async function(assert) {
    await render(hbs `{{#rsa-content-accordion isCollapsed=true}}foo{{/rsa-content-accordion}}`);
    const accordionCount = findAll('.is-collapsed').length;
    assert.equal(accordionCount, 1);
    await click('h3');
    const accordion = find('.rsa-content-accordion');
    assert.notOk(accordion.classList.contains('is-collapsed'));
  });

  test('it toggles content on Enter', async function(assert) {
    await render(hbs `{{#rsa-content-accordion isCollapsed=true}}foo{{/rsa-content-accordion}}`);
    const accordionCount = findAll('.is-collapsed').length;
    assert.equal(accordionCount, 1);
    await triggerKeyEvent('h3', 'keyup', 13);
    const accordion = find('.rsa-content-accordion');
    assert.notOk(accordion.classList.contains('is-collapsed'));
  });

  test('it toggles content on Space', async function(assert) {
    await render(hbs `{{#rsa-content-accordion isCollapsed=true}}foo{{/rsa-content-accordion}}`);
    const accordionCount = findAll('.is-collapsed').length;
    assert.equal(accordionCount, 1);
    await triggerKeyEvent('h3', 'keyup', 32);
    const accordion = find('.rsa-content-accordion');
    assert.notOk(accordion.classList.contains('is-collapsed'));
  });
});
