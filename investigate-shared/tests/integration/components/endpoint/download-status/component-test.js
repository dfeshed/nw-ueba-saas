import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/download-status', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('Download status renders', async function(assert) {
    this.set('downloadInfo', { status: 'Downloaded' });
    this.set('checksum', '3c3ec5cffb9ec28c2e7810cc536e8b560b6ad7b1245ad963d79a4dca1e0a7c76');

    await render(hbs`{{endpoint/download-status downloadInfo=downloadInfo checksum=checksum}}`);

    assert.equal(findAll('.download-status').length, 1, 'Download status has rendered.');
    assert.equal(findAll('.download-status .downloadToServerError3c3ec').length, 0, 'Tethered panel not present in successful state');
    assert.equal(findAll('.rsa-green-color.rsa-icon-check-2-filled').length, 1, 'Successful Download status has rendered.');
  });

  test('Download status renders with error', async function(assert) {
    this.set('downloadInfo', { status: 'Error', error: 'Error message' });
    this.set('checksum', '3c3ec5cffb9ec28c2e7810cc536e8b560b6ad7b1245ad963d79a4dca1e0a7c76');

    await render(hbs`{{endpoint/download-status downloadInfo=downloadInfo checksum=checksum}}`);

    assert.equal(findAll('.download-status .downloadToServerError3c3ec').length, 1, 'Tethered panel present in error state');
    assert.equal(findAll('.rsa-red-color.rsa-icon-delete-1-filled').length, 1, 'Error Download status has rendered.');
  });

  test('Download status renders with not downloaded', async function(assert) {
    this.set('downloadInfo', { status: 'NotDownloaded' });
    this.set('checksum', '3c3ec5cffb9ec28c2e7810cc536e8b560b6ad7b1245ad963d79a4dca1e0a7c76');

    await render(hbs`{{endpoint/download-status downloadInfo=downloadInfo checksum=checksum}}`);

    assert.equal(findAll('.download-status .downloadToServerError3c3ec').length, 0, 'Tethered panel not present in not downloaded state');
    assert.equal(findAll('.download-status  .not-downloaded').length, 1, 'Not downloaded status has rendered.');
    assert.equal(find('.download-status  .not-downloaded').textContent.trim(), '--', 'Not downloaded status has rendered.');
  });

  test('Download status renders when no download info is passed', async function(assert) {
    this.set('downloadInfo', undefined);
    this.set('checksum', '3c3ec5cffb9ec28c2e7810cc536e8b560b6ad7b1245ad963d79a4dca1e0a7c76');

    await render(hbs`{{endpoint/download-status downloadInfo=downloadInfo checksum=checksum}}`);

    assert.equal(findAll('.download-status .downloadToServerError3c3ec').length, 0, 'Tethered panel not present in not downloaded state');
    assert.equal(findAll('.download-status  .not-downloaded').length, 1, 'Not downloaded status has rendered when downloadInfo is not present.');
    assert.equal(find('.download-status  .not-downloaded').textContent.trim(), '--', 'Not downloaded status has rendered when downloadInfo is not present.');
  });
});
