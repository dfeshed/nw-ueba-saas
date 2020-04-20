import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';

module('Integration | Component | rsa-collapseable-actions', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    await render(hbs `{{rsa-collapseable-actions}}`);
    assert.ok(find('.rsa-collapseable-actions.actions-expanded'));

    assert.ok(find('.for-fully-collapsed'));
    assert.ok(find('.for-collapsed'));
    assert.ok(find('.for-expanded'));

    assert.notOk(find('.dropdowns'));
    assert.notOk(find('.buttons'));
    assert.notOk(find('.toggles'));
    assert.notOk(find('.find'));
  });

  test('it renders with find', async function(assert) {
    await render(hbs `{{rsa-collapseable-actions displayFind=true}}`);
    assert.ok(find('.find'));
  });

  test('it renders with dropdowns', async function(assert) {
    this.set('dropdownList', [{}]);
    await render(hbs `{{rsa-collapseable-actions dropdownList=dropdownList}}`);
    assert.ok(find('.dropdowns'));
  });

  test('it renders with buttons', async function(assert) {
    this.set('buttonList', [{}]);
    await render(hbs `{{rsa-collapseable-actions buttonList=buttonList}}`);
    assert.ok(find('.buttons'));
  });

  test('it renders with toggles', async function(assert) {
    this.set('toggleList', [{ action: () => {} }]);
    await render(hbs `{{rsa-collapseable-actions toggleList=toggleList}}`);
    assert.ok(find('.toggles'));
  });


  test('it collapses when its width changes', async function(assert) {
    this.set('buttonList', [
      EmberObject.create({
        component: 'button',
        label: 'Primary Button',
        icon: 'cog',
        isPrimary: true,
        action: () => assert.ok(true)
      }),
      EmberObject.create({
        component: 'button',
        label: 'Primary Button',
        icon: 'cog',
        isPrimary: false,
        action: () => assert.ok(true)
      }),
      EmberObject.create({
        component: 'button',
        label: 'Primary Button',
        icon: 'cog',
        isPrimary: false,
        action: () => assert.ok(true)
      }),
      EmberObject.create({
        component: 'button-group',
        label: 'Secondary Group 1',
        icon: 'cog',
        isPrimary: false,
        action: () => assert.ok(true),
        nestedActions: [{
          label: 'Secondary Group 2',
          icon: 'cog',
          action: () => assert.ok(true)
        }, {
          label: 'Secondary Group 3',
          icon: 'cog',
          action: () => assert.ok(true)
        }]
      })
    ]);

    assert.expect(3);

    await render(hbs `{{rsa-collapseable-actions buttonList=buttonList}}`);

    const actions = find('.rsa-collapseable-actions');

    assert.ok(actions.className.includes('actions-expanded'));
    actions.parentElement.style.width = '640px';

    // wait for current frame batch to finish
    // also wait for next frame batch, that intersection observer will trigger
    await new Promise((resolve) => requestAnimationFrame(() => requestAnimationFrame(resolve)));

    assert.ok(actions.className.includes('actions-collapsed'));
    actions.parentElement.style.width = '1px';

    // wait for current frame batch to finish
    // also wait for next frame batch, that intersection observer will trigger
    await new Promise((resolve) => requestAnimationFrame(() => requestAnimationFrame(resolve)));

    assert.ok(actions.className.includes('actions-fully-collapsed'));
  });

});
