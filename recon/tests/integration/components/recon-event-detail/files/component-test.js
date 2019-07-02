import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import DataHelper from '../../../../helpers/data-helper';
import sinon from 'sinon';
import buildInvestigateEventsUrlUtils from 'component-lib/utils/build-url';

const _getChars = (amt, str) => str.trim().replace(/\s/g, '').substring(0, amt);

moduleForComponent('recon-event-detail-files', 'Integration | Component | recon event detail files', {
  integration: true,
  beforeEach() {
    this.registry.injection('component:rsa-content-memsize', 'i18n', 'service:i18n');
    this.inject.service('redux');
  }
});

test('it renders files', function(assert) {
  new DataHelper(this.get('redux')).populateFiles();
  this.render(hbs`{{recon-event-detail/files}}`);
  return wait().then(() => {
    const str = _getChars(200, document.querySelector('.recon-event-detail-files').textContent);
    assert.equal(str, 'FileNameMIMETypeFileSizeHashesa_file_name.docxapplication/vnd.openxmlformats-officedocument.wordprocessingml.document305.1KBMD5:f71f80a9cb8e24b06419a895cadd1a47SHA1:5a39b799a8f63cf4dd774d4ee024715ed25');
  });
});

test('it renders an empty message when no files', function(assert) {
  new DataHelper(this.get('redux')).populateFiles([]);
  this.render(hbs`{{recon-event-detail/files}}`);
  return wait().then(() => {
    const str = _getChars(100, document.querySelector('.recon-event-detail-files').textContent);
    assert.equal(document.querySelectorAll('.recon-pager').length, 1);
    assert.equal(str, 'Therearenofilesavailableforthisevent.');
  });
});

test('with 4 non-linked files + 1 linked file, 5 checkboxes total, has one in header', function(assert) {
  new DataHelper(this.get('redux')).populateFiles();
  this.render(hbs`{{recon-event-detail/files}}`);
  return wait().then(() => {
    assert.equal(document.querySelectorAll('.recon-event-detail-files input').length, 5);
  });
});

test('clicking top checkbox clicks them all', function(assert) {
  new DataHelper(this.get('redux')).populateFiles();
  this.render(hbs`{{recon-event-detail/files}}`);
  assert.equal(document.querySelectorAll('.recon-event-detail-files input.checked').length, 0);
  return wait().then(() => {
    document.querySelectorAll('.recon-event-detail-files input')[0].click();
    return wait().then(() => {
      assert.equal(document.querySelectorAll('.recon-event-detail-files input.checked').length, 5);
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
    assert.equal(document.querySelectorAll('.recon-event-detail-files input').length, 1);
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
    assert.equal(document.querySelectorAll('.recon-event-detail-files input').length, 0);
  });
});

test('file on click calls linkToFile util when linkToFileAction is not passed', function(assert) {
  const linkToFileMock = sinon.stub(buildInvestigateEventsUrlUtils, 'buildInvestigateEventsFileLinkUrl');
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

  new DataHelper(this.get('redux')).initializeData().populateFiles(files);
  this.render(hbs`{{recon-event-detail/files}}`);
  return wait().then(() => {
    document.querySelector('.recon-file-ellipsis.recon-file-link').click();
    assert.ok(linkToFileMock.calledOnce, 'linkToFile called when linkToFileAction not provided');
  });
});
