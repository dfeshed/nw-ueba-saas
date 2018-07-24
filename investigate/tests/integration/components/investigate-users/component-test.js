import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import LinkComponent from '@ember/routing/link-component';
import { find, findAll, render, settled, waitUntil, clearRender } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { scheduleOnce } from '@ember/runloop';

const imgSelector = '[test-id=uebaUnavailable]';
const iframeSelector = '[test-id=uebaIframe]';

module('Integration | Component | Investigate Users', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate')
  });

  hooks.beforeEach(function() {
    this.owner.register('component:link-to-external', LinkComponent);
  });

  test('displays ueba unavailable when iframe redirects to internal error', async function(assert) {
    assert.expect(5);

    this.owner.register(
      'template:components/ueba-iframe',
      hbs`<iframe id={{id}} src="/internal-error" onload={{action handleOnLoad}}></iframe>`
    );

    await render(hbs`{{investigate-users}}`);

    assert.equal(findAll(imgSelector).length, 0);
    assert.equal(findAll(iframeSelector).length, 1);
    assert.equal(find(iframeSelector).getAttribute('style'), 'display: none');

    await waitUntil(() => find(imgSelector));

    assert.equal(findAll(imgSelector).length, 1);
    assert.equal(findAll(iframeSelector).length, 0);
  });

  test('does not display ueba unavailable when iframe onload retry is exhausted', async function(assert) {
    assert.expect(6);

    this.owner.register(
      'template:components/ueba-iframe',
      hbs`<iframe id={{id}} src="/some-url" onload={{action handleOnLoad}}></iframe>`
    );

    await render(hbs`{{investigate-users}}`);

    assert.equal(findAll(imgSelector).length, 0);
    assert.equal(findAll(iframeSelector).length, 1);
    assert.equal(find(iframeSelector).getAttribute('style'), 'display: none');

    await waitUntil(() => find(iframeSelector).getAttribute('style') !== 'display: none', { timeout: 2000 });

    assert.equal(findAll(imgSelector).length, 0);
    assert.equal(findAll(iframeSelector).length, 1);
    assert.equal(find(iframeSelector).getAttribute('style'), 'display: block');
  });

  test('iframe onload closure action will not throw exception when isDestorying', async function(assert) {
    assert.expect(1);

    this.owner.register(
      'template:components/ueba-iframe',
      hbs`<iframe id={{id}} src="/some-url" onload={{action handleOnLoad}}></iframe>`
    );

    await render(hbs`{{investigate-users}}`);

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
