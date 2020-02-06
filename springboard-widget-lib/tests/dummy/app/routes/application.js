import Route from '@ember/routing/route';

export default class SpringboardRoute extends Route {

  model() {
    return {
      widget: {
        name: 'Top Risky Hosts',
        leadType: 'Hosts',
        leadCount: 25,
        visualConfig: {
          type: 'donut-chart',
          aggregate: {
            column: ['hostOsType'],
            type: 'COUNT'
          }
        },
        tableConfig: {
          columns: ['hostName', 'score', 'hostOsType'],
          sort: {
            keys: ['score'],
            descending: true
          }
        }
      },
      widgetData: {
        aggregate: {
          data: [
            { name: 'cats', count: 3 },
            { name: 'dogs', count: 10 },
            { name: 'horses', count: 17 }
          ]
        },
        items: [
          {
            hostName: 'Test',
            score: '100',
            hostOsType: 'Windows'
          },
          {
            hostName: 'Test2',
            score: '80',
            hostOsType: 'Linux'
          },
          {
            hostName: 'Test3',
            score: '10',
            hostOsType: 'windows'
          },
          {
            hostName: 'Test4',
            score: '100',
            hostOsType: 'Mac'
          }
        ]
      }
    };
  }
}
