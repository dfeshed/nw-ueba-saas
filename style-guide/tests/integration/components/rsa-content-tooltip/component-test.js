import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import Ember from 'ember';

const {
  Service,
  Evented
} = Ember;

const eventBusStub = Service.extend(Evented, {});
const FIX_ELEMENT_ID = 'tether_fix_style_element';

function insertTetherFix() {
  let styleElement = document.createElement('style');
  styleElement.id = FIX_ELEMENT_ID;
  styleElement.innerText =
    '#ember-testing-container, #ember-testing-container * {' +
      'position: static !important;' +
    '}';

  document.body.appendChild(styleElement);
}

function removeTetherFix() {
  let styleElement = document.getElementById(FIX_ELEMENT_ID);
  document.body.removeChild(styleElement);
}

moduleForComponent('/rsa-content-tooltip', 'Integration | Component | rsa-content-tooltip', {
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
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tooltip}}Label{{/rsa-content-tooltip}}`);
  assert.equal(this.$('.rsa-content-tooltip').length, 1);
});

test('it includes the proper classes when is style is standard', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tooltip isDisplayed=true tooltipId="foo"}}Label{{/rsa-content-tooltip}}`);
  assert.equal(this.$('.rsa-content-tooltip .tooltip-content.standard').length, 1);
});

test('it includes the proper classes when is style is error', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tooltip isDisplayed=true style="error" tooltipId="foo"}}Label{{/rsa-content-tooltip}}`);
  assert.equal(this.$('.rsa-content-tooltip .tooltip-content.error').length, 1);
});

test('it includes the proper classes when is style is primary', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tooltip isDisplayed=true style="primary" tooltipId="foo"}}Label{{/rsa-content-tooltip}}`);
  assert.equal(this.$('.rsa-content-tooltip .tooltip-content.primary').length, 1);
});

test('it includes the proper classes when is position is top', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tooltip isDisplayed=true position="top" tooltipId="foo"}}Label{{/rsa-content-tooltip}}`);
  assert.equal(this.$('.rsa-content-tooltip .tooltip-content.top').length, 1);
});

test('it includes the proper classes when is position is bottom', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tooltip isDisplayed=true position="bottom" tooltipId="foo"}}Label{{/rsa-content-tooltip}}`);
  assert.equal(this.$('.rsa-content-tooltip .tooltip-content.bottom').length, 1);
});

test('it includes the proper classes when is position is left', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tooltip isDisplayed=true position="left" tooltipId="foo"}}Label{{/rsa-content-tooltip}}`);
  assert.equal(this.$('.rsa-content-tooltip .tooltip-content.left').length, 1);
});

test('it includes the proper classes when is position is right', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tooltip isDisplayed=true position="right" tooltipId="foo"}}Label{{/rsa-content-tooltip}}`);
  assert.equal(this.$('.rsa-content-tooltip .tooltip-content.right').length, 1);
});

test('it displays the close button for click triggerEvent', function(assert) {
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tooltip isDisplayed=true triggerEvent="click" tooltipId="foo"}}Label{{/rsa-content-tooltip}}`);
  assert.equal(this.$('.rsa-content-tooltip .close-icon').length, 1);
});

test('it toggles isDisplayed when the close button is clicked', function(assert) {
  this.set('isDisplayed', true);
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tooltip isDisplayed=isDisplayed tooltipId="foo" triggerEvent="click"}}Label{{/rsa-content-tooltip}}`);
  this.$('.rsa-content-tooltip .close-icon').click();

  return wait().then(() => {
    assert.equal(this.get('isDisplayed'), false);
  });
});

test('it updates isDisplayed when relevant events are fired', function(assert) {
  this.set('isDisplayed', false);
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tooltip isDisplayed=isDisplayed tooltipId="foo"}}Label{{/rsa-content-tooltip}}`);

  this.get('eventBus').trigger('rsa-content-tooltip-display-foo', 'click');

  return wait().then(() => {
    assert.equal(this.get('isDisplayed'), true);
  });
});

test('it updates isDisplayed when relevant events are fired', function(assert) {
  this.set('isDisplayed', true);
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tooltip isDisplayed=isDisplayed tooltipId="foo"}}Label{{/rsa-content-tooltip}}`);

  this.get('eventBus').trigger('rsa-content-tooltip-hide-foo', 'click');

  return wait().then(() => {
    assert.equal(this.get('isDisplayed'), false);
  });
});

test('it updates isDisplayed when relevant events are fired', function(assert) {
  this.set('isDisplayed', false);
  this.render(hbs `<a class='foo'>Link</a>{{#rsa-content-tooltip isDisplayed=isDisplayed tooltipId="foo"}}Label{{/rsa-content-tooltip}}`);

  this.get('eventBus').trigger('rsa-content-tooltip-toggle-foo', 'hover');

  return wait().then(() => {
    assert.equal(this.get('isDisplayed'), true);
  });
});
