import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import LinkComponent from '@ember/routing/link-component';
import { find, findAll, render, settled } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

const iframeSelector = '[test-id=uebaIframe]';

module('Integration | Component | Ueba Iframe', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate')
  });

  hooks.beforeEach(function() {
    this.owner.register('component:link-to-external', LinkComponent);
  });

  test('iframe src based on incoming iframeUrl property', async function(assert) {
    assert.expect(2);

    this.set('iframeUrl', '/some-url');
    this.set('iframeLoad', () => {});
    await render(hbs`{{ueba-iframe iframeUrl=iframeUrl testId="uebaIframe" handleOnLoad=(action iframeLoad)}}`);

    assert.equal(findAll(iframeSelector).length, 1);
    assert.equal(find(`${iframeSelector} iframe`).getAttribute('src'), '/some-url');

    await settled();
  });
});
