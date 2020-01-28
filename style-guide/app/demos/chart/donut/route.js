import Route from '@ember/routing/route';

export default Route.extend({
  model() {
    return {
      title: 'Donut',
      description: 'Creates an SVG Path element used to represent a data as a donut chart. ',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-chart-donut/component.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_chart.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-chart-donut/template.hbs',
      properties: [
        {
          name: 'data',
          type: 'array',
          required: true,
          description: 'The data to render. The objects within the arrays should be flat Objects.'
        },
        {
          name: 'radius',
          type: 'number',
          required: false,
          description: 'Radius value for the donut chart',
          default: '120'
        },
        {
          name: 'innerRadius',
          type: 'number',
          required: false,
          description: 'Inner radius to form donut',
          default: '100'
        },
        {
          name: 'showLegend',
          type: 'boolean',
          required: false,
          description: 'To control display of legend, if set to false then legends are not visible',
          default: 'true'
        },
        {
          name: 'showNoResultMessage',
          type: 'boolean',
          required: false,
          description: 'If set to false then no result messages are not visible',
          default: 'true'
        },
        {
          name: 'width',
          type: 'number | string',
          required: false,
          description: 'The width of the parent SVG element. Can be expressed as a fixed number like "800" or as a percentage like "90%".',
          default: 600
        },
        {
          name: 'height',
          type: 'number | string',
          required: false,
          description: 'The height of the parent SVG element. Can be expressed as a fixed number like "200" or as a percentage like "100%".',
          default: 150
        },
        {
          name: 'colorScale',
          type: 'string',
          required: false,
          description: 'Color range for the donut chart, currently supported values are [BLUE, GREEN, RED, ORANGE and MULTI]',
          default: 'BLUE'
        },
        {
          name: 'columnName',
          type: 'string',
          required: false,
          description: 'Aggregate column name which is displayed in center of the donut',
          default: 'BLUE'
        }
      ],
      options: {
        radius: 150,
        innerRadius: 125,
        colorScale: 'MULTI'
      },
      columnName: 'Operating System',
      chartData: [
        {
          name: 'Windows',
          count: 55
        },
        {
          name: 'Mac',
          count: 15
        },
        {
          name: 'Linux',
          count: 35
        },
        {
          name: 'Others',
          count: 5
        }
      ]
    };
  }
});
