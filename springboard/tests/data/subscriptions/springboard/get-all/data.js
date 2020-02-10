export default {
  items: [
    {
      id: 'spring_board_1',
      name: 'Analyst Springboard',
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
            name: 'Top Risky Hosts 2',
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
          columnIndex: 3
        },
        {
          columnIndex: 4
        },
        {
          columnIndex: 5
        },
        {
          columnIndex: 6
        }
      ]
    }
  ]
};