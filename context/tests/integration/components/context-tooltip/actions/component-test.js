import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

moduleForComponent('context-tooltip', 'Integration | Component | context tooltip actions', {
  integration: true
});

const defaultAction = { glyph: 'defaultGlyph', text: 'defaultText' };
const ipAction = { icon: 'ipIcon', text: 'ipText' };
const ipAction2 = { icon: 'ipIcon2', text: 'ipText2' };
const actionsMap = {
  default: [ defaultAction ],
  IP: [ ipAction, ipAction2 ]
};

test('it renders an actionsList based on the entityType', function(assert) {
  let actionIcons, actionGlyphs;

  this.setProperties({
    actionsMap,
    entityType: 'IP'
  });

  this.render(hbs`{{context-tooltip/actions entityType=entityType actionsMap=actionsMap}}`);

  return wait()
    .then(() => {
      assert.equal(this.$('.rsa-context-tooltip-actions').length, 1, 'Expected to find root DOM node');

      actionIcons = this.$('.action .icon .rsa-icon');
      assert.equal(actionIcons.length, 2, 'Expected to find two action icons');

      actionGlyphs = this.$('.action .icon .glyph');
      assert.notOk(actionGlyphs.length, 'Expected to not find any glyphs');

      this.set('entityType', 'unknown');

      return wait();
    })
    .then(() => {
      actionIcons = this.$('.action .icon .rsa-icon');
      assert.notOk(actionIcons.length, 'Expected to not find any icons');

      actionGlyphs = this.$('.action .icon .glyph');
      assert.ok(actionGlyphs.text().trim(), 'Expected to find one action glyph');
    });
});
