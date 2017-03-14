import { module, test } from 'qunit';
import StoryPoint from 'respond/utils/storypoint/storypoint';
import { storyPoints } from 'respond/selectors/storyline';
import { storyline } from '../../server/data';

module('Unit | Mixin | storyline selector');

test('storyPoints selector works', function(assert) {

  const state = {
    respond: {
      incident: {
        storyline: storyline.relatedIndicators
      }
    }
  };

  const isStoryPoint = (obj) => obj instanceof StoryPoint;

  const result = storyPoints(state);

  assert.expect(result.length);
  result.forEach((obj) => {
    assert.ok(isStoryPoint(obj), 'Selector should return an array of StoryPoint objects');
  });
});