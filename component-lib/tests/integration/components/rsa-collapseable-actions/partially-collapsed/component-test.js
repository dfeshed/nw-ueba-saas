import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';

module('Integration | Component | rsa-collapseable-actions/partially-collapsed', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders dropdowns', async function(assert) {
    this.set('dropdownList', [
      EmberObject.create({
        component: 'dropdown',
        label: this.get('dropdownLabel'),
        icon: 'cog',
        nestedActions: [{
          label: 'Dropdown Option 1',
          icon: 'cog',
          action: () => assert.ok(true)
        }, {
          label: 'Dropdown Option 2',
          icon: 'cog',
          action: () => assert.ok(true)
        }]
      })
    ]);

    await render(hbs `{{rsa-collapseable-actions/partially-collapsed dropdownList=dropdownList}}`);

    assert.ok(find('.dropdowns'));
    assert.ok(find('.dropdown'));
  });


  test('it renders buttons', async function(assert) {
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
        label: 'Secondary Button',
        icon: 'cog',
        isPrimary: false,
        action: () => assert.ok(true)
      }),
      EmberObject.create({
        component: 'button-group',
        label: 'Primary Group 1',
        icon: 'cog',
        isPrimary: true,
        action: () => assert.ok(true),
        nestedActions: [{
          label: 'Primary Group 2',
          icon: 'cog',
          action: () => assert.ok(true)
        }, {
          label: 'Primary Group 3',
          icon: 'cog',
          action: () => assert.ok(true)
        }]
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

    await render(hbs `{{rsa-collapseable-actions/partially-collapsed buttonList=buttonList}}`);

    assert.equal(findAll('.buttons > .rsa-form-button-wrapper').length, 2, 'a');
    assert.equal(findAll('.buttons > .rsa-form-button-wrapper .rsa-icon-cog').length, 2, 'b');
    assert.equal(findAll('.buttons > .rsa-form-button-wrapper')[0].textContent.trim(), 'Primary Button');
    assert.notOk(findAll('.buttons > .rsa-form-button-wrapper')[1].textContent.trim());

    assert.equal(findAll('.buttons > .rsa-button-group').length, 2, 'c');
    assert.equal(findAll('.buttons > .rsa-button-group .rsa-icon-cog').length, 2, 'd');
    assert.equal(find('.buttons > .rsa-button-group:nth-child(3) .rsa-form-button-wrapper').textContent.trim(), 'Primary Group 1');
    assert.notOk(find('.buttons > .rsa-button-group:nth-child(4) .rsa-form-button-wrapper').textContent.trim());
  });

  test('it renders toggles', async function(assert) {
    assert.expect(3);

    this.set('toggleList', [
      EmberObject.create({
        component: 'toggle',
        label: 'Show Nulls',
        value: this.get('showNulls'),
        action: () => assert.ok(true)
      }),
      EmberObject.create({
        component: 'toggle',
        label: 'Show Nulls',
        value: this.get('showNulls'),
        action: () => assert.ok(true)
      })
    ]);

    await render(hbs `{{rsa-collapseable-actions/partially-collapsed toggleList=toggleList}}`);

    assert.ok(find('.toggles'));
    assert.equal(findAll('.x-toggle-component').length, 2);
    findAll('.x-toggle-btn')[0].click();
  });

});
