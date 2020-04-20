import contextHelpUtil from 'context/utils/help-ids';
import { module, test } from 'qunit';

module('Unit | Utility | help-ids', function() {

  test('contextHelpIds should return help ids for Investigate', function(assert) {
    const helpId = contextHelpUtil.contextHelpIds();
    assert.ok(helpId.panelHelpId.moduleId === 'investigation');
    assert.ok(helpId.panelHelpId.topicId === 'invContextPnl2');
    assert.ok(helpId.AddToListHelpIds.moduleId === 'investigation');
    assert.ok(helpId.AddToListHelpIds.topicId === 'invAddToList2');
  });
});
