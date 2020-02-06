import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { springboardConfig, springboardWidgets } from 'springboard/reducers/springboard/selectors';

module('Unit | Selectors | Springboard', function() {

  test('springboardConfig should return the configuration based on active id', function(assert) {
    const state = Immutable.from({
      springboard: {
        activeSpringboardId: 'springboard_1',
        springboards: [
          {
            id: 'springboard_1'
          }
        ]
      }

    });
    const config = springboardConfig(state);
    assert.equal(config.id, 'springboard_1', 'Correct config');
  });

  test('springboardConfig should return the null if id is not set', function(assert) {
    const state1 = Immutable.from({
      springboard: {
        activeSpringboardId: null,
        springboards: [
          {
            id: 'springboard_1'
          }
        ]
      }

    });

    const state2 = Immutable.from({
      springboard: {
        activeSpringboardId: 1,
        springboards: []
      }

    });

    const config1 = springboardConfig(state1);
    const config2 = springboardConfig(state2);
    assert.equal(config1, null, 'Correct config');
    assert.equal(config2, null, 'Correct config');
  });


  test('widgets should return list of widgets from active springboard', function(assert) {
    const state = Immutable.from({
      springboard: {
        activeSpringboardId: 'springboard_1',
        springboards: [
          {
            id: 'springboard_1',
            widgets: [
              {
                columnIndex: 1
              },
              {
                columnIndex: 2
              }
            ]
          }
        ]
      }

    });
    const widgets = springboardWidgets(state);
    const widget2 = springboardWidgets(Immutable.from({ springboard: {} }));
    assert.equal(widgets.length, 2, 'widgets are returned');
    assert.equal(widget2, null);
  });
});