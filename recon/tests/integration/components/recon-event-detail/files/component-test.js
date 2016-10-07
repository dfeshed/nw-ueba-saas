import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import DataActions from 'recon/actions/data-creators';
import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';


const { run } = Ember;

moduleForComponent('recon-event-detail-files', 'Integration | Component | recon event detail files', {
  integration: true,
  setup() {
    this.inject.service('redux');
  }
});

test('it renders', function(assert) {
  const done = assert.async();
  this.get('redux').dispatch(DataActions.setNewReconView(RECON_VIEW_TYPES_BY_NAME.FILE));

  this.render(hbs`{{recon-event-detail/files}}`);
  run.later(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 100);
    assert.equal(str, 'FileNameMIMETypeFileSizeHashesa_file_name.docxapplication/vnd.openxmlformats-officedocument.wordproc', 'Recon can change views');
    done();
  }, 400);
});