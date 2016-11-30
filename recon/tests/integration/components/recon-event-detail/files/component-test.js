import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import DataHelper from '../../../../helpers/data-helper';

moduleForComponent('recon-event-detail-files', 'Integration | Component | recon event detail files', {
  integration: true,
  setup() {
    this.inject.service('redux');
  }
});

test('it renders files', function(assert) {
  new DataHelper(this.get('redux')).populateFiles();
  this.render(hbs`{{recon-event-detail/files}}`);
  return wait().then(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'FileNameMIMETypeFileSizeHashesa_file_name.docxapplication/vnd.openxmlformats-officedocument.wordprocessingml.document305.1KBmd5:f71f80a9cb8e24b06419a895cadd1a47sha1:5a39b799a8f63cf4dd774d4ee024715ed25');
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

test('with 4 rows of data, 5 checkboxes total, has one in header', function(assert) {
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
  assert.equal(this.$('.is-selected').length, 0);
  return wait().then(() => {
    this.$('input').first().click();
    return wait().then(() => {
      assert.equal(this.$('.is-selected').length, 5);
    });
  });
});

test('with 1 rows of data, 1 checkboxes total, none in header', function(assert) {
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
