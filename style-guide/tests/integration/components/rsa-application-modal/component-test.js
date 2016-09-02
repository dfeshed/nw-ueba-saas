import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import sinon from 'sinon';

const {
  Service,
  Evented
} = Ember;

const eventBusStub = Service.extend(Evented, {});

moduleForComponent('/rsa-application-modal', 'Integration | Component | rsa-application-modal', {
  integration: true,

  beforeEach() {
    this.register('service:event-bus', eventBusStub);
    this.inject.service('event-bus', { as: 'eventBus' });
  }
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{rsa-application-modal}}`);
  let content = this.$().find('.rsa-application-modal').length;
  assert.equal(content, 1);
});

test('it includes the proper classes when isOpen is true', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal isOpen=true}}foo{{/rsa-application-modal}}`);
  let modal = this.$().find('.rsa-application-modal').first();
  assert.ok(modal.hasClass('is-open'));
});

test('it does not render content initially', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);
  let modal = this.$().find('#modalDestination .rsa-application-modal-content').length;
  assert.equal(modal, 0);
});

test('it renders content after clicking the trigger', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);
  this.$().find('.modal-trigger').click();

  let that = this;
  return wait().then(function() {
    let modal = that.$().find('#modalDestination .rsa-application-modal-content').length;
    assert.equal(modal, 1);
  });

});

test('it closes the modal when clicking the overlay', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal isOpen=true}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);
  this.$().find('.rsa-application-overlay').click();

  let that = this;
  return wait().then(function() {
    let modal = that.$().find('#modalDestination .rsa-application-modal-content').length;
    assert.equal(modal, 0);
  });
});

test('it closes the modal when clicking ESC', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal isOpen=true}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);

  // this.$ does not have "Event" on it, use window
  // eslint-disable-next-line new-cap
  let e = window.$.Event('keyup');
  e.keyCode = 27;

  this.$('.rsa-application-modal').trigger(e);

  let that = this;
  return wait().then(function() {
    let modal = that.$().find('#modalDestination .rsa-application-modal-content').length;
    assert.equal(modal, 0);
  });
});

test('it emits the rsa-application-modal-did-open event when triggering the modal', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);

  let spy = sinon.spy(this.get('eventBus'), 'trigger');

  this.$().find('.modal-trigger').click();

  return wait().then(function() {
    assert.ok(spy.withArgs('rsa-application-modal-did-open').calledOnce);
  });
});

test('it closes when rsa-application-modal-close-all is triggered', function(assert) {
  this.set('isOpen', true);
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal isOpen=isOpen}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);
  this.get('eventBus').trigger('rsa-application-modal-close-all');

  let that = this;
  return wait().then(function() {
    assert.equal(that.get('isOpen'), false);
  });
});

test('it closes when a custom close event is triggered', function(assert) {
  this.set('isOpen', true);
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal eventId="test" isOpen=isOpen}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);
  this.get('eventBus').trigger('rsa-application-modal-close-test');

  let that = this;
  return wait().then(function() {
    assert.equal(that.get('isOpen'), false);
  });
});

test('it opens when a custom open event is triggered', function(assert) {
  this.set('isOpen', false);
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal eventId="test" isOpen=isOpen}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);
  this.get('eventBus').trigger('rsa-application-modal-open-test');

  let that = this;
  return wait().then(function() {
    assert.equal(that.get('isOpen'), true);
  });
});

test('it closes when a .close-modal element is clicked', function(assert) {
  this.set('isOpen', true);
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal eventId="test" isOpen=isOpen}}<button class='modal-trigger'>Click</button><div class='modal-content'><div class='modal-close'>Close</div></div>{{/rsa-application-modal}}`);
  this.$().find('.modal-close').click();

  let that = this;
  return wait().then(function() {
    assert.equal(that.get('isOpen'), false);
  });
});
