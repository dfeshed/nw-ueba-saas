import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  springboardConfig,
  springboardWidgets,
  springboardPagerData,
  isPagerLeftDisabled,
  isPagerRightDisabled
} from 'springboard/reducers/springboard/selectors';

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
  test('Widgets should return list of widgets pager data from springboard config data', function(assert) {
    const state = Immutable.from({
      springboard: {
        activeSpringboardId: 'springboard_1',
        springboards: [
          {
            id: 'springboard_1',
            widgets: [
              {
                columnIndex: 1,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10,
                  content: [
                    {
                      type: 'chart',
                      chartType: 'donut-chart',
                      aggregate: {
                        column: ['hostOsType'],
                        type: 'COUNT'
                      },
                      extraCss: 'flexi-fit'
                    },
                    {
                      type: 'table',
                      columns: ['hostName', 'score', 'hostOsType'],
                      sort: {
                        keys: ['score'],
                        descending: true
                      }
                    }
                  ]
                }
              },
              {
                columnIndex: 2,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10,
                  content: [
                    {
                      type: 'chart',
                      chartType: 'donut-chart',
                      aggregate: {
                        column: ['hostOsType'],
                        type: 'COUNT'
                      },
                      extraCss: 'flexi-fit'
                    },
                    {
                      type: 'table',
                      columns: ['hostName', 'score', 'hostOsType'],
                      sort: {
                        keys: ['score'],
                        descending: true
                      }
                    }
                  ]
                }
              }
            ]
          }
        ]
      }

    });
    const widgets = springboardPagerData(state);
    assert.equal(widgets.length, 2, 'widgets data returned');
  });
  test('Pager left disabled', function(assert) {
    const state1 = Immutable.from({
      springboard: {
        activeSpringboardId: 'springboard_1',
        isPagerLeftDisabled: false,
        isPagerRightDisabled: false,
        pagerPosition: 0,
        defaultActiveLeads: 0,
        springboards: [
          {
            id: 'springboard_1',
            widgets: [
              {
                columnIndex: 1,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              },
              {
                columnIndex: 2,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              }
            ]
          }
        ]
      }

    });
    const state2 = Immutable.from({
      springboard: {
        activeSpringboardId: 'springboard_1',
        isPagerLeftDisabled: false,
        isPagerRightDisabled: false,
        pagerPosition: 1,
        defaultActiveLeads: 0,
        springboards: [
          {
            id: 'springboard_1',
            widgets: [
              {
                columnIndex: 1,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              },
              {
                columnIndex: 2,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              }
            ]
          }
        ]
      }

    });
    const state3 = Immutable.from({
      springboard: {
        activeSpringboardId: 'springboard_1',
        isPagerLeftDisabled: false,
        isPagerRightDisabled: false,
        pagerPosition: 1,
        defaultActiveLeads: 2,
        springboards: [
          {
            id: 'springboard_1',
            widgets: [
              {
                columnIndex: 1,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              },
              {
                columnIndex: 2,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              }
            ]
          }
        ]
      }

    });
    const isLeftDisabled1 = isPagerLeftDisabled(state1);
    const isLeftDisabled2 = isPagerLeftDisabled(state2);
    const isLeftDisabled3 = isPagerLeftDisabled(state3);
    assert.equal(isLeftDisabled1, true, 'pager Left disabled');
    assert.equal(isLeftDisabled2, false, 'pager Left enabled');
    assert.equal(isLeftDisabled3, true, 'pager Left disabled');
  });

  test('Pager right disabled', function(assert) {
    const state1 = Immutable.from({
      springboard: {
        activeSpringboardId: 'springboard_1',
        isPagerLeftDisabled: false,
        isPagerRightDisabled: false,
        pagerPosition: 0,
        defaultActiveLeads: 0,
        springboards: [
          {
            id: 'springboard_1',
            widgets: [
              {
                columnIndex: 1,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              },
              {
                columnIndex: 2,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              }
            ]
          }
        ]
      }

    });
    const state2 = Immutable.from({
      springboard: {
        activeSpringboardId: 'springboard_1',
        isPagerLeftDisabled: false,
        isPagerRightDisabled: false,
        pagerPosition: 1,
        defaultActiveLeads: 0,
        springboards: [
          {
            id: 'springboard_1',
            widgets: [
              {
                columnIndex: 1,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              },
              {
                columnIndex: 2,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              }
            ]
          }
        ]
      }

    });
    const state3 = Immutable.from({
      springboard: {
        activeSpringboardId: 'springboard_1',
        isPagerLeftDisabled: false,
        isPagerRightDisabled: false,
        pagerPosition: 1,
        defaultActiveLeads: 2,
        springboards: [
          {
            id: 'springboard_1',
            widgets: [
              {
                columnIndex: 1,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              },
              {
                columnIndex: 2,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              }
            ]
          }
        ]
      }

    });
    const state4 = Immutable.from({
      springboard: {
        activeSpringboardId: 'springboard_1',
        isPagerLeftDisabled: false,
        isPagerRightDisabled: false,
        pagerPosition: 3,
        defaultActiveLeads: 1,
        springboards: [
          {
            id: 'springboard_1',
            widgets: [
              {
                columnIndex: 1,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              },
              {
                columnIndex: 2,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              },
              {
                columnIndex: 2,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              },
              {
                columnIndex: 2,
                widget: {
                  name: 'Top Risky Hosts 1',
                  leadType: 'hosts',
                  leadCount: 10
                }
              }
            ]
          }
        ]
      }

    });
    const isLeftDisabled1 = isPagerRightDisabled(state1);
    const isLeftDisabled2 = isPagerRightDisabled(state2);
    const isLeftDisabled3 = isPagerRightDisabled(state3);
    const isLeftDisabled4 = isPagerRightDisabled(state4);
    assert.equal(isLeftDisabled1, false, 'pager right enabled');
    assert.equal(isLeftDisabled2, false, 'pager right enabled');
    assert.equal(isLeftDisabled3, true, 'pager right disabled');
    assert.equal(isLeftDisabled4, true, 'pager right disabled');
  });

});