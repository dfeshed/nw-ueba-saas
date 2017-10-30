import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import DataHelper from '../../../../helpers/data-helper';

moduleForComponent('recon-event-detail-files', 'Integration | Component | recon event detail files', {
  integration: true,
  beforeEach() {
    this.registry.injection('component:rsa-content-memsize', 'i18n', 'service:i18n');
    this.inject.service('redux');
    initialize(this);
  }
});

test('it renders files', function(assert) {
  new DataHelper(this.get('redux')).populateFiles();
  this.render(hbs`{{recon-event-detail/files}}`);
  return wait().then(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'FileNameMIMETypeFileSizeHashesa_file_name.docxapplication/vnd.openxmlformats-officedocument.wordprocessingml.document305.1KBMD5:f71f80a9cb8e24b06419a895cadd1a47SHA1:5a39b799a8f63cf4dd774d4ee024715ed25');
  });
});

test('it renders an empty message when no files', function(assert) {
  new DataHelper(this.get('redux')).populateFiles([]);
  this.render(hbs`{{recon-event-detail/files}}`);
  return wait().then(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 100);
    assert.equal(str, 'Therearenofilesavailableforthisevent.');
  });
});

test('with 4 non-linked files + 1 linked file, 5 checkboxes total, has one in header', function(assert) {
  new DataHelper(this.get('redux')).populateFiles();
  this.render(hbs`{{recon-event-detail/files}}`);
  return wait().then(() => {
    const numInputs = this.$('input').length;
    assert.equal(numInputs, 5);
  });
});

test('clicking top checkbox clicks them all', function(assert) {
  new DataHelper(this.get('redux')).populateFiles();
  this.render(hbs`{{recon-event-detail/files}}`);
  assert.equal(this.$('input.checked').length, 0);
  return wait().then(() => {
    this.$('input').first().click();
    return wait().then(() => {
      assert.equal(this.$('input.checked').length, 5);
    });
  });
});

test('with 1 non-linked file only, 1 checkboxes total, none in header', function(assert) {
  const files = [{
    type: 'session',
    extension: 'docx',
    fileName: 'a_file_name.docx',
    mimeType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    id: '1234125',
    fileSize: 312412,
    hashes: [{
      type: 'md5',
      value: 'f71f80a9cb8e24b06419a895cadd1a47'
    }, {
      type: 'sha1',
      value: '5a39b799a8f63cf4dd774d4ee024715ed25e252a'
    }]
  }];

  new DataHelper(this.get('redux')).populateFiles(files);
  this.render(hbs`{{recon-event-detail/files}}`);
  return wait().then(() => {
    const numInputs = this.$('input').length;
    assert.equal(numInputs, 1);
  });
});

test('with 1 linked file only, 0 checkboxes total, none in header', function(assert) {
  const files = [{
    type: 'link',
    extension: 'zip',
    fileName: 'a_file_name.zip',
    mimeType: 'application/zip',
    id: null,
    fileSize: 0,
    hashes: [],
    query: 'a_query',
    start: 'a_start',
    end: 'an_end'
  }];

  new DataHelper(this.get('redux')).populateFiles(files);
  this.render(hbs`{{recon-event-detail/files}}`);
  return wait().then(() => {
    const numInputs = this.$('input').length;
    assert.equal(numInputs, 0);
  });
});
