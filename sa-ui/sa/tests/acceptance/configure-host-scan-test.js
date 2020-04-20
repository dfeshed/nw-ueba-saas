import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleForAcceptance('Acceptance | Hosts Scan Route', {
  beforeEach() {
    this.application.inject('route:application', 'session', 'service:session');
    initialize(this.application);
  }
});

test('should display schedule page content with application header when isNwUIPrimary is true', function(assert) {
  this.application.__container__.lookup('service:session').set('isNwUIPrimary', true);
  visit('/configure/hosts-scan');
  andThen(() => {
    assert.equal(find('.rsa-application-header:visible').length, 1);
    assert.equal(find('.rsa-nav-tab-group:visible').length, 1);
  });
});

test('should NOT display schedule page content with application header when isNwUIPrimary is false', function(assert) {
  this.application.__container__.lookup('service:session').set('isNwUIPrimary', false);
  visit('/configure/hosts-scan');
  andThen(() => {
    assert.equal(find('.rsa-nav-tab-group:visible').length, 0);
  });
});

test('should hide the application header', function(assert) {
  visit('/configure/hosts-scan?iframedIntoClassic=true');
  andThen(() => {
    assert.equal(find('.rsa-application-header:visible').length, 0);
    assert.equal(find('.rsa-nav-tab-group:visible').length, 0);
  });
});
