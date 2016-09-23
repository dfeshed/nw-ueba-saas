import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

const { run } = Ember;

moduleForComponent('recon-event-detail-files', 'Integration | Component | recon event detail files', {
  integration: true
});

test('it renders', function(assert) {
  const done = assert.async();

  this.render(hbs`{{recon-event-detail/files}}`);
  run.later(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 100);
    assert.equal(str, 'FileNameExtensionMIMETypeFileSizeHashesa_file_name.docxdocxapplication/vnd.openxmlformats-officedocu', 'Recon can change views');
    done();
  }, 200);
});