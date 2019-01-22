import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, click, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-page-layout', function(hooks) {
  setupRenderingTest(hooks);

  test('render complete layout', async function(assert) {
    await render(hbs`{{rsa-page-layout}}`);
    assert.equal(this.element.textContent.trim(), '', 'Empty. Only block rendering is supported');

    await render(hbs`
      {{#rsa-page-layout as |layout|}}
        {{#layout.left as |left|}}
          {{left.header}}
        {{/layout.left}}
        {{#layout.center}}
          {{#rsa-form-button isIconOnly=true defaultAction=(action layout.open 'right')}}
            {{rsa-icon name='filter-2'}}
          {{/rsa-form-button}}
        {{/layout.center}}
        {{#layout.right as |right|}}
          {{right.header}}
        {{/layout.right}}
      {{/rsa-page-layout}}
    `);

    assert.equal(find('.right-zone .rsa-header .title').textContent.trim(), 'Title', 'Default Title is displayed');
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 1, 'right panel is visible by default');

    await click('.right-zone .close-zone .rsa-icon-close-filled');
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 0, 'right panel is not visible after close');

    await click('.center-zone .rsa-icon-filter-2-filled');
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 1, 'right panel is visible on external open action');

  });

  test('left zone open/close', async function(assert) {
    this.set('title', 'Filters');
    await render(hbs`
      {{#rsa-page-layout as |layout|}}
        {{#layout.left as |left|}}
          {{left.header title=title}}
        {{/layout.left}}
        {{#layout.center}}
          {{#rsa-form-button isIconOnly=true defaultAction=(action layout.open 'left')}}
            {{rsa-icon name='filter-2'}}
          {{/rsa-form-button}}
        {{/layout.center}}
      {{/rsa-page-layout}}
    `);

    assert.equal(find('.left-zone .rsa-header .title').textContent.trim(), 'Filters', 'Custom Title is displayed');
    assert.equal(findAll('hbox.rsa-page-layout.show-left-zone .left-zone').length, 1, 'left panel is visible by default');
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 0, 'right panel is not set');

    await click('.left-zone .close-zone .rsa-icon-close-filled');
    assert.equal(findAll('hbox.rsa-page-layout.show-left-zone .left-zone').length, 0, 'left panel is not visible after close');

    await click('.center-zone .rsa-icon-filter-2-filled');
    assert.equal(findAll('hbox.rsa-page-layout.show-left-zone .left-zone').length, 1, 'left panel is visible on external open action');

  });

  test('close right zone using action', async function(assert) {
    await render(hbs`
      {{#rsa-page-layout as |layout|}}
        {{layout.right}}
        {{#layout.center}}
          {{#rsa-form-button isIconOnly=true defaultAction=(action layout.close 'right')}}
            {{rsa-icon name='close'}}
          {{/rsa-form-button}}
        {{/layout.center}}
      {{/rsa-page-layout}}
    `);
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 1, 'right panel is visible by default');

    await click('.center-zone .rsa-icon-close-filled');
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 0, 'left panel is close on external action');
  });

  test('header should not render for center zone', async function(assert) {
    await render(hbs`
    {{#rsa-page-layout as |layout|}}
      {{#layout.center as |center|}}
        {{center.header}}
      {{/layout.center}}
    {{/rsa-page-layout}}
    `);
    assert.equal(findAll('.center-zone .rsa-header').length, 0, 'header is not rendered in center zone');
  });

  test('on smaller screen close the left panel if open', async function(assert) {
    await render(hbs`
      {{#rsa-page-layout showRightZone=false as |layout|}}
        {{layout.right}}
        {{#layout.center}}
          {{#rsa-form-button isIconOnly=true defaultAction=(action layout.open 'right')}}
            {{rsa-icon name='close'}}
          {{/rsa-form-button}}
        {{/layout.center}}
      {{/rsa-page-layout}}
    `);
    assert.equal(findAll('hbox.rsa-page-layout.show-left-zone .left-zone').length, 0, 'right panel is visible by default');

    await click('.center-zone .rsa-icon-close-filled');
    assert.equal(findAll('hbox.rsa-page-layout.show-left-zone .left-zone').length, 0, 'right panel is close');
  });

  test('on smaller screen close the right panel if open', async function(assert) {
    await render(hbs`
      {{#rsa-page-layout showLeftZone=false as |layout|}}
        {{layout.right}}
        {{#layout.center}}
          {{#rsa-form-button isIconOnly=true defaultAction=(action layout.open 'left')}}
            {{rsa-icon name='close'}}
          {{/rsa-form-button}}
        {{/layout.center}}
      {{/rsa-page-layout}}
    `);
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 1, 'right panel is visible by default');

    await click('.center-zone .rsa-icon-close-filled');
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 0, 'rigth panel is close');
  });

});
