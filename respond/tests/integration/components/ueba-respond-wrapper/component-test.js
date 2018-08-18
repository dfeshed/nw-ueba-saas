import { module, test } from 'qunit';
import * as ACTION_TYPES from 'respond/actions/types';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, settled, waitUntil, clearRender } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import { scheduleOnce } from '@ember/runloop';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

const errorSelector = '[test-id=uebaError]';
const iframeSelector = '[test-id=uebaIframe]';
const loaderSelector = '[test-id=uebaLoader]';

module('Integration | Component | UebaWrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.set('close', () => {
    });
  });

  test('resolvedWidth should return correct calc value', async function(assert) {
    assert.expect(2);

    patchReducer(this, Immutable.from({
      respond: {
        incident: {
          inspectorWidth: 400
        }
      }
    }));

    this.owner.register(
      'template:components/ueba-iframe',
      hbs`<iframe id={{id}} src="/some-url" onload={{action handleOnLoad}}></iframe>`
    );

    await render(hbs`{{ueba-respond-wrapper ueba="/user/123/alert/456" uebaClose=(action close)}}`);

    assert.equal(find('.ueba-container__main').style.width, 'calc(100% - 400px)');

    const redux = this.owner.lookup('service:redux');
    redux.dispatch({ type: ACTION_TYPES.RESIZE_INCIDENT_INSPECTOR, payload: 350 });

    return settled().then(() => {
      assert.equal(find('.ueba-container__main').style.width, 'calc(100% - 350px)');
    });
  });

  test('displays ueba error when iframe redirects to internal error', async function(assert) {
    assert.expect(7);

    this.owner.register(
      'template:components/ueba-iframe',
      hbs`<iframe id={{id}} src="/internal-error" onload={{action handleOnLoad}}></iframe>`
    );

    await render(hbs`{{ueba-respond-wrapper ueba="/user/123/alert/456" uebaClose=(action close)}}`);

    assert.equal(findAll(errorSelector).length, 0);
    assert.equal(findAll(loaderSelector).length, 1);
    assert.equal(findAll(iframeSelector).length, 1);
    assert.equal(find(iframeSelector).getAttribute('style'), 'display: none');

    await waitUntil(() => find(errorSelector), { timeout: 5000 });

    assert.equal(findAll(errorSelector).length, 1);
    assert.equal(findAll(loaderSelector).length, 0);
    assert.equal(findAll(iframeSelector).length, 0);
  });

  test('does not display ueba error when iframe onload retry is exhausted', async function(assert) {
    assert.expect(8);

    this.owner.register(
      'template:components/ueba-iframe',
      hbs`<iframe id={{id}} src="/some-url" onload={{action handleOnLoad}}></iframe>`
    );

    await render(hbs`{{ueba-respond-wrapper ueba="/user/123/alert/456" uebaClose=(action close)}}`);

    assert.equal(findAll(errorSelector).length, 0);
    assert.equal(findAll(loaderSelector).length, 1);
    assert.equal(findAll(iframeSelector).length, 1);
    assert.equal(find(iframeSelector).getAttribute('style'), 'display: none');

    await waitUntil(() => find(iframeSelector).getAttribute('style') !== 'display: none', { timeout: 5000 });

    assert.equal(findAll(errorSelector).length, 0);
    assert.equal(findAll(loaderSelector).length, 0);
    assert.equal(findAll(iframeSelector).length, 1);
    assert.equal(find(iframeSelector).getAttribute('style'), 'display: block');
  });

  test('iframe onload closure action will not throw exception when isDestorying', async function(assert) {
    assert.expect(1);

    this.owner.register(
      'template:components/ueba-iframe',
      hbs`<iframe id={{id}} src="/some-url" onload={{action handleOnLoad}}></iframe>`
    );

    await render(hbs`{{ueba-respond-wrapper ueba="/user/123/alert/456" uebaClose=(action close)}}`);

    return settled().then(() => {

      // will destory the component but not until the iframe load callback has fired
      scheduleOnce('render', this, async function() {
        clearRender();
      });

      return settled().then(() => {
        assert.ok(this.element, 'Should not blow up because iframe load callbacks were prevented from running while destroyed');
      });

    });
  });
});
