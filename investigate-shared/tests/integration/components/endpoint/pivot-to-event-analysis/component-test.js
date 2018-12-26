import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, settled, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/pivot-to-event-analysis', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it renders the two button', async function(assert) {
    await render(hbs`{{endpoint/pivot-to-event-analysis}}`);
    assert.equal(findAll('.rsa-form-button').length, 2, 'it renders endpoint load-more');
  });

  test('it renders menu buttons correctly', async function(assert) {
    this.set('selections', new Array(1));
    await render(hbs`{{endpoint/pivot-to-event-analysis selections=selections}}`);
    await click('.rsa-split-dropdown .rsa-form-button');
    const selector = '.button-menu';
    const buttonMenu = find(selector);
    return settled().then(async() => {
      assert.ok(buttonMenu.classList.contains('expanded'));
      assert.equal(findAll(`${selector} li`).length, 4, 'expected to render 4 menu options');
    });
  });

  test('buttons are disabled if more than one selections', async function(assert) {
    this.set('selections', new Array(10));
    await render(hbs`{{endpoint/pivot-to-event-analysis selections=selections}}`);
    const selector = '.rsa-split-dropdown';
    const buttonMenu = find(selector);
    assert.ok(buttonMenu.classList.contains('is-disabled'));
  });

  test('It will call the external function on clicking the button', async function(assert) {
    assert.expect(1);
    this.set('selections', new Array(1));
    this.set('pivotToInvestigate', () => {
      assert.ok(true);
    });
    await render(hbs`{{endpoint/pivot-to-event-analysis pivotToInvestigate=pivotToInvestigate selections=selections}}`);
    const selector = '.event-analysis button';
    await click(selector);
  });

  test('on clicking the menu item will call external function', async function(assert) {
    assert.expect(2);
    this.set('selections', new Array(1));
    this.set('pivotToInvestigate', (item, category) => {
      assert.equal(category, 'Network Event');
    });
    await render(hbs`{{endpoint/pivot-to-event-analysis selections=selections}}`);
    await click('.rsa-split-dropdown .rsa-form-button');
    const selector = '.button-menu';
    const buttonMenu = find(selector);
    return settled().then(async() => {
      assert.ok(buttonMenu.classList.contains('expanded'));
      assert.equal(findAll(`${selector} li`).length, 4, 'expected to render 4 menu options');
      await click(findAll(`${selector} li`)[0]);
    });
  });
});
