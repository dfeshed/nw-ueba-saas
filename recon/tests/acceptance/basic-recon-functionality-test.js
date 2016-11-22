import { test } from 'qunit';
import moduleForAcceptance from '../../tests/helpers/module-for-acceptance';
import Ember from 'ember';
const { run } = Ember;

moduleForAcceptance('Acceptance | basic recon functionality');

test('show/hide header items', (assert) => {
  visit('/');

  run.later(() => {
    andThen(() => {
      assert.ok(find('.recon-event-header .header-item').length > 0, 'Header items shown');
    });

    click('.recon-event-titlebar .toggle-header');

    andThen(() => {
      assert.ok(find('.recon-event-header .header-item').length === 0, 'Header items hidden');
    });
  }, 2000);
});

test('show/hide meta', (assert) => {
  visit('/');

  run.later(() => {
    andThen(() => {
      assert.ok(find('.recon-meta-content').length === 0, 'Meta is hidden');
    });

    click('.recon-event-titlebar .toggle-meta');

    andThen(() => {
      assert.ok(find('.recon-meta-content').length === 1, 'Meta is shown');
    });
  }, 2000);
});

test('change recon views', (assert) => {
  visit('/');

  andThen(() => {
    run.later(() => {
      assert.ok(find('.recon-event-detail-packets').length > 0, 'Recon initially on packet view');
      selectChoose('.recon-view-selector', 'File View');

      andThen(() => {
        assert.ok(find('.recon-event-detail-files').length > 0, 'Recon can change to file view');
        assert.ok(find('.recon-event-detail-packets').length === 0, 'Packet view is gone');
      });
    }, 2000);
  });
});

test('toggle request on/off', (assert) => {
  visit('/');

  run.later(() => {
    // Toggle request off
    click('.rsa-icon-arrow-circle-right-2');

    andThen(() => {
      // Height should be 0, as the request ones are hidden, but their containers are still there
      assert.equal(find('.rsa-packet.request').first().height(), 0, 'Requests are hidden');
    });

    // Toggle request on
    click('.rsa-icon-arrow-circle-right-2');

    andThen(() => {
      // Height should be > 0, as the requests are now shown
      assert.ok(find('.rsa-packet.request').first().height() > 0, 'Requests are shown');
    });
  }, 2000);
});

test('toggle response on/off', (assert) => {
  visit('/');

  run.later(() => {
    // Toggle response off
    click('.rsa-icon-arrow-circle-left-2');

    andThen(() => {
      // Height should be 0, as the response ones are hidden, but their containers are still there
      assert.equal(find('.rsa-packet.response').first().height(), 0, 'Responses are hidden');
    });

    // Toggle response on
    click('.rsa-icon-arrow-circle-left-2');

    andThen(() => {
      // Height should be > 0, as the responses are now shown
      assert.ok(find('.rsa-packet.response').first().height() > 0, 'Responses are shown');
    });
  }, 2000);
});
