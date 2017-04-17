import { skip } from 'qunit';
import moduleForAcceptance from '../../tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | basic recon functionality');

skip('show/hide header items', (assert) => {
  visit('/');
  waitForReduxStateChange('recon.data.headerItems');
  andThen(() => {
    assert.equal(find('.recon-event-header .header-item').length, 12, 'Header items shown');
    click('.recon-event-titlebar .toggle-header');
  });
  waitForReduxStateToEqual('recon.visuals.isHeaderOpen', false);
  andThen(() => {
    assert.equal(find('.recon-event-header .header-item').length, 0, 'Header items hidden');
  });
});

skip('show/hide meta', (assert) => {
  visit('/');
  waitForReduxStateChange('recon.data.headerItems');
  andThen(() => {
    assert.ok(find('.recon-meta-content').length === 0, 'Meta is hidden');
    click('.recon-event-titlebar .toggle-meta');
  });
  waitForReduxStateChange('recon.meta.meta');
  andThen(() => {
    assert.ok(find('.recon-meta-content').length === 1, 'Meta is shown');
  });
});

skip('change recon views', (assert) => {
  visit('/');

  waitForReduxStateChange('recon.packets.packets');

  andThen(() => {
    assert.ok(find('.recon-event-detail-packets').length > 0, 'Recon initially on packet view');
    selectChoose('.recon-view-selector', 'File View');
    andThen(() => {
      waitForReduxStateChange('recon.files.files');
      andThen(() => {
        assert.ok(find('.recon-event-detail-files').length > 0, 'Recon can change to file view');
        assert.ok(find('.recon-event-detail-packets').length === 0, 'Packet view is gone');
      });
    });
  });
});

skip('toggle request on/off', (assert) => {
  visit('/');

  waitForReduxStateChange('recon.packets.packets');
  andThen(() => {
    click('.rsa-icon-arrow-circle-right-2');
  });
  andThen(() => {
    assert.ok(find('.rsa-packet.request').first().height() < 5, 'Requests are hidden');
    click('.rsa-icon-arrow-circle-right-2');
  });
  andThen(() => {
    assert.ok(find('.rsa-packet.request').first().height() > 0, 'Requests are shown');
  });
});

skip('toggle response on/off', (assert) => {
  visit('/');
  waitForReduxStateChange('recon.packets.packets');
  click('.rsa-icon-arrow-circle-left-2');
  andThen(() => {
    // Height should be 0, as the response ones are hidden, but their containers are still there
    assert.ok(find('.rsa-packet.response').first().height() < 5, 'Responses are hidden');
    click('.rsa-icon-arrow-circle-left-2');
    andThen(() => {
      // Height should be > 0, as the responses are now shown
      assert.ok(find('.rsa-packet.response').first().height() > 0, 'Responses are shown');
    });
  });
});
