import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import sinon from 'sinon';
import $ from 'jquery';

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
  const content = this.$().find('.rsa-application-modal').length;
  assert.equal(this.$().find('.standard').length, 1);
  assert.equal(content, 1);
});

test('it includes the proper classes when style is error', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{rsa-application-modal style="error"}}`);
  const content = this.$().find('.rsa-application-modal').length;
  assert.equal(this.$().find('.error').length, 1);
  assert.equal(content, 1);
});


test('it includes the proper classes when isOpen is true', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal isOpen=true}}foo{{/rsa-application-modal}}`);
  const modal = this.$().find('.rsa-application-modal').first();
  assert.ok(modal.hasClass('is-open'));
});

test('it does not render content initially', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);
  const modal = this.$().find('#modalDestination .rsa-application-modal-content').length;
  assert.equal(modal, 0);
});

test('it renders content after clicking the trigger', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);
  this.$().find('.modal-trigger').click();

  const that = this;
  return wait().then(function() {
    const modal = that.$().find('#modalDestination .rsa-application-modal-content').length;
    assert.equal(modal, 1);
  });

});

test('it closes the modal when clicking the overlay', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal isOpen=true}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);
  this.$().find('.rsa-application-overlay').click();

  const that = this;
  return wait().then(function() {
    const modal = that.$().find('#modalDestination .rsa-application-modal-content').length;
    assert.equal(modal, 0);
  });
});

test('it closes the modal when clicking ESC', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal isOpen=true}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);

  // this.$ does not have "Event" on it, use window
  // eslint-disable-next-line new-cap
  const e = window.$.Event('keyup');
  e.keyCode = 27;

  this.$('.rsa-application-modal').trigger(e);

  const that = this;
  return wait().then(function() {
    const modal = that.$().find('#modalDestination .rsa-application-modal-content').length;
    assert.equal(modal, 0);
  });
});

test('it emits the rsa-application-modal-did-open event when triggering the modal', function(assert) {
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);

  const spy = sinon.spy(this.get('eventBus'), 'trigger');

  this.$().find('.modal-trigger').click();

  return wait().then(function() {
    assert.ok(spy.withArgs('rsa-application-modal-did-open').calledOnce);
  });
});

test('it closes when rsa-application-modal-close-all is triggered', function(assert) {
  this.set('isOpen', true);
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal isOpen=isOpen}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);
  this.get('eventBus').trigger('rsa-application-modal-close-all');

  const that = this;
  return wait().then(function() {
    assert.equal(that.get('isOpen'), false);
  });
});

test('it closes when a custom close event is triggered', function(assert) {
  this.set('isOpen', true);
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal eventId="test" isOpen=isOpen}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);
  this.get('eventBus').trigger('rsa-application-modal-close-test');

  const that = this;
  return wait().then(function() {
    assert.equal(that.get('isOpen'), false);
  });
});

test('it opens when a custom open event is triggered', function(assert) {
  this.set('isOpen', false);
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal eventId="test" isOpen=isOpen}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`);
  this.get('eventBus').trigger('rsa-application-modal-open-test');

  const that = this;
  return wait().then(function() {
    assert.equal(that.get('isOpen'), true);
  });
});

test('it closes when a .close-modal element is clicked', function(assert) {
  this.set('isOpen', true);
  this.render(hbs `<div id="modalDestination"></div>{{#rsa-application-modal eventId="test" isOpen=isOpen}}<button class='modal-trigger'>Click</button><div class='modal-content'><div class='modal-close'>Close</div></div>{{/rsa-application-modal}}`);
  this.$().find('.modal-close').click();

  const that = this;
  return wait().then(function() {
    assert.equal(that.get('isOpen'), false);
  });
});

test('it no longer has an active modal destination after the modal is destroyed', function(assert) {
  $('body').append('<div id="modalDestination"></div>'); // don't render the modalDestination with the modal otherwise it will be removed on clearRender
  this.render(hbs `{{#rsa-application-modal eventId="test" autoOpen=true}}<button class='modal-trigger'>Click</button><div class='modal-content'><div class='modal-close'>Close</div></div>{{/rsa-application-modal}}`);
  return wait().then(() => {
    assert.equal($('#modalDestination.active').length, 1, 'The modal is active');
    this.clearRender();
    return wait().then(() => {
      assert.equal($('#modalDestination.active').length, 0, 'The modal is no longer active');
      $('#modalDestination').remove(); // clean up
    });
  });
});
