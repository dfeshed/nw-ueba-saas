import Ember from 'ember';
import NotificationHelper from 'sa/protected/respond/mixins/notificationHelper';
import { module, test } from 'qunit';

const { Object: EmberObject } = Ember;

module('Unit | Mixin | protected/respond/mixins/notificationHelper');

test('Public methods are present', function(assert) {
  const containerObject = EmberObject.extend(NotificationHelper);
  const subject = containerObject.create();

  assert.ok(subject.displayFlashErrorLoadingModel, 'displayFlashErrorLoadingModel is present');
  assert.ok(subject.displayEditFieldSuccessMessage, 'displayEditFieldSuccessMessage is present');
  assert.ok(subject.displaySuccessFlashMessage, 'displaySuccessFlashMessage is present');
  assert.ok(subject.displayWarningFlashMessage, 'displayWarningFlashMessage is present');
  assert.ok(subject.displayErrorFlashMessage, 'displayErrorFlashMessage is present');
  assert.ok(subject.displayFatalUnexpectedError, 'displayFatalUnexpectedError is present');
  assert.ok(subject.displayFatalTimeoutError, 'displayFatalTimeoutError is present');
});


// import Ember from 'ember';
// import { moduleFor, test } from 'ember-qunit';
// import NotificationHelper from 'sa/protected/respond/mixins/notificationHelper';

// const { Object: EmberObject, getOwner } = Ember;

// moduleFor('mixin:sa/protected/respond/mixins/notificationHelper', 'Unit | Mixin | protected/respond/mixins/notificationHelper', {
//   needs: ['service:i18n', 'service:fatalErrors', 'service:flashMessages'],

//   subject() {
//     const NotificationHelperContainer = EmberObject.extend(NotificationHelper);
//     this.register('test-container:notification-object', NotificationHelperContainer);
//     return getOwner(this).lookup('test-container:notification-object');
//   }
// });

// test('it works', function(assert) {
//   const subject = this.subject();
//   assert.ok(subject);
// });