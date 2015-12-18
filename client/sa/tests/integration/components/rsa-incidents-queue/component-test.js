import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import initializer from 'sa/instance-initializers/ember-i18n';
import Cube from 'sa/utils/cube/base';

moduleForComponent('rsa-incidents-queue', 'Integration | Component | rsa incidents queue', {
  integration: true,

  beforeEach() {

    // Initializes the locale so that i18n content in the component will work.
    // See: https://github.com/jamesarosen/ember-i18n/wiki/Doc:-Testing
    initializer.initialize(this);
  }
});

test('it renders', function(assert) {

  let myCube = Cube.create({});
  myCube.get('records').pushObjects([
      { id: 1, assignee: 'Person 1' },
      { id: 2, assignee: 'Person 2' },
      { id: 3, assignee: 'Person 3' }
  ]);
  assert.ok(myCube, 'Unable to create cube.');

  myCube.filter = function() {
    assert.ok(true, 'Click on All Incidents fired its callback to filter the cube.');
  };

  this.setProperties({
    myCube,
    actions: {
      onclickedrecord() {
        assert.ok(true, 'Clicking on an incident list item fired its callback.');
      }
    }
  });
  this.render(hbs`{{rsa-incidents-queue cube=myCube onselect=(action 'onclickedrecord') onSelectTimeRangeUnit=(action set 'timeRangeUnit')}}`);
  assert.ok(this.$('.rsa-incidents-queue').length, 'Could not find component DOM element.');

  let content = this.$('.rsa-incidents-queue .js-test-respond-incs-all-inc-btn');
  assert.ok(content.length, 'Could not find the All Incidents link.');
  content.trigger('click');

  content = this.$('.rsa-incidents-queue .rsa-incident-li');
  assert.ok(content.length, 'Could not find an Incident list item.');
  content.trigger('click');

});
