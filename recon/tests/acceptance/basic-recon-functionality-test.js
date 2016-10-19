import { test } from 'qunit';
import moduleForAcceptance from '../../tests/helpers/module-for-acceptance';
import Ember from 'ember';
const { run } = Ember;

moduleForAcceptance('Acceptance | basic recon functionality');

test('show/hide header items', function(assert) {
  visit('/');

  run.later(this, function() {
    andThen(function() {
      assert.ok(find('.recon-event-header .header-item').length > 0, 'Header items shown');
    });
    click('.recon-event-titlebar .toggle-header');
    andThen(function() {
      assert.ok(find('.recon-event-header .header-item').length === 0, 'Header items hidden');
    });
  }, 2000);
});

test('show/hide meta', function(assert) {
  visit('/');

  run.later(this, function() {
    andThen(function() {
      assert.ok(find('.recon-meta-content').length === 0, 'Meta is hidden');
    });
    click('.recon-event-titlebar .toggle-meta');
    andThen(function() {
      assert.ok(find('.recon-meta-content').length === 1, 'Meta is shown');
    });
  }, 2000);
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
      assert.equal(str, 'FileNameMIMETypeFileSizeHashesa_file_name.docxapplication/vnd.openxmlformats-officedocument.wordproc', 'Recon can change views');
    }, 1000);
  });
});

test('toggle request on/off', function(assert) {
  visit('/');

  run.later(function() {
    // Toggle request off
    click('.rsa-icon-arrow-circle-right-2');

    andThen(function() {
      // Height should be 0, as the request ones are hidden, but their containers are still there
      assert.equal(find('.rsa-packet.request').first().height(), 0, 'Requests are hidden');
    });

    // Toggle request on
    click('.rsa-icon-arrow-circle-right-2');

    andThen(function() {
      // Height should be > 0, as the requests are now shown
      assert.ok(find('.rsa-packet.request').first().height() > 0, 'Requests are shown');
    });
  }, 2000);
});

test('toggle response on/off', function(assert) {
  visit('/');

  run.later(function() {
    // Toggle response off
    click('.rsa-icon-arrow-circle-left-2');

    andThen(function() {
      // Height should be 0, as the response ones are hidden, but their containers are still there
      assert.equal(find('.rsa-packet.response').first().height(), 0, 'Responses are hidden');
    });

    // Toggle response on
    click('.rsa-icon-arrow-circle-left-2');

    andThen(function() {
      // Height should be > 0, as the responses are now shown
      assert.ok(find('.rsa-packet.response').first().height() > 0, 'Responses are shown');
    });
  }, 2000);
});
