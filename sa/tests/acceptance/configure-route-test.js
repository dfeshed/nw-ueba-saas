import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleForAcceptance('Acceptance | Configure Route', {
  beforeEach() {
    this.application.inject('route:application', 'session', 'service:session');
    initialize(this.application);
  }
});

test('should have an active Configure tab in the navigation header when isNwUIPrimary is true', function(assert) {
  this.application.__container__.lookup('service:session').set('isNwUIPrimary', true);
  visit('/configure');
  andThen(() => {
    assert.equal(find('.rsa-header-nav-configure.active').length, 1);
  });
});

test('should NOT have an active Configure tab in the navigation header when isNwUIPrimary is false', function(assert) {
  this.application.__container__.lookup('service:session').set('isNwUIPrimary', false);
  visit('/configure');
  andThen(() => {
    assert.equal(find('.rsa-header-nav-configure.active').length, 0);
  });
});
