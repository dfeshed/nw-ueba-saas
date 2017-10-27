import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import startApp from '../../../../helpers/start-app';

import VisualActions from 'recon/actions/visual-creators';
import DataHelper from '../../../../helpers/data-helper';

moduleForComponent('recon-event-detail/text-content', 'Integration | Component | recon event detail text', {
  integration: true,
  beforeEach() {
    this.registry.injection('component:recon-event-detail/text-content', 'i18n', 'service:i18n');
    this.registry.injection('component:recon-event-detail/single-text', 'i18n', 'service:i18n');
    this.inject.service('redux');
    const application = startApp();
    initialize(application);
  }
});

test('text view renders encoded text', function(assert) {
  new DataHelper(this.get('redux')).populateTexts();
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'requestGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1$Host:www.saavn.comresponseHTTP/1.1200OKCache-control:no-store,no-cache,must-revalidate,');
  });
});

test('text view renders decoded text', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToText()
    .populateTexts(true);
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'requestGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1Host:www.saavn.comConnection:keep-aliveAccept:*/*X-Requested-With:XMLHttpRequestUser-Age');
  });
});

test('text view renders log text', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData({ meta: [['medium', 32]] })
    .setViewToText()
    .populateTexts();
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'RawLogGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1$Host:www.saavn.comRawLogHTTP/1.1200OKCache-control:no-store,no-cache,must-revalidate,pri');
  });
});

test('renders spinner when data present but in the process of being rendered', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToText()
    .populateTexts(false, false);
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const loader = this.$('.recon-loader').length;
    assert.equal(loader, 1);
  });
});

test('renders error when no data present', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToText()
    .noTexts();
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const str = this.$('.rsa-panel-message').text().trim().replace(/\s/g, '');
    assert.equal(str, 'Notextdatawasgeneratedduringcontentreconstruction.Thiscouldmeanthattheeventdatawascorruptorinvalid.Checktheotherreconstructionviews.');
  });
});

test('renders nothing when data present, but hidden by request/response', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToText()
    .populateTexts();

  this.get('redux').dispatch(VisualActions.toggleRequestData());
  this.get('redux').dispatch(VisualActions.toggleResponseData());

  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    // remove the pager so its text doesn't confuse test
    this.$('.recon-pager').remove();

    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, '');
  });
});
