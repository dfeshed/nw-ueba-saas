import { test, module } from 'qunit';
import Component from '@ember/component';
import hbs from 'htmlbars-inline-precompile';
import { next } from '@ember/runloop';
import { setupRenderingTest } from 'ember-qunit';
import { inject as service } from '@ember/service';
import { render, settled, triggerEvent, waitUntil } from '@ember/test-helpers';
import { Promise } from 'rsvp';
import sinon from 'sinon';

const sessionKey = 'rsa-nw-last-session-access';
const clearLocalStorage = () => {
  localStorage.clear();
  return new Promise((resolve) => {
    waitUntil(() => localStorage.getItem(sessionKey) === null).then(() => {
      next(null, resolve);
    });
  });
};

module('Unit | Services | user-idle', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    const TheComponent = Component.extend({
      userIdle: service(),
      layout: hbs`
        <p class="ready">go</p>
      `
    });
    this.owner.register('component:the-component', TheComponent);

    return clearLocalStorage();
  });

  hooks.afterEach(function() {
    return clearLocalStorage();
  });

  test('when mousemove event listener fires the idle timeout is reset', async function(assert) {
    assert.expect(4);

    await render(hbs`{{the-component}}`);

    const userIdleService = this.owner.lookup('service:userIdle');
    const idleSpy = sinon.spy(userIdleService, 'setIdle');

    assert.equal(idleSpy.callCount, 0);
    assert.ok(localStorage.getItem(sessionKey) === null);

    await triggerEvent('.ready', 'mousemove');

    await waitUntil(() => idleSpy.callCount > 0);

    assert.equal(idleSpy.callCount, 1);
    assert.ok(localStorage.getItem(sessionKey) !== null);

    return settled(() => idleSpy.restore());
  });

  test('when scroll event listener fires the idle timeout is reset', async function(assert) {
    assert.expect(4);

    await render(hbs`{{the-component}}`);

    const userIdleService = this.owner.lookup('service:userIdle');
    const idleSpy = sinon.spy(userIdleService, 'setIdle');

    assert.equal(idleSpy.callCount, 0);
    assert.ok(localStorage.getItem(sessionKey) === null);

    await triggerEvent('.ready', 'scroll');

    await waitUntil(() => idleSpy.callCount > 0);

    assert.equal(idleSpy.callCount, 1);
    assert.ok(localStorage.getItem(sessionKey) !== null);

    return settled(() => idleSpy.restore());
  });

});
