import Service from '@ember/service';
import Evented from '@ember/object/evented';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { clearRender, render, settled, findAll, find, click, triggerKeyEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';

const eventBusStub = Service.extend(Evented, {});

module('Integration | Component | rsa-application-modal', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.register('service:event-bus', eventBusStub);
    this.eventBus = this.owner.lookup('service:event-bus');
  });

  test('it includes the proper classes', async function(assert) {
    await render(hbs `<div id="modalDestination"></div>{{rsa-application-modal}}`);
    const content = findAll('.rsa-application-modal').length;
    assert.equal(findAll('.standard').length, 1);
    assert.equal(content, 1);
  });

  test('it includes the proper classes when style is error', async function(assert) {
    await render(hbs `<div id="modalDestination"></div>{{rsa-application-modal style="error"}}`);
    const content = findAll('.rsa-application-modal').length;
    assert.equal(findAll('.error').length, 1);
    assert.equal(content, 1);
  });


  test('it includes the proper classes when isOpen is true', async function(assert) {
    await render(
      hbs `<div id="modalDestination"></div>{{#rsa-application-modal isOpen=true}}foo{{/rsa-application-modal}}`
    );
    const modal = find('.rsa-application-modal');
    assert.ok(modal.classList.contains('is-open'));
  });

  test('it does not render content initially', async function(assert) {
    await render(
      hbs `<div id="modalDestination"></div>{{#rsa-application-modal}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`
    );
    const modal = findAll('#modalDestination .rsa-application-modal-content').length;
    assert.equal(modal, 0);
  });

  test('it renders content after clicking the trigger', async function(assert) {
    await render(
      hbs `<div id="modalDestination"></div>{{#rsa-application-modal}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`
    );
    await click('.modal-trigger');

    return settled().then(function() {
      const modal = findAll('#modalDestination .rsa-application-modal-content').length;
      assert.equal(modal, 1);
    });

  });

  test('it closes the modal when clicking the overlay', async function(assert) {
    await render(
      hbs `<div id="modalDestination"></div>{{#rsa-application-modal isOpen=true}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`
    );
    await click('.rsa-application-overlay');

    return settled().then(function() {
      const modal = findAll('#modalDestination .rsa-application-modal-content').length;
      assert.equal(modal, 0);
    });
  });

  test('it closes the modal when clicking ESC', async function(assert) {
    await render(
      hbs `<div id="modalDestination"></div>{{#rsa-application-modal isOpen=true}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`
    );

    await triggerKeyEvent('.rsa-application-modal', 'keyup', 27);

    return settled().then(function() {
      const modal = findAll('#modalDestination .rsa-application-modal-content').length;
      assert.equal(modal, 0);
    });
  });

  test('it emits the rsa-application-modal-did-open event when triggering the modal', async function(assert) {
    await render(
      hbs `<div id="modalDestination"></div>{{#rsa-application-modal}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`
    );

    const spy = sinon.spy(this.get('eventBus'), 'trigger');

    await click('.modal-trigger');

    return settled().then(function() {
      assert.ok(spy.withArgs('rsa-application-modal-did-open').calledOnce);
    });
  });

  test('it closes when rsa-application-modal-close-all is triggered', async function(assert) {
    this.set('isOpen', true);
    await render(
      hbs `<div id="modalDestination"></div>{{#rsa-application-modal isOpen=isOpen}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`
    );
    this.get('eventBus').trigger('rsa-application-modal-close-all');

    const that = this;
    return settled().then(function() {
      assert.equal(that.get('isOpen'), false);
    });
  });

  test('it closes when a custom close event is triggered', async function(assert) {
    this.set('isOpen', true);
    await render(
      hbs `<div id="modalDestination"></div>{{#rsa-application-modal eventId="test" isOpen=isOpen}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`
    );
    this.get('eventBus').trigger('rsa-application-modal-close-test');

    const that = this;
    return settled().then(function() {
      assert.equal(that.get('isOpen'), false);
    });
  });

  test('it opens when a custom open event is triggered', async function(assert) {
    this.set('isOpen', false);
    await render(
      hbs `<div id="modalDestination"></div>{{#rsa-application-modal eventId="test" isOpen=isOpen}}<button class='modal-trigger'>Click</button>{{/rsa-application-modal}}`
    );
    this.get('eventBus').trigger('rsa-application-modal-open-test');

    const that = this;
    return settled().then(function() {
      assert.equal(that.get('isOpen'), true);
    });
  });

  test('it no longer has an active modal destination after the modal is destroyed', async function(assert) {
    const modelDiv = document.createElement('div');
    modelDiv.id = 'modalDestination';
    document.body.appendChild(modelDiv); // don't render the modalDestination with the modal otherwise it will be removed on clearRender
    await render(
      hbs `{{#rsa-application-modal eventId="test" autoOpen=true}}<button class='modal-trigger'>Click</button><div class='modal-content'><div class='modal-close'>Close</div></div>{{/rsa-application-modal}}`
    );
    return settled().then(async() => {
      assert.equal(document.querySelectorAll('#modalDestination.active').length, 1, 'The modal is active');
      await clearRender();
      return settled().then(() => {
        assert.equal(document.querySelectorAll('#modalDestination.active').length, 0, 'The modal is no longer active');
        document.body.removeChild(modelDiv);// clean up
      });
    });
  });

});