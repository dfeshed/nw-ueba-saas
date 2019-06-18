import { click, find, findAll, render, settled } from '@ember/test-helpers';
import Service from '@ember/service';
import Evented from '@ember/object/evented';
import { module, skip, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

const eventBusStub = Service.extend(Evented, {});
const FIX_ELEMENT_ID = 'tether_fix_style_element';

function insertTetherFix() {
  const styleElement = document.createElement('style');
  styleElement.id = FIX_ELEMENT_ID;
  styleElement.innerText =
    '#ember-testing-container, #ember-testing-container * {' +
      'position: static !important;' +
    '}';

  document.body.appendChild(styleElement);
}

function removeTetherFix() {
  const styleElement = document.getElementById(FIX_ELEMENT_ID);
  document.body.removeChild(styleElement);
}

module('Integration | Component | rsa-content-tethered-panel', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    insertTetherFix();
    this.owner.register('service:event-bus', eventBusStub);
    this.eventBus = this.owner.lookup('service:event-bus');
  });

  hooks.afterEach(function() {
    removeTetherFix();
  });

  test('it includes the proper classes', async function(assert) {
    await render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel}}Label{{/rsa-content-tethered-panel}}`);
    assert.equal(findAll('.rsa-content-tethered-panel').length, 1);
  });

  test('it includes the proper classes when isPopover is true ', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isPopover=true isDisplayed=true panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.is-popover').length, 1);
  });

  test('it includes the proper classes when is style is standard', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.standard').length, 1);
  });

  test('it includes the proper classes when is style is error', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true style="error" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.error').length, 1);
  });

  test('it includes the proper classes when is style is primary', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true style="primary" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.primary').length, 1);
  });

  test('it includes the proper classes when is position is top', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="top" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.top').length, 1);
  });

  test('it includes the proper classes when is position is top-left', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="top-left" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.top-left').length, 1);
  });

  test('it includes the proper classes when is position is top-right', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="top-right" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.top-right').length, 1);
  });

  test('it includes the proper classes when is position is bottom-left', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="bottom-left" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.bottom-left').length, 1);
  });

  test('it includes the proper classes when is position is bottom-right', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="bottom-right" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.bottom-right').length, 1);
  });

  test('it includes the proper classes when is position is bottom', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="bottom" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.bottom').length, 1);
  });

  test('it includes the proper classes when is position is left', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="left" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.left').length, 1);
  });

  test('it includes the proper classes when is position is left-top', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="left-top" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.left-top').length, 1);
  });

  test('it includes the proper classes when is position is left-bottom', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="left-bottom" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.left-bottom').length, 1);
  });

  test('it includes the proper classes when is position is right', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="right" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.right').length, 1);
  });

  test('it includes the proper classes when is position is right-top', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="right-top" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.right-top').length, 1);
  });

  test('it includes the proper classes when is position is right-bottom', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="right-bottom" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .panel-content.right-bottom').length, 1);
  });

  test('it displays the close button', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .close-icon').length, 1);
  });

  test('it hides the close button when displayCloseButton is false', async function(assert) {
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel displayCloseButton=false isDisplayed=true panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    assert.equal(findAll('.rsa-content-tethered-panel .close-icon').length, 0);
  });

  test('it toggles isDisplayed when the close button is clicked', async function(assert) {
    this.set('isDisplayed', true);
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=isDisplayed panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );
    await click('.rsa-content-tethered-panel .close-icon');

    return settled().then(() => {
      assert.equal(this.get('isDisplayed'), false);
    });
  });

  test('it updates isDisplayed when relevant events are fired', async function(assert) {
    this.set('isDisplayed', false);
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=isDisplayed panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );

    this.get('eventBus').trigger('rsa-content-tethered-panel-display-foo');

    return settled().then(() => {
      assert.equal(this.get('isDisplayed'), true);
    });
  });

  test('it does not update isDisplayed when app click event are fired but turned off', async function(assert) {
    this.set('isDisplayed', false);
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel closeOnAppClick=false isDisplayed=isDisplayed panelId="foo"}}Label{{/rsa-content-tethered-panel}}`
    );

    this.get('eventBus').trigger('rsa-application-click');

    return settled().then(() => {
      assert.equal(this.get('isDisplayed'), false);

      this.get('eventBus').trigger('rsa-application-header-click');

      return settled().then(() => {
        assert.equal(this.get('isDisplayed'), false);
      });
    });
  });

  skip('it updates isDisplayed when esc is pressed', function(assert) {
    this.set('isDisplayed', true);
    this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel closeOnEsc=true isDisplayed=isDisplayed panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);

    const event = new KeyboardEvent('keydown', {
      keyCode: 27
    });
    document.dispatchEvent(event);

    return settled().then(() => {
      assert.equal(this.get('isDisplayed'), false);
    });
  });

  skip('it does not update isDisplayed when esc is pressed when turned off', function(assert) {
    this.set('isDisplayed', true);
    this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=isDisplayed panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);

    const event = new KeyboardEvent('keydown', {
      keyCode: 27
    });
    document.dispatchEvent(event);

    return settled().then(() => {
      assert.equal(this.get('isDisplayed'), true);
    });
  });

  test('it updates model when display event is fired', async function(assert) {
    assert.expect(3);
    const modelValue = 'bar';
    this.set('model', null);
    this.set('panelDidOpen', () => {
      assert.ok(true);
    });
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel panelDidOpen=panelDidOpen model=model panelId="foo" as |hide model|}}<span class="model-value">{{model}}</span>{{/rsa-content-tethered-panel}}`
    );

    this.get('eventBus').trigger('rsa-content-tethered-panel-display-foo', null, null, null, modelValue);

    return settled().then(() => {
      assert.equal(this.get('model'), modelValue);
      assert.equal(find('.model-value').textContent.trim(), modelValue);
    });
  });

  test('it updates model when toggle event is fired', async function(assert) {
    assert.expect(3);
    const modelValue = 'bar';
    this.set('model', null);
    this.set('panelDidOpen', () => {
      assert.ok(true);
    });
    await render(
      hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel panelDidOpen=panelDidOpen model=model panelId="foo" as |hide model|}}<span class="model-value">{{model}}</span>{{/rsa-content-tethered-panel}}`
    );

    this.get('eventBus').trigger('rsa-content-tethered-panel-toggle-foo', null, null, null, modelValue);

    return settled().then(() => {
      assert.equal(this.get('model'), modelValue);
      assert.equal(find('.model-value').textContent.trim(), modelValue);
    });
  });
});
