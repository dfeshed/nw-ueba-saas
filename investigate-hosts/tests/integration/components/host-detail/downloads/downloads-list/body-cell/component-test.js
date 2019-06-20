import { module, test, setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { findAll, find, render, click } from '@ember/test-helpers';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import EmberObject from '@ember/object';

module('Integration | Component | host-detail/downloads/downloads-list/body-cell', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders the checkbox column', async function(assert) {
    this.set('column', { componentClass: 'rsa-form-checkbox' });
    this.set('selections', [{ id: '5cda8882c8811e511649e335' }]);
    this.set('item', { id: '5cda8882c8811e511649e335', fileType: 'Mft', agentId: 'A0351965-30D0-2201-F29B-FDD7FD32EB21', serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0' });
    this.set('checkBoxAction', (id) => {
      assert.equal(id, 1);
    });
    await render(hbs`{{host-detail/downloads/downloads-list/body-cell selections=selections column=column item=item checkBoxAction=(action checkBoxAction 1)}}`);

    assert.equal(findAll('.rsa-form-checkbox').length, 1);
    assert.equal(findAll('.rsa-form-checkbox:checked').length, 1, 'Expecting to select the checkbox');
    await click('.rsa-form-checkbox');
    assert.equal(findAll('.rsa-form-checkbox:checked').length, 0, 'Expecting to un-select the checkbox');
  });

  test('it should render downloaded file size', async function(assert) {
    this.set('column', { field: 'size' });
    this.set('item', { id: '5cda8882c8811e511649e335', size: 1080 });

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item}}`);
    assert.equal(find('.size').textContent.includes('1.1'), true, 'File size rendered in kb');
  });

  test('it should render filename', async function(assert) {
    this.set('column', { field: 'fileName' });
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile' });

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item}}`);
    assert.equal(findAll('.downloaded-file-name').length, 1, 'Renders file name');
    assert.equal(find('.fileName').textContent.trim(), 'testFile', 'Renders file name');
  });

  test('it should render the downloaded time', async function(assert) {
    const column = EmberObject.create({ field: 'downloadedTime' });
    this.set('column', column);
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile', downloadedTime: '2019-05-14T09:21:06.923+0000' });

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item}}`);
    assert.equal(findAll('.downloadedTime').length, 1, 'Expected to render downloaded time');
  });

  test('it should render the downloaded status', async function(assert) {
    this.set('column', { field: 'status' });
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile', status: 'Downloaded' });

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item}}`);
    assert.equal(findAll('.status').length, 1, 'Expected to render downloaded time');
  });

  test('it should render the checksum if present', async function(assert) {
    this.set('column', { field: 'checksumSha256' });
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile', checksumSha256: 'c34f34t3' });

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item}}`);
    assert.equal(find('.checksumSha256').textContent.trim(), 'c34f34t3', 'Expected to render checksum');
  });

  test('it should render NA if checksum is not present', async function(assert) {
    this.set('column', { field: 'checksumSha256' });
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile' });

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item}}`);
    assert.equal(find('.checksumSha256').textContent.trim(), 'NA', 'render NA if checksum is not present');
  });
});
