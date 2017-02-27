import { module, test } from 'qunit';
import StoryPoint from 'respond/utils/storypoint/storypoint';
import { storypoints } from 'respond/selectors/storyline';
import storyline from '../../data/storyline';

module('Unit | Mixin | storyline selector');

test('storypoints selector works', function(assert) {

  const state = {
    incident: {
      storyline
    }
  };

  const isStoryPoint = (obj) => obj instanceof StoryPoint;

  const result = storypoints(state);

  assert.expect(result.length);
  result.forEach((obj) => {
    assert.ok(isStoryPoint(obj), 'Selector should return an array of StoryPoint objects');
  });
});