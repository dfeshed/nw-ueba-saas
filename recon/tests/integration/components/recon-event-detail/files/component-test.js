import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import * as ACTION_TYPES from 'recon/actions/types';
import DataActions from 'recon/actions/data-creators';

import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';

const { run } = Ember;

moduleForComponent('recon-event-detail-files', 'Integration | Component | recon event detail files', {
  integration: true,
  setup() {
    this.inject.service('redux');
  }
});

test('it renders files', function(assert) {
  const done = assert.async();
  this.get('redux').dispatch(DataActions.setNewReconView(RECON_VIEW_TYPES_BY_NAME.FILE));

  this.render(hbs`{{recon-event-detail/files}}`);
  run.later(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'FileNameMIMETypeFileSizeHashesa_file_name.docxapplication/vnd.openxmlformats-officedocument.wordprocessingml.document305.1KBmd5:f71f80a9cb8e24b06419a895cadd1a47sha1:5a39b799a8f63cf4dd774d4ee024715ed25');
    done();
  }, 400);
});

test('it renders an empty message when no files', function(assert) {
  const done = assert.async();
  this.get('redux').dispatch({ type: ACTION_TYPES.FILES_RETRIEVE_SUCCESS, payload: [] });
  this.render(hbs`{{recon-event-detail/files}}`);
  run.later(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 100);
    assert.equal(str, 'Therearenofilesavailableforthisevent.');
    done();
  }, 400);
});

test('with 4 rows of data, 5 checkboxes total, has one in header', function(assert) {
  const done = assert.async();
  this.get('redux').dispatch(DataActions.setNewReconView(RECON_VIEW_TYPES_BY_NAME.FILE));

  this.render(hbs`{{recon-event-detail/files}}`);
  run.later(() => {
    const numInputs = this.$('input').length;
    assert.equal(numInputs, 5);
    done();
  }, 400);
});

test('clicking top checkbox clicks them all', function(assert) {
  const done = assert.async();
  this.get('redux').dispatch(DataActions.setNewReconView(RECON_VIEW_TYPES_BY_NAME.FILE));

  this.render(hbs`{{recon-event-detail/files}}`);
  assert.equal(this.$('.is-selected').length, 0);
  run.later(() => {
    this.$('input').first().click();
    run.later(() => {
      assert.equal(this.$('.is-selected').length, 5);
      done();
    }, 600);
  }, 400);
});

test('with 1 rows of data, 1 checkboxes total, none in header', function(assert) {
  const done = assert.async();
  this.get('redux').dispatch(DataActions.setNewReconView(RECON_VIEW_TYPES_BY_NAME.FILE));

  this.render(hbs`{{recon-event-detail/files}}`);
  run.later(() => {
    this.get('redux').dispatch({
      type: ACTION_TYPES.FILES_RETRIEVE_SUCCESS,
      payload: [{
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
      }]
    });

    run.later(() => {
      const numInputs = this.$('input').length;
      assert.equal(numInputs, 1);
      done();
    }, 400);
  }, 400);
});
