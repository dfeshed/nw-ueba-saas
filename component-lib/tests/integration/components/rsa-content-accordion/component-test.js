import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-content-accordion', function(hooks) {
  setupRenderingTest(hooks);

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{#rsa-content-accordion}}foo{{/rsa-content-accordion}}`);
    const accordionCount = this.$().find('.rsa-content-accordion').length;
    assert.equal(accordionCount, 1);
  });

  test('it sets the label', async function(assert) {
    await render(hbs `{{#rsa-content-accordion label="bar"}}foo{{/rsa-content-accordion}}`);
    const label = this.$().find('h3').text();
    assert.notEqual(label.indexOf('bar'), -1);
  });

  test('it includes the proper classes when isCollapsed is true', async function(assert) {
    await render(hbs `{{#rsa-content-accordion isCollapsed=true}}foo{{/rsa-content-accordion}}`);
    const accordion = this.$().find('.rsa-content-accordion').first();
    assert.ok(accordion.hasClass('is-collapsed'));
  });

  test('it toggles content visibility when clicked', async function(assert) {
    await render(hbs `{{#rsa-content-accordion isCollapsed=true}}foo{{/rsa-content-accordion}}`);
    const accordionCount = this.$().find('.is-collapsed').length;
    assert.equal(accordionCount, 1);
    this.$().find('h3').click();
    const accordion = this.$().find('.rsa-content-accordion');
    assert.notOk(accordion.hasClass('is-collapsed'));
  });
});
