import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({
  model() {
    return {
      title: 'Area Series',
      description: 'Creates an SVG Path element used to represent a series of data as an area chart. ',
      properties: [
        {
          name: 'data',
          type: 'array',
          required: true,
          description: 'The data to render. This needs to be an Array of Arrays. The objects within the inner arrays should be flat Objects.'
        },
        {
          name: 'dataIndex',
          type: 'number',
          required: false,
          description: 'The zero-based index of "data" to render.',
          default: 0
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
          name: 'xScale',
          type: 'function',
          required: true,
          description: 'A function that scales the values of "data" to pixel values.'
        },
        {
          name: 'yScale',
          type: 'function',
          required: true,
          description: 'A function that scales the values of "data" to pixel values.'
        },
        {
          name: 'height',
          type: 'number',
          required: true,
          description: 'The height of the parent SVG element.'
        },
        {
          name: 'interpolator',
          type: 'function',
          required: false,
          description: 'The function used to interoplate between points to produce a continuous shape.',
          default: 'd3.curveLinear',
          see: 'https://github.com/d3/d3-shape/blob/master/README.md#curves'
        }
      ],
      singleSeries: [
        [
          { x: 1412011600000, y: 57240797 },
          { x: 1412035200000, y: 40802921 },
          { x: 1412208000000, y: 54824229 },
          { x: 1412294400000, y: 62621099 },
          { x: 1412380800000, y: 60796090 },
          { x: 1412467200000, y: 46186237 },
          { x: 1412553600000, y: 53948266 },
          { x: 1412640000000, y: 63232914 },
          { x: 1412726400000, y: 52186213 },
          { x: 1412812800000, y: 3251360  }
        ]
      ],
      multiSeries: [
        [
          { x: 1412011600000, y: 57240797 },
          { x: 1412035200000, y: 40802921 },
          { x: 1412208000000, y: 54824229 },
          { x: 1412294400000, y: 62621099 },
          { x: 1412380800000, y: 60796090 },
          { x: 1412467200000, y: 46186237 },
          { x: 1412553600000, y: 53948266 },
          { x: 1412640000000, y: 63232914 },
          { x: 1412726400000, y: 52186213 },
          { x: 1412812800000, y: 3251360  }
        ],
        [
          { x: 1412011600000, y: 32946237 },
          { x: 1412035200000, y: 36956237 },
          { x: 1412208000000, y: 39926237 },
          { x: 1412294400000, y: 36966237 },
          { x: 1412380800000, y: 35916237 },
          { x: 1412467200000, y: 36926237 },
          { x: 1412553600000, y: 31966237 },
          { x: 1412640000000, y: 36976237 },
          { x: 1412726400000, y: 30926237 },
          { x: 1412812800000, y: 35986237 }
        ]
      ]
    };
  }
});
