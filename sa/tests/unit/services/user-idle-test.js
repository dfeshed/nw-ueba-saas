import { test, module } from 'qunit';
import Component from '@ember/component';
import hbs from 'htmlbars-inline-precompile';
import { next } from '@ember/runloop';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import { setupRenderingTest } from 'ember-qunit';
import { inject as service } from '@ember/service';
import { render, settled, triggerEvent } from '@ember/test-helpers';
import { Promise } from 'rsvp';
import sinon from 'sinon';

const sessionKey = 'rsa-nw-last-session-access';
const clearLocalStorage = () => {
  localStorage.clear();
  return new Promise((resolve) => {
    waitFor(() => localStorage.getItem(sessionKey) === null).then(() => {
      next(null, resolve);
    });
  });
};

module('Unit | Services | user-idle', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    return clearLocalStorage();
  });

  hooks.afterEach(function() {
    return clearLocalStorage();
  });

  test('when mousemove event listener fires the idle timeout is reset', async function(assert) {
    assert.expect(4);

    const TheComponent = Component.extend({
      userIdle: service(),
      layout: hbs`
        <p class="ready">go</p>
      `
    });
    this.owner.register('component:the-component', TheComponent);

    await render(hbs`{{the-component}}`);

    const userIdleService = this.owner.lookup('service:userIdle');
    const idleSpy = sinon.spy(userIdleService, 'setIdle');

    assert.equal(idleSpy.callCount, 0);
    assert.ok(localStorage.getItem(sessionKey) === null);

    await triggerEvent('.ready', 'mousemove');

    await waitFor(() => idleSpy.callCount > 0);

    assert.equal(idleSpy.callCount, 1);
    assert.ok(localStorage.getItem(sessionKey) !== null);

    return settled(() => idleSpy.restore());
  });

});
