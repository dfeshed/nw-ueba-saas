import Column from 'respond/utils/group-table/column';
import { module, test } from 'qunit';

module('Unit | Utility | group table/column');

test('it is visible and has some width by default', function(assert) {

  const subject = Column.create();

  const width = subject.get('width');
  assert.ok(width);
  assert.ok(subject.get('visible'));
  assert.ok(subject.get('styleText.string').indexOf(`width:${width}`) > -1, 'Expected styleText to include width');

  const newWidth = '50%';
  subject.set('width', newWidth);
  assert.ok(subject.get('styleText.string').indexOf(`width:${newWidth}`) > -1, 'Expected styleText to update with new width');
});