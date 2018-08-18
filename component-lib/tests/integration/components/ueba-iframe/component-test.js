import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, settled } from '@ember/test-helpers';

const iframeSelector = '[test-id=uebaIframe]';

module('Integration | Component | Ueba Iframe', function(hooks) {
  setupRenderingTest(hooks);

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
