import { test } from 'qunit';
import moduleForAcceptance from '../../tests/helpers/module-for-acceptance';
import Ember from 'ember';
const { run } = Ember;

moduleForAcceptance('Acceptance | basic recon functionality');

test('show/hide header items', function(assert) {
  visit('/');

  andThen(function() {
    run.later(this, function() {
      assert.ok(find('.recon-event-header .header-item').length > 0, 'Header items shown');
    }, 1000);
  });

  click('.recon-event-titlebar .toggle-header');

  andThen(function() {
    run.later(this, function() {
      assert.ok(find('.recon-event-header .header-item').length === 0, 'Header items hidden');
    }, 1000);
  });
});

test('show/hide meta', function(assert) {
  visit('/');

  andThen(function() {
    run.later(this, function() {
      assert.ok(find('.recon-meta-content').length === 0, 'Meta is hidden');
    }, 1000);
  });

  click('.recon-event-titlebar .toggle-meta');

  andThen(function() {
    run.later(this, function() {
      assert.ok(find('.recon-meta-content').length === 1, 'Meta is shown');
    }, 1000);
  });
});

test('change recon views', function(assert) {
  visit('/');

  andThen(function() {
    // run.later is a hack, but nothing else seems to work
    run.later(function() {
      find('.prompt').click();
      find('select').val('2').trigger('change');
    }, 1000);
  });

  andThen(function() {
    run.later(function() {
      const str = find('.recon-event-content .scroll-box').text().trim().replace(/\s/g, '').substring(0, 100);
      assert.equal(str, 'FileNameExtensionMIMETypeFileSizeHashesa_file_name.docxdocxapplication/vnd.openxmlformats-officedocu', 'Recon can change views');
    }, 1000);
  });

});
