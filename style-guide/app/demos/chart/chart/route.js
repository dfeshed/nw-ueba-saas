import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({
  model() {
    return {
      title: 'Chart',
      description: 'Creates a base SVG object that is used for creating single or multi series charts. The main responsibility of this component is to construct the base SVG and calculate the x and y extents across multiple data series (which are used by the x/y scales). It is designed such that you can add whatever charting sub-components needed to properly represent your data. We use a Hash Helper to yield params to chart subcomponents. The available params are: data, xScale, yScale, graphWidth, graphHeight, hoverData, and duration.',
      properties: [
        {
          name: 'data',
          type: 'array',
          required: true,
          description: 'The data to render. This needs to be an Array of Arrays. The objects within the inner arrays should be flat Objects.'
        },
        {
          name: 'xProp',
          type: 'string',
          required: false,
          description: 'The name of the property from which to pull X values.',
          default: 'x'
        },
        {
          name: 'yProp',
          type: 'string',
          required: false,
          description: 'The name of the property from which to pull Y values.',
          default: 'y'
        },
        {
          name: 'xScaleFn',
          type: 'function',
          required: false,
          description: 'A function that can map one set of values to another.',
          default: 'd3.scaleTime',
          see: 'https://github.com/d3/d3-scale'
        },
        {
          name: 'yScaleFn',
          type: 'function',
          required: false,
          description: 'A function that can map one set of values to another.',
          default: 'd3.scaleLinear',
          see: 'https://github.com/d3/d3-scale'
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
          name: 'margin',
          type: 'object',
          required: false,
          description: 'The spacing around the portion of the chart that is used to render the graph. This is mainly used to provide room for the axes to be rendered.',
          default: '{ top: 5, bottom: 30, left: 30, right: 0 }'
        }
      ],
      singleSeries: [
        [
          { 'name': 'day', 'type': 'TimeT', 'value': 1412011600000, 'count': 57240797 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412035200000, 'count': 40802921 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412208000000, 'count': 54824229 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412294400000, 'count': 62621099 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412380800000, 'count': 60796090 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412467200000, 'count': 46186237 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412553600000, 'count': 53948266 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412640000000, 'count': 63232914 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412726400000, 'count': 52186213 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412812800000, 'count': 3251360  }
        ]
      ],
      multiSeries: [
        [
          { 'name': 'day', 'type': 'TimeT', 'value': 1412011600000, 'count': 57240797 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412035200000, 'count': 40802921 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412208000000, 'count': 54824229 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412294400000, 'count': 62621099 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412380800000, 'count': 60796090 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412467200000, 'count': 46186237 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412553600000, 'count': 53948266 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412640000000, 'count': 63232914 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412726400000, 'count': 52186213 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412812800000, 'count': 3251360  }
        ],
        [
          { 'name': 'day', 'type': 'TimeT', 'value': 1412011600000, 'count': 47840797 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412035200000, 'count': 30302921 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412208000000, 'count': 44524229 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412294400000, 'count': 52921099 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412380800000, 'count': 50296090 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412467200000, 'count': 36986237 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412553600000, 'count': 43548266 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412640000000, 'count': 53832914 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412726400000, 'count': 48186213 },
          { 'name': 'day', 'type': 'TimeT', 'value': 1412812800000, 'count': 2051360  }
        ]
      ]
    };
  }
});
