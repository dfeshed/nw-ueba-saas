import Service from '@ember/service';
import Evented from '@ember/object/evented';
import $ from 'jquery';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

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

moduleForComponent('/rsa-content-tethered-panel', 'Integration | Component | rsa-content-tethered-panel', {
  integration: true,

  beforeEach() {
    insertTetherFix();
    this.register('service:event-bus', eventBusStub);
    this.inject.service('event-bus', { as: 'eventBus' });
  },

  afterEach() {
    removeTetherFix();
  }
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel').length, 1);
});

test('it includes the proper classes when isPopover is true ', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isPopover=true isDisplayed=true panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal($('.is-popover').length, 1);
});

test('it includes the proper classes when is style is standard', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.standard').length, 1);
});

test('it includes the proper classes when is style is error', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true style="error" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.error').length, 1);
});

test('it includes the proper classes when is style is primary', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true style="primary" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.primary').length, 1);
});

test('it includes the proper classes when is position is top', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="top" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.top').length, 1);
});

test('it includes the proper classes when is position is top-left', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="top-left" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.top-left').length, 1);
});

test('it includes the proper classes when is position is top-right', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="top-right" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.top-right').length, 1);
});

test('it includes the proper classes when is position is bottom-left', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="bottom-left" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.bottom-left').length, 1);
});

test('it includes the proper classes when is position is bottom-right', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="bottom-right" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.bottom-right').length, 1);
});

test('it includes the proper classes when is position is bottom', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="bottom" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.bottom').length, 1);
});

test('it includes the proper classes when is position is left', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="left" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.left').length, 1);
});

test('it includes the proper classes when is position is left-top', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="left-top" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.left-top').length, 1);
});

test('it includes the proper classes when is position is left-bottom', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="left-bottom" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.left-bottom').length, 1);
});

test('it includes the proper classes when is position is right', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="right" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.right').length, 1);
});

test('it includes the proper classes when is position is right-top', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="right-top" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.right-top').length, 1);
});

test('it includes the proper classes when is position is right-bottom', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true position="right-bottom" panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .panel-content.right-bottom').length, 1);
});

test('it displays the close button', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=true panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .close-icon').length, 1);
});

test('it hides the close button when displayCloseButton is false', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel displayCloseButton=false isDisplayed=true panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  assert.equal(this.$('.rsa-content-tethered-panel .close-icon').length, 0);
});

test('it toggles isDisplayed when the close button is clicked', function(assert) {
  this.set('isDisplayed', true);
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=isDisplayed panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);
  this.$('.rsa-content-tethered-panel .close-icon').click();

  return wait().then(() => {
    assert.equal(this.get('isDisplayed'), false);
  });
});

test('it updates isDisplayed when relevant events are fired', function(assert) {
  this.set('isDisplayed', false);
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel isDisplayed=isDisplayed panelId="foo"}}Label{{/rsa-content-tethered-panel}}`);

  this.get('eventBus').trigger('rsa-content-tethered-panel-display-foo');

  return wait().then(() => {
    assert.equal(this.get('isDisplayed'), true);
  });
});

test('it updates model when display event is fired', function(assert) {
  const modelValue = 'bar';
  this.set('model', null);
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel model=model panelId="foo" as |hide model|}}<span class="model-value">{{model}}</span>{{/rsa-content-tethered-panel}}`);

  this.get('eventBus').trigger('rsa-content-tethered-panel-display-foo', null, null, null, modelValue);

  return wait().then(() => {
    assert.equal(this.get('model'), modelValue);
    assert.equal(this.$('.model-value').text().trim(), modelValue);
  });
});

test('it updates model when toggle event is fired', function(assert) {
  const modelValue = 'bar';
  this.set('model', null);
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tethered-panel model=model panelId="foo" as |hide model|}}<span class="model-value">{{model}}</span>{{/rsa-content-tethered-panel}}`);

  this.get('eventBus').trigger('rsa-content-tethered-panel-toggle-foo', null, null, null, modelValue);

  return wait().then(() => {
    assert.equal(this.get('model'), modelValue);
    assert.equal(this.$('.model-value').text().trim(), modelValue);
  });
});
